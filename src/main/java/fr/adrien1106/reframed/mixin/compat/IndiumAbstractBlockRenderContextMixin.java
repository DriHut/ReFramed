package fr.adrien1106.reframed.mixin.compat;

import com.llamalad7.mixinextras.sugar.Local;
import fr.adrien1106.reframed.client.util.RenderHelper;
import fr.adrien1106.reframed.util.mixin.IBlockRenderInfoMixin;
import link.infra.indium.renderer.mesh.MutableQuadViewImpl;
import link.infra.indium.renderer.render.AbstractBlockRenderContext;
import link.infra.indium.renderer.render.BlockRenderInfo;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractBlockRenderContext.class)
public abstract class IndiumAbstractBlockRenderContextMixin {

    @Shadow(remap = false) protected BlockRenderInfo blockInfo;

    @Shadow public abstract boolean isFaceCulled(@Nullable Direction face);

    @Redirect(method = "renderQuad", at = @At(value = "INVOKE", target = "Llink/infra/indium/renderer/render/AbstractBlockRenderContext;isFaceCulled(Lnet/minecraft/util/math/Direction;)Z"))
    private boolean shouldDrawInnerQuad(AbstractBlockRenderContext instance, Direction face, @Local(argsOnly = true) MutableQuadViewImpl quad) {
        if (face != null || quad.tag() == 0 || !(blockInfo instanceof IBlockRenderInfoMixin info) || info.getThemeIndex() == 0) return isFaceCulled(face);

        return !RenderHelper.shouldDrawInnerFace(blockInfo.blockState, blockInfo.blockView, blockInfo.blockPos, quad.tag() >>> 8, info.getThemeIndex(), info.getModelHash());
    }
}
