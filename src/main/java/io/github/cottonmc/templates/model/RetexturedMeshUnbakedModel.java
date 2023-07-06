package io.github.cottonmc.templates.model;

import io.github.cottonmc.templates.TemplatesClient;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
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
import java.util.function.Supplier;

@SuppressWarnings("ClassCanBeRecord")
public class RetexturedMeshUnbakedModel implements UnbakedModel {
	public RetexturedMeshUnbakedModel(Identifier parent, Supplier<Mesh> baseMeshFactory) {
		this.parent = parent;
		this.baseMeshFactory = baseMeshFactory;
	}
	
	protected final Identifier parent;
	protected final Supplier<Mesh> baseMeshFactory;
	
	@Override
	public Collection<Identifier> getModelDependencies() {
		return Collections.singletonList(parent);
	}
	
	@Override
	public void setParents(Function<Identifier, UnbakedModel> function) {
		function.apply(parent).setParents(function); //Still not sure what this function does lol
	}
	
	@Override
	public BakedModel bake(Baker baker, Function<SpriteIdentifier, Sprite> spriteLookup, ModelBakeSettings modelBakeSettings, Identifier identifier) {
		return new RetexturedMeshBakedModel(
			baker.bake(parent, modelBakeSettings),
			TemplatesClient.provider.getOrCreateTemplateApperanceManager(spriteLookup),
			modelBakeSettings,
			baseMeshFactory.get()
		);
	}
}
