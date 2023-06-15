package io.github.cottonmc.templates.model;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;

@FunctionalInterface
public interface SimpleUnbakedModel extends UnbakedModel {
	BakedModel bake();
	
	@Override
	default Collection<Identifier> getModelDependencies() {
		return Collections.emptyList();
	}
	
	@Override
	default void setParents(Function<Identifier, UnbakedModel> function) {
		// nope
	}
	
	@Override
	default @Nullable BakedModel bake(Baker baker, Function<SpriteIdentifier, Sprite> function, ModelBakeSettings modelBakeSettings, Identifier identifier) {
		return bake();
	}
}
