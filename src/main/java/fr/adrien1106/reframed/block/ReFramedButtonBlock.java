package fr.adrien1106.reframed.block;

import com.google.common.base.MoreObjects;
import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.util.ReFramedInteractionUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ButtonBlock;
import net.minecraft.block.ShapeContext;
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

public class ReFramedButtonBlock extends ButtonBlock implements BlockEntityProvider {
	public ReFramedButtonBlock(Settings settings) {
		this(settings, BlockSetType.OAK, 30);
	}
	
	public ReFramedButtonBlock(Settings settings, BlockSetType blockSetType, int i) {
		super(blockSetType, i, settings);
		setDefaultState(ReFramedInteractionUtil.setDefaultStates(getDefaultState()));
	}
	
	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
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
	}
	
	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView view, BlockPos pos, ShapeContext ctx) {
		return MoreObjects.firstNonNull(ReFramedInteractionUtil.getCollisionShape(state, view, pos, ctx), super.getCollisionShape(state, view, pos, ctx));
	}
	
	@Override
	public boolean emitsRedstonePower(BlockState state) {
		return super.emitsRedstonePower(state);
	}
	
	@Override
	public int getWeakRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction dir) {
		boolean a = 0 != super.getWeakRedstonePower(state, view, pos, dir);
		boolean b = 0 != ReFramedInteractionUtil.getWeakRedstonePower(state, view, pos, dir);
		return (a ^ b) ? 15 : 0;
	}
	
	@Override
	public int getStrongRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction dir) {
		if(getDirection(state) != dir) return 0;
		else return getWeakRedstonePower(state, view, pos, dir);
	}
}
