package fr.adrien1106.reframed.client.model;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;

public interface DynamicBakedModel {
    BakedModel computeQuads(@Nullable BlockRenderView level, BlockState origin_state, @Nullable BlockPos pos, int theme_index);
}
