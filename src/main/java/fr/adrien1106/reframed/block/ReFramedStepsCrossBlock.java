package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.util.VoxelHelper;
import fr.adrien1106.reframed.util.blocks.BlockHelper;
import fr.adrien1106.reframed.util.blocks.Edge;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

import static fr.adrien1106.reframed.block.ReFramedStepBlock.getStepShape;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.EDGE;

public class ReFramedStepsCrossBlock extends WaterloggableReFramedDoubleBlock {

    public static VoxelShape[] STEP_CROSS_VOXELS;

    public ReFramedStepsCrossBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(EDGE, Edge.NORTH_DOWN));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(EDGE));
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        Edge edge = BlockHelper.getPlacementCorner(ctx).getEdge(ctx.getSide().getOpposite());
        return super.getPlacementState(ctx).with(EDGE, edge);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return STEP_CROSS_VOXELS[state.get(EDGE).ordinal()];
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(EDGE, state.get(EDGE).rotate(rotation));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.with(EDGE, state.get(EDGE).mirror(mirror));
    }

    @Override
    public VoxelShape getShape(BlockState state, int i) {
//        return getStepShape(i == 1 ? state.get(EDGE): state.get(EDGE).opposite());
        return getOutlineShape(state, null, null, null);
    }

    static {
        VoxelShape STEP_CROSS = VoxelShapes.combineAndSimplify(
            getStepShape(Edge.NORTH_DOWN),
            getStepShape(Edge.SOUTH_UP),
            BooleanBiFunction.OR
        );
        STEP_CROSS_VOXELS = VoxelHelper.VoxelListBuilder.create(STEP_CROSS, 12)
            .add(VoxelHelper::rotateX)
            .add(VoxelHelper::rotateX)
            .add(VoxelHelper::rotateX)
            .add(0, VoxelHelper::rotateCY)
            .add(VoxelHelper::rotateZ)
            .add(VoxelHelper::rotateZ)
            .add(VoxelHelper::rotateZ)
            .add(0, VoxelHelper::rotateCZ)
            .add(VoxelHelper::rotateY)
            .add(VoxelHelper::rotateY)
            .add(VoxelHelper::rotateY)
            .build();
    }
}
