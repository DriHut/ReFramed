package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.util.blocks.BlockHelper;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.Registries;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static fr.adrien1106.reframed.block.ReFramedEntity.BLOCKSTATE_KEY;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.LIGHT;

public class ReFramedBlock extends Block implements BlockEntityProvider {

	public ReFramedBlock(Settings settings) {
		super(settings);
		setDefaultState(getDefaultState().with(LIGHT, false));
	}

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
		if (!canUse(world, pos, player)) return ActionResult.PASS;
		ActionResult result = BlockHelper.useUpgrade(state, world, pos, player, hand);
		if (result.isAccepted()) return result;
		return BlockHelper.useCamo(state, world, pos, player, hand, hit, 1);

	}

	protected boolean canUse(World world, BlockPos pos, PlayerEntity player) {
		return player.canModifyBlocks() && world.canPlayerModifyAt(player, pos);
	}
	
	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState new_state, boolean moved) {
		if(!(new_state.getBlock() instanceof ReFramedBlock) &&
			world.getBlockEntity(pos) instanceof ReFramedEntity frame_entity &&
			world.getGameRules().getBoolean(GameRules.DO_TILE_DROPS)
		) {
			DefaultedList<ItemStack> drops = DefaultedList.of();

			List<BlockState> themes = frame_entity.getThemes();
			themes.forEach(theme -> {
				if(theme.getBlock() != Blocks.AIR) drops.add(new ItemStack(theme.getBlock()));
			});

			ItemScatterer.spawn(world, pos, drops);
		}
		super.onStateReplaced(state, world, pos, new_state, moved);
	}

	public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack, BlockState old_state, BlockEntity old_entity) {
		if (!(world.getBlockEntity(pos) instanceof ReFramedEntity frame_entity)) {
			onPlaced(world, pos, state, placer, stack);
			return;
		}

		// apply state change keeping the old information
		if (old_state.getBlock() instanceof ReFramedBlock old_frame_block
			&& old_entity instanceof ReFramedEntity old_frame_entity) {
			Map<Integer, Integer> theme_map = old_frame_block.getThemeMap(old_state, state);
			theme_map.forEach((self, other) ->
				frame_entity.setTheme(old_frame_entity.getTheme(self), other)
			);

			// apply any changes needed to keep previous properties
			if (old_frame_entity.emitsLight() && !frame_entity.emitsLight()) {
				frame_entity.toggleLight();
				world.setBlockState(pos, state.with(LIGHT, true));
			}
			if (old_frame_entity.emitsRedstone() && !frame_entity.emitsRedstone()) {
				frame_entity.toggleRedstone();
				world.updateNeighbors(pos, this);
			}
			if (old_frame_entity.isSolid() && !frame_entity.isSolid()) frame_entity.toggleSolidity();

			// apply themes from item
			NbtCompound tag = BlockItem.getBlockEntityNbt(stack);
			if(tag != null) {
				// determine a list of themes than can be used
				Iterator<Integer> free_themes = IntStream
					.rangeClosed(1, frame_entity.getThemes().size())
					.filter(value -> !theme_map.containsValue(value))
					.iterator();
				// apply all the themes possible from item
				for (int i = 1; tag.contains(BLOCKSTATE_KEY + i) && free_themes.hasNext(); i++) {
					BlockState theme = NbtHelper.toBlockState(Registries.BLOCK.getReadOnlyWrapper(), tag.getCompound(BLOCKSTATE_KEY + i));
					if (theme == null || theme.getBlock() == Blocks.AIR) continue;
					frame_entity.setTheme(theme, free_themes.next());
				}
			}
		} else if(world.isClient) { // prevents flashing with default texture before server sends the update
			NbtCompound tag = BlockItem.getBlockEntityNbt(stack);
			if(tag != null) frame_entity.readNbt(tag);
		}
		onPlaced(world, pos, state, placer, stack);
	}

    public boolean matchesShape(Vec3d hit, BlockPos pos, BlockState state) {
        return matchesShape(hit, pos, state, 0);
    }

    public boolean matchesShape(Vec3d hit, BlockPos pos, BlockState state, int i) {
        Vec3d rel = BlockHelper.getRelativePos(hit, pos);
        return matchesShape(rel, getShape(state, i));
    }

    public boolean matchesShape(Vec3d rel_hit, VoxelShape shape) {
        return BlockHelper.cursorMatchesFace(shape, rel_hit);
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

	/**
	 * @param state - the block state to get the top theme index from
	 * @return the index of the top theme to use for the block
	 */
	public int getTopThemeIndex(BlockState state) {
		return 1;
	}

	/**
	 * @param state     - the block state of the block that is being replaced
	 * @param new_state - the block state of the block that is replacing the block
	 * @return a map of the theme indexes to map when changing state so that the themes are preserved
	 */
	public Map<Integer, Integer> getThemeMap(BlockState state, BlockState new_state) {
		return Map.of();
	}
}
