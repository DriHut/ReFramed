package fr.adrien1106.reframed.block;

import com.google.common.base.MoreObjects;
import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.util.ReFramedInteractionUtil;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ReFramedBlock extends Block implements BlockEntityProvider {
	public ReFramedBlock(Settings settings) {
		super(settings);
		setDefaultState(ReFramedInteractionUtil.setDefaultStates(getDefaultState()));
	}
	
	//For addon devs: override this so your blocks don't end up trying to place my block entity, my BlockEntityType only handles blocks internal to the mod
	//Just make your own BlockEntityType, it's fine, you can even use the same ReFramedEntity class
	@Override
	public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return ReFramed.REFRAMED_BLOCK_ENTITY.instantiate(pos, state);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(ReFramedInteractionUtil.appendProperties(builder));
	}
	
	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return ReFramedInteractionUtil.modifyPlacementState(super.getPlacementState(ctx), ctx);
	}
	
	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		ActionResult r = ReFramedInteractionUtil.onUse(state, world, pos, player, hand, hit);
		if(!r.isAccepted()) r = super.onUse(state, world, pos, player, hand, hit);
		return r;
	}
	
	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		ReFramedInteractionUtil.onStateReplaced(state, world, pos, newState, moved);
		super.onStateReplaced(state, world, pos, newState, moved);
	}
	
	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		ReFramedInteractionUtil.onPlaced(world, pos, state, placer, stack);
		super.onPlaced(world, pos, state, placer, stack);
	}
	
	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView view, BlockPos pos, ShapeContext ctx) {
		return MoreObjects.firstNonNull(ReFramedInteractionUtil.getCollisionShape(state, view, pos, ctx), super.getCollisionShape(state, view, pos, ctx));
	}
	
	@Override
	public boolean emitsRedstonePower(BlockState state) {
		return ReFramedInteractionUtil.emitsRedstonePower(state);
	}
	
	@Override
	public int getWeakRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction dir) {
		return ReFramedInteractionUtil.getWeakRedstonePower(state, view, pos, dir);
	}
	
	@Override
	public int getStrongRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction dir) {
		return ReFramedInteractionUtil.getStrongRedstonePower(state, view, pos, dir);
	}
}
