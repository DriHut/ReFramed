package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.util.VoxelHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

import static net.minecraft.state.property.Properties.AXIS;

public class ReFramedPostBlock extends PillarReFramedBlock {

    public static final VoxelShape[] POST_VOXELS;

    public ReFramedPostBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getPillarShape(state.get(AXIS));
    }

    public static VoxelShape getPillarShape(Direction.Axis axis) {
        return POST_VOXELS[axis.ordinal()];
    }

    static {
        final VoxelShape POST = createCuboidShape(0, 6, 6, 16, 10, 10);
        POST_VOXELS = VoxelHelper.VoxelListBuilder.create(POST, 3)
            .add(VoxelHelper::rotateZ)
            .add(VoxelHelper::rotateX)
            .build();
    }
}
