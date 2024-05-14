package fr.adrien1106.reframed.mixin.logic;

import fr.adrien1106.reframed.block.ReFramedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public class BlockItemMixin {

    @Unique private final ThreadLocal<BlockState> old_state = new ThreadLocal<>();
    @Unique private final ThreadLocal<BlockEntity> old_entity = new ThreadLocal<>();

    @Redirect(
        method = "place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/Block;onPlaced(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;)V"
        )
    )
    private void placeMoreInfo(Block block, World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (!(block instanceof ReFramedBlock frame_block)) {
            block.onPlaced(world, pos, state, placer, itemStack);
            return;
        }
        frame_block.onPlaced(world, pos, state, placer, itemStack, old_state.get(), old_entity.get());
    }

    @Inject(
        method = "place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/BlockItem;place(Lnet/minecraft/item/ItemPlacementContext;Lnet/minecraft/block/BlockState;)Z",
            shift = At.Shift.BEFORE
        )
    )
    private void savePreviousInfo(ItemPlacementContext context, CallbackInfoReturnable<ActionResult> cir) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        old_state.set(world.getBlockState(pos));
        old_entity.set(world.getBlockEntity(pos));
    }

}
