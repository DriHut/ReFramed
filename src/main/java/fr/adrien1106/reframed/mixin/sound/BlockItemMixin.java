package fr.adrien1106.reframed.mixin.sound;

import com.llamalad7.mixinextras.sugar.Local;
import fr.adrien1106.reframed.block.ReFramedBlock;
import fr.adrien1106.reframed.util.blocks.ThemeableBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockItem.class)
public class BlockItemMixin {

    @Redirect(
        method = "place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/BlockState;getSoundGroup()Lnet/minecraft/sound/BlockSoundGroup;"
        )
    )
    public BlockSoundGroup getCamoPlaceSound(BlockState state, @Local World world, @Local BlockPos pos) {
        if (state.getBlock() instanceof ReFramedBlock frame_block
            && world.getBlockEntity(pos) instanceof ThemeableBlockEntity frame_entity
        ) {
            BlockState camo_state = frame_entity.getTheme(frame_block.getTopThemeIndex(state));
            state = camo_state.getBlock() != Blocks.AIR ? camo_state : state;
        }
        return state.getSoundGroup();
    }

    @Redirect(
        method = "place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/BlockItem;getPlaceSound(Lnet/minecraft/block/BlockState;)Lnet/minecraft/sound/SoundEvent;"
        )
    )
    private SoundEvent getCamoSoundEvent(BlockItem item, BlockState state, @Local BlockSoundGroup group) {
        // I don't know why it wasn't doing that by default
        return group.getPlaceSound();
    }
}
