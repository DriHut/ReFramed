package fr.adrien1106.reframed.item;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.block.ReFramedEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static fr.adrien1106.reframed.block.ReFramedEntity.BLOCKSTATE_KEY;

public class ReFramedBlueprintWrittenItem extends Item {
    public ReFramedBlueprintWrittenItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        if (!player.isSneaking() || !stack.hasNbt()) return super.use(world, player, hand);
        stack.decrement(1);
        player.giveItemStack(ReFramed.BLUEPRINT.getDefaultStack());
        world.playSound(player, player.getBlockPos(), SoundEvents.ITEM_BOOK_PUT, SoundCategory.PLAYERS);

        return TypedActionResult.success(stack);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockPos pos = context.getBlockPos();
        World world = context.getWorld();
        if (!(world.getBlockEntity(pos) instanceof ReFramedEntity frame_entity)
            || frame_entity.getThemes().stream().anyMatch(state -> state.getBlock() != Blocks.AIR)
            || !context.getStack().hasNbt()
        ) return ActionResult.PASS;

        NbtCompound tag = BlockItem.getBlockEntityNbt(context.getStack());
        if(tag == null) return ActionResult.FAIL;

        PlayerEntity player = context.getPlayer();
        if (!player.isCreative()) { // verify player has blocks and remove them
            PlayerInventory inventory = player.getInventory();
            List<ItemStack> stacks = getBlockStates(tag).values().stream()
                .map(AbstractBlock.AbstractBlockState::getBlock)
                .map(Block::asItem)
                .map(Item::getDefaultStack)
                .toList();
            if (stacks.stream().anyMatch(stack -> !inventory.contains(stack)))
                return ActionResult.FAIL;
            stacks.stream().map(inventory::getSlotWithStack).forEach(index -> inventory.removeStack(index, 1));
            player.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 0.5f, 0.5f);
        }
        frame_entity.readNbt(tag);
        world.playSound(player, player.getBlockPos(), SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.PLAYERS);

        return ActionResult.SUCCESS;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        NbtCompound tag = BlockItem.getBlockEntityNbt(stack);
        if(tag == null) return;

        Map<Integer, BlockState> states = getBlockStates(tag);
        states.forEach((index, state) -> tooltip.add(
            Text.literal("Theme " + index + ": ")
                .append(
                    Text.translatable(state.getBlock().getTranslationKey())
                        .formatted(Formatting.GRAY)
                )
        ));
        super.appendTooltip(stack, world, tooltip, context);
    }

    private static Map<Integer, BlockState> getBlockStates(NbtCompound tag) {
        return tag.getKeys().stream()
            .filter(key ->
                key.startsWith(BLOCKSTATE_KEY)
                && key.replace(BLOCKSTATE_KEY,"").chars().allMatch(Character::isDigit)
            )
            .collect(Collectors.toMap(
                key -> Integer.parseInt(key.substring(BLOCKSTATE_KEY.length())),
                key -> NbtHelper.toBlockState(
                    Registries.BLOCK.getReadOnlyWrapper(),
                    tag.getCompound(key)
                )
            ));
    }
}
