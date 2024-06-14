package fr.adrien1106.reframed.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.enums.WallShape;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.stream.Collectors;

import static fr.adrien1106.reframed.block.ReFramedWallBlock.*;
import static net.minecraft.state.property.Properties.*;
import static net.minecraft.util.shape.VoxelShapes.empty;

public class ReFramedPillarsWallBlock extends WaterloggableReFramedDoubleBlock {

    public ReFramedPillarsWallBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState()
            .with(EAST_WALL_SHAPE, WallShape.NONE)
            .with(NORTH_WALL_SHAPE, WallShape.NONE)
            .with(WEST_WALL_SHAPE, WallShape.NONE)
            .with(SOUTH_WALL_SHAPE, WallShape.NONE)
        );
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(EAST_WALL_SHAPE, NORTH_WALL_SHAPE, SOUTH_WALL_SHAPE, WEST_WALL_SHAPE));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction dir, BlockState other_state, WorldAccess world, BlockPos pos, BlockPos moved) {
        BlockState new_state = super.getStateForNeighborUpdate(state, dir, other_state, world, pos, moved);
        if (dir == Direction.DOWN) return new_state;
        BlockState top_state = dir == Direction.UP? other_state: world.getBlockState(pos.up());
        boolean fs = top_state.isSideSolidFullSquare(world, pos.up(), Direction.DOWN);
        VoxelShape top_shape = fs ? null : top_state.getCollisionShape(world, pos.up()).getFace(Direction.DOWN);
        Map<Direction, BlockState> neighbors = Direction.Type.HORIZONTAL.stream()
            .collect(Collectors.toMap(d -> d, d -> {
                if (d == dir) return other_state;
                return world.getBlockState(pos.offset(d));
            }));
        return getWallState(new_state, top_state, neighbors, top_shape, fs, world, pos);
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState state = super.getPlacementState(ctx);
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();

        BlockState top_state = world.getBlockState(pos.up());
        boolean fs = top_state.isSideSolidFullSquare(world, pos.up(), Direction.DOWN);
        VoxelShape top_shape = fs ? null : top_state.getCollisionShape(world, pos.up()).getFace(Direction.DOWN);

        Map<Direction, BlockState> neighbors = Direction.Type.HORIZONTAL.stream()
            .collect(Collectors.toMap(d -> d, d -> world.getBlockState(pos.offset(d))));
        return getWallState(state, top_state, neighbors, top_shape, fs, world, pos);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState new_state, boolean moved) {
        super.onStateReplaced(state, world, pos, new_state, moved);

        if(!state.isOf(new_state.getBlock())) world.removeBlockEntity(pos);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView view, BlockPos pos, ShapeContext ctx) {
        if (isGhost(view, pos)) return empty();
        VoxelShape shape = WALL_VOXELS[9];
        for (Direction dir : Direction.Type.HORIZONTAL) {
            if (state.get(getWallShape(dir)) != WallShape.NONE)
                shape = VoxelShapes.union(shape, WALL_VOXELS[8 + dir.ordinal()]);
        }
        return shape;
    }

    @Override
    public VoxelShape getCullingShape(BlockState state, BlockView view, BlockPos pos) {
        return isGhost(view, pos) ? empty(): getOutlineShape(state, view, pos, ShapeContext.absent());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        VoxelShape shape = WALL_VOXELS[0];
        for (Direction dir: Direction.Type.HORIZONTAL) {
            WallShape wall_shape = state.get(getWallShape(dir));
            if (wall_shape != WallShape.NONE)
                shape = VoxelShapes.union(shape, WALL_VOXELS[1 + (wall_shape.ordinal()-1) * 4 + (dir.ordinal() - 2)]);
        }
        return shape;
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return Direction.Type.HORIZONTAL.stream().reduce(state, (s, dir) ->
            s.with(getWallShape(rotation.rotate(dir)), state.get(getWallShape(dir)))
        , (prev, next) -> next);
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return Direction.Type.HORIZONTAL.stream().reduce(state, (s, dir) ->
                s.with(getWallShape(mirror.apply(dir)), state.get(getWallShape(dir)))
            , (prev, next) -> next);
    }

    @Override
    public VoxelShape getShape(BlockState state, int i) {
        if (i == 1) return WALL_VOXELS[0];
        VoxelShape shape = VoxelShapes.empty();
        for (Direction dir: Direction.Type.HORIZONTAL) {
            WallShape wall_shape = state.get(getWallShape(dir));
            if (wall_shape != WallShape.NONE)
                shape = VoxelShapes.union(shape, WALL_VOXELS[1 + (wall_shape.ordinal()-1) * 4 + (dir.ordinal() - 2)]);
        }
        return shape;
    }
}
