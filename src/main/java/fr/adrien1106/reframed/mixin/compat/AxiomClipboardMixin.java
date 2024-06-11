package fr.adrien1106.reframed.mixin.compat;

import com.moulberry.axiom.clipboard.Clipboard;
import com.moulberry.axiom.clipboard.ClipboardObject;
import com.moulberry.axiom.utils.IntMatrix;
import fr.adrien1106.reframed.util.mixin.IAxiomChunkedBlockRegionMixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Clipboard.class)
public class AxiomClipboardMixin {

    @Inject(
        method = "setClipboard(Lcom/moulberry/axiom/clipboard/ClipboardObject;)I",
        at = @At(
            value = "TAIL"
        ),
        remap = false
    )
    private void onInit(ClipboardObject object, CallbackInfoReturnable<Integer> cir) {
        ((IAxiomChunkedBlockRegionMixin) object.blockRegion()).setTransform(new IntMatrix(), object.blockEntities());
    }
}
