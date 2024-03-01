package fr.adrien1106.reframed.mixin.render;

import fr.adrien1106.reframed.client.model.MultiRetexturableModel;
import fr.adrien1106.reframed.util.IBlockRenderInfoMixin;
import fr.adrien1106.reframed.util.IMultipartBakedModelMixin;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.AbstractBlockRenderContext;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.TerrainRenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(TerrainRenderContext.class)
public abstract class TerrainRenderContextMixin extends AbstractBlockRenderContext {

    @Inject(method = "tessellateBlock", at = @At(
            value = "INVOKE",
            target = "Lnet/fabricmc/fabric/impl/client/indigo/renderer/aocalc/AoCalculator;clear()V",
            shift = At.Shift.AFTER
        ), cancellable = true)
    private void renderMultipleModels(BlockState state, BlockPos pos, BakedModel wrapper, MatrixStack matrixStack, CallbackInfo ci) {
        if (!(wrapper instanceof IMultipartBakedModelMixin wrapped)
            || !(wrapped.getModel(state) instanceof MultiRetexturableModel retexturing_model)) return;

        List<BakedModel> models = retexturing_model.models();
        int i = 0;
        for (BakedModel bakedModel : models) {
            i++;
            aoCalc.clear();
            ((IBlockRenderInfoMixin) blockInfo).prepareForBlock(state, pos, bakedModel.useAmbientOcclusion(), i);
            bakedModel.emitBlockQuads(blockInfo.blockView, blockInfo.blockState, blockInfo.blockPos, blockInfo.randomSupplier, this);
        }
        ci.cancel();
    }
}
