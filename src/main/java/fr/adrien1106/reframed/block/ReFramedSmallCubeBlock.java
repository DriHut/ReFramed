package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.util.VoxelHelper;
import fr.adrien1106.reframed.util.blocks.BlockHelper;
import fr.adrien1106.reframed.util.blocks.Corner;
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
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static fr.adrien1106.reframed.util.VoxelHelper.VoxelListBuilder;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.*;
import static fr.adrien1106.reframed.util.blocks.Corner.*;
import static net.minecraft.state.property.Properties.WATERLOGGED;

public class ReFramedSmallCubeBlock extends WaterloggableReFramedBlock {

    public static final VoxelShape[] SMALL_CUBE_VOXELS;

    public ReFramedSmallCubeBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(CORNER, NORTH_EAST_DOWN));
    }

    @Override
    public Object getModelCacheKey(BlockState state) {
        return state.get(CORNER);
    }

    @Override
    public int getModelStateCount() {
        return 8;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(CORNER));
    }

    @Override
    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        Corner corner = state.get(CORNER);
        return !(
            context.getPlayer().isSneaking()
                || !(context.getStack().getItem() instanceof BlockItem block_item)
                || (
                    !(
                        block_item.getBlock() == ReFramed.HALF_STAIR
                        && !(corner.hasDirection(context.getSide())
                            || (corner.hasDirection(context.getSide().getOpposite())
                                && BlockHelper.cursorMatchesFace(
                                    getOutlineShape(state, context.getWorld(), context.getBlockPos(), null),
                                    BlockHelper.getRelativePos(context.getHitPos(), context.getBlockPos())
                                )
                            )
                        )
                    )
                    && !(
                        block_item.getBlock() == this
                        && (
                            ((ReFramedSmallCubesStepBlock) ReFramed.SMALL_CUBES_STEP)
                                .matchesShape(
                                    context.getHitPos(),
                                    context.getBlockPos(),
                                    ReFramed.SMALL_CUBES_STEP.getDefaultState().with(EDGE, corner.getEdge(corner.getFirstDirection())),
                                    corner.getFirstDirection().getDirection() == Direction.AxisDirection.POSITIVE ? 1 : 2
                                )
                            || ((ReFramedSmallCubesStepBlock) ReFramed.SMALL_CUBES_STEP)
                                .matchesShape(
                                    context.getHitPos(),
                                    context.getBlockPos(),
                                    ReFramed.SMALL_CUBES_STEP.getDefaultState().with(EDGE, corner.getEdge(corner.getSecondDirection())),
                                    corner.getSecondDirection().getDirection() == Direction.AxisDirection.POSITIVE ? 1 : 2
                                )
                            || ((ReFramedSmallCubesStepBlock) ReFramed.SMALL_CUBES_STEP)
                                .matchesShape(
                                    context.getHitPos(),
                                    context.getBlockPos(),
                                    ReFramed.SMALL_CUBES_STEP.getDefaultState().with(EDGE, corner.getEdge(corner.getThirdDirection())),
                                    corner.getThirdDirection().getDirection() == Direction.AxisDirection.POSITIVE ? 1 : 2
                                )
                        )
                )
            )
        );
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos pos = ctx.getBlockPos();
        BlockState current_state = ctx.getWorld().getBlockState(pos);
        if (current_state.isOf(ReFramed.HALF_STAIR))
            return ReFramed.HALF_STAIRS_SLAB.getDefaultState()
                .with(CORNER, current_state.get(CORNER))
                .with(CORNER_FACE, current_state.get(CORNER_FACE))
                .with(WATERLOGGED, current_state.get(WATERLOGGED));


        if (current_state.isOf(this)) {
            Vec3d hit = ctx.getHitPos();
            Corner corner = current_state.get(CORNER);
            ReFramedSmallCubesStepBlock block = ((ReFramedSmallCubesStepBlock) ReFramed.SMALL_CUBES_STEP);
            BlockState state = block.getDefaultState()
                .with(EDGE, corner.getEdge(corner.getFirstDirection()))
                .with(WATERLOGGED, current_state.get(WATERLOGGED));
            if (block.matchesShape(
                hit, pos, state,
                corner.getFirstDirection().getDirection() == Direction.AxisDirection.POSITIVE ? 1 : 2
            )) return state;
            state = state.with(EDGE, corner.getEdge(corner.getSecondDirection()));
            if (block.matchesShape(
                hit, pos, state,
                corner.getSecondDirection().getDirection() == Direction.AxisDirection.POSITIVE ? 1 : 2
            )) return state;
            return state.with(EDGE, corner.getEdge(corner.getThirdDirection()));
        }

        return super.getPlacementState(ctx).with(CORNER, BlockHelper.getPlacementCorner(ctx));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SMALL_CUBE_VOXELS[state.get(CORNER).getID()];
    }

    @Override
    public Map<Integer, Integer> getThemeMap(BlockState state, BlockState new_state) {
        if (new_state.isOf(ReFramed.HALF_STAIRS_SLAB)) return Map.of(1, 2);
        if (new_state.isOf(ReFramed.SMALL_CUBES_STEP))
            return Map.of(
                1,
                state.get(CORNER)
                    .getOtherDirection(new_state.get(EDGE))
                    .getDirection() == Direction.AxisDirection.POSITIVE ? 2 : 1
            );
        return super.getThemeMap(state, new_state);
    }

    static {
        final VoxelShape SMALL_CUBE = VoxelShapes.cuboid(.5f, 0f, 0f, 1f, .5f, .5f);

        SMALL_CUBE_VOXELS = VoxelListBuilder.create(SMALL_CUBE, 8)
            .add(VoxelHelper::rotateY)
            .add(VoxelHelper::rotateY)
            .add(VoxelHelper::rotateY)

            .add(SMALL_CUBE, VoxelHelper::mirrorY)
            .add(VoxelHelper::rotateY)
            .add(VoxelHelper::rotateY)
            .add(VoxelHelper::rotateY)
            .build();
    }
}
