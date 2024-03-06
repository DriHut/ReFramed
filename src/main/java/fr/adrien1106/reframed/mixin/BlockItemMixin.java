package fr.adrien1106.reframed.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static fr.adrien1106.reframed.block.ReFramedEntity.BLOCKSTATE_KEY;

@Mixin(BlockItem.class)
public class BlockItemMixin {

    @Shadow @Final @Deprecated private Block block;

    @Inject(
        method = "writeNbtToBlockEntity",
        at = @At(
            value = "INVOKE_ASSIGN",
            target = "Lnet/minecraft/item/BlockItem;getBlockEntityNbt(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/nbt/NbtCompound;",
            shift = At.Shift.AFTER
        )
    )
    private static void placeBlockWithOffHandCamo(World world, PlayerEntity player, BlockPos pos, ItemStack stack, CallbackInfoReturnable<Boolean> cir, @Local LocalRef<NbtCompound> compound) {
        if (compound.get() != null
            || player.getOffHandStack().isEmpty()
            || !(player.getOffHandStack().getItem() instanceof BlockItem block)
            || block.getBlock() instanceof BlockEntityProvider
            || !Block.isShapeFullCube(block.getBlock().getDefaultState().getCollisionShape(world, pos))
        ) return;
        NbtCompound new_comp = new NbtCompound();
        player.getOffHandStack().decrement(1);
        new_comp.put(BLOCKSTATE_KEY + 1, NbtHelper.fromBlockState(block.getBlock().getDefaultState()));
        compound.set(new_comp);
    }

}
