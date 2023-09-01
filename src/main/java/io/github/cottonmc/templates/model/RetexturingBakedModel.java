package io.github.cottonmc.templates.model;

import io.github.cottonmc.templates.block.TemplateEntity;
import io.github.cottonmc.templates.mixin.MinecraftAccessor;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachedBlockView;
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
	@Deprecated(forRemoval = true) //binary-compat from before there was an AO boolean
	public RetexturingBakedModel(BakedModel baseModel, TemplateAppearanceManager tam, ModelBakeSettings settings, BlockState itemModelState) {
		this(baseModel, tam, settings, itemModelState, true);
	}
	
	public RetexturingBakedModel(BakedModel baseModel, TemplateAppearanceManager tam, ModelBakeSettings settings, BlockState itemModelState, boolean ao) {
		this.wrapped = baseModel;
		
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
	
	protected record CacheKey(BlockState state, TemplateAppearance appearance) {}
	private final ConcurrentMap<CacheKey, Mesh> retexturedMeshes = new ConcurrentHashMap<>(); //mutable, append-only cache
	
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
		return tam.getDefaultAppearance().getParticleSprite();
	}
	
	@Override
	public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
		BlockState theme = (((RenderAttachedBlockView) blockView).getBlockEntityRenderAttachment(pos) instanceof BlockState s) ? s : null;
		if(theme == null || theme.isAir()) {
			context.meshConsumer().accept(getUntintedRetexturedMesh(new CacheKey(state, tam.getDefaultAppearance())));
			return;
		} else if(theme.getBlock() == Blocks.BARRIER) {
			//TODO i don't love putting this rare specialcase smack in the middle of the hot code path
			return;
		}
		
		TemplateAppearance ta = tam.getAppearance(theme);
		
		int tint = 0xFF000000 | MinecraftClient.getInstance().getBlockColors().getColor(theme, blockView, pos, 0);
		Mesh untintedMesh = getUntintedRetexturedMesh(new CacheKey(state, ta));
		
		//The specific tint might vary a lot; imagine grass color smoothly changing. Trying to bake the tint into
		//the cached mesh will pollute it with a ton of single-use meshes with only slighly different colors.
		if(tint == 0xFFFFFFFF) {
			context.meshConsumer().accept(untintedMesh);
		} else {
			context.pushTransform(new TintingTransformer(ta, tint));
			context.meshConsumer().accept(untintedMesh);
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
			nbtAppearance = tam.getAppearance(theme);
			tint = 0xFF000000 | ((MinecraftAccessor) MinecraftClient.getInstance()).templates$getItemColors().getColor(new ItemStack(theme.getBlock()), 0);
		} else {
			nbtAppearance = tam.getDefaultAppearance();
			tint = 0xFFFFFFFF;
		}
		
		Mesh untintedMesh = getUntintedRetexturedMesh(new CacheKey(itemModelState, nbtAppearance));
		
		if(tint == 0xFFFFFFFF) {
			context.meshConsumer().accept(untintedMesh);
		} else {
			context.pushTransform(new TintingTransformer(nbtAppearance, tint));
			context.meshConsumer().accept(untintedMesh);
			context.popTransform();
		}
	}
	
	protected Mesh getUntintedRetexturedMesh(CacheKey key) {
		return retexturedMeshes.computeIfAbsent(key, this::createUntintedRetexturedMesh);
	}
	
	protected Mesh createUntintedRetexturedMesh(CacheKey key) {
		return MeshTransformUtil.pretransformMesh(getBaseMesh(key.state), new RetexturingTransformer(key.appearance, 0xFFFFFFFF));
	}
	
	protected class RetexturingTransformer implements RenderContext.QuadTransform {
		//TODO: remove the "tint" parameter, it's been kicked to TintingTransformer
		protected RetexturingTransformer(TemplateAppearance ta, int tint) {
			this.ta = ta;
			this.tint = tint;
		}
		
		protected final TemplateAppearance ta;
		protected final int tint;
		
		@Override
		public boolean transform(MutableQuadView quad) {
			quad.material(ta.getRenderMaterial(ao));
			
			int tag = quad.tag();
			if(tag == 0) return true; //Pass the quad through unmodified.
			
			//The quad tag numbers were selected so this magic trick works:
			Direction dir = facePermutation.get(DIRECTIONS[quad.tag() - 1]);
			if(ta.hasColor(dir)) quad.color(tint, tint, tint, tint);
			
			quad.spriteBake(ta.getSprite(dir), MutableQuadView.BAKE_NORMALIZED | ta.getBakeFlags(dir) | (uvlock ? MutableQuadView.BAKE_LOCK_UV : 0));
			
			return true;
		}
	}
	
	protected class TintingTransformer implements RenderContext.QuadTransform {
		protected TintingTransformer(TemplateAppearance ta, int tint) {
			this.ta = ta;
			this.tint = tint;
		}
		
		protected final TemplateAppearance ta;
		protected final int tint;
		
		@Override
		public boolean transform(MutableQuadView quad) {
			int tag = quad.tag();
			if(tag == 0) return true;
			
			Direction dir = facePermutation.get(DIRECTIONS[quad.tag() - 1]);
			if(ta.hasColor(dir)) quad.color(tint, tint, tint, tint);
			
			return true;
		}
	}
}
