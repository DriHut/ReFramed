package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.generator.RecipeSetter;
import fr.adrien1106.reframed.util.blocks.BlockHelper;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.state.StateManager;
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

import java.util.List;
import java.util.function.Consumer;

import static fr.adrien1106.reframed.util.blocks.BlockProperties.LIGHT;

public class ReFramedBlock extends Block implements BlockEntityProvider, RecipeSetter {

	public ReFramedBlock(Settings settings) {
		super(settings);
		setDefaultState(getDefaultState().with(LIGHT, false));
	}

	/**
	 * Generates a record for the key so that it replaces the blockstate
	 * which may have states that returns same models
	 * @param state - the state_key to generate the key from
	 * @return a cache key with only relevant properties
	 */
	public Object getModelCacheKey(BlockState state) {
		return "";
	}

	/**
	 * @return the amount of models the block can have prevents allocating too much space for a model
	 */
	public int getModelStateCount() {
		return 1;
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
		ActionResult result = BlockHelper.useUpgrade(state, world, pos, player, hand);
		if (result.isAccepted()) return result;
		return BlockHelper.useCamo(state, world, pos, player, hand, hit, 1);

	}

	// don't like this but might be useful
	protected ActionResult superUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
		return super.onUse(state, world, pos, player, hand, hit);
	}

	protected boolean canUse(World world, BlockPos pos, PlayerEntity player) {
		return player.canModifyBlocks() && world.canPlayerModifyAt(player, pos);
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
				&& themes.stream().anyMatch(AbstractBlockState::isSolid))
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

	@Override
	public void setRecipe(Consumer<RecipeJsonProvider> exporter) {
		ShapedRecipeJsonBuilder
			.create(RecipeCategory.BUILDING_BLOCKS, this)
			.pattern("III")
			.pattern("I~I")
			.pattern("III")
			.input('I', Items.BAMBOO)
			.input('~', Items.STRING)
			.criterion(FabricRecipeProvider.hasItem(Items.BAMBOO), FabricRecipeProvider.conditionsFromItem(Items.BAMBOO))
			.criterion(FabricRecipeProvider.hasItem(this), FabricRecipeProvider.conditionsFromItem(this))
			.offerTo(exporter);
	}
}
