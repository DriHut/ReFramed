package fr.adrien1106.reframed.util;

import fr.adrien1106.reframed.block.ReFramedEntity;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

//For an example of how to use this class, have a look at TemplateBlock.
//Basically there are several methods that would like to modify the return value of something.
public class ReFramedInteractionUtil {
	public static final BooleanProperty LIGHT = BooleanProperty.of("frame_light");
	
	public static StateManager.Builder<Block, BlockState> appendProperties(StateManager.Builder<Block, BlockState> builder) {
		return builder.add(LIGHT);
	}
	
	//Use this to obtain a Block.Settings that'll make your Template act like the ones in the mod.
	//(To complete the look, don't forget to tag your blocks with mineable/axe.)
	private static final AbstractBlock.ContextPredicate NOPE = (blah, blahdey, blahh) -> false;
	public static AbstractBlock.Settings configureSettings(AbstractBlock.Settings s) {
		return s.luminance(ReFramedInteractionUtil::luminance).nonOpaque().sounds(BlockSoundGroup.WOOD).hardness(0.2f).suffocates(NOPE).blockVision(NOPE);
	}
	
	//And if you don't have a Block.Settings to copy off of.
	public static AbstractBlock.Settings makeSettings() {
		return configureSettings(AbstractBlock.Settings.create());
	}
	
	public static BlockState setDefaultStates(BlockState in) {
		if(in.contains(LIGHT)) in = in.with(LIGHT, false);
		return in;
	}
	
	public static @Nullable BlockState modifyPlacementState(@Nullable BlockState in, ItemPlacementContext ctx) {
		return ReFramedEntity.weirdNbtLightLevelStuff(in, ctx.getStack());
	}
	
	public static ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		if(!(world.getBlockEntity(pos) instanceof ReFramedEntity block_entity)) return ActionResult.PASS;
		if(!player.canModifyBlocks() || !world.canPlayerModifyAt(player, pos)) return ActionResult.PASS;
		
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
		
		// Changing the theme TODO Move outside
		if(held.getItem() instanceof BlockItem block_item && block_entity.getFirstTheme().getBlock() == Blocks.AIR) {
			Block block = block_item.getBlock();
			ItemPlacementContext ctx = new ItemPlacementContext(new ItemUsageContext(player, hand, hit));
			BlockState placementState = block.getPlacementState(ctx);
			if(placementState != null && Block.isShapeFullCube(placementState.getCollisionShape(world, pos)) && !(block instanceof BlockEntityProvider)) {
				// TODO FOR SECOND
				if(!world.isClient) block_entity.setFirstTheme(placementState);

				// check for default light emission
				if (placementState.getLuminance() > 0)
					if (block_entity.emitsLight()) Block.dropStack(world, pos, new ItemStack(Items.GLOWSTONE_DUST));
					else block_entity.toggleLight();

				world.setBlockState(pos, state.with(LIGHT, block_entity.emitsLight()));

				// check for redstone emission
				if (placementState.getWeakRedstonePower(world, pos, Direction.NORTH) > 0)
					if (block_entity.emitsRedstone()) Block.dropStack(world, pos, new ItemStack(Items.GLOWSTONE_DUST));
					else block_entity.toggleRedstone();
				
				if(!player.isCreative()) held.decrement(1);
				world.playSound(player, pos, placementState.getSoundGroup().getPlaceSound(), SoundCategory.BLOCKS, 1f, 1.1f);
				return ActionResult.SUCCESS;
			}
		}
		
		return ActionResult.PASS;
	}

	public static void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if(!state.isOf(newState.getBlock()) &&
			world.getBlockEntity(pos) instanceof ReFramedEntity frame_entity &&
			world.getGameRules().getBoolean(GameRules.DO_TILE_DROPS)
		) {
			DefaultedList<ItemStack> drops = DefaultedList.of();

			BlockState theme = frame_entity.getFirstTheme();
			if(theme.getBlock() != Blocks.AIR) drops.add(new ItemStack(theme.getBlock()));
			
			if(frame_entity.emitsRedstone() && theme.getWeakRedstonePower(world, pos, Direction.NORTH) == 0)
				drops.add(new ItemStack(Items.REDSTONE_TORCH));
			if(frame_entity.emitsLight() && theme.getLuminance() == 0)
				drops.add(new ItemStack(Items.GLOWSTONE_DUST));
			if(!frame_entity.isSolid() && theme.isSolid())
				drops.add(new ItemStack(Items.POPPED_CHORUS_FRUIT));
			
			ItemScatterer.spawn(world, pos, drops);
		}
	}
	
	public static void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		//Load the BlockEntityTag clientside, which fixes the template briefly showing its default state when placing it.
		//I'm surprised this doesn't happen by default; the BlockEntityTag stuff is only done serverside.
		if(world.isClient && world.getBlockEntity(pos) instanceof ReFramedEntity be) {
			NbtCompound tag = BlockItem.getBlockEntityNbt(stack);
			if(tag != null) be.readNbt(tag);
		}
	}
	
	//Returns "null" to signal "no opinion". Imagine it like an InteractionResult.PASS.
	public static @Nullable VoxelShape getCollisionShape(BlockState state, BlockView view, BlockPos pos, ShapeContext ctx) {
		return view.getBlockEntity(pos) instanceof ReFramedEntity be && !be.isSolid() ? VoxelShapes.empty() : null;
	}
	
	public static boolean emitsRedstonePower(BlockState state) {
		//return state.contains(REDSTONE) ? state.get(REDSTONE) : false;
		return false; //TODO, not available after punting this to BlockEntity. Yarn makes this method sound more important than it is, it's just for dust redirection.
	}
	
	public static int getWeakRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction dir) {
		return view.getBlockEntity(pos) instanceof ReFramedEntity be && be.emitsRedstone() ? 15 : 0;
	}
	
	public static int getStrongRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction dir) {
		return view.getBlockEntity(pos) instanceof ReFramedEntity be && be.emitsRedstone() ? 15 : 0;
	}
	
	public static int luminance(BlockState state) {
		return state.contains(LIGHT) && state.get(LIGHT) ? 15 : 0;
	}
}
