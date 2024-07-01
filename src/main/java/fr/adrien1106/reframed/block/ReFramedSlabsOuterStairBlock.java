package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.util.blocks.Corner;
import fr.adrien1106.reframed.util.blocks.Edge;
import fr.adrien1106.reframed.util.blocks.StairShape;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

import static fr.adrien1106.reframed.block.ReFramedSlabBlock.getSlabShape;
import static fr.adrien1106.reframed.block.ReFramedSmallCubeBlock.getSmallCubeShape;
import static fr.adrien1106.reframed.block.ReFramedStairBlock.getStairShape;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.CORNER;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.CORNER_FACE;

public class ReFramedSlabsOuterStairBlock extends CornerDoubleReFramedBlock {

    public ReFramedSlabsOuterStairBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Corner corner = state.get(CORNER);
        Direction face = corner.getDirection(state.get(CORNER_FACE));
        Edge edge = corner.getEdgeWith(face);
        return getStairShape(
            edge,
            corner.getOtherDirection(edge).getDirection() == Direction.AxisDirection.POSITIVE
            ? edge.getDirectionIndex(face) == 1
                ? StairShape.FIRST_OUTER_LEFT
                : StairShape.SECOND_OUTER_LEFT
            : edge.getDirectionIndex(face) == 1
                ? StairShape.FIRST_OUTER_RIGHT
                : StairShape.SECOND_OUTER_RIGHT
        );
    }

    @Override
    public VoxelShape getShape(BlockState state, int i) {
        Corner corner = state.get(CORNER);
        Direction face = corner.getDirection(state.get(CORNER_FACE));
        return i == 2
            ? getSmallCubeShape(corner.change(face))
            : getSlabShape(face);
    }

}
