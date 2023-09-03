package io.github.cottonmc.templates.model;

import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;

//TODO: move to the api package
public interface TemplateAppearance {
	@NotNull Sprite getParticleSprite(); //TODO: plug this in (particle mixins don't use it atm)
	
	@NotNull RenderMaterial getRenderMaterial(boolean ao);
	@NotNull Sprite getSprite(Direction dir);
	int getBakeFlags(Direction dir);
	boolean hasColor(Direction dir);
	
	//binary-compat
	@Deprecated(forRemoval = true)
	default @NotNull RenderMaterial getRenderMaterial() {
		return getRenderMaterial(false);
	}
}
