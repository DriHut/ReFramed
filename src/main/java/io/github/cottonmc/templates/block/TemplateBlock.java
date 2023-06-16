package io.github.cottonmc.templates.block;

import io.github.cottonmc.templates.Templates;
import io.github.cottonmc.templates.block.entity.TemplateEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public abstract class TemplateBlock extends Block implements BlockEntityProvider {
	public static final IntProperty LIGHT = IntProperty.of("light", 0, 15);
	public static final BooleanProperty REDSTONE = BooleanProperty.of("redstone");
	
	public TemplateBlock(Settings settings) {
		super(settings);
	}
	
	public static Settings configureSettings(Settings s) {
		return s
			.luminance(state -> ((TemplateBlock) state.getBlock()).luminance(state))
			.nonOpaque();
	}
	
	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if(world.isClient || !(world.getBlockEntity(pos) instanceof TemplateEntity be)) return ActionResult.SUCCESS;
		
		ItemStack stack = player.getStackInHand(hand);
		if(stack.getItem() instanceof BlockItem) {
			Block block = ((BlockItem) stack.getItem()).getBlock();
			if(block == Blocks.REDSTONE_TORCH) {
				be.setRedstone(true);
				if(!player.isCreative()) stack.decrement(1);
			}
			ItemPlacementContext ctx = new ItemPlacementContext(new ItemUsageContext(player, hand, hit));
			BlockState placementState = block.getPlacementState(ctx);
			if(placementState != null &&
				Block.isShapeFullCube(placementState.getCollisionShape(world, pos)) &&
				!(block instanceof BlockEntityProvider) &&
				be.getRenderedState().getBlock() == Blocks.AIR)
			{
				be.setRenderedState(placementState);
				if(!player.isCreative()) stack.decrement(1);
			}
		} else if(stack.getItem() == Items.GLOWSTONE_DUST) {
			be.setGlowstone(true);
			if(!player.isCreative()) stack.decrement(1);
		}
		return ActionResult.SUCCESS;
	}
	
	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if(newState.getBlock() == Templates.SLOPE) return;
		BlockEntity be = world.getBlockEntity(pos);
		if(be instanceof TemplateEntity) {
			TemplateEntity template = (TemplateEntity) be;
			if(template.getRenderedState().getBlock() != Blocks.AIR) {
				ItemStack stack = new ItemStack(template.getRenderedState().getBlock());
				ItemEntity entity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack);
				world.spawnEntity(entity);
			}
			if(template.hasRedstone()) {
				ItemStack stack = new ItemStack(Items.REDSTONE_TORCH);
				ItemEntity entity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack);
				world.spawnEntity(entity);
			}
			if(template.hasGlowstone()) {
				ItemStack stack = new ItemStack(Items.GLOWSTONE_DUST);
				ItemEntity entity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), stack);
				world.spawnEntity(entity);
			}
		}
		super.onStateReplaced(state, world, pos, newState, moved);
	}
	
	@Override
	public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos posFrom, boolean bool) {
		BlockEntity be = world.getBlockEntity(pos);
		if(be instanceof TemplateEntity) {
			TemplateEntity template = (TemplateEntity) be;
			BlockState beState = template.getRenderedState();
			world.setBlockState(pos, state.with(LIGHT, template.hasGlowstone() ? 15 : beState.getLuminance()).with(REDSTONE, template.hasRedstone() || beState.emitsRedstonePower()));
		}
	}
	
	@Override
	public boolean emitsRedstonePower(BlockState state) {
		return state.get(REDSTONE);
	}
	
	@Override
	public int getWeakRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction dir) {
		BlockEntity be = view.getBlockEntity(pos);
		if(be instanceof TemplateEntity) {
			TemplateEntity template = (TemplateEntity) be;
			if(template.hasRedstone()) return 15;
			BlockState beState = template.getRenderedState();
			return beState.getWeakRedstonePower(view, pos, dir);
		}
		return 0;
	}
	
	@Override
	public int getStrongRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction dir) {
		BlockEntity be = view.getBlockEntity(pos);
		if(be instanceof TemplateEntity) {
			TemplateEntity template = (TemplateEntity) be;
			if(template.hasRedstone()) return 15;
			BlockState beState = template.getRenderedState();
			return beState.getStrongRedstonePower(view, pos, dir);
		}
		return 0;
	}
	
	//TODO: pass to Block.Settings
	// "Cannot reference 'TemplateBlock.luminance' before supertype constructor has been called"
	public int luminance(BlockState state) {
		return state.get(LIGHT);
	}
}
