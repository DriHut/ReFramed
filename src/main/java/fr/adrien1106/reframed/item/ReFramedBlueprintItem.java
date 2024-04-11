package fr.adrien1106.reframed.item;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.block.ReFramedEntity;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ReFramedBlueprintItem extends Item {
    public ReFramedBlueprintItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockPos pos = context.getBlockPos();
        World world = context.getWorld();
        if (!(world.getBlockEntity(pos) instanceof ReFramedEntity frame_entity)
            || frame_entity.getThemes().stream().noneMatch(state -> state.getBlock() != Blocks.AIR)
        ) return ActionResult.PASS;

        context.getStack().decrement(1);
        ItemStack stack = ReFramed.BLUEPRINT_WRITTEN.getDefaultStack();
        frame_entity.setStackNbt(stack);
        context.getPlayer().giveItemStack(stack);
        world.playSound(context.getPlayer(), context.getPlayer().getBlockPos(), SoundEvents.ITEM_BOOK_PUT, SoundCategory.PLAYERS);

        return ActionResult.SUCCESS;
    }
}
