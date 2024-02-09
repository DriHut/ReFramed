package fr.adrien1106.reframedtemplates.model;

import fr.adrien1106.reframedtemplates.block.TemplateEntity;
import fr.adrien1106.reframedtemplates.mixin.MinecraftAccessor;
import fr.adrien1106.reframedtemplates.model.apperance.TemplateAppearance;
import fr.adrien1106.reframedtemplates.model.apperance.TemplateAppearanceManager;
import fr.adrien1106.reframedtemplates.model.apperance.WeightedComputedAppearance;
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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

public abstract class RetexturingBakedModel extends ForwardingBakedModel {
	public RetexturingBakedModel(BakedModel baseModel, TemplateAppearanceManager tam, ModelBakeSettings settings, BlockState itemModelState, boolean ao) {
		this.wrapped = baseModel; //field from the superclass; vanilla getQuads etc. will delegate through to this
		
		this.tam = tam;
		this.facePermutation = MeshTransformUtil.facePermutation(settings);
		this.uvlock = settings.isUvLocked();
		this.itemModelState = itemModelState;
		this.ao = ao;
	}
	
	protected final TemplateAppearanceManager tam;
	protected final Map<Direction, Direction> facePermutation; //immutable
	protected final boolean uvlock;
	protected final BlockState itemModelState;
	protected final boolean ao;
	
	protected record MeshCacheKey(BlockState state, TransformCacheKey transform) {}
	protected final ConcurrentMap<MeshCacheKey, Mesh> retextured_meshes = new ConcurrentHashMap<>(); //mutable, append-only cache
	protected record TransformCacheKey(TemplateAppearance appearance, int model_id) {}
	protected final ConcurrentMap<TransformCacheKey, RetexturingTransformer> retextured_transforms = new ConcurrentHashMap<>();
	
	protected static final Direction[] DIRECTIONS = Direction.values();
	protected static final Direction[] DIRECTIONS_AND_NULL = new Direction[DIRECTIONS.length + 1];
	static { System.arraycopy(DIRECTIONS, 0, DIRECTIONS_AND_NULL, 0, DIRECTIONS.length); }
	
	protected abstract Mesh getBaseMesh(BlockState state);
	
	@Override
	public boolean isVanillaAdapter() {
		return false;
	}
	
	@Override
	public Sprite getParticleSprite() {
		return tam.getDefaultAppearance().getSprite(Direction.UP, 0);
	}
	
	@Override
	public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
		BlockState theme = (blockView.getBlockEntityRenderData(pos) instanceof BlockState s) ? s : null;
		QuadEmitter quad_emitter = context.getEmitter();
		if(theme == null || theme.isAir()) {
			getUntintedRetexturedMesh(new MeshCacheKey(state, new TransformCacheKey(tam.getDefaultAppearance(), 0)), 0).outputTo(quad_emitter);
			return;
		}
		if(theme.getBlock() == Blocks.BARRIER) return;
		
		TemplateAppearance ta = tam.getTemplateAppearance(theme);
		long seed = theme.getRenderingSeed(pos);
		int model_id = 0;
		if (ta instanceof WeightedComputedAppearance wca) model_id = wca.getAppearanceIndex(seed);
		
		int tint = 0xFF000000 | MinecraftClient.getInstance().getBlockColors().getColor(theme, blockView, pos, 0);
		Mesh untintedMesh = getUntintedRetexturedMesh(new MeshCacheKey(state, new TransformCacheKey(ta, model_id)), seed);
		
		//The specific tint might vary a lot; imagine grass color smoothly changing. Trying to bake the tint into
		//the cached mesh will pollute it with a ton of single-use meshes with only slightly different colors.
		if(tint == 0xFFFFFFFF) {
			untintedMesh.outputTo(quad_emitter);
		} else {
			context.pushTransform(new TintingTransformer(ta, tint, seed));
			untintedMesh.outputTo(quad_emitter);
			context.popTransform();
		}
	}
	
	@Override
	public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
		//cheeky: if the item has NBT data, pluck out the blockstate from it & look up the item color provider
		//none of this is accessible unless you're in creative mode doing ctrl-pick btw
		TemplateAppearance nbtAppearance;
		int tint;
		BlockState theme = TemplateEntity.readStateFromItem(stack);
		if(!theme.isAir()) {
			nbtAppearance = tam.getTemplateAppearance(theme);
			tint = 0xFF000000 | ((MinecraftAccessor) MinecraftClient.getInstance()).templates$getItemColors().getColor(new ItemStack(theme.getBlock()), 0);
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
	
	protected class RetexturingTransformer implements RenderContext.QuadTransform {
		private final long seed;
		protected RetexturingTransformer(TemplateAppearance ta, long seed) {
			this.ta = ta;
			this.seed = seed;
		}
		
		protected final TemplateAppearance ta;
		
		@Override
		public boolean transform(MutableQuadView quad) {
			quad.material(ta.getRenderMaterial(ao));
			
			int tag = quad.tag();
			if(tag == 0) return true; //Pass the quad through unmodified.
			
			//The quad tag numbers were selected so this magic trick works:
			Direction direction = facePermutation.get(DIRECTIONS[quad.tag() - 1]);
			quad.spriteBake(ta.getSprite(direction, seed), MutableQuadView.BAKE_NORMALIZED | ta.getBakeFlags(direction, seed) | (uvlock ? MutableQuadView.BAKE_LOCK_UV : 0));
			return true;
		}
	}
	
	protected class TintingTransformer implements RenderContext.QuadTransform {
		private final long seed;
		protected TintingTransformer(TemplateAppearance ta, int tint, long seed) {
			this.ta = ta;
			this.tint = tint;
			this.seed = seed;
		}
		
		protected final TemplateAppearance ta;
		protected final int tint;
		
		@Override
		public boolean transform(MutableQuadView quad) {
			int tag = quad.tag();
			if(tag == 0) return true;
			
			Direction dir = facePermutation.get(DIRECTIONS[quad.tag() - 1]);
			if(ta.hasColor(dir, seed)) quad.color(tint, tint, tint, tint);
			
			return true;
		}
	}
}
