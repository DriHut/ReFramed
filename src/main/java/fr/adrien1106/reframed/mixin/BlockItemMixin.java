package fr.adrien1106.reframed.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import fr.adrien1106.reframed.block.ReFramedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.component.ComponentType;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static fr.adrien1106.reframed.block.ReFramedEntity.BLOCKSTATE_KEY;

@Mixin(BlockItem.class)
public class BlockItemMixin {

    @Redirect(
        method = "writeNbtToBlockEntity",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;getOrDefault(Lnet/minecraft/component/ComponentType;Ljava/lang/Object;)Ljava/lang/Object;"
        )
    )
    private static Object placeBlockWithOffHandCamo(ItemStack instance, ComponentType component_type, Object o, @Local(argsOnly = true) World world, @Local(argsOnly = true) BlockPos pos, @Local(argsOnly = true) PlayerEntity player) {
        NbtComponent component = (NbtComponent) instance.getOrDefault(component_type, o);
        if (!component.isEmpty() || player == null
            || player.getOffHandStack().isEmpty()
            || player.getMainHandStack().isEmpty()
            || !(player.getMainHandStack().getItem() instanceof BlockItem frame)
            || !(frame.getBlock() instanceof ReFramedBlock)
            || !(player.getOffHandStack().getItem() instanceof BlockItem block)
            || block.getBlock() instanceof BlockEntityProvider
            || (world.getBlockState(pos).contains(Properties.LAYERS) && world.getBlockState(pos).get(Properties.LAYERS) > 1)
            || !Block.isShapeFullCube(block.getBlock().getDefaultState().getCollisionShape(world, pos))
        ) return component;

        // Add the camo block state to the stack's component data
        NbtCompound new_comp = new NbtCompound();
        if (!player.isCreative()) player.getOffHandStack().decrement(1);
        new_comp.put(BLOCKSTATE_KEY + 1, NbtHelper.fromBlockState(block.getBlock().getDefaultState()));
        return NbtComponent.of(new_comp);
    }

}
