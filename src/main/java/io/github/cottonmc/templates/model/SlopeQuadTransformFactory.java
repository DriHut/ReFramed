package io.github.cottonmc.templates.model;

import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachedBlockView;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.texture.Sprite;
import net.minecraft.item.ItemStack;
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
		Object renderAttach = ((RenderAttachedBlockView) blockView).getBlockEntityRenderAttachment(pos);
		BlockState template = (renderAttach instanceof BlockState s) ? s : Blocks.AIR.getDefaultState();
		Block block = template.getBlock();
		
		TemplateAppearance appearance;
		int globalTint = 0xFFFFFF;
		
		if(block == Blocks.AIR) {
			appearance = tam.getDefaultAppearance();
		} else {
			appearance = tam.getAppearance(template);
			
			BlockColorProvider tint = ColorProviderRegistry.BLOCK.get(block);
			if(tint != null) globalTint = 0xFF000000 | tint.getColor(template, blockView, pos, 1);
		}
		
		return new Transformer(appearance, globalTint);
	}
	
	@Override
	public @NotNull RenderContext.QuadTransform itemTransformer(ItemStack stack, Supplier<Random> randomSupplier) {
		return new Transformer(tam.getDefaultAppearance(), 0xFFFFFF);
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
