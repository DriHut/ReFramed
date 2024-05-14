package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.util.blocks.BlockHelper;
import fr.adrien1106.reframed.util.blocks.Corner;
import fr.adrien1106.reframed.util.blocks.Edge;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

import static fr.adrien1106.reframed.block.ReFramedSmallCubeBlock.SMALL_CUBE_VOXELS;
import static fr.adrien1106.reframed.block.ReFramedStepBlock.getStepShape;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.EDGE;
import static net.minecraft.util.shape.VoxelShapes.empty;

public class ReFramedSmallCubesStepBlock extends WaterloggableReFramedDoubleBlock {

    public ReFramedSmallCubesStepBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(EDGE, Edge.NORTH_DOWN));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(EDGE));
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx).with(EDGE, BlockHelper.getPlacementEdge(ctx));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView view, BlockPos pos, ShapeContext ctx) {
        return isGhost(view, pos) ? empty(): getStepShape(state.get(EDGE));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getStepShape(state.get(EDGE));
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(EDGE, state.get(EDGE).rotate(rotation));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.with(EDGE, state.get(EDGE).mirror(mirror));
    }

    @Override
    public VoxelShape getShape(BlockState state, int i) {
        Edge edge = state.get(EDGE);
        return SMALL_CUBE_VOXELS[Corner.getByDirections(
            edge.getFirstDirection(),
            edge.getSecondDirection(),
            i == 1
                ? edge.getRightDirection()
                : edge.getLeftDirection()
        ).getID()];
    }

    @Override
    public int getTopThemeIndex(BlockState state) {
        return 2;
    }
}
