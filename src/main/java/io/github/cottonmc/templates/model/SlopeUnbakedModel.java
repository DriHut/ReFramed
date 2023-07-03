package io.github.cottonmc.templates.model;

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

public class SlopeUnbakedModel implements UnbakedModel {
	@Override
	public Collection<Identifier> getModelDependencies() {
		return Collections.emptyList();
	}
	
	@Override
	public void setParents(Function<Identifier, UnbakedModel> function) {
		//nothing to see here
	}
	
	@Override
	public BakedModel bake(Baker baker, Function<SpriteIdentifier, Sprite> spriteLookup, ModelBakeSettings modelBakeSettings, Identifier identifier) {
		//TODO: this is weird, should use my own model instead.
		// I should also adjust the item frame/first-person rotations (previously I used SANDSTONE_STAIRS, which has models/block/stairs.json as a parent,
		// and that one brings some extra custom rotations along for the ride
		BakedModel baseModel = baker.bake(BlockModels.getModelId(Blocks.SANDSTONE.getDefaultState()), modelBakeSettings);
		
		//TODO: push this up (it's just a cache of data sourced from blockmodels, and can be shared among *all* templates/cached until resource-reload)
		TemplateAppearanceManager tam = new TemplateAppearanceManager(spriteLookup);
		
		return new TemplateBakedModel(baseModel, tam, modelBakeSettings.getRotation(), SlopeBaseMesh.make());
	}
}
