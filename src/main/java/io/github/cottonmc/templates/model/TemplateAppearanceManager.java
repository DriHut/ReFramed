package io.github.cottonmc.templates.model;

import io.github.cottonmc.templates.TemplatesClient;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.MaterialFinder;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.block.BlockState;
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
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class TemplateAppearanceManager {
	public TemplateAppearanceManager(Function<SpriteIdentifier, Sprite> spriteLookup) {
		MaterialFinder finder = TemplatesClient.getFabricRenderer().materialFinder();
		for(BlendMode blend : BlendMode.values()) {
			blockMaterials.put(blend, finder.clear().disableDiffuse(false).ambientOcclusion(TriState.FALSE).blendMode(blend).find());
		}
		
		Sprite defaultSprite = spriteLookup.apply(DEFAULT_SPRITE_ID);
		if(defaultSprite == null) throw new IllegalStateException("Couldn't locate " + DEFAULT_SPRITE_ID + " !");
		this.defaultAppearance = new SingleSpriteAppearance(defaultSprite, blockMaterials.get(BlendMode.CUTOUT), serialNumber.getAndIncrement());
	}
	
	public static final SpriteIdentifier DEFAULT_SPRITE_ID = new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, new Identifier("minecraft:block/scaffolding_top"));
	private final TemplateAppearance defaultAppearance;
	
	private final ConcurrentHashMap<BlockState, TemplateAppearance> appearanceCache = new ConcurrentHashMap<>(); //Mutable, append-only cache
	private final AtomicInteger serialNumber = new AtomicInteger(0); //Mutable
	private final EnumMap<BlendMode, RenderMaterial> blockMaterials = new EnumMap<>(BlendMode.class); //Immutable contents
	
	public TemplateAppearance getDefaultAppearance() {
		return defaultAppearance;
	}
	
	public TemplateAppearance getAppearance(BlockState state) {
		return appearanceCache.computeIfAbsent(state, this::computeAppearance);
	}
	
	//I'm pretty sure ConcurrentHashMap semantics allow for this function to be called multiple times on the same key, on different threads.
	//The computeIfAbsent map update will work without corrupting the map, but there will be some "wasted effort" computing the value twice.
	//The results are going to be the same, apart from their serialNumbers differing (= their equals & hashCode differing).
	//Tiny amount of wasted space in some caches if TemplateAppearances are used as a map key, then. IMO it's not a critical issue.
	private TemplateAppearance computeAppearance(BlockState state) {
		Random rand = Random.create();
		BakedModel model = MinecraftClient.getInstance().getBlockRenderManager().getModel(state);
		
		Sprite[] sprites = new Sprite[7];
		byte hasColorMask = 0b000000;
		
		//Read quads off the model by their `cullface`
		for(Direction dir : Direction.values()) {
			List<BakedQuad> sideQuads = model.getQuads(null, dir, rand);
			if(sideQuads.isEmpty()) continue;
			
			BakedQuad arbitraryQuad = sideQuads.get(0); //TODO: maybe pick a largest quad instead?
			if(arbitraryQuad == null) continue;
			
			if(arbitraryQuad.hasColor()) hasColorMask |= (1 << dir.ordinal());
			
			Sprite sprite = arbitraryQuad.getSprite();
			if(sprite == null) continue;
			
			sprites[dir.ordinal()] = sprite;
		}
		
		//Just for space-usage purposes, we store the particle in sprites[6] instead of using another field.
		sprites[6] = model.getParticleSprite();
		
		//Fill out any missing values in the sprites array, since failure to pick textures shouldn't lead to NPEs later on
		for(int i = 0; i < sprites.length; i++) {
			if(sprites[i] == null) sprites[i] = defaultAppearance.getParticleSprite();
		}
		
		return new ComputedApperance(
			sprites,
			hasColorMask,
			blockMaterials.get(BlendMode.fromRenderLayer(RenderLayers.getBlockLayer(state))),
			serialNumber.getAndIncrement()
		);
	}
	
	@SuppressWarnings("ClassCanBeRecord")
	private static final class ComputedApperance implements TemplateAppearance {
		private final Sprite @NotNull[] sprites;
		private final byte hasColorMask;
		private final RenderMaterial mat;
		private final int id;
		
		private ComputedApperance(@NotNull Sprite @NotNull[] sprites, byte hasColorMask, RenderMaterial mat, int id) {
			this.sprites = sprites;
			this.hasColorMask = hasColorMask;
			this.mat = mat;
			this.id = id;
		}
		
		@Override
		public @NotNull Sprite getParticleSprite() {
			return sprites[6];
		}
		
		@Override
		public @NotNull RenderMaterial getRenderMaterial() {
			return mat;
		}
		
		@Override
		public @NotNull Sprite getSprite(Direction dir) {
			return sprites[dir.ordinal()];
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
			return "ComputedApperance[sprites=%s, hasColorMask=%s, mat=%s, id=%d]".formatted(Arrays.toString(sprites), hasColorMask, mat, id);
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
		public @NotNull RenderMaterial getRenderMaterial() {
			return mat;
		}
		
		@Override
		public @NotNull Sprite getSprite(Direction dir) {
			return defaultSprite;
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
