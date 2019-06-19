package io.github.cottonmc.slopetest.model;

import java.util.Random;
import java.util.function.Supplier;

import net.fabricmc.fabric.api.renderer.v1.render.RenderContext.QuadTransform;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ExtendedBlockView;

public interface MeshTransformer extends QuadTransform {
    MeshTransformer prepare(ExtendedBlockView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier);
    
    MeshTransformer prepare(ItemStack stack, Supplier<Random> randomSupplier);
}
