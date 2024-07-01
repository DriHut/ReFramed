package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.util.blocks.BlockHelper;
import fr.adrien1106.reframed.util.blocks.Corner;
import fr.adrien1106.reframed.util.blocks.Edge;
import fr.adrien1106.reframed.util.blocks.StairShape;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

import static fr.adrien1106.reframed.block.ReFramedHalfStairBlock.getHalfStairShape;
import static fr.adrien1106.reframed.block.ReFramedStairBlock.getStairShape;
import static fr.adrien1106.reframed.block.ReFramedStepBlock.getStepShape;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.*;

public class ReFramedHalfStairsStepStairBlock extends CornerDoubleReFramedBlock {

    public ReFramedHalfStairsStepStairBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(CORNER_FEATURE, 0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(CORNER_FEATURE));
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState state = super.getPlacementState(ctx);
        Corner corner = state.get(CORNER);
        int face_index = state.get(CORNER_FACE);
        Direction face = corner.getDirection(face_index);
        face = BlockHelper.getPlacementEdge(ctx).getOtherDirection(face);
        int feature_index = corner.getDirectionIndex(face);
        return state.with(CORNER_FEATURE, feature_index > face_index ? feature_index - 1 : feature_index);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Corner corner = state.get(CORNER);
        int feature_index = state.get(CORNER_FEATURE), face_index = state.get(CORNER_FACE);
        Direction feature_face = corner.getDirection(feature_index >= face_index ? feature_index + 1 : feature_index);
        Direction face = corner.getDirection(face_index);
        Edge edge = Edge.getByDirections(feature_face, face);
        return getStairShape(
            edge,
            corner.getOtherDirection(edge).getDirection() == Direction.AxisDirection.POSITIVE
            ? edge.getDirectionIndex(face) == 0
                ? StairShape.FIRST_OUTER_LEFT
                : StairShape.SECOND_OUTER_LEFT
            : edge.getDirectionIndex(face) == 0
                ? StairShape.FIRST_OUTER_RIGHT
                : StairShape.SECOND_OUTER_RIGHT
        );
    }

    @Override
    public VoxelShape getShape(BlockState state, int i) {
        Corner corner = state.get(CORNER);
        int feature_index = state.get(CORNER_FEATURE), face_index = state.get(CORNER_FACE);
        Direction feature_face = corner.getDirection(feature_index >= face_index ? feature_index + 1 : feature_index);
        Direction face = corner.getDirection(face_index);
        return i == 2
            ? getStepShape(Edge.getByDirections(face.getOpposite(), feature_face))
            : getHalfStairShape(corner, face_index);
    }

}
