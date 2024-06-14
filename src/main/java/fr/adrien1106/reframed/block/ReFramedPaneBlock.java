package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.util.VoxelHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.PaneBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class ReFramedPaneBlock extends ConnectingReFramedBlock {

    public static final VoxelShape[] PANE_VOXELS;

    public ReFramedPaneBlock(Settings settings) {
        super(settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        VoxelShape shape = PANE_VOXELS[0];
        for (Direction dir: Direction.Type.HORIZONTAL) {
            if (state.get(getConnectionProperty(dir)))
                shape = VoxelShapes.union(shape, PANE_VOXELS[dir.ordinal() - 1]);
        }
        return shape;
    }

    @Override
    protected boolean connectsTo(BlockState state, boolean fs, Direction dir) {
        return !cannotConnect(state) && fs || state.getBlock() instanceof PaneBlock || state.isIn(BlockTags.WALLS);
    }

    static {
        VoxelShape POST = createCuboidShape(7, 0, 7, 9, 16, 9);
        VoxelShape SIDE = createCuboidShape(7, 0, 0, 9, 16, 7);
        PANE_VOXELS = VoxelHelper.VoxelListBuilder.create(POST, 5)
            .add(SIDE)
            .add(VoxelHelper::mirrorZ)
            .add(VoxelHelper::rotateY)
            .add(VoxelHelper::mirrorX)
            .build();
    }
}
