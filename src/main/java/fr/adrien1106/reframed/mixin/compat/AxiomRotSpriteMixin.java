package fr.adrien1106.reframed.mixin.compat;

import com.moulberry.axiom.render.regions.ChunkedBlockRegion;
import com.moulberry.axiom.scaling.RotSprite;
import fr.adrien1106.reframed.util.mixin.IAxiomChunkedBlockRegionMixin;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RotSprite.class)
public class AxiomRotSpriteMixin {

    @Inject(
        method = "rotateCachedWithOutput",
        at = @At(
            value = "HEAD"
        ),
        remap = false
    )
    private static void onRotateCachedWithOutput(ChunkedBlockRegion in, Matrix4f matrix4f, ChunkedBlockRegion out, int x, int y, int z, CallbackInfoReturnable<ChunkedBlockRegion> cir) {
        IAxiomChunkedBlockRegionMixin iin = (IAxiomChunkedBlockRegionMixin) in;
        ((IAxiomChunkedBlockRegionMixin) out).setTransform(iin.getTransform(), iin.getBlockEntities());
    }

}
