package fr.adrien1106.reframed.mixin.compat;

import com.moulberry.axiom.clipboard.Placement;
import com.moulberry.axiom.render.regions.ChunkedBlockRegion;
import com.moulberry.axiom.utils.IntMatrix;
import com.moulberry.axiom.world_modification.CompressedBlockEntity;
import fr.adrien1106.reframed.util.mixin.IAxiomChunkedBlockRegionMixin;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Placement.class)
public class AxiomPlacementMixin {

    @Shadow(remap = false) private Long2ObjectMap<CompressedBlockEntity> blockEntities;

    @Inject(
        method = "replacePlacement(Lcom/moulberry/axiom/render/regions/ChunkedBlockRegion;Ljava/lang/String;)V",
        at = @At("HEAD"),
        remap = false
    )
    private void onReplacePlacement(ChunkedBlockRegion region, String description, CallbackInfo ci) {
        ((IAxiomChunkedBlockRegionMixin) region).setTransform(new IntMatrix(), blockEntities);
    }

    @Inject(
        method = "startPlacement(Lnet/minecraft/util/math/BlockPos;Lcom/moulberry/axiom/render/regions/ChunkedBlockRegion;Lit/unimi/dsi/fastutil/longs/Long2ObjectMap;Ljava/lang/String;)I",
        at = @At("HEAD")
    )
    private void onStartPlacement(BlockPos target, ChunkedBlockRegion region, Long2ObjectMap<CompressedBlockEntity> entities, String description, CallbackInfoReturnable<Integer> cir) {
        ((IAxiomChunkedBlockRegionMixin) region).setTransform(new IntMatrix(), entities);
    }
}
