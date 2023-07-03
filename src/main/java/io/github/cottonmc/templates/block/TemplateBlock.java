package io.github.cottonmc.templates.block;

import io.github.cottonmc.templates.block.entity.TemplateEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
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
		
		setDefaultState(getDefaultState().with(LIGHT, 0).with(REDSTONE, false));
	}
	
	public static Settings configureSettings(Settings s) {
		return s
			.luminance(state -> ((TemplateBlock) state.getBlock()).luminance(state))
			.nonOpaque();
	}
	
	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder.add(LIGHT, REDSTONE));
	}
	
	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if(!state.isOf(this) || !(world.getBlockEntity(pos) instanceof TemplateEntity be)) return ActionResult.PASS; //shouldn't happen
		if(!player.canModifyBlocks() || !world.canPlayerModifyAt(player, pos)) return ActionResult.PASS;
		
		ItemStack held = player.getStackInHand(hand);
		
		//Glowstone
		if(held.getItem() == Items.GLOWSTONE_DUST && state.get(LIGHT) != 15 && !be.hasSpentGlowstoneDust()) {
			world.setBlockState(pos, state.with(LIGHT, 15));
			be.spentGlowstoneDust();
			
			if(!player.isCreative()) held.decrement(1);
			world.playSound(player, pos, SoundEvents.BLOCK_GLASS_HIT, SoundCategory.BLOCKS, 1f, 1f);
			return ActionResult.SUCCESS;
		}
		
		//Redstone
		if(held.getItem() == Blocks.REDSTONE_TORCH.asItem() && !state.get(REDSTONE) && !be.hasSpentRedstoneTorch()) {
			world.setBlockState(pos, state.with(REDSTONE, true));
			be.spentRedstoneTorch();
			
			if(!player.isCreative()) held.decrement(1);
			world.playSound(player, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 1f, 1f);
			return ActionResult.SUCCESS;
		}
		
		//Changing the theme
		if(held.getItem() instanceof BlockItem bi && be.getRenderedState().getBlock() == Blocks.AIR) {
			Block block = bi.getBlock();
			ItemPlacementContext ctx = new ItemPlacementContext(new ItemUsageContext(player, hand, hit));
			BlockState placementState = block.getPlacementState(ctx);
			if(placementState != null && Block.isShapeFullCube(placementState.getCollisionShape(world, pos)) && !(block instanceof BlockEntityProvider)) {
				if(!world.isClient) be.setRenderedState(placementState);
				
				//Even if the block does not glow, this'll do a block update when adding a redstoney block
				int newLuminance = be.hasSpentGlowstoneDust() ? 15 : placementState.getLuminance();
				world.setBlockState(pos, state.with(LIGHT, newLuminance));
				
				if(!player.isCreative()) held.decrement(1);
				world.playSound(player, pos, state.getSoundGroup().getPlaceSound(), SoundCategory.BLOCKS, 1f, 1f);
				return ActionResult.SUCCESS;
			}
		}
		
		return ActionResult.PASS;
	}
	
	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if(!state.isOf(newState.getBlock()) && world.getBlockEntity(pos) instanceof TemplateEntity template) {
			DefaultedList<ItemStack> drops = DefaultedList.of();
			
			Block theme = template.getRenderedState().getBlock();
			if(theme != Blocks.AIR) drops.add(new ItemStack(theme));
			if(template.hasSpentRedstoneTorch()) drops.add(new ItemStack(Items.REDSTONE_TORCH));
			if(template.hasSpentGlowstoneDust()) drops.add(new ItemStack(Items.GLOWSTONE_DUST));
			
			ItemScatterer.spawn(world, pos, drops);
		}
		
		super.onStateReplaced(state, world, pos, newState, moved);
	}
	
	@Override
	public boolean emitsRedstonePower(BlockState state) {
		return state.get(REDSTONE);
	}
	
	@Override
	public int getWeakRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction dir) {
		if(state.get(REDSTONE)) return 15;
		else if(view.getBlockEntity(pos) instanceof TemplateEntity template) return template.getRenderedState().getWeakRedstonePower(view, pos, dir);
		else return 0;
	}
	
	@Override
	public int getStrongRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction dir) {
		if(state.get(REDSTONE)) return 15;
		else if(view.getBlockEntity(pos) instanceof TemplateEntity template) return template.getRenderedState().getStrongRedstonePower(view, pos, dir);
		else return 0;
	}
	
	public int luminance(BlockState state) {
		return state.get(LIGHT);
	}
}
