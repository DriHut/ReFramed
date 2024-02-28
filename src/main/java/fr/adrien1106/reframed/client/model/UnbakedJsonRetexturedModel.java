package fr.adrien1106.reframed.client.model;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.client.ReFramedClient;
import fr.adrien1106.reframed.util.ThemeableBlockEntity;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;

public class UnbakedJsonRetexturedModel extends UnbakedRetexturedModel {
	public UnbakedJsonRetexturedModel(Identifier parent, Function<ThemeableBlockEntity, BlockState> state_getter) {
        super(parent, state_getter);
	}
	
	@Nullable
	@Override
	public BakedModel bake(Baker baker, Function<SpriteIdentifier, Sprite> spriteLookup, ModelBakeSettings bake_settings, Identifier identifier) {
		Direction[] DIRECTIONS = RetexturingBakedModel.DIRECTIONS;
		
		Sprite[] specialSprites = new Sprite[DIRECTIONS.length];
		for(int i = 0; i < DIRECTIONS.length; i++) {
			SpriteIdentifier id = new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, ReFramed.id("reframed_special/" + DIRECTIONS[i].getName()));
			specialSprites[i] = Objects.requireNonNull(spriteLookup.apply(id), () -> "Couldn't find sprite " + id + " !");
		}

		return new RetexturingBakedModel(
			baker.bake(parent, bake_settings),
			ReFramedClient.HELPER.getCamoApperanceManager(spriteLookup),
			state_getter,
			bake_settings,
			item_state,
			ao
		) {
			protected Mesh convertModel(BlockState state) {
				Renderer r = ReFramedClient.HELPER.getFabricRenderer();
				MeshBuilder builder = r.meshBuilder();
				QuadEmitter emitter = builder.getEmitter();
				RenderMaterial mat = tam.getCachedMaterial(state, false);
				
				Random rand = Random.create(42);

				for(Direction cullFace : DIRECTIONS_AND_NULL) {
					for(BakedQuad quad : wrapped.getQuads(state, cullFace, rand)) {
						emitter.fromVanilla(quad, mat, cullFace);
						
						QuadUvBounds bounds = QuadUvBounds.read(emitter);
						for(int i = 0; i < specialSprites.length; i++) {
							if(bounds.displaysSprite(specialSprites[i])) {
								bounds.normalizeUv(emitter, specialSprites[i]);
								emitter.tag(i + 1);
								break;
							}
						}
						
						emitter.emit();
					}
				}
				
				return builder.build();
			}
		};
	}
}
