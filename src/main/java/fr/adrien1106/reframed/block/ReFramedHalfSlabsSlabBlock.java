package fr.adrien1106.reframed.block;

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

import static fr.adrien1106.reframed.block.ReFramedHalfSlabBlock.getHalfSlabShape;
import static fr.adrien1106.reframed.block.ReFramedSlabBlock.getSlabShape;
import static net.minecraft.state.property.Properties.FACING;

public class ReFramedHalfSlabsSlabBlock extends WaterloggableReFramedDoubleBlock {

    public static VoxelShape[] HALF_SLAB_COMP_SHAPES;

    public ReFramedHalfSlabsSlabBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(FACING, Direction.DOWN));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(FACING));
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx)
            .with(FACING, ctx.getSide().getOpposite());
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getSlabShape(state.get(FACING));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state
            .with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.with(FACING, mirror.apply(state.get(FACING)));
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
