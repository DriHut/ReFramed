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

import java.util.EnumMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class TemplateAppearanceManager {
	public TemplateAppearanceManager(Function<SpriteIdentifier, Sprite> spriteLookup) {
		SpriteIdentifier defaultSpriteId = new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, new Identifier("minecraft:block/scaffolding_top"));
		Sprite defaultSprite = spriteLookup.apply(defaultSpriteId);
		if(defaultSprite == null) throw new IllegalStateException("Couldn't locate " + defaultSpriteId + " !");
		
		MaterialFinder finder = TemplatesClient.getFabricRenderer().materialFinder();
		
		for(BlendMode blend : BlendMode.values()) {
			blockMaterials.put(blend, finder.clear().disableDiffuse(false).ambientOcclusion(TriState.FALSE).blendMode(blend).find());
		}
		
		this.defaultAppearance = new SingleSpriteAppearance(defaultSprite, blockMaterials.get(BlendMode.CUTOUT));
	}
	
	private final TemplateAppearance defaultAppearance;
	
	//Mutable, append-only cache:
	private final ConcurrentHashMap<BlockState, TemplateAppearance> appearanceCache = new ConcurrentHashMap<>();
	
	//Immutable contents:
	private final EnumMap<BlendMode, RenderMaterial> blockMaterials = new EnumMap<>(BlendMode.class);
	
	public TemplateAppearance getDefaultAppearance() {
		return defaultAppearance;
	}
	
	public TemplateAppearance getAppearance(BlockState state) {
		return appearanceCache.computeIfAbsent(state, this::computeAppearance);
	}
	
	private TemplateAppearance computeAppearance(BlockState state) {
		Random rand = Random.create();
		BakedModel model = MinecraftClient.getInstance().getBlockRenderManager().getModel(state);
		
		Sprite[] sprites = new Sprite[7];
		byte hasColorMask = 0b000000;
		
		//Read quads off the model.
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
		
		//Fill out any missing values in the sprites array. Failure to pick textures shouldn't lead to NPEs later on.
		for(int i = 0; i < sprites.length; i++) {
			if(sprites[i] == null) sprites[i] = defaultAppearance.getParticleSprite();
		}
		
		return new ComputedApperance(sprites, hasColorMask, blockMaterials.get(BlendMode.fromRenderLayer(RenderLayers.getBlockLayer(state))));
	}
	
	private static record ComputedApperance(@NotNull Sprite[] sprites, byte hasColorMask, RenderMaterial mat) implements TemplateAppearance {
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
	}
	
	private static record SingleSpriteAppearance(@NotNull Sprite defaultSprite, RenderMaterial mat) implements TemplateAppearance {
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
	}
}
