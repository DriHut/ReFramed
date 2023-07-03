package io.github.cottonmc.templates.model;

import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachedBlockView;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.color.block.BlockColorProvider;
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
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

@SuppressWarnings("ClassCanBeRecord")
public class SlopeQuadTransformFactory implements TemplateQuadTransformFactory {
	public SlopeQuadTransformFactory(TemplateAppearanceManager tam) {
		this.tam = tam;
	}
	
	private final TemplateAppearanceManager tam;
	
	@Override
	public @NotNull RenderContext.QuadTransform blockTransformer(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier) {
		BlockState template = (((RenderAttachedBlockView) blockView).getBlockEntityRenderAttachment(pos) instanceof BlockState s) ? s : null;
		if(template == null || template.isAir()) return new Transformer(tam.getDefaultAppearance(), 0xFFFFFFFF);
		
		BlockColorProvider prov = ColorProviderRegistry.BLOCK.get(template.getBlock());
		int globalTint = prov != null ? prov.getColor(state, blockView, pos, 1) : 0xFFFFFFFF;
		return new Transformer(tam.getAppearance(template), globalTint);
	}
	
	@Override
	public @NotNull RenderContext.QuadTransform itemTransformer(ItemStack stack, Supplier<Random> randomSupplier) {
		//cheeky: if the item has NBT data, pluck out the blockstate from it
		NbtCompound tag = BlockItem.getBlockEntityNbt(stack);
		if(tag != null && tag.contains("BlockState")) {
			BlockState state = NbtHelper.toBlockState(Registries.BLOCK.getReadOnlyWrapper(), tag.getCompound("BlockState"));
			if(!state.isAir()) return new Transformer(tam.getAppearance(state), 0xFFFFFFFF);
		}
		
		return new Transformer(tam.getDefaultAppearance(), 0xFFFFFFFF);
	}
	
	public static record Transformer(TemplateAppearance appearance, int color) implements RenderContext.QuadTransform {
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
