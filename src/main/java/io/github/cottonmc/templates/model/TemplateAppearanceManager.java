package io.github.cottonmc.templates.model;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class TemplateAppearanceManager {
	public TemplateAppearanceManager(Function<SpriteIdentifier, Sprite> spriteLookup) {
		Sprite defaultSprite = spriteLookup.apply(DEFAULT_SPRITE_ID);
		if(defaultSprite == null) throw new IllegalStateException("Couldn't locate " + DEFAULT_SPRITE_ID + " !");
		defaultAppearance = new TemplateAppearance.SingleSprite(defaultSprite);
	}
	
	private static final SpriteIdentifier DEFAULT_SPRITE_ID = new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, new Identifier("minecraft:block/scaffolding_top"));
	private final TemplateAppearance defaultAppearance;
	
	private final ConcurrentHashMap<BlockState, TemplateAppearance> appearanceCache = new ConcurrentHashMap<>();
	
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
		
		//Fill out any missing values in the sprites array
		for(int i = 0; i < sprites.length; i++) {
			if(sprites[i] == null) sprites[i] = defaultAppearance.getParticleSprite();
		}
		
		return new ComputedApperance(sprites, hasColorMask);
	}
	
	private static record ComputedApperance(@NotNull Sprite[] sprites, byte hasColorMask) implements TemplateAppearance {
		@Override
		public @NotNull Sprite getParticleSprite() {
			return sprites[6];
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
}
