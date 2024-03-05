package fr.adrien1106.reframed.mixin.sound;

import fr.adrien1106.reframed.block.ReFramedBlock;
import fr.adrien1106.reframed.util.ThemeableBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Redirect(
        method = "playBlockFallSound",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"
        )
    )
    private BlockState playCamoFallSound(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof ReFramedBlock frame_block
            && world.getBlockEntity(pos) instanceof ThemeableBlockEntity frame_entity
        ) {
            BlockState camo_state = frame_entity.getTheme(frame_block.getTopThemeIndex(state));
            state = camo_state.getBlock() != Blocks.AIR ? camo_state : state;
        }
        return state;
    }
}
