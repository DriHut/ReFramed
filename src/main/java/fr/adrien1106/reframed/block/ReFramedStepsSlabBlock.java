package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.util.blocks.BlockHelper;
import fr.adrien1106.reframed.util.blocks.Edge;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

import static fr.adrien1106.reframed.block.ReFramedSlabBlock.getSlabShape;
import static fr.adrien1106.reframed.block.ReFramedStepBlock.getStepShape;
import static net.minecraft.state.property.Properties.AXIS;
import static net.minecraft.state.property.Properties.FACING;
import static net.minecraft.util.shape.VoxelShapes.empty;

public class ReFramedStepsSlabBlock extends WaterloggableReFramedDoubleBlock {

    public ReFramedStepsSlabBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(FACING, Direction.DOWN).with(AXIS, Axis.X));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(FACING, AXIS));
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        Vec3d pos = BlockHelper.getHitPos(ctx.getHitPos(), ctx.getBlockPos());
        return super.getPlacementState(ctx)
            .with(FACING, ctx.getSide().getOpposite())
            .with(AXIS, BlockHelper.getHitAxis(pos, ctx.getSide()));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView view, BlockPos pos, ShapeContext ctx) {
        return isGhost(view, pos) ? empty() : getSlabShape(state.get(FACING));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getSlabShape(state.get(FACING));
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state
            .with(AXIS, rotation.rotate(Direction.get(Direction.AxisDirection.POSITIVE, state.get(AXIS))).getAxis())
            .with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        if (state.get(FACING).getAxis() != Axis.Y)
            return state.with(FACING, mirror.apply(state.get(FACING)));

        return state;
    }

    @Override
    public VoxelShape getShape(BlockState state, int i) {
        Axis axis = state.get(AXIS);
        return getStepShape(Edge.getByDirections(
            state.get(FACING),
            switch (axis) {
                case X -> i == 1 ? Direction.WEST : Direction.EAST;
                case Y -> i == 1 ? Direction.DOWN : Direction.UP;
                case Z -> i == 1 ? Direction.NORTH : Direction.SOUTH;
            }
        ));
    }

    @Override
    public int getTopThemeIndex(BlockState state) {
        return 2;
    }
}
