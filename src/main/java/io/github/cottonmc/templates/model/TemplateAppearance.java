package io.github.cottonmc.templates.model;

import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;

//TODO: move to the api package
public interface TemplateAppearance {
	@NotNull RenderMaterial getRenderMaterial(boolean ao);
	@NotNull Sprite getSprite(Direction dir);
	int getBakeFlags(Direction dir);
	boolean hasColor(Direction dir);
	
	//binary-compat - from before conditional model AO was added
	@Deprecated(forRemoval = true)
	default @NotNull RenderMaterial getRenderMaterial() {
		return getRenderMaterial(false);
	}
	
	//binary-compat - I never ended up implementing this, it's much easier to modify particles via the BlockState
	@Deprecated(forRemoval = true)
	default @NotNull Sprite getParticleSprite() {
		return getSprite(Direction.NORTH);
	}
}
