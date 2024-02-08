package fr.adrien1106.reframedtemplates.model;

import fr.adrien1106.reframedtemplates.Templates;
import fr.adrien1106.reframedtemplates.api.TemplatesClientApi;
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
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

public class UnbakedJsonRetexturedModel implements UnbakedModel, TemplatesClientApi.TweakableUnbakedModel {
	public UnbakedJsonRetexturedModel(Identifier parent) {
		this.parent = parent;
	}
	
	protected final Identifier parent;
	protected BlockState itemModelState;
	protected boolean ao = true;
	
	/// user configuration
	
	@Override
	public UnbakedJsonRetexturedModel disableAo() {
		ao = false;
		return this;
	}
	
	@Override
	public TemplatesClientApi.TweakableUnbakedModel itemModelState(BlockState state) {
		this.itemModelState = state;
		return this;
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
	
	@Nullable
	@Override
	public BakedModel bake(Baker baker, Function<SpriteIdentifier, Sprite> spriteLookup, ModelBakeSettings modelBakeSettings, Identifier identifier) {
		Direction[] DIRECTIONS = RetexturingBakedModel.DIRECTIONS;
		
		Sprite[] specialSprites = new Sprite[DIRECTIONS.length];
		for(int i = 0; i < DIRECTIONS.length; i++) {
			SpriteIdentifier id = new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, Templates.id("templates_special/" + DIRECTIONS[i].getName()));
			specialSprites[i] = Objects.requireNonNull(spriteLookup.apply(id), () -> "Couldn't find sprite " + id + " !");
		}
		
		return new RetexturingBakedModel(
			baker.bake(parent, modelBakeSettings),
			TemplatesClientApi.getInstance().getOrCreateTemplateApperanceManager(spriteLookup),
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
				Renderer r = TemplatesClientApi.getInstance().getFabricRenderer();
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
	
	//TODO ABI: (2.2) use TemplatesClientApi.getInstance.json, and use the builder properties to set this field
	@Deprecated(forRemoval = true)
	public UnbakedJsonRetexturedModel(Identifier parent, BlockState itemModelState) {
		this(parent);
		itemModelState(itemModelState);
	}
}
