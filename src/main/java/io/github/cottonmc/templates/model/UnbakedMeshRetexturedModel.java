package io.github.cottonmc.templates.model;

import io.github.cottonmc.templates.api.TemplatesClientApi;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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

public class UnbakedMeshRetexturedModel implements UnbakedModel, TemplatesClientApi.TweakableUnbakedModel {
	public UnbakedMeshRetexturedModel(Identifier parent, Function<Function<SpriteIdentifier, Sprite>, Mesh> baseMeshFactory) {
		this.parent = parent;
		this.baseMeshFactory = baseMeshFactory;
	}
	
	protected final Identifier parent;
	protected final Function<Function<SpriteIdentifier, Sprite>, Mesh> baseMeshFactory;
	protected boolean ao = true;
	
	/// user configuration
	
	@Override
	public UnbakedMeshRetexturedModel disableAo() {
		ao = false;
		return this;
	}
	
	@Override
	public TemplatesClientApi.TweakableUnbakedModel itemModelState(BlockState state) {
		throw new UnsupportedOperationException("UnbakedMeshRetexturedModel does not need an item model state set; it uses meshes");
	}
	
	/// actual unbakedmodel stuff
	
	@Override
	public Collection<Identifier> getModelDependencies() {
		return Collections.singletonList(parent);
	}
	
	@Override
	public void setParents(Function<Identifier, UnbakedModel> function) {
		function.apply(parent).setParents(function);
	}
	
	@Override
	public BakedModel bake(Baker baker, Function<SpriteIdentifier, Sprite> spriteLookup, ModelBakeSettings modelBakeSettings, Identifier identifier) {
		Mesh transformedBaseMesh = MeshTransformUtil.pretransformMesh(baseMeshFactory.apply(spriteLookup), MeshTransformUtil.applyAffine(modelBakeSettings));
		
		return new RetexturingBakedModel(
			baker.bake(parent, modelBakeSettings),
			TemplatesClientApi.getInstance().getOrCreateTemplateApperanceManager(spriteLookup),
			modelBakeSettings,
			Blocks.AIR.getDefaultState(),
			ao
		) {
			@Override
			protected Mesh getBaseMesh(BlockState state) {
				return transformedBaseMesh;
			}
		};
	}
	
	//ABI compat
	@Deprecated(forRemoval = true) //2.2 - use TemplatesClientApi.getInstance().mesh
	public UnbakedMeshRetexturedModel(Identifier parent, Supplier<Mesh> baseMeshFactory) {
		this(parent, __ -> baseMeshFactory.get());
	}
}
