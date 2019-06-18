package io.github.cottonmc.slopetest.block;

import io.github.cottonmc.slopetest.block.entity.SlopeTestEntity;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateFactory;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class SlopeTestBlock extends Block implements BlockEntityProvider {
	public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

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
		if (player.getStackInHand(hand).getItem() instanceof BlockItem) {
			Block block = ((BlockItem)player.getStackInHand(hand).getItem()).getBlock();
			SlopeTestEntity be = (SlopeTestEntity)world.getBlockEntity(pos);
			be.setRenderedBlock(block);
			player.getStackInHand(hand).decrement(1);
		}
		return true;
	}

	@Override
	public boolean isOpaque(BlockState state) {
		return false;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.INVISIBLE;
	}
}
