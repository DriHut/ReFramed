package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.util.blocks.BlockHelper;
import fr.adrien1106.reframed.util.blocks.ThemeableBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.util.shape.VoxelShapes.empty;
import static net.minecraft.util.shape.VoxelShapes.fullCube;

public abstract class ReFramedDoubleBlock extends ReFramedBlock {
    public ReFramedDoubleBlock(Settings settings) {
        super(settings);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return ReFramed.REFRAMED_DOUBLE_BLOCK_ENTITY.instantiate(pos, state);
    }

    public int getHitShape(BlockState state, BlockHitResult hit) {
        return getHitShape(state, hit.getPos(), hit.getBlockPos(), hit.getSide());
    }

    public int getHitShape(BlockState state, Vec3d hit, BlockPos pos, Direction side) {
        VoxelShape first_shape = getShape(state, 1);
        VoxelShape second_shape = getShape(state, 2);

        // Determine if any of the two shape is covering the side entirely
        if (isFaceFullSquare(first_shape, side)) return 1;
        if (isFaceFullSquare(second_shape, side)) return 2;

        Vec3d rel = BlockHelper.getRelativePos(hit, pos);
        if (BlockHelper.cursorMatchesFace(first_shape, rel)) return 1;
        if (BlockHelper.cursorMatchesFace(second_shape, rel)) return 2;
        return 0;
    }

    @Override
    public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        return world.getBlockEntity(pos) instanceof ThemeableBlockEntity framed_entity
            && framed_entity.getThemes().stream().allMatch(theme -> theme.isTransparent(world, pos));
    }

    public VoxelShape getRenderOutline(BlockState state, BlockHitResult hit) {
        return getShape(state, getHitShape(state, hit));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView view, BlockPos pos, ShapeContext ctx) {
        return isGhost(view, pos) ? empty() : fullCube();
    }

    @Override
    public VoxelShape getCullingShape(BlockState state, BlockView view, BlockPos pos) {
        return getCollisionShape(state, view, pos, ShapeContext.absent());
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!canUse(world, pos, player)) return ActionResult.PASS;
        ActionResult result = BlockHelper.useUpgrade(state, world, pos, player, hand);
        if (result.isAccepted()) return result;
        return BlockHelper.useCamo(state, world, pos, player, hand, hit, getHitShape(state, hit));
    }
}
