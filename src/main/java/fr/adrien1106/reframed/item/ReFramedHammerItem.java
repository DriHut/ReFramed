package fr.adrien1106.reframed.item;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.block.ReFramedDoubleBlock;
import fr.adrien1106.reframed.util.blocks.ThemeableBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ReFramedHammerItem extends Item {
    public ReFramedHammerItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        if (!(world.getBlockEntity(pos) instanceof ThemeableBlockEntity frame_entity)) return ActionResult.PASS;
        BlockState state = world.getBlockState(pos);
        PlayerEntity player = context.getPlayer();
        int theme_index = state.getBlock() instanceof ReFramedDoubleBlock b
            ? b.getHitShape(
            state,
            context.getHitPos(),
            context.getBlockPos(),
            context.getSide()
        )
            : 1;

        if (frame_entity.getTheme(theme_index).getBlock() == Blocks.AIR) return ActionResult.PASS;

        if (!player.isCreative()) {
            player.giveItemStack(new ItemStack(frame_entity.getTheme(theme_index).getBlock()));
            world.playSound(player, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 1f, 1.1f);
        }
        frame_entity.setTheme(Blocks.AIR.getDefaultState(), theme_index);
        ReFramed.chunkRerenderProxy.accept(world, pos);
        return ActionResult.SUCCESS;
    }
}
