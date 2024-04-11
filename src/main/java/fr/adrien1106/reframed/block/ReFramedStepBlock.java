package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.util.VoxelHelper;
import fr.adrien1106.reframed.util.blocks.BlockHelper;
import fr.adrien1106.reframed.util.blocks.Edge;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static fr.adrien1106.reframed.util.VoxelHelper.VoxelListBuilder;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.EDGE;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.STAIR_SHAPE;
import static net.minecraft.state.property.Properties.*;
import static net.minecraft.state.property.Properties.WATERLOGGED;

public class ReFramedStepBlock extends WaterloggableReFramedBlock {

    public static final VoxelShape[] STEP_VOXELS;

    public ReFramedStepBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(EDGE, Edge.NORTH_DOWN));
    }

    @Override
    public Object getModelCacheKey(BlockState state) {
        return state.get(EDGE);
    }

    @Override
    public int getModelStateCount() {
        return 12;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(EDGE));
    }

    @Override
    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        Edge edge = state.get(EDGE);
        return !(
            context.getPlayer().isSneaking()
            || !(context.getStack().getItem() instanceof BlockItem block_item)
            || (
                !(
                    block_item.getBlock() == ReFramed.STAIR
                    && ((ReFramedStairsCubeBlock) ReFramed.STAIRS_CUBE)
                        .matchesShape(
                            context.getHitPos(),
                            context.getBlockPos(),
                            ReFramed.STAIRS_CUBE.getDefaultState().with(EDGE, edge.opposite()),
                            1
                        )

                )
                && !(
                    block_item.getBlock() == this
                    && (
                        ((ReFramedStepsSlabBlock) ReFramed.STEPS_SLAB)
                            .matchesShape(
                                context.getHitPos(),
                                context.getBlockPos(),
                                ReFramed.STEPS_SLAB.getDefaultState()
                                    .with(FACING, edge.getFirstDirection())
                                    .with(AXIS, edge.getSecondDirection().getAxis()),
                                edge.getSecondDirection().getDirection() == Direction.AxisDirection.POSITIVE ? 1 : 2
                            )
                        || ((ReFramedStepsSlabBlock) ReFramed.STEPS_SLAB)
                            .matchesShape(
                                context.getHitPos(),
                                context.getBlockPos(),
                                ReFramed.STEPS_SLAB.getDefaultState()
                                    .with(FACING, edge.getSecondDirection())
                                    .with(AXIS, edge.getFirstDirection().getAxis()),
                                edge.getFirstDirection().getDirection() == Direction.AxisDirection.POSITIVE ? 1 : 2
                            )
                    )
                )
            )
        );
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos pos = ctx.getBlockPos();
        BlockState current_state = ctx.getWorld().getBlockState(pos);
        if (current_state.isOf(ReFramed.STAIR))
            return ReFramed.STAIRS_CUBE.getDefaultState()
                .with(EDGE, current_state.get(EDGE))
                .with(STAIR_SHAPE, current_state.get(STAIR_SHAPE));


        if (current_state.isOf(this)) {
            Vec3d hit = ctx.getHitPos();
            Edge edge = current_state.get(EDGE);
            Direction dir = edge.getFirstDirection();
            ReFramedStepsSlabBlock block = ((ReFramedStepsSlabBlock) ReFramed.STEPS_SLAB);
            BlockState state = block.getDefaultState()
                .with(FACING, dir)
                .with(AXIS, edge.getOtherDirection(dir).getAxis())
                .with(WATERLOGGED, current_state.get(WATERLOGGED));
            if (!block.matchesShape(
                hit, pos,
                state,
                edge.getOtherDirection(dir).getDirection() == Direction.AxisDirection.POSITIVE ? 1 : 2
            )) {
                dir = edge.getSecondDirection();
                state = state
                    .with(FACING, dir)
                    .with(AXIS, edge.getOtherDirection(dir).getAxis());
            }
            return state;
        }

        return super.getPlacementState(ctx).with(EDGE, BlockHelper.getPlacementEdge(ctx));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getStepShape(state.get(EDGE));
    }

    public static VoxelShape getStepShape(Edge edge) {
        return STEP_VOXELS[edge.getID()];
    }

    @Override
    public Map<Integer, Integer> getThemeMap(BlockState state, BlockState new_state) {
        if (new_state.isOf(ReFramed.STAIRS_CUBE)) return Map.of(1, 2);
        if (new_state.isOf(ReFramed.STEPS_SLAB))
            return Map.of(
                1,
                state.get(EDGE)
                    .getOtherDirection(new_state.get(FACING))
                    .getDirection() == Direction.AxisDirection.POSITIVE ? 2 : 1
            );
        return super.getThemeMap(state, new_state);
    }

    static {
        final VoxelShape STEP = createCuboidShape(0, 0, 0, 16, 8, 8);

        STEP_VOXELS = VoxelListBuilder.create(STEP, 12)
            .add(VoxelHelper::rotateCX)
            .add(VoxelHelper::rotateCX)
            .add(VoxelHelper::rotateCX)

            .add(STEP, VoxelHelper::rotateCY)
            .add(VoxelHelper::rotateZ)
            .add(VoxelHelper::rotateZ)
            .add(VoxelHelper::rotateZ)

            .add(STEP, VoxelHelper::rotateCZ)
            .add(VoxelHelper::rotateY)
            .add(VoxelHelper::rotateY)
            .add(VoxelHelper::rotateY)
            .build();
    }
}
