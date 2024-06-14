package fr.adrien1106.reframed.mixin.compat;

import fr.adrien1106.reframed.client.model.MultiRetexturableModel;
import fr.adrien1106.reframed.client.model.RetexturingBakedModel;
import fr.adrien1106.reframed.client.util.RenderHelper;
import fr.adrien1106.reframed.util.mixin.IBlockRenderInfoMixin;
import fr.adrien1106.reframed.util.mixin.IMultipartBakedModelMixin;
import link.infra.indium.renderer.aocalc.AoCalculator;
import link.infra.indium.renderer.render.AbstractBlockRenderContext;
import link.infra.indium.renderer.render.BlockRenderInfo;
import link.infra.indium.renderer.render.NonTerrainBlockRenderContext;
import link.infra.indium.renderer.render.SingleBlockLightDataCache;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(NonTerrainBlockRenderContext.class)
public abstract class IndiumNonTerrainBlockRenderContextMixin extends AbstractBlockRenderContext {

    @Shadow(remap = false) @Final private SingleBlockLightDataCache lightCache;

    @Shadow private VertexConsumer vertexConsumer;

    @Inject(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Llink/infra/indium/renderer/render/BlockRenderInfo;prepareForWorld(Lnet/minecraft/world/BlockRenderView;Z)V",
            shift = At.Shift.AFTER
        ),
        cancellable = true
    )
    private void renderMultipleModels(BlockRenderView blockView, BakedModel wrapper, BlockState state, BlockPos pos, MatrixStack matrixStack, VertexConsumer buffer, boolean cull, Random random, long seed, int overlay, CallbackInfo ci) {
        if (!(wrapper instanceof IMultipartBakedModelMixin wrapped)) return;
        List<BakedModel> models = wrapped.getModels(state);
        if (models.stream().noneMatch(bakedModel ->
            bakedModel instanceof MultiRetexturableModel
                || bakedModel instanceof RetexturingBakedModel
        )) return;

        models.forEach(model -> {
            if (model instanceof MultiRetexturableModel multi_model) {
                RenderHelper.computeInnerCull(state, multi_model.models(), model.hashCode());
                multi_model.models().forEach(rexteruable_model ->
                    renderModel(state, pos, seed, rexteruable_model, aoCalc, blockInfo, this, model.hashCode())
                );
            } else if (model instanceof RetexturingBakedModel rexteruable_model)
                renderModel(state, pos, seed, rexteruable_model, aoCalc, blockInfo, this, model.hashCode());
            else model.emitBlockQuads(blockInfo.blockView, blockInfo.blockState, blockInfo.blockPos, blockInfo.randomSupplier, this);
        });

        blockInfo.release();
        lightCache.release();
        vertexConsumer = null;
        ci.cancel();
    }

    @Unique
    private static void renderModel(BlockState state, BlockPos pos, long seed, RetexturingBakedModel model, AoCalculator aoCalc, BlockRenderInfo block_info, RenderContext context, int model_hash) {
        aoCalc.clear();
        ((IBlockRenderInfoMixin) block_info).prepareForBlock(
            state, pos, seed,
            model.useAmbientOcclusion(block_info.blockView, pos),
            model.getThemeIndex(), model_hash
        );
        model.emitBlockQuads(block_info.blockView, block_info.blockState, block_info.blockPos, block_info.randomSupplier, context);
    }

}
