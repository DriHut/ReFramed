package fr.adrien1106.reframed.client.model;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;

public interface DynamicBakedModel {
    BakedModel computeQuads(BlockRenderView level, BlockState state, BlockPos pos, int theme_index);
}
