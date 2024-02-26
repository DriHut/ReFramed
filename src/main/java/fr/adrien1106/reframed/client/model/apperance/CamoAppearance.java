package fr.adrien1106.reframed.client.model.apperance;

import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface CamoAppearance {
	@NotNull RenderMaterial getRenderMaterial(boolean ao);
	@NotNull List<SpriteProperties> getSprites(Direction dir, long seed);
	boolean hasColor(Direction dir, long seed, int index);
}
