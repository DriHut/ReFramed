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
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

import static fr.adrien1106.reframed.util.blocks.BlockProperties.EDGE;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.EDGE_FACE;

public abstract class EdgeDoubleReFramedBlock extends WaterloggableReFramedDoubleBlock {

    public EdgeDoubleReFramedBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(EDGE, Edge.NORTH_DOWN).with(EDGE_FACE, 0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(EDGE, EDGE_FACE));
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        Edge edge = BlockHelper.getPlacementEdge(ctx);
        return super.getPlacementState(ctx)
            .with(EDGE, edge)
            .with(EDGE_FACE, edge.getDirectionIndex(ctx.getSide().getOpposite()));
    }

    @Override
    @SuppressWarnings("deprecation")
    public abstract VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context);

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        Edge edge = state.get(EDGE).rotate(rotation);
        Direction face = state.get(EDGE).getDirection(state.get(EDGE_FACE));
        return state.with(EDGE, edge).with(EDGE_FACE, edge.getDirectionIndex(rotation.rotate(face)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        Edge edge = state.get(EDGE).mirror(mirror);
        Direction face = state.get(EDGE).getDirection(state.get(EDGE_FACE));
        return state.with(EDGE, edge).with(EDGE_FACE, edge.getDirectionIndex(mirror.apply(face)));
    }

    @Override
    public abstract VoxelShape getShape(BlockState state, int i);

}
