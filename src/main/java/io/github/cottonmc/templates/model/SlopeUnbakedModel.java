package io.github.cottonmc.templates.model;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;

public record SlopeUnbakedModel(BlockState slopeState) implements UnbakedModel {
	@Override
	public Collection<Identifier> getModelDependencies() {
		return Collections.emptyList();
	}
	
	@Override
	public void setParents(Function<Identifier, UnbakedModel> function) {
		//nothing to see here
	}
	
	@Override
	public BakedModel bake(Baker baker, Function<SpriteIdentifier, Sprite> function, ModelBakeSettings modelBakeSettings, Identifier identifier) {
		//TODO: weird, should use my own model instead of STONE
		BakedModel baseModel = baker.bake(BlockModels.getModelId(Blocks.SANDSTONE_STAIRS.getDefaultState()), modelBakeSettings);
		
		return new SlopeBakedModel(baseModel, slopeState, function);
	}
}
