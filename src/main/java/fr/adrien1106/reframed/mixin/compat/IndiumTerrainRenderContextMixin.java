package fr.adrien1106.reframed.mixin.compat;

import fr.adrien1106.reframed.client.model.MultiRetexturableModel;
import fr.adrien1106.reframed.util.IBlockRenderInfoMixin;
import fr.adrien1106.reframed.util.IMultipartBakedModelMixin;
import link.infra.indium.renderer.render.AbstractBlockRenderContext;
import link.infra.indium.renderer.render.TerrainRenderContext;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderContext;
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

        List<BakedModel> models = retexturing_model.models();
        int i = 0;
        for (BakedModel bakedModel : models) {
            i++;
            aoCalc.clear();
            ((IBlockRenderInfoMixin) blockInfo).prepareForBlock(ctx.state(), ctx.pos(), ctx.seed(), bakedModel.useAmbientOcclusion(), i);
            bakedModel.emitBlockQuads(blockInfo.blockView, blockInfo.blockState, blockInfo.blockPos, blockInfo.randomSupplier, this);
        }
        ci.cancel();
    }
}
