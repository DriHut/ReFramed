package fr.adrien1106.reframed.mixin.compat;

import com.llamalad7.mixinextras.sugar.Local;
import com.moulberry.axiom.render.regions.ChunkedBlockRegion;
import com.moulberry.axiom.scaling.Scale3x;
import fr.adrien1106.reframed.util.mixin.IAxiomChunkedBlockRegionMixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Scale3x.class)
public class AxiomScale3xMixin {

    @Inject(
        method = "scale3x",
        at = @At(
            value = "INVOKE_ASSIGN",
            target = "Lcom/moulberry/axiom/render/regions/ChunkedBlockRegion;<init>()V",
            shift = At.Shift.AFTER
        ),
        remap = false
    )
    private static void onInit(ChunkedBlockRegion in, boolean postProcessing, CallbackInfoReturnable<ChunkedBlockRegion> cir, @Local(ordinal = 1) ChunkedBlockRegion out) {
        IAxiomChunkedBlockRegionMixin iin = (IAxiomChunkedBlockRegionMixin) in;
        ((IAxiomChunkedBlockRegionMixin) out).setTransform(iin.getTransform(), iin.getBlockEntities());
    }

}
