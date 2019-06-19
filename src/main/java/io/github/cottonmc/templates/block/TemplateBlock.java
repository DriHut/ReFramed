package io.github.cottonmc.templates.block;

import io.github.cottonmc.templates.Templates;
import io.github.cottonmc.templates.block.entity.TemplateBlockEntity;
import io.github.cottonmc.templates.util.StateContainer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public abstract class TemplateBlock extends Block implements BlockEntityProvider, StateContainer {
	public static final IntProperty LIGHT = IntProperty.of("light", 0, 15);
	public static final BooleanProperty REDSTONE = BooleanProperty.of("redstone");

	public TemplateBlock(Settings settings) {
		super(settings);
	}

	@Override
	public boolean activate(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (world.isClient || !(world.getBlockEntity(pos) instanceof TemplateBlockEntity)) return true;
		TemplateBlockEntity be = (TemplateBlockEntity) world.getBlockEntity(pos);
		ItemStack stack = player.getStackInHand(hand);
		if (stack.getItem() instanceof BlockItem) {
			Block block = ((BlockItem)stack.getItem()).getBlock();
			if (block == Blocks.REDSTONE_TORCH) {
				be.addRedstone();
				if (!player.abilities.creativeMode) stack.decrement(1);
			}
			ItemPlacementContext ctx = new ItemPlacementContext(new ItemUsageContext(player, hand, hit));
			BlockState placementState = block.getPlacementState(ctx);
			if (placementState.getOutlineShape(world, pos) == VoxelShapes.fullCube() && !(block instanceof BlockEntityProvider)) {
				if (be.getRenderedState().getBlock() == Blocks.AIR) {
					be.setRenderedState(placementState);
					if (!player.abilities.creativeMode) stack.decrement(1);
				}
			}
		} else if (stack.getItem() == Items.GLOWSTONE_DUST) {
			be.addGlowstone();
			if (!player.abilities.creativeMode) stack.decrement(1);
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

	@Override
	public void onBlockRemoved(BlockState state, World world, BlockPos pos, BlockState newState, boolean bool) {
		if (newState.getBlock() == Templates.SLOPE) return;
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof TemplateBlockEntity) {
			TemplateBlockEntity template = (TemplateBlockEntity)be;
			if (template.getRenderedState().getBlock() != Blocks.AIR) {
				ItemStack stack = new ItemStack(template.getRenderedState().getBlock());
				ItemEntity entity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack);
				world.spawnEntity(entity);
			}
			if (template.hasRedstone()) {
				ItemStack stack = new ItemStack(Items.REDSTONE_TORCH);
				ItemEntity entity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack);
				world.spawnEntity(entity);
			}
			if (template.hasGlowstone()) {
				ItemStack stack = new ItemStack(Items.GLOWSTONE_DUST);
				ItemEntity entity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack);
				world.spawnEntity(entity);
			}
		}
		super.onBlockRemoved(state, world, pos, newState, bool);
	}

	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos posFrom, boolean bool) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof TemplateBlockEntity) {
			TemplateBlockEntity template = (TemplateBlockEntity)be;
			BlockState beState = template.getRenderedState();
			world.setBlockState(pos, state.with(LIGHT, template.hasGlowstone()? 15 : beState.getLuminance()).with(REDSTONE, template.hasRedstone() || beState.emitsRedstonePower()));
		}
	}

	@Override
	public int getLuminance(BlockState state) {
		return state.get(LIGHT);
	}

	@Override
	public boolean emitsRedstonePower(BlockState state) {
		return state.get(REDSTONE);
	}

	@Override
	public int getWeakRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction dir) {
		BlockEntity be = view.getBlockEntity(pos);
		if (be instanceof TemplateBlockEntity) {
			TemplateBlockEntity template = (TemplateBlockEntity)be;
			if (template.hasRedstone()) return 15;
			BlockState beState = template.getRenderedState();
			return beState.getWeakRedstonePower(view, pos, dir);
		}
		return 0;
	}

	@Override
	public int getStrongRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction dir) {
		BlockEntity be = view.getBlockEntity(pos);
		if (be instanceof TemplateBlockEntity) {
			TemplateBlockEntity template = (TemplateBlockEntity)be;
			if (template.hasRedstone()) return 15;
			BlockState beState = template.getRenderedState();
			return beState.getStrongRedstonePower(view, pos, dir);
		}
		return 0;
	}

	@Override
	public BlockState getContainedState(World world, BlockPos pos) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof TemplateBlockEntity) return ((TemplateBlockEntity)be).getRenderedState();
		return Blocks.AIR.getDefaultState();
	}
}
