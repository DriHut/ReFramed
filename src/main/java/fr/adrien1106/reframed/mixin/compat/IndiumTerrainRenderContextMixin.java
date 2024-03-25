package fr.adrien1106.reframed.mixin.compat;

import fr.adrien1106.reframed.client.model.MultiRetexturableModel;
import fr.adrien1106.reframed.client.util.RenderHelper;
import fr.adrien1106.reframed.util.mixin.IBlockRenderInfoMixin;
import fr.adrien1106.reframed.util.mixin.IMultipartBakedModelMixin;
import link.infra.indium.renderer.render.AbstractBlockRenderContext;
import link.infra.indium.renderer.render.TerrainRenderContext;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderContext;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.minecraft.client.render.model.BakedModel;
import org.spongepowered.asm.mixin.Mixin;
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
        if (!(ctx.model() instanceof IMultipartBakedModelMixin wrapped)
            || !(wrapped.getModel(ctx.state()) instanceof MultiRetexturableModel retexturing_model)) return;

        List<ForwardingBakedModel> models = retexturing_model.models();
        RenderHelper.computeInnerCull(ctx.state(), models);
        int i = 0;
        for (BakedModel model : models) {
            i++;
            aoCalc.clear();
            ((IBlockRenderInfoMixin) blockInfo).prepareForBlock(ctx.state(), ctx.pos(), ctx.seed(), model.useAmbientOcclusion(), i);
            model.emitBlockQuads(blockInfo.blockView, blockInfo.blockState, blockInfo.blockPos, blockInfo.randomSupplier, this);
        }
        ci.cancel();
    }
}
