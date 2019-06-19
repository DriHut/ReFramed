package io.github.cottonmc.slopetest;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SpriteSet {
	Sprite global;
	Map<Direction, BakedQuad> quads = new HashMap<>();
	boolean singleSprite = false;
	boolean globalHasColor = false;

	public SpriteSet(Sprite allSprite, boolean hasColor) {
		this.global = allSprite;
		singleSprite = true;
		globalHasColor = hasColor;
	}

	public SpriteSet(BakedModel model) {
		Random rand = new Random();
		for (Direction dir : Direction.values()) {
			List<BakedQuad> quads = model.getQuads(null, dir, rand);
			this.quads.put(dir, quads.get(0));
		}
	}

	public Sprite getSprite(Direction dir) {
		if (singleSprite) return global;
		else return quads.get(dir).getSprite();
	}

	public boolean hasColor(Direction dir) {
		if (singleSprite) return globalHasColor;
		else return quads.get(dir).hasColor();
	}
}
