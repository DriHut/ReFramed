package fr.adrien1106.reframedtemplates.model.apperance;

import net.minecraft.client.texture.Sprite;

record Appearance(Sprite[] sprites, int[] flags, byte color_mask) {
}
