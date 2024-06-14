package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.util.VoxelHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

import static net.minecraft.state.property.Properties.AXIS;

public class ReFramedPillarBlock extends PillarReFramedBlock {

    public static final VoxelShape[] PILLAR_VOXELS;

    public ReFramedPillarBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getPillarShape(state.get(AXIS));
    }

    public static VoxelShape getPillarShape(Direction.Axis axis) {
        return PILLAR_VOXELS[axis.ordinal()];
    }

    static {
        final VoxelShape PILLAR = createCuboidShape(0, 4, 4, 16, 12, 12);
        PILLAR_VOXELS = VoxelHelper.VoxelListBuilder.create(PILLAR, 3)
            .add(VoxelHelper::rotateZ)
            .add(VoxelHelper::rotateX)
            .build();
    }
}
