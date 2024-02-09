package fr.adrien1106.reframedtemplates.model.apperance;

import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface TemplateAppearance {
	@NotNull RenderMaterial getRenderMaterial(boolean ao);
	@NotNull Sprite getSprite(Direction dir, @NotNull long seed);
	int getBakeFlags(Direction dir, @NotNull long seed);
	boolean hasColor(Direction dir, @NotNull long seed);
}
