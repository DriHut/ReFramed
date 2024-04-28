package fr.adrien1106.reframed.mixin.render;

import fr.adrien1106.reframed.client.model.MultiRetexturableModel;
import fr.adrien1106.reframed.client.model.RetexturingBakedModel;
import fr.adrien1106.reframed.client.util.RenderHelper;
import fr.adrien1106.reframed.util.mixin.IBlockRenderInfoMixin;
import fr.adrien1106.reframed.util.mixin.IMultipartBakedModelMixin;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.impl.client.indigo.renderer.aocalc.AoCalculator;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.AbstractBlockRenderContext;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.BlockRenderInfo;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.TerrainRenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(TerrainRenderContext.class)
public abstract class TerrainRenderContextMixin extends AbstractBlockRenderContext {

    @Inject(method = "tessellateBlock", at = @At(
            value = "INVOKE",
            target = "Lnet/fabricmc/fabric/impl/client/indigo/renderer/aocalc/AoCalculator;clear()V"
        ), remap = false,
        cancellable = true)
    private void renderMultipleModels(BlockState state, BlockPos pos, BakedModel wrapper, MatrixStack matrixStack, CallbackInfo ci) {
        if (!(wrapper instanceof IMultipartBakedModelMixin wrapped)) return;
        List<BakedModel> models = wrapped.getModels(state);
        if (models.stream().noneMatch(bakedModel ->
            bakedModel instanceof MultiRetexturableModel
            || bakedModel instanceof RetexturingBakedModel
        )) return;

        models.forEach(model -> {
            if (model instanceof MultiRetexturableModel multi_model) {
                RenderHelper.computeInnerCull(state, multi_model.models(), model.hashCode());
                multi_model.models().forEach(retexturable_model ->
                    renderModel(state, retexturable_model, pos, aoCalc, blockInfo, this, model.hashCode())
                );
            } else if (model instanceof RetexturingBakedModel retexturable_model)
                renderModel(state, retexturable_model, pos, aoCalc, blockInfo, this, model.hashCode());
            else model.emitBlockQuads(blockInfo.blockView, blockInfo.blockState, blockInfo.blockPos, blockInfo.randomSupplier, this);
        });
        ci.cancel();
    }

    @Unique
    private static void renderModel(BlockState state, RetexturingBakedModel model, BlockPos pos, AoCalculator aoCalc, BlockRenderInfo block_info, RenderContext context, int model_hash) {
        aoCalc.clear();
        ((IBlockRenderInfoMixin) block_info).prepareForBlock(
            state, pos,
            model.useAmbientOcclusion(block_info.blockView, pos),
            model.getThemeIndex(), model_hash
        );
        model.emitBlockQuads(block_info.blockView, block_info.blockState, block_info.blockPos, block_info.randomSupplier, context);
    }
}
