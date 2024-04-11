package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.util.VoxelHelper;
import net.minecraft.block.*;
import net.minecraft.block.enums.WallShape;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

import static net.minecraft.state.property.Properties.*;

public class ReframedWallBlock extends WaterloggableReFramedBlock {

    public static final VoxelShape[] WALL_VOXELS;

    private record ModelCacheKey(boolean up, WallShape east, WallShape north, WallShape west, WallShape south) {}

    public ReframedWallBlock(Settings settings) {
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
    public Object getModelCacheKey(BlockState state) {
        return new ModelCacheKey(
            state.get(UP),
            state.get(EAST_WALL_SHAPE),
            state.get(NORTH_WALL_SHAPE),
            state.get(WEST_WALL_SHAPE),
            state.get(SOUTH_WALL_SHAPE)
        );
    }

    @Override
    public int getModelStateCount() {
        return 162;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(UP, EAST_WALL_SHAPE, NORTH_WALL_SHAPE, SOUTH_WALL_SHAPE, WEST_WALL_SHAPE));
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState state = super.getPlacementState(ctx);
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();
        BlockState top_state = world.getBlockState(pos.up());
        boolean fs = top_state.isSideSolidFullSquare(world, pos.up(), Direction.DOWN);
        VoxelShape top_shape = fs ? null : top_state.getCollisionShape(world, pos.up()).getFace(Direction.DOWN);
        for (Direction dir : Direction.Type.HORIZONTAL) {
            BlockState neighbor = world.getBlockState(pos.offset(dir));
            if (shouldConnectTo(neighbor, fs, dir.getOpposite())) {
                state.with(
                    getWallShape(dir),
                    fs || shouldUseTall(WALL_VOXELS[dir.ordinal() + 3], top_shape)
                        ? WallShape.TALL
                        : WallShape.LOW
                );
            }
        }
        return state.with(UP, shouldHavePost(state, top_state, top_shape));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        VoxelShape shape = state.get(UP) ? WALL_VOXELS[0]: VoxelShapes.empty();
        for (Direction dir : Direction.Type.HORIZONTAL) {
            WallShape wall_shape = state.get(getWallShape(dir));
            if (wall_shape != WallShape.NONE) {
//                System.out.println("wall_shape: " + wall_shape + " wall_shape.ordinal-1: " + (wall_shape.ordinal()-1) + " dir.ordinal() - 2: " + (dir.ordinal() - 2));
                shape = VoxelShapes.union(shape, WALL_VOXELS[1 + (wall_shape.ordinal()-1) * 4 + (dir.ordinal() - 2)]);
            }
        }
        return shape;
    }

    private static boolean shouldHavePost(BlockState state, BlockState top_state, VoxelShape top_shape) {
        // above has post
        if (top_state.contains(NORTH_WALL_SHAPE) && top_state.get(UP)) return true;

        if (Stream.of(Direction.SOUTH, Direction.EAST) // Opposites are different
            .anyMatch(dir -> state.get(getWallShape(dir)) != state.get(getWallShape(dir.getOpposite())))
        ) return true;

        // no sides
        if (Direction.Type.HORIZONTAL.stream().allMatch(dir -> state.get(getWallShape(dir)) == WallShape.NONE))
            return true;

        // tall Matching sides
        if (Stream.of(Direction.SOUTH, Direction.EAST)
            .anyMatch(dir ->
                   state.get(getWallShape(dir))               == WallShape.TALL
                && state.get(getWallShape(dir.getOpposite())) == WallShape.TALL
            )) return false;

        return top_state.isIn(BlockTags.WALL_POST_OVERRIDE) || top_shape == null || shouldUseTall(WALL_VOXELS[0], top_shape);
    }

    private static boolean shouldConnectTo(BlockState state, boolean faceFullSquare, Direction side) {
        Block block = state.getBlock();
        boolean bl = block instanceof FenceGateBlock && FenceGateBlock.canWallConnect(state, side);
        return state.isIn(BlockTags.WALLS) || !WallBlock.cannotConnect(state) && faceFullSquare || block instanceof PaneBlock || bl;
    }

    private static boolean shouldUseTall(VoxelShape self_shape, VoxelShape other_shape) {
        return !VoxelShapes.matchesAnywhere(
            self_shape,
            other_shape,
            BooleanBiFunction.ONLY_FIRST
        );
    }

    private static Property<WallShape> getWallShape(Direction dir) {
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
        VoxelShape LOW = createCuboidShape(5, 0, 0, 11, 14, 8);
        VoxelShape TALL = createCuboidShape(5, 0, 0, 11, 16, 8);
        WALL_VOXELS = VoxelHelper.VoxelListBuilder.create(POST, 9)
            .add(LOW)
            .add(VoxelHelper::mirrorZ)
            .add(VoxelHelper::rotateY)
            .add(VoxelHelper::mirrorX)
            .add(TALL)
            .add(VoxelHelper::mirrorZ)
            .add(VoxelHelper::rotateY)
            .add(VoxelHelper::mirrorX)
            .build();
    }
}
