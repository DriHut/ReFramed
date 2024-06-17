package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.util.VoxelHelper;
import net.minecraft.block.*;
import net.minecraft.block.enums.WallShape;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.minecraft.state.property.Properties.*;

public class ReFramedWallBlock extends WaterloggableReFramedBlock {

    public static final VoxelShape[] WALL_VOXELS;

    public ReFramedWallBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState()
            .with(UP, true)
            .with(EAST_WALL_SHAPE, WallShape.NONE)
            .with(NORTH_WALL_SHAPE, WallShape.NONE)
            .with(WEST_WALL_SHAPE, WallShape.NONE)
            .with(SOUTH_WALL_SHAPE, WallShape.NONE)
        );
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(UP, EAST_WALL_SHAPE, NORTH_WALL_SHAPE, SOUTH_WALL_SHAPE, WEST_WALL_SHAPE));
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
        new_state = getWallState(new_state, top_state, neighbors, top_shape, fs, world, pos);
        return new_state.with(UP, shouldHavePost(new_state, top_state, top_shape));
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
        state = getWallState(state, top_state, neighbors, top_shape, fs, world, pos);
        return state.with(UP, shouldHavePost(state, top_state, top_shape));
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState new_state, boolean moved) {
        super.onStateReplaced(state, world, pos, new_state, moved);

        if(!state.isOf(new_state.getBlock())) world.removeBlockEntity(pos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        VoxelShape shape = state.get(UP) ? WALL_VOXELS[0]: VoxelShapes.empty();
        for (Direction dir : Direction.Type.HORIZONTAL) {
            WallShape wall_shape = state.get(getWallShape(dir));
            if (wall_shape != WallShape.NONE)
                shape = VoxelShapes.union(shape, WALL_VOXELS[1 + (wall_shape.ordinal()-1) * 4 + (dir.ordinal() - 2)]);
        }
        return shape;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView view, BlockPos pos, ShapeContext ctx) {
        if (isGhost(view, pos)) return VoxelShapes.empty();
        VoxelShape shape = state.get(UP) ? WALL_VOXELS[9]: VoxelShapes.empty();
        for (Direction dir : Direction.Type.HORIZONTAL) {
            if (state.get(getWallShape(dir)) != WallShape.NONE)
                shape = VoxelShapes.union(shape, WALL_VOXELS[8 + dir.ordinal()]);
        }
        return shape;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return Direction.Type.HORIZONTAL.stream().reduce(state, (s, dir) ->
                s.with(getWallShape(rotation.rotate(dir)), state.get(getWallShape(dir)))
            , (prev, next) -> next);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return Direction.Type.HORIZONTAL.stream().reduce(state, (s, dir) ->
                s.with(getWallShape(mirror.apply(dir)), state.get(getWallShape(dir)))
            , (prev, next) -> next);
    }

    public static BlockState getWallState(BlockState state, BlockState top_state, Map<Direction, BlockState> neighbors, VoxelShape top_shape, boolean fs, WorldView world, BlockPos pos) {
        for (Direction dir : Direction.Type.HORIZONTAL) {
            BlockPos offset = pos.offset(dir);
            BlockState neighbor = neighbors.get(dir);
            boolean side_full = neighbor.isSideSolidFullSquare(world, offset, dir.getOpposite());
            Property<WallShape> wall_shape = getWallShape(dir);
            if (shouldConnectTo(neighbor, side_full, dir.getOpposite())) {
                state = state.with(
                    wall_shape,
                    fs
                        || (top_state.contains(wall_shape) && top_state.get(wall_shape) != WallShape.NONE)
                        || shouldUseTall(WALL_VOXELS[dir.ordinal() + 3], top_shape)
                        ? WallShape.TALL
                        : WallShape.LOW
                );
            } else state = state.with(wall_shape, WallShape.NONE);
        }
        return state;
    }

    public static boolean shouldHavePost(BlockState state, BlockState top_state, VoxelShape top_shape) {
        // above has post
        if ((top_state.contains(UP) && top_state.get(UP)) || (!top_state.contains(UP) && top_state.contains(NORTH_WALL_SHAPE))) return true;

        if (Stream.of(Direction.SOUTH, Direction.EAST) // Opposites are different
            .anyMatch(dir -> state.get(getWallShape(dir)) != state.get(getWallShape(dir.getOpposite())))
        ) return true;

        // no sides
        if (Direction.Type.HORIZONTAL.stream().allMatch(dir -> state.get(getWallShape(dir)) == WallShape.NONE))
            return true;

        // Matching sides
        if (Stream.of(Direction.SOUTH, Direction.EAST)
            .anyMatch(dir ->
                   state.get(getWallShape(dir)) == state.get(getWallShape(dir.getOpposite()))
            )) return false;

        return top_state.isIn(BlockTags.WALL_POST_OVERRIDE) || top_shape == null || shouldUseTall(WALL_VOXELS[0], top_shape);
    }

    public static boolean shouldConnectTo(BlockState state, boolean side_full, Direction side) {
        Block block = state.getBlock();
        boolean bl = block instanceof FenceGateBlock && FenceGateBlock.canWallConnect(state, side);
        return state.isIn(BlockTags.WALLS) || !WallBlock.cannotConnect(state) && side_full || block instanceof PaneBlock || bl;
    }

    public static boolean shouldUseTall(VoxelShape self_shape, VoxelShape other_shape) {
        return !VoxelShapes.matchesAnywhere(
            self_shape,
            other_shape,
            BooleanBiFunction.ONLY_FIRST
        );
    }

    public static Property<WallShape> getWallShape(Direction dir) {
        return switch (dir) {
            case EAST -> EAST_WALL_SHAPE;
            case NORTH -> NORTH_WALL_SHAPE;
            case WEST -> WEST_WALL_SHAPE;
            case SOUTH -> SOUTH_WALL_SHAPE;
            default -> null;
        };
    }

    static {
        VoxelShape POST = createCuboidShape(4, 0, 4, 12, 16, 12);
        VoxelShape POST_COLLISION = createCuboidShape(4, 0, 4, 12, 24, 12);
        VoxelShape LOW = createCuboidShape(5, 0, 0, 11, 14, 8);
        VoxelShape TALL = createCuboidShape(5, 0, 0, 11, 16, 8);
        VoxelShape SIDE_COLLISION = createCuboidShape(5, 0, 0, 11, 24, 8);
        WALL_VOXELS = VoxelHelper.VoxelListBuilder.create(POST, 14)
            .add(LOW)
            .add(VoxelHelper::mirrorZ)
            .add(VoxelHelper::rotateY)
            .add(VoxelHelper::mirrorX)
            .add(TALL)
            .add(VoxelHelper::mirrorZ)
            .add(VoxelHelper::rotateY)
            .add(VoxelHelper::mirrorX)
            .add(POST_COLLISION)
            .add(SIDE_COLLISION)
            .add(VoxelHelper::mirrorZ)
            .add(VoxelHelper::rotateY)
            .add(VoxelHelper::mirrorX)
            .build();
    }
}
