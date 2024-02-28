package fr.adrien1106.reframed.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import fr.adrien1106.reframed.util.ThemeableBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class BlockMixin {

    @Redirect(method = "shouldDrawSide", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOpaque()Z"))
    private static boolean isNeighborCamoOpaque(BlockState state, @Local(argsOnly = true) BlockView world, @Local(ordinal = 1, argsOnly = true) BlockPos pos) {
        BlockEntity block_entity = world.getBlockEntity(pos);
        if (!(block_entity instanceof ThemeableBlockEntity frame_entity)) return state.isOpaque();
        return frame_entity.getFirstTheme().isOpaque();
    }

    @Redirect(method = "shouldDrawSide", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isSideInvisible(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/Direction;)Z"))
    private static boolean isCamoInvisible(BlockState state, BlockState other_state, Direction direction, @Local(argsOnly = true) BlockView world, @Local(ordinal = 0, argsOnly = true) BlockPos pos, @Local(ordinal = 1, argsOnly = true) BlockPos other_pos) {
        if (world.getBlockEntity(other_pos) instanceof ThemeableBlockEntity entity) other_state = entity.getFirstTheme();
        if (world.getBlockEntity(pos) instanceof ThemeableBlockEntity entity) state = entity.getFirstTheme();
        return state.isSideInvisible(other_state, direction);
    }

    @Inject(method = "shouldDrawSide", at = @At(value = "RETURN", ordinal = 0), cancellable = true)
    private static void shouldDrawGlassCamoSide(BlockState state, BlockView world, BlockPos pos, Direction side, BlockPos other_pos, CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 1) BlockState neighbor) {
        if (!(world.getBlockEntity(pos) instanceof ThemeableBlockEntity) && !(world.getBlockEntity(other_pos) instanceof ThemeableBlockEntity)) return;
        VoxelShape voxel_block = state.getCullingFace(world, pos, side);
        if (voxel_block.isEmpty()) {
            cir.setReturnValue(true);
            return;
        }
        VoxelShape voxel_neighbor = neighbor.getCullingFace(world, pos, side.getOpposite());
        cir.setReturnValue(VoxelShapes.matchesAnywhere(voxel_block, voxel_neighbor, BooleanBiFunction.ONLY_FIRST));
    }
}
