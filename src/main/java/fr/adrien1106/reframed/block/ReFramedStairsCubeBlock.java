package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.util.blocks.BlockHelper;
import fr.adrien1106.reframed.util.blocks.Edge;
import fr.adrien1106.reframed.util.blocks.StairShape;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import static fr.adrien1106.reframed.block.ReFramedStairBlock.*;
import static fr.adrien1106.reframed.util.VoxelHelper.VoxelListBuilder;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.EDGE;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.STAIR_SHAPE;

public class ReFramedStairsCubeBlock extends ReFramedDoubleBlock {

    private static final VoxelShape[] STAIRS_CUBE_VOXELS = VoxelListBuilder.buildFrom(STAIR_VOXELS);
    private record ModelCacheKey(Edge edge, StairShape shape) {}

    public ReFramedStairsCubeBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(EDGE, Edge.NORTH_DOWN).with(STAIR_SHAPE, StairShape.STRAIGHT));
    }

    @Override
    public Object getModelCacheKey(BlockState state) {
        return new ModelCacheKey(state.get(EDGE), state.get(STAIR_SHAPE));
    }

    @Override
    public int getModelStateCount() {
        return 108; // Has 12 * 9 state combination and 52 models still reduces cache size
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(EDGE, STAIR_SHAPE));
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighbor_state, WorldAccess world, BlockPos pos, BlockPos moved) {
        return super.getStateForNeighborUpdate(state, direction, neighbor_state, world, pos, moved)
            .with(STAIR_SHAPE, BlockHelper.getStairsShape(state.get(EDGE), world, pos));
    }


    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Edge face = BlockHelper.getPlacementEdge(ctx);
        StairShape shape = BlockHelper.getStairsShape(face, ctx.getWorld(), ctx.getBlockPos());
        return super.getPlacementState(ctx).with(EDGE, face).with(STAIR_SHAPE, shape);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        super.onStateReplaced(state, world, pos, newState, moved);

        if(!state.isOf(newState.getBlock())) world.removeBlockEntity(pos);
    }

    @Override
    public VoxelShape getShape(BlockState state, int i) {
        Edge edge = state.get(EDGE);
        StairShape shape = state.get(STAIR_SHAPE);
        return i == 2 ? STAIRS_CUBE_VOXELS[edge.getID() * 9 + shape.getID()] : getStairShape(edge, shape);
    }
}
