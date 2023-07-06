package io.github.cottonmc.templates.model;

import io.github.cottonmc.templates.TemplatesClient;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;

public class RetexturedJsonModelUnbakedModel implements UnbakedModel {
	public RetexturedJsonModelUnbakedModel(Identifier parent) {
		this(parent, Blocks.AIR.getDefaultState());
	}
	
	public RetexturedJsonModelUnbakedModel(Identifier parent, BlockState itemModelState) {
		this.parent = parent;
		this.itemModelState = itemModelState;
	}
	
	protected final Identifier parent;
	protected final BlockState itemModelState;
	
	@Override
	public Collection<Identifier> getModelDependencies() {
		return Collections.singletonList(parent);
	}
	
	@Override
	public void setParents(Function<Identifier, UnbakedModel> function) {
		function.apply(parent).setParents(function);
	}
	
	@Nullable
	@Override
	public BakedModel bake(Baker baker, Function<SpriteIdentifier, Sprite> spriteLookup, ModelBakeSettings modelBakeSettings, Identifier identifier) {
		return new RetexturedJsonModelBakedModel(
			baker.bake(parent, modelBakeSettings),
			TemplatesClient.provider.getOrCreateTemplateApperanceManager(spriteLookup),
			modelBakeSettings,
			spriteLookup,
			itemModelState
		);
	}
}
