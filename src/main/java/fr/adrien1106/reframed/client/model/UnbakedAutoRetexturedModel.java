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
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class UnbakedAutoRetexturedModel extends UnbakedRetexturedModel {

	public UnbakedAutoRetexturedModel(Identifier parent, int state_count, Property<?>... properties) {
		super(parent, state_count, properties);
		item_state = Blocks.AIR.getDefaultState();
	}
	
	@Nullable
	@Override
	public BakedModel bake(Baker baker, Function<SpriteIdentifier, Sprite> texture_getter, ModelBakeSettings bake_settings, Identifier identifier) {
		return new RetexturingBakedModel(
			baker.bake(parent, bake_settings),
			ReFramedClient.HELPER.getCamoAppearanceManager(texture_getter),
			theme_index,
			bake_settings,
			item_state,
			state_count,
			properties
		) {
			protected Mesh convertModel(BlockState state) {
				Renderer r = ReFramedClient.HELPER.getFabricRenderer();
				MeshBuilder builder = r.meshBuilder();
				QuadEmitter emitter = builder.getEmitter();
				RenderMaterial mat = appearance_manager.getCachedMaterial(state, false);
				
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
