package io.github.cottonmc.templates.util;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Function;

public class SpriteSet {
	public SpriteSet(Function<SpriteIdentifier, Sprite> spriteLookup) {
		//TODO: I can probably find these from a public static location
		// I think I tried that, though, and they were null. But I might have been doing it too early
		// Regardless, they should not be stored in static fields (resource-reload could invalidate them)
		this.defaultSprite = spriteLookup.apply(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier("minecraft:block/scaffolding_top")));
		this.missingSprite = spriteLookup.apply(new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, MissingSprite.getMissingSpriteId()));
		
		if(defaultSprite == null) throw new IllegalStateException("defaultSprite == null!");
		if(missingSprite == null) throw new IllegalStateException("missingSprite == null!");
		
		clear();
	}
	
	private static final Direction[] DIRECTIONS = Direction.values();
	
	private final Sprite defaultSprite;
	private final Sprite missingSprite;
	
	private final EnumMap<Direction, BakedQuad> quads = new EnumMap<>(Direction.class);
	private boolean isDefault = true;
	
	/** Allow re-use of instances to avoid allocation in render loop */
	public void clear() {
		isDefault = true;
	}
	
	/** Allow re-use of instances to avoid allocation in render loop */
	//TODO: pass in block state?
	public void prepare(BakedModel model, Random rand) {
		this.quads.clear();
		isDefault = false;
		
		for(Direction dir : DIRECTIONS) {
			List<BakedQuad> quads = model.getQuads(null, dir, rand);
			if(!quads.isEmpty()) this.quads.put(dir, quads.get(0));
		}
	}
	
	public Sprite getSprite(Direction dir) {
		if(isDefault) return defaultSprite;
		
		BakedQuad quad = quads.get(dir);
		if(quad == null) return missingSprite;
		
		Sprite sprite = quad.getSprite();
		if(sprite == null) return missingSprite;
		
		return sprite;
	}
	
	public boolean hasColor(Direction dir) {
		if(isDefault) return false;
		
		BakedQuad quad = quads.get(dir);
		if(quad == null) return false;
		
		return quad.hasColor();
	}
}
