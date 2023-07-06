package io.github.cottonmc.templates.model;

import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachedBlockView;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.texture.Sprite;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class RetexturedMeshBakedModel extends ForwardingBakedModel {
	public RetexturedMeshBakedModel(BakedModel baseModel, TemplateAppearanceManager tam, ModelBakeSettings settings, Mesh baseMesh) {
		this.wrapped = baseModel;
		this.tam = tam;
		this.baseMesh = MeshTransformUtil.aroundCenter(baseMesh, settings);
		this.facePermutation = MeshTransformUtil.facePermutation(settings);
		this.uvLock = settings.isUvLocked();
	}
	
	private final TemplateAppearanceManager tam;
	private final Mesh baseMesh;
	private final Map<Direction, Direction> facePermutation;
	private final boolean uvLock;
	
	private final ConcurrentHashMap<TemplateAppearance, Mesh> meshCache = new ConcurrentHashMap<>();
	
	@Override
	public boolean isVanillaAdapter() {
		return false;
	}
	
	@Override
	public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
		BlockState theme = (((RenderAttachedBlockView) blockView).getBlockEntityRenderAttachment(pos) instanceof BlockState s) ? s : null;
		if(theme == null || theme.isAir()) {
			context.meshConsumer().accept(getUntintedMesh(tam.getDefaultAppearance()));
			return;
		}
		
		TemplateAppearance ta = tam.getAppearance(theme);
		
		BlockColorProvider prov = ColorProviderRegistry.BLOCK.get(theme.getBlock());
		int tint = prov == null ? 0xFFFFFFFF : (0xFF000000 | prov.getColor(theme, blockView, pos, 1));
		
		if(tint == 0xFFFFFFFF) {
			//Cache this mesh indefinitely.
			context.meshConsumer().accept(getUntintedMesh(ta));
		} else {
			//The specific tint might vary a lot; imagine grass color smoothly changing. Baking the tint into the cached mesh
			//is likely unnecessary and will fill the cache with a ton of single-use meshes with only slighly different colors.
			//We'd also have to percolate that tint color into the cache key, which is an allocation, blah blah blah.
			//Let's fall back to a quad transform. In practice this is still nice and quick.
			context.pushTransform(new RetexturingTransformer(ta, tint, facePermutation, uvLock));
			context.meshConsumer().accept(baseMesh);
			context.popTransform();
		}
	}
	
	@Override
	public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
		TemplateAppearance ta = tam.getDefaultAppearance();
		
		//cheeky: if the item has NBT data, pluck out the blockstate from it
		NbtCompound tag = BlockItem.getBlockEntityNbt(stack);
		if(tag != null && tag.contains("BlockState")) {
			BlockState state = NbtHelper.toBlockState(Registries.BLOCK.getReadOnlyWrapper(), tag.getCompound("BlockState"));
			if(state != null && !state.isAir()) ta = tam.getAppearance(state);
		}
		
		context.meshConsumer().accept(getUntintedMesh(ta));
	}
	
	@Override
	public Sprite getParticleSprite() {
		return tam.getDefaultAppearance().getParticleSprite();
	}
	
	protected Mesh getUntintedMesh(TemplateAppearance ta) {
		return meshCache.computeIfAbsent(ta, this::makeUntintedMesh);
	}
	
	protected Mesh makeUntintedMesh(TemplateAppearance appearance) {
		return MeshTransformUtil.pretransformMesh(baseMesh, new RetexturingTransformer(appearance, 0xFFFFFFFF, facePermutation, uvLock));
	}
	
	public static record RetexturingTransformer(TemplateAppearance appearance, int color, Map<Direction, Direction> facePermutation, boolean uvLock) implements RenderContext.QuadTransform {
		private static final Direction[] DIRECTIONS = Direction.values();
		
		@Override
		public boolean transform(MutableQuadView quad) {
			quad.material(appearance.getRenderMaterial());
			
			//The quad tag numbers were selected so this magic trick works:
			Direction dir = facePermutation.get(DIRECTIONS[quad.tag() - 1]);
			//TODO: this newly-simplified direction passing to hasColor is almost certainly incorrect
			// I think hasColor was kinda incorrect in the first place tho
			if(appearance.hasColor(dir)) quad.color(color, color, color, color);
			
			Sprite sprite = appearance.getSprite(dir);
			
			int flags = MutableQuadView.BAKE_NORMALIZED;
			flags |= appearance.getBakeFlags(dir);
			if(uvLock) flags |= MutableQuadView.BAKE_LOCK_UV;
			
			quad.spriteBake(sprite, flags);
			
			return true;
		}
	}
}
