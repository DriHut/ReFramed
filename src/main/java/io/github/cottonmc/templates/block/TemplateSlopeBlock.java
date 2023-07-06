package io.github.cottonmc.templates.block;

import com.google.common.base.MoreObjects;
import io.github.cottonmc.templates.Templates;
import io.github.cottonmc.templates.api.TemplateInteractionUtil;
import io.github.cottonmc.templates.util.StairShapeMaker;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

import javax.annotation.Nullable;

public class TemplateSlopeBlock extends TemplateBlock {
	public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
	public static final EnumProperty<BlockHalf> HALF = Properties.BLOCK_HALF;
	
	private static final VoxelShape[] shapes = new VoxelShape[8];
	private static int shapeIndex(Direction dir, BlockHalf half) {
		return dir.getHorizontal() + (half == BlockHalf.TOP ? 4 : 0);
	}
	static {
		for(BlockHalf half : BlockHalf.values()) {
			for(Direction d : Direction.values()) {
				if(d.getHorizontal() == -1) continue;
				shapes[shapeIndex(d, half)] = StairShapeMaker.createHorizontal(d, half, 8, 0.125d, 0.125d);
			}
		}
	}
	
	public TemplateSlopeBlock(Settings settings) {
		super(settings);
		setDefaultState(getDefaultState().with(FACING, Direction.NORTH).with(HALF, BlockHalf.BOTTOM));
	}
	
	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder.add(FACING, HALF));
	}
	
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return Templates.SLOPE_ENTITY.instantiate(pos, state);
	}
	
	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockHalf half = switch(ctx.getSide()) {
			case UP -> BlockHalf.BOTTOM;
			case DOWN -> BlockHalf.TOP;
			default -> (ctx.getHitPos().getY() - (double) ctx.getBlockPos().getY() < 0.5) ? BlockHalf.BOTTOM : BlockHalf.TOP;
		};
		
		return getDefaultState()
			.with(FACING, ctx.getHorizontalPlayerFacing())
			.with(HALF, half);
	}
	
	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView view, BlockPos pos, ShapeContext ctx) {
		return MoreObjects.firstNonNull(super.getCollisionShape(state, view, pos, ctx), shapes[shapeIndex(state.get(FACING), state.get(HALF))]);
	}
	
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext ctx) {
		return shapes[shapeIndex(state.get(FACING), state.get(HALF))];
	}
}
