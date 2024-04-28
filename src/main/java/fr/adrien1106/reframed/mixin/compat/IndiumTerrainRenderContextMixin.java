package fr.adrien1106.reframed.mixin.compat;

import fr.adrien1106.reframed.client.model.MultiRetexturableModel;
import fr.adrien1106.reframed.client.model.RetexturingBakedModel;
import fr.adrien1106.reframed.client.util.RenderHelper;
import fr.adrien1106.reframed.util.mixin.IBlockRenderInfoMixin;
import fr.adrien1106.reframed.util.mixin.IMultipartBakedModelMixin;
import link.infra.indium.renderer.aocalc.AoCalculator;
import link.infra.indium.renderer.render.AbstractBlockRenderContext;
import link.infra.indium.renderer.render.BlockRenderInfo;
import link.infra.indium.renderer.render.TerrainRenderContext;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderContext;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.render.model.BakedModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;


@Mixin(TerrainRenderContext.class)
public abstract class IndiumTerrainRenderContextMixin extends AbstractBlockRenderContext {

    @Inject(
        method = "tessellateBlock",
        at = @At(
            value = "INVOKE",
            target = "Llink/infra/indium/renderer/aocalc/AoCalculator;clear()V"
        ), remap = false,
        cancellable = true)
    private void renderMultipleModels(BlockRenderContext ctx, CallbackInfo ci) {
        if (!(ctx.model() instanceof IMultipartBakedModelMixin wrapped)) return;
        List<BakedModel> models = wrapped.getModels(ctx.state());
        if (models.stream().noneMatch(bakedModel ->
            bakedModel instanceof MultiRetexturableModel
                || bakedModel instanceof RetexturingBakedModel
        )) return;

        models.forEach(model -> {
            if (model instanceof MultiRetexturableModel multi_model) {
                RenderHelper.computeInnerCull(ctx.state(), multi_model.models(), model.hashCode());
                multi_model.models().forEach(rexteruable_model ->
                    renderModel(ctx, rexteruable_model, aoCalc, blockInfo, this, model.hashCode())
                );
            } else if (model instanceof RetexturingBakedModel rexteruable_model)
                renderModel(ctx, rexteruable_model, aoCalc, blockInfo, this, model.hashCode());
            else model.emitBlockQuads(blockInfo.blockView, blockInfo.blockState, blockInfo.blockPos, blockInfo.randomSupplier, this);
        });
        ci.cancel();
    }

    @Unique
    private static void renderModel(BlockRenderContext ctx, RetexturingBakedModel model, AoCalculator aoCalc, BlockRenderInfo block_info, RenderContext context, int model_hash) {
        aoCalc.clear();
        ((IBlockRenderInfoMixin) block_info).prepareForBlock(
            ctx.state(), ctx.pos(), ctx.seed(),
            model.useAmbientOcclusion(block_info.blockView, ctx.pos()),
            model.getThemeIndex(), model_hash
        );
        model.emitBlockQuads(block_info.blockView, block_info.blockState, block_info.blockPos, block_info.randomSupplier, context);
    }
}
