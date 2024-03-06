package fr.adrien1106.reframed.mixin.sound;

import com.llamalad7.mixinextras.sugar.Local;
import fr.adrien1106.reframed.block.ReFramedBlock;
import fr.adrien1106.reframed.util.blocks.ThemeableBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @Shadow @Nullable private ClientWorld world;

    @Redirect(
        method = "processWorldEvent",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/BlockState;getSoundGroup()Lnet/minecraft/sound/BlockSoundGroup;"
        )
    )
    private BlockSoundGroup getCamoBreakSound(BlockState state, @Local(argsOnly = true) BlockPos pos) {
        if (state.getBlock() instanceof ReFramedBlock frame_block
            && world.getBlockEntity(pos) instanceof ThemeableBlockEntity frame_entity
        ) {
            BlockState camo_state = frame_entity.getTheme(frame_block.getTopThemeIndex(state));
            state = camo_state.getBlock() != Blocks.AIR ? camo_state : state;
        }
        return state.getSoundGroup();
    }
}
