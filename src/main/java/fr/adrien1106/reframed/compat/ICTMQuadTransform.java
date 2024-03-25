package fr.adrien1106.reframed.compat;

import me.pepperbell.continuity.client.model.QuadProcessors;
import me.pepperbell.continuity.impl.client.ProcessingContextImpl;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;

import java.util.function.Function;
import java.util.function.Supplier;

public interface ICTMQuadTransform extends RenderContext.QuadTransform {

    void invokePrepare(BlockRenderView view, BlockState state, BlockPos pos, Supplier<Random> random, boolean manual_culling, Function<Sprite, QuadProcessors.Slice> slice);

    ProcessingContextImpl getProcessingContext();

    void invokeReset();
}
