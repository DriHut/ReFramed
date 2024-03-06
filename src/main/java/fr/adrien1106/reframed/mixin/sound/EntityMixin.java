package fr.adrien1106.reframed.mixin.sound;

import com.llamalad7.mixinextras.sugar.Local;
import fr.adrien1106.reframed.block.ReFramedBlock;
import fr.adrien1106.reframed.util.blocks.ThemeableBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow public abstract World getWorld();

    @Redirect(
        method = "playStepSound",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/BlockState;getSoundGroup()Lnet/minecraft/sound/BlockSoundGroup;"
        )
    )
    private BlockSoundGroup playStepCamoSound(BlockState state, @Local(argsOnly = true) BlockPos pos) {
        if (state.getBlock() instanceof ReFramedBlock frame_block
            && getWorld().getBlockEntity(pos) instanceof ThemeableBlockEntity frame_entity
        ) {
            BlockState camo_state = frame_entity.getTheme(frame_block.getTopThemeIndex(state));
            state = camo_state.getBlock() != Blocks.AIR ? camo_state : state;
        }
        return state.getSoundGroup();
    }
}
