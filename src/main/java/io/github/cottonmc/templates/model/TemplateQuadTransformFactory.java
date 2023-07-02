package io.github.cottonmc.templates.model;

import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Please keep thread-safety in mind - `getQuads`/`emitBlockQuads` can be called concurrently from multiple worker threads.
 */
public interface TemplateQuadTransformFactory {
	@NotNull RenderContext.QuadTransform blockTransformer(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier);
	@NotNull RenderContext.QuadTransform itemTransformer(ItemStack stack, Supplier<Random> randomSupplier);
}
