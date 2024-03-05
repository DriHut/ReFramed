package fr.adrien1106.reframed.mixin.compat;

import com.llamalad7.mixinextras.sugar.Local;
import fr.adrien1106.reframed.util.BlockHelper;
import fr.adrien1106.reframed.util.ThemeableBlockEntity;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockOcclusionCache;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockOcclusionCache.class)
public class SodiumBlockOcclusionCacheMixin {

    @Inject(
        method = "shouldDrawSide",
        at = @At(
            value = "INVOKE_ASSIGN",
            target = "Lnet/minecraft/world/BlockView;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;",
            shift = At.Shift.AFTER
        ), cancellable = true)
    private void shouldDrawFrameNeighborSide(BlockState self_state, BlockView view, BlockPos self_pos, Direction face, CallbackInfoReturnable<Boolean> cir, @Local BlockPos.Mutable other_pos) {
        if (!(view.getBlockEntity(other_pos) instanceof ThemeableBlockEntity)) return;
        cir.setReturnValue(BlockHelper.shouldDrawSide(self_state, view, self_pos, face, other_pos, 0));
    }
}
