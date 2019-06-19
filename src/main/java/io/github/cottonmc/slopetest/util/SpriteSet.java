package io.github.cottonmc.slopetest.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SpriteSet {
	private Sprite global;
	private Map<Direction, BakedQuad> quads;
	private boolean singleSprite = false;
	private boolean globalHasColor = false;
	public static final Sprite FALLBACK = MinecraftClient.getInstance().getSpriteAtlas().getSprite(new Identifier("minecraft:block/scaffolding_top"));

	public SpriteSet(Sprite allSprite, boolean hasColor) {
		this.global = allSprite;
		singleSprite = true;
		globalHasColor = hasColor;
	}

	public SpriteSet(BakedModel model) {
		Random rand = new Random();
		quads = new HashMap<>();
		for (Direction dir : Direction.values()) {
			List<BakedQuad> quads = model.getQuads(null, dir, rand);
			if (!quads.isEmpty()) this.quads.put(dir, quads.get(0));
		}
	}

	public Sprite getSprite(Direction dir) {
		if (singleSprite) return global;
		if (quads.get(dir) == null) return FALLBACK;
		else return quads.get(dir).getSprite();
	}

	public boolean hasColor(Direction dir) {
		if (singleSprite) return globalHasColor;
		if (quads.get(dir) == null) return false;
		else return quads.get(dir).hasColor();
	}
}
