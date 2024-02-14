package fr.adrien1106.reframed.client.model.apperance;

import net.minecraft.client.texture.Sprite;

public record Appearance(Sprite[] sprites, int[] flags, byte color_mask) {}
