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
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static fr.adrien1106.reframed.util.VoxelHelper.VoxelListBuilder;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.*;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.STAIR_SHAPE;
import static net.minecraft.state.property.Properties.*;

public class ReFramedStepBlock extends WaterloggableReFramedBlock {

    public static final VoxelShape[] STEP_VOXELS;

    public ReFramedStepBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(EDGE, Edge.NORTH_DOWN));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(EDGE));
    }

    @Override
    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        if (context.getPlayer() == null
            || context.getPlayer().isSneaking()
            || !(context.getStack().getItem() instanceof BlockItem block_item)
        ) return false;

        Edge edge = state.get(EDGE);
        // allow replacing with stair
        if (block_item.getBlock() == ReFramed.STAIR)
            return ReFramed.STAIRS_CUBE
                .matchesShape(
                    context.getHitPos(),
                    context.getBlockPos(),
                    ReFramed.STAIRS_CUBE.getDefaultState().with(EDGE, edge.opposite()),
                    1
                );

        if (block_item.getBlock() == this)
            return ReFramed.STAIR
                .matchesShape(
                    context.getHitPos(),
                    context.getBlockPos(),
                    ReFramed.STAIR.getDefaultState()
                        .with(EDGE, edge.opposite())
                );

        return false;
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

            // Steps Slab
            if (matchesShape(hit, pos,
                current_state.with(EDGE, edge.getOpposite(edge.getFirstDirection()))
            )) return ReFramed.STEPS_SLAB.getDefaultState()
                .with(FACING, edge.getFirstDirection())
                .with(AXIS, edge.getSecondDirection().getAxis())
                .with(WATERLOGGED, current_state.get(WATERLOGGED));

            else if (matchesShape(hit, pos,
                current_state.with(EDGE, edge.getOpposite(edge.getSecondDirection()))
            )) return ReFramed.STEPS_SLAB.getDefaultState()
                .with(FACING, edge.getFirstDirection())
                .with(AXIS, edge.getSecondDirection().getAxis())
                .with(WATERLOGGED, current_state.get(WATERLOGGED));

            // Steps Cross
            return null; // TODO STEP cross
        } else if (current_state.isOf(ReFramed.SLAB)) {
            Direction facing = current_state.get(FACING);
            Edge edge;

            // Slabs Stair
            if (ctx.getSide() == facing || ctx.getSide() == facing.getOpposite())
                edge = BlockHelper.getPlacementEdge(ctx);
            else
                edge = Edge.getByDirections(facing, ctx.getSide().getOpposite());
            return ReFramed.SLABS_STAIR.getDefaultState()
                .with(EDGE, edge)
                .with(EDGE_FACE, edge.getDirectionIndex(facing));

        }

        return super.getPlacementState(ctx).with(EDGE, BlockHelper.getPlacementEdge(ctx));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getStepShape(state.get(EDGE));
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(EDGE, state.get(EDGE).rotate(rotation));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.with(EDGE, state.get(EDGE).mirror(mirror));
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
