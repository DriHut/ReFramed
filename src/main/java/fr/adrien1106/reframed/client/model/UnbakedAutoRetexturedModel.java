package fr.adrien1106.reframed.client.model;

import fr.adrien1106.reframed.client.ReFramedClient;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

public class UnbakedAutoRetexturedModel implements UnbakedModel {
	public UnbakedAutoRetexturedModel(Identifier parent) {
		this.parent = parent;
	}
	
	protected final Identifier parent;
	protected BlockState itemModelState = Blocks.AIR.getDefaultState();
	protected boolean ao = true;
	
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
		return new RetexturingBakedModel(
			baker.bake(parent, modelBakeSettings),
			ReFramedClient.HELPER.getCamoApperanceManager(spriteLookup),
			modelBakeSettings,
			itemModelState,
			ao
		) {
			final ConcurrentMap<BlockState, Mesh> jsonToMesh = new ConcurrentHashMap<>();
			
			@Override
			protected Mesh getBaseMesh(BlockState state) {
				//Convert models to retexturable Meshes lazily, the first time we encounter each blockstate
				return jsonToMesh.computeIfAbsent(state, this::convertModel);
			}
			
			private Mesh convertModel(BlockState state) {
				Renderer r = ReFramedClient.HELPER.getFabricRenderer();
				MeshBuilder builder = r.meshBuilder();
				QuadEmitter emitter = builder.getEmitter();
				RenderMaterial mat = tam.getCachedMaterial(state, false);
				
				Random rand = Random.create(42);

				for(Direction direction : DIRECTIONS_AND_NULL) {
					for(BakedQuad quad : wrapped.getQuads(state, direction, rand)) {
						emitter.fromVanilla(quad, mat, direction);
						QuadUvBounds.read(emitter).normalizeUv(emitter, quad.getSprite());
						emitter.tag(emitter.lightFace().ordinal() + 1);
						emitter.emit();
					}
				}
				
				return builder.build();
			}
		};
	}
}
