package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.util.VoxelHelper;
import fr.adrien1106.reframed.util.blocks.BlockHelper;
import fr.adrien1106.reframed.util.blocks.Edge;
import fr.adrien1106.reframed.util.blocks.StairShape;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.stream.Stream;

import static fr.adrien1106.reframed.util.VoxelHelper.VoxelListBuilder;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.EDGE;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.STAIR_SHAPE;
import static fr.adrien1106.reframed.util.blocks.StairShape.*;

public class ReFramedStairBlock extends WaterloggableReFramedBlock {
	public static final VoxelShape[] STAIR_VOXELS;
	private record ModelCacheKey(Edge edge, StairShape shape) {}

	public ReFramedStairBlock(Settings settings) {
		super(settings);
		setDefaultState(getDefaultState().with(EDGE, Edge.NORTH_DOWN).with(STAIR_SHAPE, STRAIGHT));
	}

	@Override
	public Object getModelCacheKey(BlockState state) {
		return new ModelCacheKey(state.get(EDGE), state.get(STAIR_SHAPE));
	}
	
	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder.add(EDGE, STAIR_SHAPE));
	}

	@Override
	public boolean canReplace(BlockState state, ItemPlacementContext context) {
		return !(
			context.getPlayer().isSneaking()
			|| !(context.getStack().getItem() instanceof BlockItem block_item)
			|| !(
				block_item.getBlock() == ReFramed.STEP
				&& ((ReFramedStairsCubeBlock) ReFramed.STAIRS_CUBE)
					.matchesShape(
						context.getHitPos(),
						context.getBlockPos(),
						state,
						2
					)
			)
		);
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighbor_state, WorldAccess world, BlockPos pos, BlockPos moved) {
		return super.getStateForNeighborUpdate(state, direction, neighbor_state, world, pos, moved)
			.with(STAIR_SHAPE, BlockHelper.getStairsShape(state.get(EDGE), world, pos));
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockState current_state = ctx.getWorld().getBlockState(ctx.getBlockPos());
		if (current_state.isOf(ReFramed.STEP)) {
			Edge edge = current_state.get(EDGE).opposite();
			StairShape shape = BlockHelper.getStairsShape(edge, ctx.getWorld(), ctx.getBlockPos());
			return ReFramed.STAIRS_CUBE.getDefaultState()
				.with(EDGE, edge)
				.with(STAIR_SHAPE, shape);
		}

		Edge edge = BlockHelper.getPlacementEdge(ctx);
		StairShape shape = BlockHelper.getStairsShape(edge, ctx.getWorld(), ctx.getBlockPos());
		return super.getPlacementState(ctx).with(EDGE, edge).with(STAIR_SHAPE, shape);
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		super.onStateReplaced(state, world, pos, newState, moved);

		if(!state.isOf(newState.getBlock())) world.removeBlockEntity(pos);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return getStairShape(state.get(EDGE), state.get(STAIR_SHAPE));
	}

	public static VoxelShape getStairShape(Edge edge, StairShape shape) {
		return STAIR_VOXELS[edge.getID() * 9 + shape.getID()];
	}

	@Override
	public Map<Integer, Integer> getThemeMap(BlockState state, BlockState new_state) {
		if (new_state.isOf(ReFramed.STAIRS_CUBE)) return Map.of(1, 1);
		return super.getThemeMap(state, new_state);
	}

	static {
		final VoxelShape STRAIGHT = VoxelShapes.combineAndSimplify(
			createCuboidShape(0, 8, 0, 16, 16, 8),
			createCuboidShape(0, 0, 0, 16, 8, 16),
			BooleanBiFunction.OR
		);
		final VoxelShape INNER = Stream.of(
			createCuboidShape(0, 8, 0, 16, 16, 8),
			createCuboidShape(0, 8, 8, 8, 16, 16),
			createCuboidShape(0, 0, 0, 16, 8, 16)
		).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();
		final VoxelShape OUTER = Stream.of(
			createCuboidShape(0, 8, 0, 8, 16, 8),
			createCuboidShape(8, 0, 0, 16, 8, 8),
			createCuboidShape(0, 0, 0, 8, 8, 16)
		).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();
		final VoxelShape JUNCTION = VoxelShapes.combineAndSimplify(
			createCuboidShape(0, 8, 0, 8, 16, 8),
			createCuboidShape(0, 0, 0, 16, 8, 16),
			BooleanBiFunction.OR
		);

		STAIR_VOXELS = VoxelListBuilder.create(STRAIGHT, 108)
			.add(INNER).add(VoxelHelper::rotateY)
			.add(OUTER).add(VoxelHelper::rotateY)
			.add(JUNCTION).add(VoxelHelper::rotateY)
			.add(JUNCTION, VoxelHelper::rotateX, VoxelHelper::rotateZ).add(VoxelHelper::rotateZ)
			// DOWN_SOUTH
			.add(0, VoxelHelper::rotateCX)
			.add(1, VoxelHelper::rotateCX).add(2, VoxelHelper::rotateCX)
			.add(3, VoxelHelper::rotateCX).add(4, VoxelHelper::rotateCX)
			.add(5, VoxelHelper::rotateCX).add(6, VoxelHelper::rotateCX)
			.add(7, VoxelHelper::rotateCX).add(8, VoxelHelper::rotateCX)
			// SOUTH_UP
			.add(9, VoxelHelper::rotateCX)
			.add(10, VoxelHelper::rotateCX).add(11, VoxelHelper::rotateCX)
			.add(12, VoxelHelper::rotateCX).add(13, VoxelHelper::rotateCX)
			.add(14, VoxelHelper::rotateCX).add(15, VoxelHelper::rotateCX)
			.add(16, VoxelHelper::rotateCX).add(17, VoxelHelper::rotateCX)
			// UP_NORTH
			.add(18, VoxelHelper::rotateCX)
			.add(19, VoxelHelper::rotateCX).add(20, VoxelHelper::rotateCX)
			.add(21, VoxelHelper::rotateCX).add(22, VoxelHelper::rotateCX)
			.add(23, VoxelHelper::rotateCX).add(24, VoxelHelper::rotateCX)
			.add(25, VoxelHelper::rotateCX).add(26, VoxelHelper::rotateCX)
			// WEST_DOWN
			.add(0, VoxelHelper::rotateCY)
			.add(1).add(10)
			.add(3).add(12)
			.add(5).add(16)
			.add(8, VoxelHelper::rotateCY).add(7, VoxelHelper::rotateCY)
			// DOWN_EAST
			.add(36, VoxelHelper::rotateZ)
			.add(2).add(11)
			.add(4).add(13)
			.add(41, VoxelHelper::rotateZ).add(42, VoxelHelper::rotateZ)
			.add(6).add(17)
			// EAST_UP
			.add(45, VoxelHelper::rotateZ)
			.add(29).add(20)
			.add(31).add(22)
			.add(35).add(24)
			.add(52, VoxelHelper::rotateZ).add(53, VoxelHelper::rotateZ)
			// UP_WEST
			.add(54, VoxelHelper::rotateZ)
			.add(28).add(19)
			.add(30).add(21)
			.add(59, VoxelHelper::rotateZ).add(60, VoxelHelper::rotateZ)
			.add(34).add(23)
			// WEST_NORTH
			.add(0, VoxelHelper::rotateCZ)
			.add(1).add(28)
			.add(3).add(30)
			.add(7).add(32)
			.add(43).add(68)
			// NORTH_EAST
			.add(72, VoxelHelper::rotateY)
			.add(2).add(29)
			.add(4).add(31)
			.add(50).add(61)
			.add(8).add(33)
			// EAST_SOUTH
			.add(81, VoxelHelper::rotateY)
			.add(11).add(20)
			.add(13).add(22)
			.add(15).add(26)
			.add(51).add(62)
			// SOUTH_WEST
			.add(90, VoxelHelper::rotateY)
			.add(10).add(19)
			.add(12).add(21)
			.add(44).add(69)
			.add(14).add(25)
			.build();
	}
}
