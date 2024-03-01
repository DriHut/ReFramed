package fr.adrien1106.reframed.util;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public interface IBlockRenderInfoMixin {

    void prepareForBlock(BlockState state, BlockPos pos, boolean ao, int theme_index);
}
