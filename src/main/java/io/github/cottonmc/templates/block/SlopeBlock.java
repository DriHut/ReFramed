package io.github.cottonmc.templates.block;

import io.github.cottonmc.templates.block.entity.SlopeEntity;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityContext;
import net.minecraft.item.*;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

import javax.annotation.Nullable;

public class SlopeBlock extends TemplateBlock {
	public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

	public static final VoxelShape BASE = VoxelShapes.cuboid(0f, 0f, 0f, 1f, 0.5f, 1f);
	public static final VoxelShape NORTH = VoxelShapes.cuboid(0f, 0.5f, 0f, 1f, 1f, 0.5f);
	public static final VoxelShape SOUTH = VoxelShapes.cuboid(0f, 0.5f, 0.5f, 1f, 1f, 1f);
	public static final VoxelShape EAST = VoxelShapes.cuboid(0.5f, 0.5f, 0f, 1f, 1f, 1f);
	public static final VoxelShape WEST = VoxelShapes.cuboid(0f, 0.5f, 0f, 0.5f, 1f, 1f);

	public SlopeBlock() {
		super(FabricBlockSettings.of(Material.WOOD).build());
		this.setDefaultState(this.getStateFactory().getDefaultState().with(FACING, Direction.NORTH).with(LIGHT, 0).with(REDSTONE, false));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.add(FACING, LIGHT, REDSTONE);
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockView blockView) {
		return new SlopeEntity();
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return getDefaultState().with(FACING, ctx.getPlayerFacing());
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView view, BlockPos pos, EntityContext ctx) {
		Direction dir = state.get(FACING);
		switch(dir) {
			case NORTH:
				return VoxelShapes.union(BASE, NORTH);
			case SOUTH:
				return VoxelShapes.union(BASE, SOUTH);
			case EAST:
				return VoxelShapes.union(BASE, EAST);
			case WEST:
				return VoxelShapes.union(BASE, WEST);
			default:
				return VoxelShapes.fullCube();
		}
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext ctx) {
		return getCollisionShape(state, view, pos, ctx);
	}
}
