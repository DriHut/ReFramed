package io.github.cottonmc.templates.model;

import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public interface TemplateAppearance {
	@NotNull Sprite getParticleSprite(); //TODO: plug this in
	@NotNull Sprite getSprite(Direction dir);
	boolean hasColor(Direction dir);
	
	record SingleSprite(@NotNull Sprite defaultSprite) implements TemplateAppearance {
		public SingleSprite(Sprite defaultSprite) {
			this.defaultSprite = Objects.requireNonNull(defaultSprite);
		}
		
		@Override
		public @NotNull Sprite getParticleSprite() {
			return defaultSprite;
		}
		
		@Override
		public @NotNull Sprite getSprite(Direction dir) {
			return defaultSprite;
		}
		
		@Override
		public boolean hasColor(Direction dir) {
			return false;
		}
	}
}
