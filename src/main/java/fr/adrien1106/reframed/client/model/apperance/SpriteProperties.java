package fr.adrien1106.reframed.client.model.apperance;

import fr.adrien1106.reframed.client.model.QuadPosBounds;
import net.minecraft.client.texture.Sprite;

public record SpriteProperties(Sprite sprite, int flags, QuadPosBounds bounds) {
}
