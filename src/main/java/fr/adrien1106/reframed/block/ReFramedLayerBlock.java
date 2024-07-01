package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.util.VoxelHelper;
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

import static fr.adrien1106.reframed.util.VoxelHelper.VoxelListBuilder;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.HALF_LAYERS;
import static net.minecraft.state.property.Properties.*;

public class ReFramedLayerBlock extends LayeredReFramedBlock {

    public static final VoxelShape[] LAYER_VOXELS;

    public ReFramedLayerBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(FACING, Direction.DOWN));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(FACING));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getLayerShape(state.get(FACING), state.get(LAYERS));
    }

    public static VoxelShape getLayerShape(Direction facing, int layers) {
        return LAYER_VOXELS[facing.getId() * 8 + layers - 1];
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState previous = ctx.getWorld().getBlockState(ctx.getBlockPos());
        BlockState state = super.getPlacementState(ctx);
        if (previous.isOf(this)) return state;

        if (previous.isOf(ReFramed.SLAB))
            return ReFramed.SLABS_LAYER.getDefaultState()
                .with(FACING, previous.get(FACING))
                .with(WATERLOGGED, previous.get(WATERLOGGED));

        if (previous.isOf(ReFramed.SLABS_LAYER))
            return previous.with(HALF_LAYERS, previous.get(HALF_LAYERS) + 1);

        return state.with(FACING, ctx.getSide().getOpposite());
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.with(FACING, mirror.apply(state.get(FACING)));
    }

    static {
        VoxelListBuilder builder = VoxelListBuilder.create(createCuboidShape(0, 0, 0, 16, 2, 16), 48)
            .add(createCuboidShape(0, 0, 0, 16, 4, 16))
            .add(createCuboidShape(0, 0, 0, 16, 6, 16))
            .add(createCuboidShape(0, 0, 0, 16, 8, 16))
            .add(createCuboidShape(0, 0, 0, 16, 10, 16))
            .add(createCuboidShape(0, 0, 0, 16, 12, 16))
            .add(createCuboidShape(0, 0, 0, 16, 14, 16))
            .add(createCuboidShape(0, 0, 0, 16, 16, 16));

        for (int i = 0; i < 8; i++) {
            builder.add(i, VoxelHelper::mirrorY);
        }
        for (int i = 0; i < 8; i++) {
            builder.add(i, VoxelHelper::rotateX);
        }
        for (int i = 0; i < 8; i++) {
            builder.add(i, VoxelHelper::rotateCX);
        }
        for (int i = 0; i < 8; i++) {
            builder.add(i, VoxelHelper::rotateCZ);
        }
        for (int i = 0; i < 8; i++) {
            builder.add(i, VoxelHelper::rotateZ);
        }

        LAYER_VOXELS = builder.build();
    }
}
