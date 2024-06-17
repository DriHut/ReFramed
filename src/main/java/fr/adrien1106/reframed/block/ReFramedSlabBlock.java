package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.ReFramed;
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

import static net.minecraft.state.property.Properties.AXIS;
import static net.minecraft.state.property.Properties.FACING;

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
        if (block_item.getBlock() != this
            && block_item.getBlock() != ReFramed.STEP
            && block_item.getBlock() != ReFramed.SMALL_CUBE
            && block_item.getBlock() != ReFramed.HALF_STAIR
        ) return false;

        // check if the player is clicking on the inner part of the block
		return ReFramed.SLABS_CUBE
            .matchesShape(
                context.getHitPos(),
                context.getBlockPos(),
                ReFramed.SLABS_CUBE.getDefaultState().with(AXIS, state.get(FACING).getAxis()),
                state.get(FACING).getDirection() == Direction.AxisDirection.POSITIVE ? 1 : 2
            );
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockState current_state = ctx.getWorld().getBlockState(ctx.getBlockPos());
		if (current_state.isOf(this))
			return ReFramed.SLABS_CUBE.getDefaultState()
				.with(AXIS, current_state.get(FACING).getAxis());

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
        ) return Map.of(1, 1);
		if (new_state.isOf(ReFramed.SLABS_CUBE)) return Map.of(1, state.get(FACING).getDirection() == Direction.AxisDirection.POSITIVE ? 2 : 1);
		return super.getThemeMap(state, new_state);
	}
}
