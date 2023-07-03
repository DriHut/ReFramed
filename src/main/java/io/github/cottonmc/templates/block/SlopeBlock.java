package io.github.cottonmc.templates.block;

import io.github.cottonmc.templates.Templates;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.state.StateManager;
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
		super(TemplateBlock.configureSettings(Settings.create())
			.sounds(BlockSoundGroup.WOOD)
			.hardness(0.2f)); //TODO: Material.WOOD
		setDefaultState(getDefaultState().with(FACING, Direction.NORTH));
	}
	
	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder.add(FACING));
	}
	
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return Templates.SLOPE_ENTITY.instantiate(pos, state);
	}
	
	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing());
	}
	
	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView view, BlockPos pos, ShapeContext ctx) {
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
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext ctx) {
		return getCollisionShape(state, view, pos, ctx);
	}
}
