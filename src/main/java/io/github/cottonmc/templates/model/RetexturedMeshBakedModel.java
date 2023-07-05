package io.github.cottonmc.templates.model;

import io.github.cottonmc.templates.TemplatesClient;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachedBlockView;
import net.minecraft.block.BlockState;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class RetexturedMeshBakedModel extends ForwardingBakedModel {
	public RetexturedMeshBakedModel(BakedModel baseModel, TemplateAppearanceManager tam, AffineTransformation aff, Mesh baseMesh) {
		this.wrapped = baseModel;
		this.tam = tam;
		this.baseMesh = MatrixTransformer.meshAroundCenter(aff, baseMesh);
		this.facePermutation = MatrixTransformer.facePermutation(aff);
	}
	
	private final TemplateAppearanceManager tam;
	private final Mesh baseMesh;
	private final Map<Direction, Direction> facePermutation;
	
	//TODO: Check that TemplateAppearance equals() behavior is what i want, and also that it's fast
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
			context.pushTransform(new RetexturingTransformer(ta, tint, facePermutation));
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
		return new RetexturingTransformer(appearance, 0xFFFFFF, facePermutation).applyTo(baseMesh);
	}
	
	public static record RetexturingTransformer(TemplateAppearance appearance, int color, Map<Direction, Direction> facePermutation) implements RenderContext.QuadTransform {
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
			quad.spriteBake(sprite, MutableQuadView.BAKE_NORMALIZED);
			
			return true;
		}
		
		//Pass a Mesh through a QuadTransform all at once, instead of at render time
		private Mesh applyTo(Mesh original) {
			MeshBuilder builder = TemplatesClient.getFabricRenderer().meshBuilder();
			QuadEmitter emitter = builder.getEmitter();
			
			original.forEach(quad -> {
				emitter.copyFrom(quad);
				if(transform(emitter)) emitter.emit();
			});
			
			return builder.build();
		}
	}
}
