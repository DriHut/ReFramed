package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.util.ReframedInteractible;
import fr.adrien1106.reframed.util.ThemeableBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static net.minecraft.util.shape.VoxelShapes.*;

public class ReFramedBlock extends Block implements BlockEntityProvider {

	public static final BooleanProperty LIGHT = BooleanProperty.of("frame_light");

	public ReFramedBlock(Settings settings) {
		super(settings);
		setDefaultState(getDefaultState().with(LIGHT, false));
	}
	
	//For addon devs: override this so your blocks don't end up trying to place my block entity, my BlockEntityType only handles blocks internal to the mod
	//Just make your own BlockEntityType, it's fine, you can even use the same ReFramedEntity class
	@Override
	public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return ReFramed.REFRAMED_BLOCK_ENTITY.instantiate(pos, state);
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder.add(LIGHT));
	}
	
	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return ReFramedEntity.getNbtLightLevel(super.getPlacementState(ctx), ctx.getStack());
	}
	
	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if (!canUse(world, pos, player)) return superUse(state, world, pos, player, hand, hit);
		ActionResult result = useUpgrade(state, world, pos, player, hand);
		if (result.isAccepted()) return result;
		return useCamo(state, world, pos, player, hand, hit, 1);

	}

	// don't like this but might be useful
	protected ActionResult superUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		return super.onUse(state, world, pos, player, hand, hit);
	}

	protected boolean canUse(World world, BlockPos pos, PlayerEntity player) {
		return player.canModifyBlocks() && world.canPlayerModifyAt(player, pos);
	}

	protected static ActionResult useUpgrade(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand) {
		if(!(world.getBlockEntity(pos) instanceof ReFramedEntity block_entity)) return ActionResult.PASS;

		ItemStack held = player.getStackInHand(hand);
		ReframedInteractible ext = state.getBlock() instanceof ReframedInteractible e ? e : ReframedInteractible.Default.INSTANCE;

		// frame will emit light if applied with glowstone
		if(state.contains(LIGHT) && held.getItem() == Items.GLOWSTONE_DUST) {
			block_entity.toggleLight();
			world.setBlockState(pos, state.with(LIGHT, block_entity.emitsLight()));

			if(!player.isCreative())
				if (block_entity.emitsLight()) held.decrement(1);
				else held.increment(1);
			world.playSound(player, pos, SoundEvents.BLOCK_GLASS_HIT, SoundCategory.BLOCKS, 1f, 1f);
			return ActionResult.SUCCESS;
		}

		// frame will emit redstone if applied with redstone torch can deactivate redstone block camo emission
		if(held.getItem() == Items.REDSTONE_TORCH && ext.canAddRedstoneEmission(state, world, pos)) {
			block_entity.toggleRedstone();

			if(!player.isCreative())
				if (block_entity.emitsRedstone()) held.decrement(1);
				else held.increment(1);
			world.playSound(player, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 1f, 1f);
			return ActionResult.SUCCESS;
		}

		// Frame will lose its collision if applied with popped chorus fruit
		if(held.getItem() == Items.POPPED_CHORUS_FRUIT && ext.canRemoveCollision(state, world, pos)) {
			block_entity.toggleSolidity();

			if(!player.isCreative())
				if (!block_entity.isSolid()) held.decrement(1);
				else held.increment(1);
			world.playSound(player, pos, SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.BLOCKS, 1f, 1f);
			return ActionResult.SUCCESS;
		}

		return ActionResult.PASS;
	}

	protected static ActionResult useCamo(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, int theme_index) {
		if(!(world.getBlockEntity(pos) instanceof ReFramedEntity block_entity)) return ActionResult.PASS;

		// Changing the theme
		ItemStack held = player.getStackInHand(hand);
		if(held.getItem() instanceof BlockItem block_item && block_entity.getTheme(theme_index).getBlock() == Blocks.AIR) {
			Block block = block_item.getBlock();
			ItemPlacementContext ctx = new ItemPlacementContext(new ItemUsageContext(player, hand, hit));
			BlockState placement_state = block.getPlacementState(ctx);
			if(placement_state != null && isShapeFullCube(placement_state.getCollisionShape(world, pos)) && !(block instanceof BlockEntityProvider)) {
				List<BlockState> themes = block_entity.getThemes();
				if(!world.isClient) block_entity.setTheme(placement_state, theme_index);

				// check for default light emission
				if (placement_state.getLuminance() > 0
					&& themes.stream().noneMatch(theme -> theme.getLuminance() > 0))
						if (block_entity.emitsLight()) Block.dropStack(world, pos, new ItemStack(Items.GLOWSTONE_DUST));
						else block_entity.toggleLight();

				world.setBlockState(pos, state.with(LIGHT, block_entity.emitsLight()));

				// check for default redstone emission
				if (placement_state.getWeakRedstonePower(world, pos, Direction.NORTH) > 0
					&& themes.stream().noneMatch(theme -> theme.getWeakRedstonePower(world, pos, Direction.NORTH) > 0))
						if (block_entity.emitsRedstone()) Block.dropStack(world, pos, new ItemStack(Items.GLOWSTONE_DUST));
						else block_entity.toggleRedstone();

				if(!player.isCreative()) held.decrement(1);
				world.playSound(player, pos, placement_state.getSoundGroup().getPlaceSound(), SoundCategory.BLOCKS, 1f, 1.1f);
				return ActionResult.SUCCESS;
			}
		}

		return ActionResult.PASS;
	}
	
	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if(!state.isOf(newState.getBlock()) &&
			world.getBlockEntity(pos) instanceof ReFramedEntity frame_entity &&
			world.getGameRules().getBoolean(GameRules.DO_TILE_DROPS)
		) {
			DefaultedList<ItemStack> drops = DefaultedList.of();

			List<BlockState> themes = frame_entity.getThemes();
			themes.forEach(theme -> {
				if(theme.getBlock() != Blocks.AIR) drops.add(new ItemStack(theme.getBlock()));
			});

			if(frame_entity.emitsRedstone()
				&& themes.stream().noneMatch(theme -> theme.getWeakRedstonePower(world, pos, Direction.NORTH) != 0))
					drops.add(new ItemStack(Items.REDSTONE_TORCH));
			if(frame_entity.emitsLight()
				&& themes.stream().noneMatch(theme -> theme.getLuminance() != 0))
					drops.add(new ItemStack(Items.GLOWSTONE_DUST));
			if(!frame_entity.isSolid()
				&& themes.stream().anyMatch(theme -> theme.isSolid()))
				drops.add(new ItemStack(Items.POPPED_CHORUS_FRUIT));

			ItemScatterer.spawn(world, pos, drops);
		}
		super.onStateReplaced(state, world, pos, newState, moved);
	}
	
	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		if(world.isClient && world.getBlockEntity(pos) instanceof ReFramedEntity be) {
			NbtCompound tag = BlockItem.getBlockEntityNbt(stack);
			if(tag != null) be.readNbt(tag);
		}
		super.onPlaced(world, pos, state, placer, stack);
	}
	
	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView view, BlockPos pos, ShapeContext ctx) {
		return isGhost(view, pos)
			? VoxelShapes.empty()
			: super.getCollisionShape(state, view, pos, ctx);
	}

	@Override
	public VoxelShape getCullingShape(BlockState state, BlockView view, BlockPos pos) {
		return isGhost(view, pos)
			? VoxelShapes.empty()
			: super.getCullingShape(state, view, pos);
	}

	public VoxelShape getShape(BlockState state, int i) {
		// assuming the shape don't need the world and position
		return getOutlineShape(state, null, null, null);
	}

	public boolean isGhost(BlockView view, BlockPos pos) {
		return view.getBlockEntity(pos) instanceof ReFramedEntity be && !be.isSolid();
	}
	
	@Override
	public int getWeakRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction dir) {
		return view.getBlockEntity(pos) instanceof ReFramedEntity be && be.emitsRedstone() ? 15 : 0;
	}
	
	@Override
	public int getStrongRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction dir) {
		return getWeakRedstonePower(state, view, pos, dir);
	}

	public int getTopThemeIndex(BlockState state) {
		return 1;
	}

	// Doing this method from scratch as it is simpler to do than injecting everywhere
	public static boolean shouldDrawSide(BlockState self_state, BlockView world, BlockPos pos, Direction side, BlockPos other_pos, int theme_index) {
		ThemeableBlockEntity self = world.getBlockEntity(pos) instanceof ThemeableBlockEntity e ? e : null;
		ThemeableBlockEntity other = world.getBlockEntity(other_pos) instanceof ThemeableBlockEntity e ? e : null;
		BlockState other_state = world.getBlockState(other_pos);

		// normal behaviour
		if (self == null && other == null) return shouldDrawSide(self_state, world, pos, side, other_pos);

		// self is a normal Block
		if (self == null && other_state.getBlock() instanceof ReFramedBlock other_block) {
			VoxelShape self_shape = self_state.getCullingShape(world, pos);
			if (self_shape.isEmpty()) return true;

			int i = 0;
			VoxelShape other_shape = VoxelShapes.empty();
			for (BlockState s: other.getThemes()) {
				i++;
				if (self_state.isSideInvisible(s, side) || s.isOpaque())
					other_shape = combine(
						other_shape,
						other_block
							.getShape(other_state, i)
							.getFace(side.getOpposite()),
						BooleanBiFunction.OR
					);
			}

			// determine if side needs to be rendered
			return VoxelShapes.matchesAnywhere(self_shape, other_shape, BooleanBiFunction.ONLY_FIRST);
		}

		BlockState self_theme = self.getTheme(theme_index);
		// other is normal Block
		if (other == null && self_state.getBlock() instanceof ReFramedBlock self_block) {
			// Transparent is simple if self and the neighbor are invisible don't render side (like default)
			if (self_theme.isSideInvisible(other_state, side)) return false;

			// Opaque is also simple as each model are rendered one by one
			if (other_state.isOpaque()) {
				// no cache section :( because it differs between each instance of the frame
				VoxelShape self_shape = self_block.getShape(self_state, theme_index).getFace(side);
				if (self_shape.isEmpty()) return true;
				VoxelShape other_shape = other_state.getCullingFace(world, other_pos, side.getOpposite());

				// determine if side needs to be rendered
				return VoxelShapes.matchesAnywhere(self_shape, other_shape, BooleanBiFunction.ONLY_FIRST);
			}

			return true;
		}

		// Both are frames
		// here both are computed in the same zone as there will necessarily a shape comparison
		if (self_state.getBlock() instanceof ReFramedBlock self_block && other_state.getBlock() instanceof ReFramedBlock other_block) {
			VoxelShape self_shape = self_block.getShape(self_state, theme_index).getFace(side);
			if (self_shape.isEmpty()) return true;

			int i = 0;
			VoxelShape other_shape = VoxelShapes.empty();
			for (BlockState s: other.getThemes()) {
				i++;
				if (self_theme.isSideInvisible(s, side) || s.isOpaque())
					other_shape = combine(
						other_shape,
						other_block
							.getShape(other_state, i)
							.getFace(side.getOpposite()),
						BooleanBiFunction.OR
					);
			}

			// determine if side needs to be rendered
			return VoxelShapes.matchesAnywhere(self_shape, other_shape, BooleanBiFunction.ONLY_FIRST);
		}

		return true;
	}
}
