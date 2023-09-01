package io.github.cottonmc.templates.model;

import io.github.cottonmc.templates.TemplatesClient;
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

public class TemplateAppearanceManager {
	public TemplateAppearanceManager(Function<SpriteIdentifier, Sprite> spriteLookup) {
		MaterialFinder finder = TemplatesClient.getFabricRenderer().materialFinder();
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
	
	@ApiStatus.Internal //shouldn't have made this public, just maintaining abi compat
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
	
	//I'm pretty sure ConcurrentHashMap semantics allow for this function to be called multiple times on the same key, on different threads.
	//The computeIfAbsent map update will work without corrupting the map, but there will be some "wasted effort" computing the value twice.
	//The results are going to be the same, apart from their serialNumbers differing (= their equals & hashCode differing).
	//Tiny amount of wasted space in some caches if TemplateAppearances are used as a map key, then. IMO it's not a critical issue.
	private TemplateAppearance computeAppearance(BlockState state) {
		if(state.getBlock() == Blocks.BARRIER) return barrierItemAppearance;
		
		Random rand = Random.create(42);
		BakedModel model = MinecraftClient.getInstance().getBlockRenderManager().getModel(state);
		
		//Only for parsing vanilla quads:
		QuadEmitter emitter = TemplatesClient.getFabricRenderer().meshBuilder().getEmitter();
		RenderMaterial defaultMat = TemplatesClient.getFabricRenderer().materialFinder().clear().find();
		
		Sprite[] sprites = new Sprite[7];
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
			
			//GOAL: Reconstruct the `"rotation": 90` stuff that a json model might provide, so we can bake our texture on the same way
			emitter.fromVanilla(arbitraryQuad, defaultMat, dir);
			int lowHighSignature = 0;
			for(int i = 0; i < 4; i++) {
				//For some reason the uvs stored on the Sprite have less ?precision? than the ones retrieved from the QuadEmitter.
				// [STDOUT]: emitter u 0.14065552, sprite min u 0.140625
				//?precision? in question marks cause it could be float noise. It's way higher than the epsilon in MathHelper.approximatelyEquals.
				//So im gonna guesstimate using "is it closer to the sprite's min or max u", rather than doing an approximately-equals check
				
				float diffMinU = Math.abs(emitter.u(i) - sprite.getMinU());
				float diffMaxU = Math.abs(emitter.u(i) - sprite.getMaxU());
				boolean minU = diffMinU < diffMaxU;
				
				float diffMinV = Math.abs(emitter.v(i) - sprite.getMinV());
				float diffMaxV = Math.abs(emitter.v(i) - sprite.getMaxV());
				boolean minV = diffMinV < diffMaxV;
				
				lowHighSignature <<= 2;
				lowHighSignature |= (minU ? 2 : 0) | (minV ? 1 : 0);
			}
			
			if(lowHighSignature == 0b11100001) {
				bakeFlags[dir.ordinal()] = 0;
			} else if(lowHighSignature == 0b10000111) {
				bakeFlags[dir.ordinal()] = MutableQuadView.BAKE_ROTATE_90;
			} else if(lowHighSignature == 0b00011110) {
				bakeFlags[dir.ordinal()] = MutableQuadView.BAKE_ROTATE_180;
			} else if(lowHighSignature == 0b01111000) {
				bakeFlags[dir.ordinal()] = MutableQuadView.BAKE_ROTATE_270;
			} else {
				//Its not critical error or anything, the texture will show rotated or flipped
				//System.out.println("unknown sig " + Integer.toString(lowHighSignature, 2) + ", state: " + state + ", sprite: " + sprite.getContents().getId() + ", side: " + dir);
			}
		}
		
		//Just for space-usage purposes, we store the particle in sprites[6] instead of using another field.
		sprites[6] = model.getParticleSprite();
		
		//Fill out any missing values in the sprites array, since failure to pick textures shouldn't lead to NPEs later on
		for(int i = 0; i < sprites.length; i++) {
			if(sprites[i] == null) sprites[i] = defaultAppearance.getParticleSprite();
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
		public @NotNull Sprite getParticleSprite() {
			return sprites[6];
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
		public @NotNull Sprite getParticleSprite() {
			return defaultSprite;
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
