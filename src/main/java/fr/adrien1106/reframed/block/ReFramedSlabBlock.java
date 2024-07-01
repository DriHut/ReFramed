package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.util.blocks.BlockHelper;
import fr.adrien1106.reframed.util.blocks.Corner;
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
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static fr.adrien1106.reframed.util.blocks.BlockProperties.*;
import static net.minecraft.state.property.Properties.*;

public class ReFramedSlabBlock extends WaterloggableReFramedBlock {

	protected static final VoxelShape DOWN = VoxelShapes.cuboid(0f, 0f, 0f, 1f, 0.5f, 1f);
	protected static final VoxelShape UP = VoxelShapes.cuboid(0f, 0.5f, 0f, 1f, 1f, 1f);
	protected static final VoxelShape NORTH = VoxelShapes.cuboid(0f, 0f, 0f, 1f, 1f, 0.5f);
	protected static final VoxelShape SOUTH = VoxelShapes.cuboid(0f, 0f, 0.5f, 1f, 1f, 1f);
	protected static final VoxelShape EAST = VoxelShapes.cuboid(0.5f, 0f, 0f, 1f, 1f, 1f);
	protected static final VoxelShape WEST = VoxelShapes.cuboid(0f, 0f, 0f, 0.5f, 1f, 1f);

	public ReFramedSlabBlock(Settings settings) {
		super(settings);
		setDefaultState(getDefaultState().with(FACING, Direction.DOWN));
	}
	
	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder.add(FACING));
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
        if (block != this
            && block != ReFramed.STEP
            && block != ReFramed.SMALL_CUBE
            && block != ReFramed.HALF_STAIR
            && block != ReFramed.HALF_LAYER
            && block != ReFramed.LAYER
        ) return false;

        // check if the player is clicking on the inner part of the block
		return matchesShape(
            context.getHitPos(),
            context.getBlockPos(),
            state.with(FACING, state.get(FACING).getOpposite())
        );
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockState current_state = ctx.getWorld().getBlockState(ctx.getBlockPos());

		if (current_state.isOf(this))
			return ReFramed.SLABS_CUBE.getDefaultState()
				.with(AXIS, current_state.get(FACING).getAxis());

        if (current_state.isOf(ReFramed.HALF_LAYER)) {
            Edge edge = current_state.get(EDGE);
            Direction face = edge.getDirection(current_state.get(EDGE_FACE));
            edge = edge.getOpposite(face);
            return ReFramed.SLABS_HALF_LAYER.getDefaultState()
                .with(EDGE, edge)
                .with(EDGE_FACE, edge.getDirectionIndex(edge.getOtherDirection(face)))
                .with(LAYERS, current_state.get(LAYERS))
                .with(WATERLOGGED, current_state.get(WATERLOGGED));
        }

        if (current_state.isOf(ReFramed.HALF_STAIR)) {
            Corner corner = current_state.get(CORNER);
            Direction face = corner.getDirection(current_state.get(CORNER_FACE));
            corner = corner.change(face);
            return ReFramed.SLABS_INNER_STAIR.getDefaultState()
                .with(CORNER, corner)
                .with(CORNER_FACE, corner.getDirectionIndex(face.getOpposite()))
                .with(WATERLOGGED, current_state.get(WATERLOGGED));
        }

        if (current_state.isOf(ReFramed.STEP)) {
            Edge edge = current_state.get(EDGE),
                placed = BlockHelper.getPlacementEdge(ctx),
                new_edge;
            Direction face = edge.getFirstDirection(),
                other = edge.getSecondDirection().getOpposite();

            if (!ReFramed.STEP.matchesShape(
                    ctx.getHitPos(),
                    ctx.getBlockPos(),
                    current_state.with(EDGE, new_edge = edge.getOpposite(face))
                )
                && ctx.getSide() != other.getOpposite()
                && (placed.getAxis() == edge.getAxis()
                || ctx.getSide() == other
                || !placed.hasDirection(other))
            ) {
                new_edge = edge.getOpposite(edge.getSecondDirection());
                other = edge.getFirstDirection().getOpposite();
            }

            return ReFramed.SLABS_STAIR.getDefaultState()
                .with(EDGE, new_edge)
                .with(EDGE_FACE, new_edge.getDirectionIndex(other))
                .with(WATERLOGGED, current_state.get(WATERLOGGED));
        }

        if (current_state.isOf(ReFramed.SMALL_CUBE)) {
            Corner corner = current_state.get(CORNER);
            Edge placed = BlockHelper.getPlacementEdge(ctx);
            Direction face;
            Corner new_corner;

            if (!corner.hasDirection(face = ctx.getSide())) {
                int i = 0;
                do {
                    face = corner.getDirection(i);
                    new_corner = corner.change(face);
                } while (!ReFramed.SMALL_CUBE.matchesShape(
                    ctx.getHitPos(),
                    ctx.getBlockPos(),
                    current_state.with(CORNER, new_corner)
                ) && ++i < 3);

                if (i == 3) {
                    face = placed.getOtherDirection(corner.getMatchingDirection(placed)).getOpposite();
                    new_corner = corner.change(face);
                }
            } else new_corner = corner.change(face);

            return ReFramed.SLABS_OUTER_STAIR.getDefaultState()
                .with(CORNER, new_corner)
                .with(CORNER_FACE, new_corner.getDirectionIndex(face.getOpposite()))
                .with(WATERLOGGED, current_state.get(WATERLOGGED));
        }

		return super.getPlacementState(ctx).with(FACING, ctx.getSide().getOpposite());
	}

	@Override
    @SuppressWarnings("deprecation")
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return getSlabShape(state.get(FACING));
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

	public static VoxelShape getSlabShape(Direction side) {
		return switch (side) {
			case DOWN -> DOWN;
			case UP -> UP;
			case NORTH -> NORTH;
			case SOUTH -> SOUTH;
			case EAST -> EAST;
			case WEST -> WEST;
		};
	}

	@Override
	public Map<Integer, Integer> getThemeMap(BlockState state, BlockState new_state) {
        if (new_state.isOf(ReFramed.SLABS_STAIR)
            || new_state.isOf(ReFramed.SLABS_OUTER_STAIR)
            || new_state.isOf(ReFramed.SLABS_INNER_STAIR)
            || new_state.isOf(ReFramed.SLABS_HALF_LAYER)
        ) return Map.of(1, 1);
		if (new_state.isOf(ReFramed.SLABS_CUBE)) return Map.of(1, state.get(FACING).getDirection() == Direction.AxisDirection.POSITIVE ? 2 : 1);
		return super.getThemeMap(state, new_state);
	}
}
