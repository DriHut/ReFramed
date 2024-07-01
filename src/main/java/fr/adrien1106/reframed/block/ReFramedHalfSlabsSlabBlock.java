package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.util.VoxelHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

import static fr.adrien1106.reframed.block.ReFramedHalfSlabBlock.getHalfSlabShape;
import static fr.adrien1106.reframed.block.ReFramedSlabBlock.getSlabShape;
import static net.minecraft.state.property.Properties.FACING;

public class ReFramedHalfSlabsSlabBlock extends FacingDoubleReFramedBlock {

    public static VoxelShape[] HALF_SLAB_COMP_SHAPES;

    public ReFramedHalfSlabsSlabBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getSlabShape(state.get(FACING));
    }

    @Override
    public VoxelShape getShape(BlockState state, int i) {
        Direction face = state.get(FACING);
        return i == 2
            ? HALF_SLAB_COMP_SHAPES[face.getId()]
            : getHalfSlabShape(face);
    }

    static {
        HALF_SLAB_COMP_SHAPES = VoxelHelper.VoxelListBuilder.create(createCuboidShape(0, 4, 0, 16, 8, 16),6)
            .add(createCuboidShape(0, 8, 0, 16, 12, 16))
            .add(createCuboidShape(0, 0, 4, 16, 16, 8))
            .add(createCuboidShape(0, 0, 8, 16, 16, 12))
            .add(createCuboidShape(4, 0, 0, 8, 16, 16))
            .add(createCuboidShape(8, 0, 0, 12, 16, 16))
            .build();
    }
}
