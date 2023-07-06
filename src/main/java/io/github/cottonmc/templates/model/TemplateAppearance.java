package io.github.cottonmc.templates.model;

import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;

public interface TemplateAppearance {
	@NotNull Sprite getParticleSprite(); //TODO: plug this in
	
	@NotNull RenderMaterial getRenderMaterial();
	@NotNull Sprite getSprite(Direction dir);
	int getBakeFlags(Direction dir);
	boolean hasColor(Direction dir);
}
