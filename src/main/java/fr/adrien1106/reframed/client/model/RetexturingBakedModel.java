package fr.adrien1106.reframed.client.model;

import fr.adrien1106.reframed.block.ReFramedEntity;
import fr.adrien1106.reframed.client.model.apperance.SpriteProperties;
import fr.adrien1106.reframed.mixin.MinecraftAccessor;
import fr.adrien1106.reframed.client.model.apperance.CamoAppearance;
import fr.adrien1106.reframed.client.model.apperance.CamoAppearanceManager;
import fr.adrien1106.reframed.client.model.apperance.WeightedComputedAppearance;
import fr.adrien1106.reframed.util.ThemeableBlockEntity;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.texture.Sprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class RetexturingBakedModel extends ForwardingBakedModel {
	public RetexturingBakedModel(BakedModel baseModel, CamoAppearanceManager tam, Function<ThemeableBlockEntity, BlockState> state_getter, ModelBakeSettings settings, BlockState itemModelState, boolean ao) {
		this.wrapped = baseModel; //field from the superclass; vanilla getQuads etc. will delegate through to this
		
		this.tam = tam;
		this.state_getter = state_getter;
		this.uvlock = settings.isUvLocked();
		this.itemModelState = itemModelState;
		this.ao = ao;
	}

	protected final CamoAppearanceManager tam;
	protected final Function<ThemeableBlockEntity, BlockState> state_getter;
	protected final boolean uvlock;
	protected final BlockState itemModelState;
	protected final boolean ao;
	
	protected record MeshCacheKey(BlockState state, TransformCacheKey transform) {}
	protected final ConcurrentMap<MeshCacheKey, Mesh> retextured_meshes = new ConcurrentHashMap<>(); //mutable, append-only cache
	protected record TransformCacheKey(CamoAppearance appearance, int model_id) {}
	protected final ConcurrentMap<TransformCacheKey, RetexturingTransformer> retextured_transforms = new ConcurrentHashMap<>();
	
	protected static final Direction[] DIRECTIONS = Direction.values();
	protected static final Direction[] DIRECTIONS_AND_NULL = new Direction[DIRECTIONS.length + 1];
	static { System.arraycopy(DIRECTIONS, 0, DIRECTIONS_AND_NULL, 0, DIRECTIONS.length); }


	protected final ConcurrentMap<BlockState, Mesh> jsonToMesh = new ConcurrentHashMap<>();


	protected Mesh getBaseMesh(BlockState state) {
		//Convert models to re-texturable Meshes lazily, the first time we encounter each blockstate
		return jsonToMesh.computeIfAbsent(state, this::convertModel);
	}

	protected abstract Mesh convertModel(BlockState state);
	
	@Override
	public boolean isVanillaAdapter() {
		return false;
	}
	
	@Override
	public Sprite getParticleSprite() {
		return tam.getDefaultAppearance().getSprites(Direction.UP, 0).get(0).sprite();
	}
	
	@Override
	public void emitBlockQuads(BlockRenderView world, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
		BlockState theme = (world.getBlockEntity(pos) instanceof ThemeableBlockEntity s) ? state_getter.apply(s) : null;
		QuadEmitter quad_emitter = context.getEmitter();
		if(theme == null || theme.isAir()) {
			getUntintedRetexturedMesh(new MeshCacheKey(state, new TransformCacheKey(tam.getDefaultAppearance(), 0)), 0).outputTo(quad_emitter);
			return;
		}
		if(theme.getBlock() == Blocks.BARRIER) return;
		
		CamoAppearance camo = tam.getCamoAppearance(world, theme, pos);
		long seed = theme.getRenderingSeed(pos);
		int model_id = 0;
		if (camo instanceof WeightedComputedAppearance wca) model_id = wca.getAppearanceIndex(seed);
		
		int tint = 0xFF000000 | MinecraftClient.getInstance().getBlockColors().getColor(theme, world, pos, 0);
		Mesh untintedMesh = getUntintedRetexturedMesh(
			new MeshCacheKey(
				state,
				new TransformCacheKey(camo, model_id)
			),
			seed
		);
		
		//The specific tint might vary a lot; imagine grass color smoothly changing. Trying to bake the tint into
		//the cached mesh will pollute it with a ton of single-use meshes with only slightly different colors.
		if(tint == 0xFFFFFFFF) {
			untintedMesh.outputTo(quad_emitter);
		} else {
			context.pushTransform(new TintingTransformer(camo, tint, seed));
			untintedMesh.outputTo(quad_emitter);
			context.popTransform();
		}
	}
	
	@Override
	public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
		//cheeky: if the item has NBT data, pluck out the blockstate from it & look up the item color provider
		//none of this is accessible unless you're in creative mode doing ctrl-pick btw
		CamoAppearance nbtAppearance;
		int tint;
		BlockState theme = ReFramedEntity.readStateFromItem(stack); // TODO Different states for both models
		if(!theme.isAir()) {
			nbtAppearance = tam.getCamoAppearance(null, theme, null);
			tint = 0xFF000000 | ((MinecraftAccessor) MinecraftClient.getInstance()).getItemColors().getColor(new ItemStack(theme.getBlock()), 0);
		} else {
			nbtAppearance = tam.getDefaultAppearance();
			tint = 0xFFFFFFFF;
		}
		
		Mesh untintedMesh = getUntintedRetexturedMesh(new MeshCacheKey(itemModelState, new TransformCacheKey(nbtAppearance, 0)), 0);

		QuadEmitter quad_emitter = context.getEmitter();
		if(tint == 0xFFFFFFFF) {
			untintedMesh.outputTo(quad_emitter);
		} else {
			context.pushTransform(new TintingTransformer(nbtAppearance, tint, 0));
			untintedMesh.outputTo(quad_emitter);
			context.popTransform();
		}
	}
	
	protected Mesh getUntintedRetexturedMesh(MeshCacheKey key, long seed) {
		return retextured_meshes.computeIfAbsent(key, (k) -> createUntintedRetexturedMesh(k, seed));
	}
	
	protected Mesh createUntintedRetexturedMesh(MeshCacheKey key, long seed) {
		RetexturingTransformer transformer = retextured_transforms.computeIfAbsent(key.transform, (k) -> new RetexturingTransformer(k.appearance, seed));
		return MeshTransformUtil.pretransformMesh(getBaseMesh(key.state), transformer);
	}
	
	public class RetexturingTransformer {
		private final long seed;
		protected RetexturingTransformer(CamoAppearance ta, long seed) {
			this.ta = ta;
			this.seed = seed;
		}
		
		protected final CamoAppearance ta;

		public int transform(QuadEmitter quad, int i) {
			if(quad.tag() == 0) return 0; //Pass the quad through unmodified.

			Direction direction = quad.nominalFace();
			List<SpriteProperties> sprites = ta.getSprites(direction, seed);
			if (i == -1) i = sprites.size();

			SpriteProperties properties = sprites.get(sprites.size() - i);
			i--;
			QuadPosBounds bounds = properties.bounds();

			if (bounds == null) { // sprite applies anywhere e.g. default behaviour
				quad.material(ta.getRenderMaterial(ao));
				quad.spriteBake(
					properties.sprite(),
					MutableQuadView.BAKE_NORMALIZED
						| properties.flags()
						| (uvlock ? MutableQuadView.BAKE_LOCK_UV : 0)
				);
				quad.tag(i+1);
				quad.emit();
				return i;
			}

			// verify if sprite covers the current quad and apply the new size
			QuadPosBounds origin_bounds = QuadPosBounds.read(quad, false);
			if (!bounds.matches(origin_bounds)) return i;

			// apply new quad shape
			quad.material(ta.getRenderMaterial(ao));
			bounds.intersection(origin_bounds, direction.getAxis()).apply(quad, origin_bounds);
			quad.spriteBake( // TODO check if the flags are usefull because it seems to be braking the functioning of it
				properties.sprite(),
				MutableQuadView.BAKE_NORMALIZED
					| MutableQuadView.BAKE_LOCK_UV
			);
			quad.tag(i+1);
			quad.emit();
			return i;
		}
	}
	
	protected static class TintingTransformer implements RenderContext.QuadTransform {
		private final long seed;
		protected TintingTransformer(CamoAppearance ta, int tint, long seed) {
			this.ta = ta;
			this.tint = tint;
			this.seed = seed;
		}
		
		protected final CamoAppearance ta;
		protected final int tint;
		
		@Override
		public boolean transform(MutableQuadView quad) {
			if(quad.tag() == 0) return true;

			if(ta.hasColor(quad.nominalFace(), seed, quad.tag())) quad.color(tint, tint, tint, tint);
			
			return true;
		}
	}
}
