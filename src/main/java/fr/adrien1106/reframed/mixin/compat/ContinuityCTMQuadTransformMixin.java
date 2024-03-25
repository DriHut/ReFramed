package fr.adrien1106.reframed.mixin.compat;

import fr.adrien1106.reframed.compat.ICTMQuadTransform;
import fr.adrien1106.reframed.util.blocks.ThemeableBlockEntity;
import me.pepperbell.continuity.client.model.CullingCache;
import me.pepperbell.continuity.client.model.QuadProcessors;
import me.pepperbell.continuity.impl.client.ProcessingContextImpl;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadView;
import net.minecraft.block.BlockState;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Function;
import java.util.function.Supplier;

@Mixin(targets = "me.pepperbell.continuity.client.model.CTMBakedModel$CTMQuadTransform")
public abstract class ContinuityCTMQuadTransformMixin implements ICTMQuadTransform {
    @Shadow(remap = false) @Final protected ProcessingContextImpl processingContext;

    @Shadow public abstract void prepare(BlockRenderView view, BlockState state, BlockPos pos, Supplier<Random> random, boolean manual_culling, Function<Sprite, QuadProcessors.Slice> slice);
    @Shadow(remap = false) public abstract void reset();

    @Redirect(
        method = "transform",
        at = @At(
            value = "INVOKE",
            target = "Lme/pepperbell/continuity/client/model/CullingCache;shouldCull(Lnet/fabricmc/fabric/api/renderer/v1/mesh/QuadView;Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z"
        )
    )
    private boolean camo_replacement(CullingCache cache, QuadView quad, BlockRenderView view, BlockPos pos, BlockState state) {
        if (view.getBlockEntity(pos) instanceof ThemeableBlockEntity) return false;
        return cache.shouldCull(quad, view, pos, state);
    }

    // uses this because invoker did not want to work for some reason
    public void invokePrepare(BlockRenderView view, BlockState state, BlockPos pos, Supplier<Random> random, boolean manual_culling, Function<Sprite, QuadProcessors.Slice> slice) {
        prepare(view, state, pos, random, manual_culling, slice);
    }

    @Override
    public ProcessingContextImpl getProcessingContext() {
        return processingContext;
    }

    @Override
    public void invokeReset() {
        reset();
    }
}
