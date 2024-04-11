package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.ReFramed;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static net.minecraft.data.client.VariantSettings.Rotation.*;
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
	public Object getModelCacheKey(BlockState state) {
		return state.get(FACING);
	}

	@Override
	public int getModelStateCount() {
		return 6;
	}
	
	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder.add(FACING));
	}

	@Override
	public boolean canReplace(BlockState state, ItemPlacementContext context) {
		return !(
			context.getPlayer().isSneaking()
			|| !(context.getStack().getItem() instanceof BlockItem block_item)
			|| !(
				block_item.getBlock() == this
				&& ((ReFramedSlabsCubeBlock) ReFramed.SLABS_CUBE)
					.matchesShape(
						context.getHitPos(),
						context.getBlockPos(),
						ReFramed.SLABS_CUBE.getDefaultState().with(AXIS, state.get(FACING).getAxis()),
						state.get(FACING).getDirection() == Direction.AxisDirection.POSITIVE ? 1 : 2
					)
			)
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
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return getSlabShape(state.get(FACING));
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
		if (new_state.isOf(ReFramed.SLABS_CUBE)) return Map.of(1, state.get(FACING).getDirection() == Direction.AxisDirection.POSITIVE ? 2 : 1);
		return super.getThemeMap(state, new_state);
	}
}
