package fr.adrien1106.reframed.client.model;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.client.ReFramedClient;
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
	public UnbakedJsonRetexturedModel(Identifier parent) {
        super(parent);
	}
	
	@Nullable
	@Override
	public BakedModel bake(Baker baker, Function<SpriteIdentifier, Sprite> spriteLookup, ModelBakeSettings bake_settings, Identifier identifier) {
		Direction[] directions = Direction.values();
		
		Sprite[] sprites = new Sprite[directions.length];
		for(int i = 0; i < directions.length; i++) {
			SpriteIdentifier id = new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, ReFramed.id("reframed_special/" + directions[i].getName()));
			sprites[i] = Objects.requireNonNull(spriteLookup.apply(id), () -> "Couldn't find sprite " + id + " !");
		}

		return new RetexturingBakedModel(
			baker.bake(parent, bake_settings),
			ReFramedClient.HELPER.getCamoAppearanceManager(spriteLookup),
			theme_index,
			bake_settings,
			item_state
		) {
			protected Mesh convertModel(BlockState state) {
				Renderer r = ReFramedClient.HELPER.getFabricRenderer();
				MeshBuilder builder = r.meshBuilder();
				QuadEmitter emitter = builder.getEmitter();
				RenderMaterial mat = appearance_manager.getCachedMaterial(state, false);
				
				Random rand = Random.create(42);

				for(Direction cullFace : DIRECTIONS_AND_NULL) {
					for(BakedQuad quad : wrapped.getQuads(state, cullFace, rand)) {
						emitter.fromVanilla(quad, mat, cullFace);
						
						QuadUvBounds bounds = QuadUvBounds.read(emitter);
						for(int i = 0; i < sprites.length; i++) {
							if(bounds.displaysSprite(sprites[i])) {
								bounds.normalizeUv(emitter, sprites[i]);
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
