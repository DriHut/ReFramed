package fr.adrien1106.reframed.mixin.compat;

import com.moulberry.axiom.buildertools.CloneBuilderTool;
import com.moulberry.axiom.clipboard.SelectionBuffer;
import com.moulberry.axiom.render.regions.ChunkedBlockRegion;
import com.moulberry.axiom.utils.IntMatrix;
import com.moulberry.axiom.world_modification.CompressedBlockEntity;
import fr.adrien1106.reframed.util.mixin.IAxiomChunkedBlockRegionMixin;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CloneBuilderTool.class)
public class AxiomCloneBuilderToolMixin {

    @Shadow(remap = false) private ChunkedBlockRegion blockRegion;

    @Shadow(remap = false) @Final private IntMatrix transformMatrix;

    @Shadow(remap = false) private Long2ObjectMap<CompressedBlockEntity> blockEntities;

    @Inject(
        method = "lambda$initiateClone$0",
        at = @At(
            value = "INVOKE_ASSIGN",
            target = "Lcom/moulberry/axiom/clipboard/SelectionBuffer$CopyResult;blockEntities()Lit/unimi/dsi/fastutil/longs/Long2ObjectMap;",
            shift = At.Shift.AFTER
        ),
        remap = false
    )
    private void onInitiateClone(int copyId, int offsetX, int offsetY, int offsetZ, SelectionBuffer.CopyResult copyResult, CallbackInfo ci) {
        ((IAxiomChunkedBlockRegionMixin) blockRegion).setTransform(transformMatrix, blockEntities);
    }
}
