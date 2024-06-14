package fr.adrien1106.reframed.mixin.compat;

import fr.adrien1106.reframed.block.ReFramedEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.moulberry.axiom.render.ChunkRenderOverrider$MappedBlockAndTintGetter")
public class AxiomMappedBlockAndTintGetterMixin {

    @Shadow @Final private World level;

    @Inject(
        method = "getBlockEntity",
        at = @At(
            value = "RETURN"
        ),
        cancellable = true
    )
    private void onGetBlockEntity(BlockPos pos, CallbackInfoReturnable<BlockEntity> cir) {
        if (!(level.getBlockEntity(pos) instanceof ReFramedEntity frame_entity)) return;
        cir.setReturnValue(frame_entity);
    }

}
