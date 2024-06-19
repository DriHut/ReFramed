package fr.adrien1106.reframed.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.apache.commons.lang3.function.TriFunction;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.state.property.Properties.*;
import static net.minecraft.state.property.Properties.SOUTH;

public abstract class ConnectingReFramedBlock extends WaterloggableReFramedBlock {

    public ConnectingReFramedBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState()
            .with(EAST, false)
            .with(NORTH, false)
            .with(WEST, false)
            .with(SOUTH, false)
        );
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(EAST, NORTH, SOUTH, WEST));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction dir, BlockState other_state, WorldAccess world, BlockPos pos, BlockPos moved) {
        BlockState new_state = super.getStateForNeighborUpdate(state, dir, other_state, world, pos, moved);
        if (dir == Direction.DOWN) return new_state;

        return placementState(new_state, world, pos, this::connectsTo);
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState state = super.getPlacementState(ctx);
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();

        return placementState(state, world, pos, this::connectsTo);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return Direction.Type.HORIZONTAL.stream().reduce(state, (s, dir) ->
                s.with(getConnectionProperty(rotation.rotate(dir)), state.get(getConnectionProperty(dir)))
            , (prev, next) -> next);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return Direction.Type.HORIZONTAL.stream().reduce(state, (s, dir) ->
                s.with(getConnectionProperty(mirror.apply(dir)), state.get(getConnectionProperty(dir)))
            , (prev, next) -> next);
    }

    protected abstract boolean connectsTo(BlockState state, boolean fs, Direction dir);

    @Override
    @SuppressWarnings("deprecation")
    public abstract VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context);

    public static BooleanProperty getConnectionProperty(Direction dir) {
        return switch (dir) {
            case NORTH -> NORTH;
            case EAST -> EAST;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            default -> null;
        };
    }

    public static BlockState placementState(BlockState state, BlockView world, BlockPos pos, TriFunction<BlockState, Boolean, Direction, Boolean> connectsTo) {
        for (Direction dir: Direction.Type.HORIZONTAL) {
            BlockState neighbor = world.getBlockState(pos.offset(dir));
            state = state.with(getConnectionProperty(dir), connectsTo.apply(
                neighbor,
                neighbor.isSideSolidFullSquare(world, pos.offset(dir), dir.getOpposite()),
                dir
            ));
        }
        return state;
    }
}
