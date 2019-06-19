package io.github.cottonmc.slopetest.block;

import io.github.cottonmc.slopetest.block.entity.SlopeTestEntity;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class SlopeTestBlock extends Block implements BlockEntityProvider {
	public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

	public static final VoxelShape BASE = VoxelShapes.cuboid(0f, 0f, 0f, 1f, 0.5f, 1f);
	public static final VoxelShape NORTH = VoxelShapes.cuboid(0f, 0.5f, 0f, 1f, 1f, 0.5f);
	public static final VoxelShape SOUTH = VoxelShapes.cuboid(0f, 0.5f, 0.5f, 1f, 1f, 1f);
	public static final VoxelShape EAST = VoxelShapes.cuboid(0.5f, 0.5f, 0f, 1f, 1f, 1f);
	public static final VoxelShape WEST = VoxelShapes.cuboid(0f, 0.5f, 0f, 0.5f, 1f, 1f);

	public SlopeTestBlock() {
		super(FabricBlockSettings.of(Material.WOOD).build());
		this.setDefaultState(this.getStateFactory().getDefaultState().with(FACING, Direction.NORTH));
	}

	@Override
	protected void appendProperties(StateFactory.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockView blockView) {
		return new SlopeTestEntity();
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return getDefaultState().with(FACING, ctx.getPlayerFacing());
	}

	@Override
	public boolean activate(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (world.isClient || !(world.getBlockEntity(pos) instanceof SlopeTestEntity)) return true;
		ItemStack stack = player.getStackInHand(hand);
		if (stack.getItem() instanceof BlockItem) {
			Block block = ((BlockItem)stack.getItem()).getBlock();
			if (block.getDefaultState().getOutlineShape(world, pos) == VoxelShapes.fullCube() && !(block instanceof BlockEntityProvider)) {
				SlopeTestEntity be = (SlopeTestEntity) world.getBlockEntity(pos);
				if (be.getRenderedState().getBlock() == Blocks.AIR) {
					ItemPlacementContext ctx = new ItemPlacementContext(new ItemUsageContext(player, hand, hit));
					be.setRenderedState(block.getPlacementState(ctx));
					if (!player.abilities.creativeMode) stack.decrement(1);
				}
			}
		}
		return true;
	}

	@Override
	public boolean isOpaque(BlockState state) {
		return false;
	}

	@Override
	public boolean isSimpleFullBlock(BlockState state, BlockView view, BlockPos pos) {
		return false;
	}

//	@Override
//	public BlockRenderType getRenderType(BlockState state) {
//		return BlockRenderType.INVISIBLE;
//	}

	@Override
	public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean boolean_1) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof SlopeTestEntity) {
			SlopeTestEntity slope = (SlopeTestEntity)be;
			if (slope.getRenderedState().getBlock() != Blocks.AIR) {
				ItemStack stack = new ItemStack(slope.getRenderedState().getBlock());
				ItemEntity entity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack);
				world.spawnEntity(entity);
			}
		}
		super.onBlockRemoved(state, world, pos, newState, boolean_1);
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
