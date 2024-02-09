package fr.adrien1106.reframedtemplates.block;

import net.minecraft.block.*;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class TemplateSlabBlock extends WaterloggableTemplateBlock implements BlockEntityProvider, Waterloggable {

	private static final VoxelShape DOWN = VoxelShapes.cuboid(0f, 0f, 0f, 1f, 0.5f, 1f);
	private static final VoxelShape UP = VoxelShapes.cuboid(0f, 0.5f, 0f, 1f, 1f, 1f);
	private static final VoxelShape NORTH = VoxelShapes.cuboid(0f, 0f, 0f, 1f, 1f, 0.5f);
	private static final VoxelShape SOUTH = VoxelShapes.cuboid(0f, 0f, 0.5f, 1f, 1f, 1f);
	private static final VoxelShape EAST = VoxelShapes.cuboid(0.5f, 0f, 0f, 1f, 1f, 1f);
	private static final VoxelShape WEST = VoxelShapes.cuboid(0f, 0f, 0f, 0.5f, 1f, 1f);

	public TemplateSlabBlock(Settings settings) {
		super(settings);
		setDefaultState(getDefaultState().with(Properties.FACING, Direction.DOWN));
	}
	
	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder.add(Properties.FACING));
	}
	
	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return super.getPlacementState(ctx).with(Properties.FACING, ctx.getSide().getOpposite());
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return switch (state.get(Properties.FACING)) {
			case DOWN -> DOWN;
			case UP -> UP;
			case NORTH -> NORTH;
			case SOUTH -> SOUTH;
			case EAST -> EAST;
			case WEST -> WEST;
		};
	}
}
