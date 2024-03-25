package fr.adrien1106.reframed.mixin.compat;

import com.llamalad7.mixinextras.sugar.Local;
import fr.adrien1106.reframed.util.blocks.ThemeableBlockEntity;
import me.pepperbell.continuity.client.processor.ConnectionPredicate;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ConnectionPredicate.class)
public interface ContinuityConnectionPredicateMixin {

    @Redirect(
        method = "shouldConnect(Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;Lnet/minecraft/client/texture/Sprite;)Z",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/BlockRenderView;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"
        )
    ) // TODO better connected textures
    private BlockState getBlockState(BlockRenderView view, BlockPos pos, @Local(argsOnly = true) BlockState state) {
        if (!(view.getBlockEntity(pos) instanceof ThemeableBlockEntity frame_entity)) return view.getBlockState(pos);
        return frame_entity.getThemes()
            .stream()
            .filter(theme -> theme.getBlock() == state.getBlock())
            .findFirst()
            .orElse(frame_entity.getTheme(0));
    }

}
