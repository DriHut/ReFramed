package fr.adrien1106.reframed.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import org.jetbrains.annotations.Nullable;

import static fr.adrien1106.reframed.block.ReFramedSlabBlock.*;
import static net.minecraft.state.property.Properties.AXIS;

public class ReFramedSlabsCubeBlock extends ReFramedDoubleBlock {

    public ReFramedSlabsCubeBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(AXIS, Direction.Axis.Y));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(AXIS));
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx).with(AXIS, ctx.getSide().getAxis());
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(AXIS, rotation.rotate(Direction.get(Direction.AxisDirection.POSITIVE, state.get(AXIS))).getAxis());
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.with(AXIS, mirror.apply(Direction.get(Direction.AxisDirection.POSITIVE, state.get(AXIS))).getAxis());
    }

    @Override
    public VoxelShape getShape(BlockState state, int i) {
        return switch (state.get(AXIS)) {
            case Y -> i == 2 ? UP    : DOWN;
            case Z -> i == 2 ? SOUTH : NORTH;
            case X -> i == 2 ? EAST  : WEST;
        };
    }

    @Override
    public int getTopThemeIndex(BlockState state) {
        return 2;
    }
}
