package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.util.VoxelHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PaneBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
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

import static net.minecraft.state.property.Properties.*;

public class ReFramedPaneBlock extends WaterloggableReFramedBlock {

    public static final VoxelShape[] PANE_VOXELS;

    public ReFramedPaneBlock(Settings settings) {
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

        for (Direction side: Direction.Type.HORIZONTAL) {
            BlockState neighbor = world.getBlockState(pos.offset(side));
            new_state = new_state.with(getPaneProperty(side), connectsTo(
                neighbor,
                neighbor.isSideSolidFullSquare(world, pos.offset(side), side.getOpposite())
            ));
        }
        return new_state;
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState state = super.getPlacementState(ctx);
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();

        for (Direction dir: Direction.Type.HORIZONTAL) {
            BlockState neighbor = world.getBlockState(pos.offset(dir));
            state = state.with(getPaneProperty(dir), connectsTo(
                neighbor,
                neighbor.isSideSolidFullSquare(world, pos.offset(dir), dir.getOpposite())
            ));
        }
        return state;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        super.onStateReplaced(state, world, pos, newState, moved);

        if(!state.isOf(newState.getBlock())) world.removeBlockEntity(pos);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        VoxelShape shape = PANE_VOXELS[0];
        for (Direction dir: Direction.Type.HORIZONTAL) {
            if (state.get(getPaneProperty(dir)))
                shape = VoxelShapes.union(shape, PANE_VOXELS[dir.ordinal() - 1]);
        }
        return shape;
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return Direction.Type.HORIZONTAL.stream().reduce(state, (s, dir) ->
            s.with(getPaneProperty(rotation.rotate(dir)), state.get(getPaneProperty(dir)))
        , (prev, next) -> next);
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return Direction.Type.HORIZONTAL.stream().reduce(state, (s, dir) ->
                s.with(getPaneProperty(mirror.apply(dir)), state.get(getPaneProperty(dir)))
            , (prev, next) -> next);
    }

    public static boolean connectsTo(BlockState state, boolean fs) {
        return !cannotConnect(state) && fs || state.getBlock() instanceof PaneBlock || state.isIn(BlockTags.WALLS);
    }

    public static BooleanProperty getPaneProperty(Direction dir) {
        return switch (dir) {
            case NORTH -> NORTH;
            case EAST -> EAST;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            default -> null;
        };
    }

    static {
        VoxelShape POST = createCuboidShape(7, 0, 7, 9, 16, 9);
        VoxelShape SIDE = createCuboidShape(7, 0, 0, 9, 16, 7);
        PANE_VOXELS = VoxelHelper.VoxelListBuilder.create(POST, 5)
            .add(SIDE)
            .add(VoxelHelper::mirrorZ)
            .add(VoxelHelper::rotateY)
            .add(VoxelHelper::mirrorX)
            .build();
    }
}
