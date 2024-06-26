package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.util.VoxelHelper;
import fr.adrien1106.reframed.util.blocks.BlockHelper;
import fr.adrien1106.reframed.util.blocks.Corner;
import fr.adrien1106.reframed.util.blocks.Edge;
import fr.adrien1106.reframed.util.blocks.StairShape;
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
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static fr.adrien1106.reframed.util.VoxelHelper.VoxelListBuilder;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.*;
import static fr.adrien1106.reframed.util.blocks.Corner.*;
import static net.minecraft.state.property.Properties.FACING;
import static net.minecraft.state.property.Properties.WATERLOGGED;

public class ReFramedSmallCubeBlock extends WaterloggableReFramedBlock {

    public static final VoxelShape[] SMALL_CUBE_VOXELS;

    public ReFramedSmallCubeBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(CORNER, NORTH_EAST_DOWN));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(CORNER));
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        if (context.getPlayer() == null
            || context.getPlayer().isSneaking()
            || !(context.getStack().getItem() instanceof BlockItem block_item)
        ) return false;

        Block block = block_item.getBlock();
        Corner corner = state.get(CORNER);
        if (block == this)
            return matchesShape(
                context.getHitPos(),
                context.getBlockPos(),
                getDefaultState().with(CORNER, corner.change(corner.getFirstDirection()))
            ) || matchesShape(
                context.getHitPos(),
                context.getBlockPos(),
                getDefaultState().with(CORNER, corner.change(corner.getSecondDirection()))
            ) || matchesShape(
                context.getHitPos(),
                context.getBlockPos(),
                getDefaultState().with(CORNER, corner.change(corner.getThirdDirection()))
            );

        if (block == ReFramed.HALF_STAIR || block == ReFramed.SLAB) {
            corner = corner.getOpposite();
            Direction face = corner.getFirstDirection();
            Edge edge = corner.getEdge(face);
            if (ReFramed.STAIR.matchesShape(
                context.getHitPos(),
                context.getBlockPos(),
                ReFramed.STAIR.getDefaultState()
                    .with(EDGE, edge)
                    .with(
                        STAIR_SHAPE,
                        face.getDirection() == Direction.AxisDirection.POSITIVE
                            ? StairShape.INNER_LEFT
                            : StairShape.INNER_RIGHT
                    )
            )) return block == ReFramed.SLAB || !matchesShape(
                context.getHitPos(),
                context.getBlockPos(),
                getDefaultState().with(CORNER, corner)
            );
        }

        return false;
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos pos = ctx.getBlockPos();
        BlockState current_state = ctx.getWorld().getBlockState(pos);

        if (current_state.isOf(ReFramed.HALF_STAIR)) {
            BlockState new_state;
            Direction face = current_state.get(CORNER).getDirection(current_state.get(CORNER_FACE));
            if (matchesShape(
                ctx.getHitPos(), pos,
                getDefaultState().with(CORNER, current_state.get(CORNER).change(face))
            )) new_state = ReFramed.HALF_STAIRS_CUBE_STAIR.getDefaultState();
            else new_state = ReFramed.HALF_STAIRS_SLAB.getDefaultState();
            return new_state
                .with(CORNER, current_state.get(CORNER))
                .with(CORNER_FACE, current_state.get(CORNER_FACE))
                .with(WATERLOGGED, current_state.get(WATERLOGGED));
        }

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

        if (current_state.isOf(ReFramed.SLAB)) {
            Corner corner = BlockHelper.getPlacementCorner(ctx);
            Direction face = current_state.get(FACING);
            if (!corner.hasDirection(face)) corner = corner.change(face.getOpposite());
            return ReFramed.SLABS_OUTER_STAIR.getDefaultState()
                .with(CORNER, corner)
                .with(CORNER_FACE, corner.getDirectionIndex(face))
                .with(WATERLOGGED, current_state.get(WATERLOGGED));
        }

        return super.getPlacementState(ctx).with(CORNER, BlockHelper.getPlacementCorner(ctx));
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getSmallCubeShape(state.get(CORNER));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(CORNER, state.get(CORNER).rotate(rotation));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.with(CORNER, state.get(CORNER).mirror(mirror));
    }

    @Override
    public Map<Integer, Integer> getThemeMap(BlockState state, BlockState new_state) {
        if (new_state.isOf(ReFramed.HALF_STAIRS_SLAB)
            || new_state.isOf(ReFramed.SLABS_OUTER_STAIR)
        ) return Map.of(1, 2);
        if (new_state.isOf(ReFramed.SMALL_CUBES_STEP))
            return Map.of(
                1,
                state.get(CORNER)
                    .getOtherDirection(new_state.get(EDGE))
                    .getDirection() == Direction.AxisDirection.POSITIVE ? 2 : 1
            );
        return super.getThemeMap(state, new_state);
    }

    public static VoxelShape getSmallCubeShape(Corner corner) {
        return SMALL_CUBE_VOXELS[corner.getID()];
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
