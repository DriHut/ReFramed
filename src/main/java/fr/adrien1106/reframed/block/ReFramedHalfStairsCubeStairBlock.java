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

import static fr.adrien1106.reframed.block.ReFramedHalfStairBlock.getHalfStairShape;
import static fr.adrien1106.reframed.block.ReFramedSmallCubeBlock.getSmallCubeShape;
import static fr.adrien1106.reframed.block.ReFramedStairBlock.getStairShape;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.CORNER;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.CORNER_FACE;

public class ReFramedHalfStairsCubeStairBlock extends CornerDoubleReFramedBlock {

    public ReFramedHalfStairsCubeStairBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Corner corner = state.get(CORNER);
        Direction face = corner.getDirection(state.get(CORNER_FACE));
        Edge edge = corner.getEdge(face);
        return getStairShape(
            edge,
            face.getDirection() == Direction.AxisDirection.POSITIVE
            ? StairShape.OUTER_LEFT
            : StairShape.OUTER_RIGHT
        );
    }

    @Override
    public VoxelShape getShape(BlockState state, int i) {
        Corner corner = state.get(CORNER);
        Direction face = corner.getDirection(state.get(CORNER_FACE));
        if (i == 2) corner = corner.change(face);
        return i == 2
            ? getSmallCubeShape(corner)
            : getHalfStairShape(corner, state.get(CORNER_FACE));
    }

}
