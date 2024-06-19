package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.util.blocks.Edge;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

import static fr.adrien1106.reframed.block.ReFramedHalfLayerBlock.getHalfLayerShape;
import static fr.adrien1106.reframed.block.ReFramedSlabBlock.getSlabShape;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.EDGE;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.EDGE_FACE;
import static net.minecraft.state.property.Properties.LAYERS;

public class ReFramedSlabsHalfLayerBlock extends HalfLayerDoubleReFramedBlock {

    private static final VoxelShape[] SLABS_HALF_LAYER_VOXELS = new VoxelShape[196];

    public ReFramedSlabsHalfLayerBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getSlabsHalfLayerShape(state.get(EDGE), state.get(EDGE_FACE), state.get(LAYERS));
    }

    public static VoxelShape getSlabsHalfLayerShape(Edge edge, int face, int layers) {
        int i = edge.ordinal() * 16 + face * 8 + layers - 1;
        VoxelShape shape = SLABS_HALF_LAYER_VOXELS[i];
        if (shape == null) {
            shape = VoxelShapes.combineAndSimplify(
                getShape(edge, edge.getDirection(face), layers, 1),
                getShape(edge, edge.getDirection(face), layers, 2),
                BooleanBiFunction.OR
            );
            SLABS_HALF_LAYER_VOXELS[i] = shape;
        }
        return shape;
    }

    @Override
    public VoxelShape getShape(BlockState state, int i) {
        Edge edge = state.get(EDGE);
        Direction face = edge.getDirection(state.get(EDGE_FACE));
        return getShape(edge, face, state.get(LAYERS), i);
    }

    private static VoxelShape getShape(Edge edge, Direction face, int layers, int i) {
        if (i == 2) {
            face = edge.getOtherDirection(face);
            edge = edge.getOpposite(face);
        }
        return i == 2
            ? getHalfLayerShape(edge, edge.getDirectionIndex(face), layers)
            : getSlabShape(face);
    }
}
