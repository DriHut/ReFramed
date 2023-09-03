package io.github.cottonmc.templates.model;

import io.github.cottonmc.templates.api.TemplatesClientApi;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.MaterialFinder;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

//TODO: extract an API for the api package
public class TemplateAppearanceManager {
	public TemplateAppearanceManager(Function<SpriteIdentifier, Sprite> spriteLookup) {
		MaterialFinder finder = TemplatesClientApi.getInstance().getFabricRenderer().materialFinder();
		for(BlendMode blend : BlendMode.values()) {
			finder.clear().disableDiffuse(false).blendMode(blend);
			
			materialsWithoutAo.put(blend, finder.ambientOcclusion(TriState.FALSE).find());
			materialsWithAo.put(blend, finder.ambientOcclusion(TriState.DEFAULT).find()); //not "true" since that *forces* AO, i just want to *allow* AO
		}
		
		Sprite defaultSprite = spriteLookup.apply(DEFAULT_SPRITE_ID);
		if(defaultSprite == null) throw new IllegalStateException("Couldn't locate " + DEFAULT_SPRITE_ID + " !");
		this.defaultAppearance = new SingleSpriteAppearance(defaultSprite, materialsWithoutAo.get(BlendMode.CUTOUT), serialNumber.getAndIncrement());
		
		Sprite barrier = spriteLookup.apply(BARRIER_SPRITE_ID);
		if(barrier == null) barrier = defaultSprite; //eh
		this.barrierItemAppearance = new SingleSpriteAppearance(barrier, materialsWithoutAo.get(BlendMode.CUTOUT), serialNumber.getAndIncrement());
	}
	
	//TODO ABI: Shouldn't have been made public. Noticed this in 2.2.
	@ApiStatus.Internal
	public static final SpriteIdentifier DEFAULT_SPRITE_ID = new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, new Identifier("minecraft:block/scaffolding_top"));
	private static final SpriteIdentifier BARRIER_SPRITE_ID = new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, new Identifier("minecraft:item/barrier"));
	
	private final TemplateAppearance defaultAppearance;
	private final TemplateAppearance barrierItemAppearance;
	
	private final ConcurrentHashMap<BlockState, TemplateAppearance> appearanceCache = new ConcurrentHashMap<>(); //Mutable, append-only cache
	private final AtomicInteger serialNumber = new AtomicInteger(0); //Mutable
	
	private final EnumMap<BlendMode, RenderMaterial> materialsWithAo = new EnumMap<>(BlendMode.class);
	private final EnumMap<BlendMode, RenderMaterial> materialsWithoutAo = new EnumMap<>(BlendMode.class); //Immutable contents
	
	public TemplateAppearance getDefaultAppearance() {
		return defaultAppearance;
	}
	
	public TemplateAppearance getAppearance(BlockState state) {
		return appearanceCache.computeIfAbsent(state, this::computeAppearance);
	}
	
	public RenderMaterial getCachedMaterial(BlockState state, boolean ao) {
		Map<BlendMode, RenderMaterial> m = ao ? materialsWithAo : materialsWithoutAo;
		return m.get(BlendMode.fromRenderLayer(RenderLayers.getBlockLayer(state)));
	}
	
	private static final int[] MAGIC_BAKEFLAGS_SBOX = new int[16];
	static {
		//left to right -> u0 v0 u1 v1
		//the bit is set if the coordinate is "high"
		MAGIC_BAKEFLAGS_SBOX[0b1110] = 0;
		MAGIC_BAKEFLAGS_SBOX[0b1000] = MutableQuadView.BAKE_ROTATE_90;
		MAGIC_BAKEFLAGS_SBOX[0b0001] = MutableQuadView.BAKE_ROTATE_180;
		MAGIC_BAKEFLAGS_SBOX[0b0111] = MutableQuadView.BAKE_ROTATE_270;
		//TODO: handle more cases. Also, it might be possible to drop v1 and still have the table be unambiguous
	}
	
	//I'm pretty sure ConcurrentHashMap semantics allow for this function to be called multiple times on the same key, on different threads.
	//The computeIfAbsent map update will work without corrupting the map, but there will be some "wasted effort" computing the value twice.
	//The results are going to be the same, apart from their serialNumbers differing (= their equals & hashCode differing).
	//Tiny amount of wasted space in some caches if TemplateAppearances are used as a map key, then. IMO it's not a critical issue.
	private TemplateAppearance computeAppearance(BlockState state) {
		if(state.getBlock() == Blocks.BARRIER) return barrierItemAppearance;
		
		Random rand = Random.create(42);
		BakedModel model = MinecraftClient.getInstance().getBlockRenderManager().getModel(state);
		
		//Only for parsing vanilla quads:
		Renderer r = TemplatesClientApi.getInstance().getFabricRenderer();
		QuadEmitter emitterAsParser = r.meshBuilder().getEmitter();
		RenderMaterial defaultMat = r.materialFinder().clear().find();
		
		Sprite[] sprites = new Sprite[6];
		int[] bakeFlags = new int[6];
		byte hasColorMask = 0b000000;
		
		//Read quads off the model by their `cullface`
		for(Direction dir : Direction.values()) {
			List<BakedQuad> sideQuads = model.getQuads(null, dir, rand);
			if(sideQuads.isEmpty()) continue;
			
			BakedQuad arbitraryQuad = sideQuads.get(0); //TODO: maybe pick a largest quad instead?
			if(arbitraryQuad == null) continue;
			
			if(arbitraryQuad.hasColor()) hasColorMask |= (byte) (1 << dir.ordinal());
			
			Sprite sprite = arbitraryQuad.getSprite();
			if(sprite == null) continue;
			sprites[dir.ordinal()] = sprite;
			
			//Problem: Some models (eg. pistons, stone, glazed terracotta) have their UV coordinates permuted in
			//non-standard ways. The actual png image appears sideways, but the original model's UVs rotate it right way up again.
			//If I simply display the texture on my model, it will appear sideways, like it does in the png.
			//If I can discover the pattern of rotations and flips on the original model, I can "un-rotate" the texture back
			//into a standard orientation.
			//
			//Solution: Look at the first and second vertices of the orignial quad, and decide whether their UV coordinates
			//are "low" or "high" compared to the middle of the sprite. The first two vertices uniquely determine the pattern
			//of the other two (since UV coordinates are unique and shouldn't cross). There are 16 possibilities so this information
			//is easily summarized in a bitfield, and the correct fabric rendering API "bake flags" to un-transform the sprite
			//are looked up with a simple table.
			emitterAsParser.fromVanilla(arbitraryQuad, defaultMat, dir);
			
			float spriteUAvg = (sprite.getMinU() + sprite.getMaxU()) / 2;
			float spriteVAvg = (sprite.getMinV() + sprite.getMaxV()) / 2;
			
			bakeFlags[dir.ordinal()] = MAGIC_BAKEFLAGS_SBOX[
				(emitterAsParser.u(0) < spriteUAvg ? 8 : 0) |
				(emitterAsParser.v(0) < spriteVAvg ? 4 : 0) |
				(emitterAsParser.u(1) < spriteUAvg ? 2 : 0) |
				(emitterAsParser.v(1) < spriteVAvg ? 1 : 0)
			];
		}
		
		//Fill out any missing values in the sprites array, since failure to pick textures shouldn't lead to NPEs later on
		for(int i = 0; i < sprites.length; i++) {
			if(sprites[i] == null) sprites[i] = defaultAppearance.getSprite(Direction.byId(i));
		}
		
		return new ComputedApperance(
			sprites,
			bakeFlags,
			hasColorMask,
			getCachedMaterial(state, true),
			getCachedMaterial(state, false),
			serialNumber.getAndIncrement()
		);
	}
	
	private static final class ComputedApperance implements TemplateAppearance {
		private final Sprite @NotNull[] sprites;
		private final int @NotNull[] bakeFlags;
		private final byte hasColorMask;
		private final int id;
		private final RenderMaterial matWithAo;
		private final RenderMaterial matWithoutAo;
		
		private ComputedApperance(@NotNull Sprite @NotNull[] sprites, int @NotNull[] bakeFlags, byte hasColorMask, RenderMaterial withAo, RenderMaterial withoutAo, int id) {
			this.sprites = sprites;
			this.bakeFlags = bakeFlags;
			this.hasColorMask = hasColorMask;
			this.id = id;
			
			this.matWithAo = withAo;
			this.matWithoutAo = withoutAo;
		}
		
		@Override
		public @NotNull RenderMaterial getRenderMaterial(boolean ao) {
			return ao ? matWithAo : matWithoutAo;
		}
		
		@Override
		public @NotNull Sprite getSprite(Direction dir) {
			return sprites[dir.ordinal()];
		}
		
		@Override
		public int getBakeFlags(Direction dir) {
			return bakeFlags[dir.ordinal()];
		}
		
		@Override
		public boolean hasColor(Direction dir) {
			return (hasColorMask & (1 << dir.ordinal())) != 0;
		}
		
		@Override
		public boolean equals(Object o) {
			if(this == o) return true;
			if(o == null || getClass() != o.getClass()) return false;
			ComputedApperance that = (ComputedApperance) o;
			return id == that.id;
		}
		
		@Override
		public int hashCode() {
			return id;
		}
		
		@Override
		public String toString() {
			return "ComputedApperance{sprites=%s, bakeFlags=%s, hasColorMask=%s, matWithoutAo=%s, matWithAo=%s, id=%d}".formatted(Arrays.toString(sprites), Arrays.toString(bakeFlags), hasColorMask, matWithoutAo, matWithAo, id);
		}
	}
	
	@SuppressWarnings("ClassCanBeRecord")
	private static final class SingleSpriteAppearance implements TemplateAppearance {
		private final @NotNull Sprite defaultSprite;
		private final RenderMaterial mat;
		private final int id;
		
		private SingleSpriteAppearance(@NotNull Sprite defaultSprite, RenderMaterial mat, int id) {
			this.defaultSprite = defaultSprite;
			this.mat = mat;
			this.id = id;
		}
		
		@Override
		public @NotNull RenderMaterial getRenderMaterial(boolean ao) {
			return mat;
		}
		
		@Override
		public @NotNull Sprite getSprite(Direction dir) {
			return defaultSprite;
		}
		
		@Override
		public int getBakeFlags(Direction dir) {
			return 0;
		}
		
		@Override
		public boolean hasColor(Direction dir) {
			return false;
		}
		
		@Override
		public boolean equals(Object o) {
			if(this == o) return true;
			if(o == null || getClass() != o.getClass()) return false;
			SingleSpriteAppearance that = (SingleSpriteAppearance) o;
			return id == that.id;
		}
		
		@Override
		public int hashCode() {
			return id;
		}
		
		@Override
		public String toString() {
			return "SingleSpriteAppearance[defaultSprite=%s, mat=%s, id=%d]".formatted(defaultSprite, mat, id);
		}
	}
}
