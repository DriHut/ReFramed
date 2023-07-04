package io.github.cottonmc.templates.block;

import io.github.cottonmc.templates.api.TemplateInteractionUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class TemplateBlock extends Block implements BlockEntityProvider {
	public TemplateBlock(Settings settings) {
		super(settings);
		setDefaultState(TemplateInteractionUtil.setDefaultStates(getDefaultState()));
	}
	
	@Override
	public abstract @Nullable BlockEntity createBlockEntity(BlockPos blockPos, BlockState blockState);
	
	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(TemplateInteractionUtil.appendProperties(builder));
	}
	
	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		return TemplateInteractionUtil.onUse(state, world, pos, player, hand, hit);
	}
	
	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		TemplateInteractionUtil.onStateReplaced(state, world, pos, newState, moved);
		super.onStateReplaced(state, world, pos, newState, moved);
	}
	
	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		TemplateInteractionUtil.onPlaced(world, pos, state, placer, stack);
		super.onPlaced(world, pos, state, placer, stack);
	}
	
	@Override
	public boolean emitsRedstonePower(BlockState state) {
		return TemplateInteractionUtil.emitsRedstonePower(state);
	}
	
	@Override
	public int getWeakRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction dir) {
		return TemplateInteractionUtil.getWeakRedstonePower(state, view, pos, dir);
	}
	
	@Override
	public int getStrongRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction dir) {
		return TemplateInteractionUtil.getStrongRedstonePower(state, view, pos, dir);
	}
}
