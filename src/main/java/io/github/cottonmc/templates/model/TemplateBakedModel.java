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
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public final class TemplateBakedModel extends ForwardingBakedModel {
	public TemplateBakedModel(BakedModel baseModel, TemplateAppearanceManager tam, AffineTransformation aff, Mesh baseMesh) {
		this.wrapped = baseModel;
		
		this.tam = tam;
		this.affineTransformer = new AffineQuadTransformer(aff);
		this.baseMesh = baseMesh;
	}
	
	private final TemplateAppearanceManager tam;
	private final AffineQuadTransformer affineTransformer;
	private final Mesh baseMesh;
	
	@Override
	public boolean isVanillaAdapter() {
		return false;
	}
	
	@Override
	public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
		context.pushTransform(affineTransformer);
		context.pushTransform(retexturingBlockTransformer(blockView, state, pos, randomSupplier));
		context.meshConsumer().accept(baseMesh);
		context.popTransform();
		context.popTransform();
	}
	
	@Override
	public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
		context.pushTransform(affineTransformer);
		context.pushTransform(retexturingItemTransformer(stack, randomSupplier));
		context.meshConsumer().accept(baseMesh);
		context.popTransform();
		context.popTransform();
	}
	
	@Override
	public boolean isSideLit() {
		//Makes item models look less bad. TODO: possibly a weird spot to put this. corresponds to `gui_light: front` in the json
		return false;
	}
	
	public @NotNull RenderContext.QuadTransform retexturingBlockTransformer(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier) {
		BlockState template = (((RenderAttachedBlockView) blockView).getBlockEntityRenderAttachment(pos) instanceof BlockState s) ? s : null;
		if(template == null || template.isAir()) return new RetexturingTransformer(tam.getDefaultAppearance(), 0xFFFFFFFF);
		
		BlockColorProvider prov = ColorProviderRegistry.BLOCK.get(template.getBlock());
		int globalTint = prov != null ? prov.getColor(state, blockView, pos, 1) : 0xFFFFFFFF;
		return new RetexturingTransformer(tam.getAppearance(template), globalTint);
	}
	
	public @NotNull RenderContext.QuadTransform retexturingItemTransformer(ItemStack stack, Supplier<Random> randomSupplier) {
		//cheeky: if the item has NBT data, pluck out the blockstate from it
		NbtCompound tag = BlockItem.getBlockEntityNbt(stack);
		if(tag != null && tag.contains("BlockState")) {
			BlockState state = NbtHelper.toBlockState(Registries.BLOCK.getReadOnlyWrapper(), tag.getCompound("BlockState"));
			if(!state.isAir()) return new RetexturingTransformer(tam.getAppearance(state), 0xFFFFFFFF);
		}
		
		return new RetexturingTransformer(tam.getDefaultAppearance(), 0xFFFFFFFF);
	}
	
	public static record RetexturingTransformer(TemplateAppearance appearance, int color) implements RenderContext.QuadTransform {
		private static final Direction[] DIRECTIONS = Direction.values();
		
		@Override
		public boolean transform(MutableQuadView quad) {
			quad.material(appearance.getRenderMaterial());
			
			//The quad tag numbers were selected so this magic trick works:
			Direction dir = DIRECTIONS[quad.tag()];
			
			//TODO: this newly-simplified direction passing to hasColor is almost certainly incorrect
			// I think hasColor was kinda incorrect in the first place tho
			if(appearance.hasColor(dir)) quad.color(color, color, color, color);
			Sprite sprite = appearance.getSprite(dir);
			
			quad.spriteBake(sprite, MutableQuadView.BAKE_NORMALIZED);
			return true;
		}
	}
}
