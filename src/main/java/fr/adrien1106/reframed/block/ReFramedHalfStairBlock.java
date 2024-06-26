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
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
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

public class ReFramedHalfStairBlock extends WaterloggableReFramedBlock {

    public static final VoxelShape[] HALF_STAIR_VOXELS;

    public ReFramedHalfStairBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(CORNER, NORTH_EAST_DOWN).with(CORNER_FACE, 0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(CORNER,CORNER_FACE));
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        if (context.getPlayer() == null
            || context.getPlayer().isSneaking()
            || !(context.getStack().getItem() instanceof BlockItem block_item)
        ) return false;

        // allow replacing with slab, step, small cube and half stair
        Block block = block_item.getBlock();
        Corner corner = state.get(CORNER);
        Direction dir = corner.getDirection(state.get(CORNER_FACE));
        if (block == this || block == ReFramed.STEP)
            return ReFramed.HALF_STAIRS_STAIR.matchesShape(
                context.getHitPos(),
                context.getBlockPos(),
                ReFramed.HALF_STAIRS_STAIR.getDefaultState().with(EDGE, corner.getEdge(dir)),
                dir.getDirection() == Direction.AxisDirection.POSITIVE ? 1 : 2
            );

        if (block == ReFramed.SMALL_CUBE)
            return ReFramed.SMALL_CUBE.matchesShape(
                context.getHitPos(),
                context.getBlockPos(),
                ReFramed.SMALL_CUBE.getDefaultState().with(CORNER, corner.change(dir))
            ) || ReFramed.SMALL_CUBE.matchesShape(
                context.getHitPos(),
                context.getBlockPos(),
                ReFramed.SMALL_CUBE.getDefaultState().with(CORNER, corner.getOpposite(dir))
            );

        if (block == ReFramed.SLAB)
            return ReFramed.SLAB.matchesShape(
                context.getHitPos(),
                context.getBlockPos(),
                ReFramed.SLAB.getDefaultState().with(FACING, dir.getOpposite())
            );

        return false;
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState current_state = ctx.getWorld().getBlockState(ctx.getBlockPos());
        if (current_state.isOf(ReFramed.SMALL_CUBE)) {
            Corner corner = current_state.get(CORNER).getOpposite(ctx.getSide().getOpposite());
            return ReFramed.HALF_STAIRS_SLAB.getDefaultState()
                .with(CORNER, corner)
                .with(CORNER_FACE, corner.getDirectionIndex(ctx.getSide().getOpposite()))
                .with(WATERLOGGED, current_state.get(WATERLOGGED));
        }

        if (current_state.isOf(this))
            return ReFramed.HALF_STAIRS_STAIR.getDefaultState()
                .with(EDGE, current_state.get(CORNER).getEdge(current_state.get(CORNER).getDirection(current_state.get(CORNER_FACE))))
                .with(WATERLOGGED, current_state.get(WATERLOGGED));
        if (current_state.isOf(ReFramed.SLAB)) {
            Corner corner = BlockHelper.getPlacementCorner(ctx);
            Direction face = current_state.get(FACING);
            if (!corner.hasDirection(face)) corner = corner.change(face.getOpposite());
            return ReFramed.SLABS_INNER_STAIR.getDefaultState()
                .with(CORNER, corner)
                .with(CORNER_FACE, corner.getDirectionIndex(face))
                .with(WATERLOGGED, current_state.get(WATERLOGGED));
        }

        Corner corner = BlockHelper.getPlacementCorner(ctx);
        return super.getPlacementState(ctx)
            .with(CORNER, corner)
            .with(CORNER_FACE, corner.getDirectionIndex(ctx.getSide().getOpposite()));
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getHalfStairShape(state.get(CORNER), state.get(CORNER_FACE));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        Corner corner = state.get(CORNER).rotate(rotation);
        Direction face = state.get(CORNER).getDirection(state.get(CORNER_FACE));
        return state.with(CORNER, corner).with(CORNER_FACE, corner.getDirectionIndex(rotation.rotate(face)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        Corner corner = state.get(CORNER).mirror(mirror);
        Direction face = state.get(CORNER).getDirection(state.get(CORNER_FACE));
        return state.with(CORNER, corner).with(CORNER_FACE, corner.getDirectionIndex(mirror.apply(face)));
    }

    @Override
    public Map<Integer, Integer> getThemeMap(BlockState state, BlockState new_state) {
        if (new_state.isOf(ReFramed.HALF_STAIRS_SLAB)
            || new_state.isOf(ReFramed.HALF_STAIRS_CUBE_STAIR)
            || new_state.isOf(ReFramed.HALF_STAIRS_STEP_STAIR)
        ) return Map.of(1, 1);
        if (new_state.isOf(ReFramed.SLABS_INNER_STAIR)) return Map.of(1, 2);
        if (new_state.isOf(ReFramed.HALF_STAIRS_STAIR))
            return Map.of(
                1,
                state.get(CORNER)
                    .getDirection(state.get(CORNER_FACE))
                    .getDirection() == Direction.AxisDirection.POSITIVE ? 2 : 1
            );
        return super.getThemeMap(state, new_state);
    }

    public static VoxelShape getHalfStairShape(Corner corner, int face) {
        return HALF_STAIR_VOXELS[face + corner.getID() * 3];
    }

    static {
        final VoxelShape HALF_STAIR = VoxelShapes.combineAndSimplify(
            createCuboidShape(8, 0, 0, 16, 16, 8),
            createCuboidShape(0, 0, 0, 8, 8, 8),
            BooleanBiFunction.OR
        );
        HALF_STAIR_VOXELS = VoxelListBuilder.create(HALF_STAIR, 24)
            .add(0 , VoxelHelper::rotateY, VoxelHelper::mirrorZ)
            .add(0 , VoxelHelper::rotateCX, VoxelHelper::mirrorZ)

            .add(0 , VoxelHelper::rotateY)
            .add(1 , VoxelHelper::rotateY)
            .add(2 , VoxelHelper::rotateY)

            .add(3 , VoxelHelper::rotateY)
            .add(4 , VoxelHelper::rotateY)
            .add(5 , VoxelHelper::rotateY)

            .add(6 , VoxelHelper::rotateY)
            .add(7 , VoxelHelper::rotateY)
            .add(8 , VoxelHelper::rotateY)

            .add(0 , VoxelHelper::mirrorY)
            .add(1 , VoxelHelper::mirrorY)
            .add(2 , VoxelHelper::mirrorY)

            .add(12, VoxelHelper::rotateY)
            .add(13, VoxelHelper::rotateY)
            .add(14, VoxelHelper::rotateY)

            .add(15, VoxelHelper::rotateY)
            .add(16, VoxelHelper::rotateY)
            .add(17, VoxelHelper::rotateY)

            .add(18, VoxelHelper::rotateY)
            .add(19, VoxelHelper::rotateY)
            .add(20, VoxelHelper::rotateY)
            .build();
    }
}
