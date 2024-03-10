package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.generator.GBlockstate;
import fr.adrien1106.reframed.generator.BlockStateProvider;
import fr.adrien1106.reframed.util.blocks.BlockHelper;
import fr.adrien1106.reframed.util.VoxelHelper;
import fr.adrien1106.reframed.util.blocks.Edge;
import fr.adrien1106.reframed.util.blocks.StairShape;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.data.client.*;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.state.StateManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static fr.adrien1106.reframed.util.blocks.BlockProperties.*;
import static fr.adrien1106.reframed.util.blocks.StairShape.*;
import static net.minecraft.data.client.VariantSettings.Rotation.*;
import static fr.adrien1106.reframed.util.blocks.Edge.*;

public class ReFramedStairBlock extends WaterloggableReFramedBlock implements BlockStateProvider {
	
	public static final List<VoxelShape> VOXEL_LIST = new ArrayList<>(52);
	private record ModelCacheKey(Edge edge, StairShape shape) {}

	public ReFramedStairBlock(Settings settings) {
		super(settings);
		setDefaultState(getDefaultState().with(EDGE, Edge.NORTH_DOWN).with(STAIR_SHAPE, STRAIGHT));
	}

	@Override
	public Object getModelCacheKey(BlockState state) {
		return new ModelCacheKey(state.get(EDGE), state.get(STAIR_SHAPE));
	}

	@Override
	public int getModelStateCount() {
		return 108; // Has 12 * 9 state combination and 52 models still reduces cache size
	}
	
	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder.add(EDGE, STAIR_SHAPE));
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighbor_state, WorldAccess world, BlockPos pos, BlockPos moved) {
		return super.getStateForNeighborUpdate(state, direction, neighbor_state, world, pos, moved)
			.with(STAIR_SHAPE, BlockHelper.getStairsShape(state.getBlock(), state.get(EDGE), world, pos));
	}

	@Nullable
	@Override // Pretty happy of how clean it is (also got it on first try :) )
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		Edge face = BlockHelper.getPlacementEdge(ctx);
		StairShape shape = BlockHelper.getStairsShape(this, face, ctx.getWorld(), ctx.getBlockPos());
		return super.getPlacementState(ctx).with(EDGE, face).with(STAIR_SHAPE, shape);
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		super.onStateReplaced(state, world, pos, newState, moved);

		if(!state.isOf(newState.getBlock())) world.removeBlockEntity(pos);
	}

	/* ---------------------------------- DON'T GO FURTHER IF YOU LIKE HAVING EYES ---------------------------------- */
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return getOutline(state);
	}

	public static VoxelShape getOutline(BlockState state) {
		StairShape shape = state.get(STAIR_SHAPE);
		Edge direction = state.get(EDGE);
		return switch (shape) {
			case STRAIGHT ->
				switch (direction) {
					case DOWN_SOUTH ->                        VOXEL_LIST.get(0);
					case NORTH_DOWN ->                        VOXEL_LIST.get(1);
					case UP_NORTH ->                          VOXEL_LIST.get(2);
					case SOUTH_UP ->                          VOXEL_LIST.get(3);
					case DOWN_EAST ->                         VOXEL_LIST.get(4);
					case WEST_DOWN ->                         VOXEL_LIST.get(5);
					case UP_WEST ->                           VOXEL_LIST.get(6);
					case EAST_UP ->                           VOXEL_LIST.get(7);
					case NORTH_EAST ->                        VOXEL_LIST.get(8);
					case EAST_SOUTH ->                        VOXEL_LIST.get(9);
					case SOUTH_WEST ->                        VOXEL_LIST.get(10);
					case WEST_NORTH ->                        VOXEL_LIST.get(11);
				};
			case INNER_LEFT ->
				switch (direction) {
					case WEST_DOWN, NORTH_DOWN ->             VOXEL_LIST.get(44);
					case DOWN_EAST ->                         VOXEL_LIST.get(45);
					case DOWN_SOUTH ->                        VOXEL_LIST.get(47);
					case UP_WEST, UP_NORTH, WEST_NORTH ->     VOXEL_LIST.get(48);
					case EAST_UP, NORTH_EAST ->               VOXEL_LIST.get(49);
					case EAST_SOUTH ->                        VOXEL_LIST.get(50);
					case SOUTH_UP, SOUTH_WEST ->              VOXEL_LIST.get(51);
				};
			case INNER_RIGHT ->
				switch (direction) {
					case WEST_NORTH ->                        VOXEL_LIST.get(44);
					case NORTH_DOWN, NORTH_EAST ->            VOXEL_LIST.get(45);
					case DOWN_EAST, DOWN_SOUTH, EAST_SOUTH -> VOXEL_LIST.get(46);
					case WEST_DOWN, SOUTH_WEST ->             VOXEL_LIST.get(47);
					case UP_NORTH ->                          VOXEL_LIST.get(49);
					case EAST_UP, SOUTH_UP ->                 VOXEL_LIST.get(50);
					case UP_WEST ->                           VOXEL_LIST.get(51);
				};
			case OUTER_LEFT ->
				switch (direction) {
					case DOWN_EAST ->                         VOXEL_LIST.get(43);
					case WEST_DOWN, NORTH_DOWN ->             VOXEL_LIST.get(42);
					case DOWN_SOUTH ->                        VOXEL_LIST.get(41);
					case EAST_UP, NORTH_EAST ->               VOXEL_LIST.get(39);
					case UP_WEST, UP_NORTH, WEST_NORTH ->     VOXEL_LIST.get(38);
					case SOUTH_UP, SOUTH_WEST ->              VOXEL_LIST.get(37);
					case EAST_SOUTH ->                        VOXEL_LIST.get(36);
				};
			case OUTER_RIGHT ->
				switch (direction) {
					case NORTH_DOWN, NORTH_EAST ->            VOXEL_LIST.get(43);
					case WEST_NORTH ->                        VOXEL_LIST.get(42);
					case WEST_DOWN, SOUTH_WEST ->             VOXEL_LIST.get(41);
					case DOWN_EAST, DOWN_SOUTH, EAST_SOUTH -> VOXEL_LIST.get(40);
					case UP_NORTH ->                          VOXEL_LIST.get(39);
					case UP_WEST ->                           VOXEL_LIST.get(37);
					case EAST_UP, SOUTH_UP ->                 VOXEL_LIST.get(36);
				};
			case FIRST_OUTER_LEFT ->
				switch (direction) {
					case WEST_DOWN, NORTH_DOWN ->             VOXEL_LIST.get(14);
					case SOUTH_UP ->                          VOXEL_LIST.get(17);
					case EAST_UP ->                           VOXEL_LIST.get(19);
					case EAST_SOUTH ->                        VOXEL_LIST.get(20);
					case DOWN_SOUTH ->                        VOXEL_LIST.get(22);
					case UP_NORTH, WEST_NORTH ->              VOXEL_LIST.get(25);
					case SOUTH_WEST ->                        VOXEL_LIST.get(28);
					case UP_WEST ->                           VOXEL_LIST.get(31);
					case DOWN_EAST ->                         VOXEL_LIST.get(34);
					case NORTH_EAST ->                        VOXEL_LIST.get(35);
				};
			case FIRST_OUTER_RIGHT ->
				switch (direction) {
					case NORTH_DOWN ->                        VOXEL_LIST.get(15);
					case SOUTH_UP, EAST_UP ->                 VOXEL_LIST.get(16);
					case WEST_DOWN ->                         VOXEL_LIST.get(13);
					case DOWN_SOUTH, EAST_SOUTH ->            VOXEL_LIST.get(23);
					case UP_NORTH ->                          VOXEL_LIST.get(24);
					case WEST_NORTH ->                        VOXEL_LIST.get(26);
					case UP_WEST ->                           VOXEL_LIST.get(28);
					case SOUTH_WEST ->                        VOXEL_LIST.get(29);
					case DOWN_EAST ->                         VOXEL_LIST.get(33);
					case NORTH_EAST ->                        VOXEL_LIST.get(34);
				};
			case SECOND_OUTER_LEFT ->
				switch (direction) {
					case DOWN_EAST ->                         VOXEL_LIST.get(15);
					case DOWN_SOUTH ->                        VOXEL_LIST.get(13);
					case UP_WEST, UP_NORTH ->                 VOXEL_LIST.get(18);
					case SOUTH_UP, SOUTH_WEST ->              VOXEL_LIST.get(21);
					case NORTH_EAST ->                        VOXEL_LIST.get(24);
					case NORTH_DOWN ->                        VOXEL_LIST.get(26);
					case WEST_DOWN ->                         VOXEL_LIST.get(30);
					case WEST_NORTH ->                        VOXEL_LIST.get(31);
					case EAST_SOUTH ->                        VOXEL_LIST.get(32);
					case EAST_UP ->                           VOXEL_LIST.get(35);
				};
			case SECOND_OUTER_RIGHT ->
				switch (direction) {
					case DOWN_SOUTH, DOWN_EAST ->             VOXEL_LIST.get(12);
					case UP_WEST ->                           VOXEL_LIST.get(17);
					case UP_NORTH ->                          VOXEL_LIST.get(19);
					case SOUTH_UP ->                          VOXEL_LIST.get(20);
					case SOUTH_WEST ->                        VOXEL_LIST.get(22);
					case NORTH_EAST, NORTH_DOWN ->            VOXEL_LIST.get(27);
					case WEST_DOWN ->                         VOXEL_LIST.get(29);
					case WEST_NORTH ->                        VOXEL_LIST.get(30);
					case EAST_UP ->                           VOXEL_LIST.get(32);
					case EAST_SOUTH ->                        VOXEL_LIST.get(33);
				};
		};
	}

	@Override
	public MultipartBlockStateSupplier getMultipart() {
		return getStairMultipart(this, false);
	}

	public static MultipartBlockStateSupplier getStairMultipart(Block block, boolean is_double) {
		String prefix = is_double ? "double_" : "";
		Identifier straight_id = ReFramed.id(prefix + "stairs_special");
		Identifier double_outer_id = ReFramed.id(prefix + "outers_stairs_special");
		Identifier inner_id = ReFramed.id(prefix + "inner_stairs_special");
		Identifier outer_id = ReFramed.id(prefix + "outer_stairs_special");
		Identifier outer_side_id = ReFramed.id(prefix + "outer_side_stairs_special");
		return MultipartBlockStateSupplier.create(block)
			/* STRAIGHT X AXIS */
			.with(GBlockstate.when(EDGE, DOWN_EAST, STAIR_SHAPE, STRAIGHT),
				GBlockstate.variant(straight_id, true, R0, R0))
			.with(GBlockstate.when(EDGE, EAST_UP, STAIR_SHAPE, STRAIGHT),
				GBlockstate.variant(straight_id, true, R180, R0))
			.with(GBlockstate.when(EDGE, UP_WEST, STAIR_SHAPE, STRAIGHT),
				GBlockstate.variant(straight_id, true, R180, R180))
			.with(GBlockstate.when(EDGE, WEST_DOWN, STAIR_SHAPE, STRAIGHT),
				GBlockstate.variant(straight_id, true, R0, R180))
			/* STRAIGHT Y AXIS */
			.with(GBlockstate.when(EDGE, EAST_SOUTH, STAIR_SHAPE, STRAIGHT),
				GBlockstate.variant(straight_id, true, R90, R0))
			.with(GBlockstate.when(EDGE, SOUTH_WEST, STAIR_SHAPE, STRAIGHT),
				GBlockstate.variant(straight_id, true, R90, R90))
			.with(GBlockstate.when(EDGE, WEST_NORTH, STAIR_SHAPE, STRAIGHT),
				GBlockstate.variant(straight_id, true, R90, R180))
			.with(GBlockstate.when(EDGE, NORTH_EAST, STAIR_SHAPE, STRAIGHT),
				GBlockstate.variant(straight_id, true, R90, R270))
			/* STRAIGHT Z AXIS */
			.with(GBlockstate.when(EDGE, DOWN_SOUTH, STAIR_SHAPE, STRAIGHT),
				GBlockstate.variant(straight_id, true, R0, R90))
			.with(GBlockstate.when(EDGE, NORTH_DOWN, STAIR_SHAPE, STRAIGHT),
				GBlockstate.variant(straight_id, true, R0, R270))
			.with(GBlockstate.when(EDGE, UP_NORTH, STAIR_SHAPE, STRAIGHT),
				GBlockstate.variant(straight_id, true, R180, R270))
			.with(GBlockstate.when(EDGE, SOUTH_UP, STAIR_SHAPE, STRAIGHT),
				GBlockstate.variant(straight_id, true, R180, R90))
			/* INNER BOTTOM */
			.with(When.anyOf(
				GBlockstate.when(EDGE, NORTH_DOWN, STAIR_SHAPE, INNER_LEFT),
				GBlockstate.when(EDGE, WEST_NORTH, STAIR_SHAPE, INNER_RIGHT),
			    GBlockstate.when(EDGE, WEST_DOWN, STAIR_SHAPE, INNER_LEFT)),
				GBlockstate.variant(inner_id, true, R0, R180))
			.with(When.anyOf(
				GBlockstate.when(EDGE, NORTH_DOWN, STAIR_SHAPE, INNER_RIGHT),
				GBlockstate.when(EDGE, NORTH_EAST, STAIR_SHAPE, INNER_RIGHT),
				GBlockstate.when(EDGE, DOWN_EAST, STAIR_SHAPE, INNER_LEFT)),
				GBlockstate.variant(inner_id, true, R0, R270))
			.with(When.anyOf(
				GBlockstate.when(EDGE, DOWN_SOUTH, STAIR_SHAPE, INNER_RIGHT),
				GBlockstate.when(EDGE, EAST_SOUTH, STAIR_SHAPE, INNER_RIGHT),
				GBlockstate.when(EDGE, DOWN_EAST, STAIR_SHAPE, INNER_RIGHT)),
				GBlockstate.variant(inner_id, true, R0, R0))
			.with(When.anyOf(
				GBlockstate.when(EDGE, DOWN_SOUTH, STAIR_SHAPE, INNER_LEFT),
				GBlockstate.when(EDGE, SOUTH_WEST, STAIR_SHAPE, INNER_RIGHT),
				GBlockstate.when(EDGE, WEST_DOWN, STAIR_SHAPE, INNER_RIGHT)),
				GBlockstate.variant(inner_id, true, R0, R90))
			/* INNER TOP */
			.with(When.anyOf(
				GBlockstate.when(EDGE, EAST_UP, STAIR_SHAPE, INNER_LEFT),
				GBlockstate.when(EDGE, NORTH_EAST, STAIR_SHAPE, INNER_LEFT),
				GBlockstate.when(EDGE, UP_NORTH, STAIR_SHAPE, INNER_RIGHT)),
				GBlockstate.variant(inner_id, true, R180, R0))
			.with(When.anyOf(
				GBlockstate.when(EDGE, EAST_UP, STAIR_SHAPE, INNER_RIGHT),
				GBlockstate.when(EDGE, EAST_SOUTH, STAIR_SHAPE, INNER_LEFT),
				GBlockstate.when(EDGE, SOUTH_UP, STAIR_SHAPE, INNER_RIGHT)),
				GBlockstate.variant(inner_id, true, R180, R90))
			.with(When.anyOf(
				GBlockstate.when(EDGE, SOUTH_UP, STAIR_SHAPE, INNER_LEFT),
				GBlockstate.when(EDGE, SOUTH_WEST, STAIR_SHAPE, INNER_LEFT),
				GBlockstate.when(EDGE, UP_WEST, STAIR_SHAPE, INNER_RIGHT)),
				GBlockstate.variant(inner_id, true, R180, R180))
			.with(When.anyOf(
				GBlockstate.when(EDGE, UP_NORTH, STAIR_SHAPE, INNER_LEFT),
				GBlockstate.when(EDGE, WEST_NORTH, STAIR_SHAPE, INNER_LEFT),
				GBlockstate.when(EDGE, UP_WEST, STAIR_SHAPE, INNER_LEFT)),
				GBlockstate.variant(inner_id, true, R180, R270))
			/* OUTER BOTTOM */
			.with(When.anyOf(
				GBlockstate.when(EDGE, DOWN_SOUTH, STAIR_SHAPE, SECOND_OUTER_RIGHT),
				GBlockstate.when(EDGE, DOWN_EAST, STAIR_SHAPE, SECOND_OUTER_RIGHT)),
				GBlockstate.variant(outer_id, true, R0, R0))
			.with(When.anyOf(
				GBlockstate.when(EDGE, DOWN_SOUTH, STAIR_SHAPE, SECOND_OUTER_LEFT),
				GBlockstate.when(EDGE, WEST_DOWN, STAIR_SHAPE, FIRST_OUTER_RIGHT)),
				GBlockstate.variant(outer_id, true, R0, R90))
			.with(When.anyOf(
				GBlockstate.when(EDGE, NORTH_DOWN, STAIR_SHAPE, FIRST_OUTER_LEFT),
				GBlockstate.when(EDGE, WEST_DOWN, STAIR_SHAPE, FIRST_OUTER_LEFT)),
				GBlockstate.variant(outer_id, true, R0, R180))
			.with(When.anyOf(
				GBlockstate.when(EDGE, NORTH_DOWN, STAIR_SHAPE, FIRST_OUTER_RIGHT),
				GBlockstate.when(EDGE, DOWN_EAST, STAIR_SHAPE, SECOND_OUTER_LEFT)),
				GBlockstate.variant(outer_id, true, R0, R270))
			/* OUTER TOP */
			.with(When.anyOf(
				GBlockstate.when(EDGE, UP_NORTH, STAIR_SHAPE, SECOND_OUTER_RIGHT),
				GBlockstate.when(EDGE, EAST_UP, STAIR_SHAPE, FIRST_OUTER_LEFT)),
				GBlockstate.variant(outer_id, true, R180, R0))
			.with(When.anyOf(
				GBlockstate.when(EDGE, SOUTH_UP, STAIR_SHAPE, FIRST_OUTER_RIGHT),
				GBlockstate.when(EDGE, EAST_UP, STAIR_SHAPE, FIRST_OUTER_RIGHT)),
				GBlockstate.variant(outer_id, true, R180, R90))
			.with(When.anyOf(
				GBlockstate.when(EDGE, SOUTH_UP, STAIR_SHAPE, FIRST_OUTER_LEFT),
				GBlockstate.when(EDGE, UP_WEST, STAIR_SHAPE, SECOND_OUTER_RIGHT)),
				GBlockstate.variant(outer_id, true, R180, R180))
			.with(When.anyOf(
				GBlockstate.when(EDGE, UP_NORTH, STAIR_SHAPE, SECOND_OUTER_LEFT),
				GBlockstate.when(EDGE, UP_WEST, STAIR_SHAPE, SECOND_OUTER_LEFT)),
				GBlockstate.variant(outer_id, true, R180, R270))
			/* OUTER EAST */
			.with(When.anyOf(
				GBlockstate.when(EDGE, EAST_SOUTH, STAIR_SHAPE, SECOND_OUTER_RIGHT),
				GBlockstate.when(EDGE, DOWN_EAST, STAIR_SHAPE, FIRST_OUTER_RIGHT)),
				GBlockstate.variant(outer_side_id, true, R0, R0))
			.with(When.anyOf(
				GBlockstate.when(EDGE, EAST_SOUTH, STAIR_SHAPE, SECOND_OUTER_LEFT),
				GBlockstate.when(EDGE, EAST_UP, STAIR_SHAPE, SECOND_OUTER_RIGHT)),
				GBlockstate.variant(outer_side_id, true, R90, R0))
			.with(When.anyOf(
				GBlockstate.when(EDGE, NORTH_EAST, STAIR_SHAPE, FIRST_OUTER_LEFT),
				GBlockstate.when(EDGE, EAST_UP, STAIR_SHAPE, SECOND_OUTER_LEFT)),
				GBlockstate.variant(outer_side_id, true, R180, R0))
			.with(When.anyOf(
				GBlockstate.when(EDGE, NORTH_EAST, STAIR_SHAPE, FIRST_OUTER_RIGHT),
				GBlockstate.when(EDGE, DOWN_EAST, STAIR_SHAPE, FIRST_OUTER_LEFT)),
				GBlockstate.variant(outer_side_id, true, R270, R0))
			/* OUTER SOUTH */
			.with(When.anyOf(
				GBlockstate.when(EDGE, SOUTH_WEST, STAIR_SHAPE, SECOND_OUTER_RIGHT),
				GBlockstate.when(EDGE, DOWN_SOUTH, STAIR_SHAPE, FIRST_OUTER_LEFT)),
				GBlockstate.variant(outer_side_id, true, R0, R90))
			.with(When.anyOf(
				GBlockstate.when(EDGE, SOUTH_WEST, STAIR_SHAPE, SECOND_OUTER_LEFT),
				GBlockstate.when(EDGE, SOUTH_UP, STAIR_SHAPE, SECOND_OUTER_LEFT)),
				GBlockstate.variant(outer_side_id, true, R90, R90))
			.with(When.anyOf(
				GBlockstate.when(EDGE, EAST_SOUTH, STAIR_SHAPE, FIRST_OUTER_LEFT),
				GBlockstate.when(EDGE, SOUTH_UP, STAIR_SHAPE, SECOND_OUTER_RIGHT)),
				GBlockstate.variant(outer_side_id, true, R180, R90))
			.with(When.anyOf(
				GBlockstate.when(EDGE, EAST_SOUTH, STAIR_SHAPE, FIRST_OUTER_RIGHT),
				GBlockstate.when(EDGE, DOWN_SOUTH, STAIR_SHAPE, FIRST_OUTER_RIGHT)),
				GBlockstate.variant(outer_side_id, true, R270, R90))
			/* OUTER WEST */
			.with(When.anyOf(
				GBlockstate.when(EDGE, WEST_NORTH, STAIR_SHAPE, SECOND_OUTER_RIGHT),
				GBlockstate.when(EDGE, WEST_DOWN, STAIR_SHAPE, SECOND_OUTER_LEFT)),
				GBlockstate.variant(outer_side_id, true, R0, R180))
			.with(When.anyOf(
				GBlockstate.when(EDGE, WEST_NORTH, STAIR_SHAPE, SECOND_OUTER_LEFT),
				GBlockstate.when(EDGE, UP_WEST, STAIR_SHAPE, FIRST_OUTER_LEFT)),
				GBlockstate.variant(outer_side_id, true, R90, R180))
			.with(When.anyOf(
				GBlockstate.when(EDGE, SOUTH_WEST, STAIR_SHAPE, FIRST_OUTER_LEFT),
				GBlockstate.when(EDGE, UP_WEST, STAIR_SHAPE, FIRST_OUTER_RIGHT)),
				GBlockstate.variant(outer_side_id, true, R180, R180))
			.with(When.anyOf(
				GBlockstate.when(EDGE, SOUTH_WEST, STAIR_SHAPE, FIRST_OUTER_RIGHT),
				GBlockstate.when(EDGE, WEST_DOWN, STAIR_SHAPE, SECOND_OUTER_RIGHT)),
				GBlockstate.variant(outer_side_id, true, R270, R180))
			/* OUTER NORTH */
			.with(When.anyOf(
				GBlockstate.when(EDGE, NORTH_EAST, STAIR_SHAPE, SECOND_OUTER_RIGHT),
				GBlockstate.when(EDGE, NORTH_DOWN, STAIR_SHAPE, SECOND_OUTER_RIGHT)),
				GBlockstate.variant(outer_side_id, true, R0, R270))
			.with(When.anyOf(
				GBlockstate.when(EDGE, NORTH_EAST, STAIR_SHAPE, SECOND_OUTER_LEFT),
				GBlockstate.when(EDGE, UP_NORTH, STAIR_SHAPE, FIRST_OUTER_RIGHT)),
				GBlockstate.variant(outer_side_id, true, R90, R270))
			.with(When.anyOf(
				GBlockstate.when(EDGE, WEST_NORTH, STAIR_SHAPE, FIRST_OUTER_LEFT),
				GBlockstate.when(EDGE, UP_NORTH, STAIR_SHAPE, FIRST_OUTER_LEFT)),
				GBlockstate.variant(outer_side_id, true, R180, R270))
			.with(When.anyOf(
				GBlockstate.when(EDGE, WEST_NORTH, STAIR_SHAPE, FIRST_OUTER_RIGHT),
				GBlockstate.when(EDGE, NORTH_DOWN, STAIR_SHAPE, SECOND_OUTER_LEFT)),
				GBlockstate.variant(outer_side_id, true, R270, R270))
			/* OUTER BOTTOM */
			.with(When.anyOf(
				GBlockstate.when(EDGE, DOWN_SOUTH, STAIR_SHAPE, OUTER_RIGHT),
				GBlockstate.when(EDGE, DOWN_EAST, STAIR_SHAPE, OUTER_RIGHT),
				GBlockstate.when(EDGE, EAST_SOUTH, STAIR_SHAPE, OUTER_RIGHT)),
				GBlockstate.variant(double_outer_id, true, R0, R0))
			.with(When.anyOf(
				GBlockstate.when(EDGE, DOWN_SOUTH, STAIR_SHAPE, OUTER_LEFT),
				GBlockstate.when(EDGE, WEST_DOWN, STAIR_SHAPE, OUTER_RIGHT),
				GBlockstate.when(EDGE, SOUTH_WEST, STAIR_SHAPE, OUTER_RIGHT)),
				GBlockstate.variant(double_outer_id, true, R0, R90))
			.with(When.anyOf(
				GBlockstate.when(EDGE, NORTH_DOWN, STAIR_SHAPE, OUTER_LEFT),
				GBlockstate.when(EDGE, WEST_DOWN, STAIR_SHAPE, OUTER_LEFT),
				GBlockstate.when(EDGE, WEST_NORTH, STAIR_SHAPE, OUTER_RIGHT)),
				GBlockstate.variant(double_outer_id, true, R0, R180))
			.with(When.anyOf(
				GBlockstate.when(EDGE, NORTH_DOWN, STAIR_SHAPE, OUTER_RIGHT),
				GBlockstate.when(EDGE, DOWN_EAST, STAIR_SHAPE, OUTER_LEFT),
				GBlockstate.when(EDGE, NORTH_EAST, STAIR_SHAPE, OUTER_RIGHT)),
				GBlockstate.variant(double_outer_id, true, R0, R270))
			/* OUTER TOP */
			.with(When.anyOf(
				GBlockstate.when(EDGE, UP_NORTH, STAIR_SHAPE, OUTER_RIGHT),
				GBlockstate.when(EDGE, EAST_UP, STAIR_SHAPE, OUTER_LEFT),
				GBlockstate.when(EDGE, NORTH_EAST, STAIR_SHAPE, OUTER_LEFT)),
				GBlockstate.variant(double_outer_id, true, R180, R0))
			.with(When.anyOf(
				GBlockstate.when(EDGE, SOUTH_UP, STAIR_SHAPE, OUTER_RIGHT),
				GBlockstate.when(EDGE, EAST_UP, STAIR_SHAPE, OUTER_RIGHT),
				GBlockstate.when(EDGE, EAST_SOUTH, STAIR_SHAPE, OUTER_LEFT)),
				GBlockstate.variant(double_outer_id, true, R180, R90))
			.with(When.anyOf(
				GBlockstate.when(EDGE, SOUTH_UP, STAIR_SHAPE, OUTER_LEFT),
				GBlockstate.when(EDGE, UP_WEST, STAIR_SHAPE, OUTER_RIGHT),
				GBlockstate.when(EDGE, SOUTH_WEST, STAIR_SHAPE, OUTER_LEFT)),
				GBlockstate.variant(double_outer_id, true, R180, R180))
			.with(When.anyOf(
				GBlockstate.when(EDGE, UP_NORTH, STAIR_SHAPE, OUTER_LEFT),
				GBlockstate.when(EDGE, UP_WEST, STAIR_SHAPE, OUTER_LEFT),
				GBlockstate.when(EDGE, WEST_NORTH, STAIR_SHAPE, OUTER_LEFT)),
				GBlockstate.variant(double_outer_id, true, R180, R270));
	}

	@Override
	public void setRecipe(RecipeExporter exporter) {
		RecipeProvider.offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, this, ReFramed.CUBE);
		ShapedRecipeJsonBuilder
			.create(RecipeCategory.BUILDING_BLOCKS, this, 4)
			.pattern("I  ")
			.pattern("II ")
			.pattern("III")
			.input('I', ReFramed.CUBE)
			.criterion(FabricRecipeProvider.hasItem(ReFramed.CUBE), FabricRecipeProvider.conditionsFromItem(ReFramed.CUBE))
			.criterion(FabricRecipeProvider.hasItem(this), FabricRecipeProvider.conditionsFromItem(this))
			.offerTo(exporter);
	}

	static {
		final VoxelShape STRAIGHT = Stream.of(
			VoxelShapes.cuboid(0.0f, 0.0f, 0.0f, 1.0f, 0.5f, 1.0f),
			VoxelShapes.cuboid(0.0f, 0.5f, 0.5f, 1.0f, 1.0f, 1.0f)
		).reduce((previous, current) -> VoxelShapes.combineAndSimplify(previous, current, BooleanBiFunction.OR)).get();
		final VoxelShape JUNCTION = Stream.of(
			VoxelShapes.cuboid(0.0f, 0.0f, 0.0f, 1.0f, 0.5f, 1.0f),
			VoxelShapes.cuboid(0.5f, 0.5f, 0.5f, 1.0f, 1.0f, 1.0f)
		).reduce((previous, current) -> VoxelShapes.combineAndSimplify(previous, current, BooleanBiFunction.OR)).get();
		final VoxelShape OUTER = Stream.of(
			VoxelShapes.cuboid(0.5f, 0.5f, 0.5f, 1.0f, 1.0f, 1.0f),
			VoxelShapes.cuboid(0.5f, 0.5f, 0.0f, 1.0f, 1.0f, 0.5f),
			VoxelShapes.cuboid(0.0f, 0.5f, 0.5f, 0.5f, 1.0f, 1.0f),
			VoxelShapes.cuboid(0.5f, 0.0f, 0.5f, 1.0f, 0.5f, 1.0f)
		).reduce((previous, current) -> VoxelShapes.combineAndSimplify(previous, current, BooleanBiFunction.OR)).get();
		final VoxelShape INNER = Stream.of(
			VoxelShapes.cuboid(0.0f, 0.0f, 0.0f, 1.0f, 0.5f, 1.0f),
			VoxelShapes.cuboid(0.0f, 0.5f, 0.0f, 0.5f, 1.0f, 1.0f),
			VoxelShapes.cuboid(0.5f, 0.5f, 0.0f, 1.0f, 1.0f, 0.5f)
		).reduce((previous, current) -> VoxelShapes.combineAndSimplify(previous, current, BooleanBiFunction.OR)).get();

		VOXEL_LIST.add(STRAIGHT);
		VOXEL_LIST.add(VoxelHelper.mirror(STRAIGHT, Axis.Z));
		VOXEL_LIST.add(VoxelHelper.mirror(VoxelHelper.rotateCounterClockwise(STRAIGHT, Axis.X), Axis.Z));
		VOXEL_LIST.add(VoxelHelper.rotateCounterClockwise(STRAIGHT, Axis.X));

		VOXEL_LIST.add(VoxelHelper.rotateCounterClockwise(STRAIGHT, Axis.Y));
		VOXEL_LIST.add(VoxelHelper.mirror(VoxelHelper.rotateCounterClockwise(STRAIGHT, Axis.Y), Axis.X));
		VOXEL_LIST.add(VoxelHelper.mirror(VoxelHelper.rotateClockwise(VoxelHelper.rotateCounterClockwise(STRAIGHT, Axis.Y), Axis.Z), Axis.X));
		VOXEL_LIST.add(VoxelHelper.rotateClockwise(VoxelHelper.rotateCounterClockwise(STRAIGHT, Axis.Y), Axis.Z));

		VOXEL_LIST.add(VoxelHelper.rotateCounterClockwise(VoxelHelper.rotateClockwise(STRAIGHT, Axis.Z), Axis.Y));
		VOXEL_LIST.add(VoxelHelper.rotateClockwise(STRAIGHT, Axis.Z));
		VOXEL_LIST.add(VoxelHelper.rotateClockwise(VoxelHelper.rotateClockwise(STRAIGHT, Axis.Z), Axis.Y));
		VOXEL_LIST.add(VoxelHelper.mirror(VoxelHelper.rotateClockwise(VoxelHelper.rotateClockwise(STRAIGHT, Axis.Z), Axis.Y), Axis.Z));

		VOXEL_LIST.add(JUNCTION);
		VOXEL_LIST.add(VoxelHelper.rotateClockwise(JUNCTION, Axis.Y));
		VOXEL_LIST.add(VoxelHelper.rotateClockwise(VoxelHelper.rotateClockwise(JUNCTION, Axis.Y), Axis.Y));
		VOXEL_LIST.add(VoxelHelper.rotateCounterClockwise(JUNCTION, Axis.Y));

		VOXEL_LIST.add(VoxelHelper.mirror(JUNCTION, Axis.Y));
		VOXEL_LIST.add(VoxelHelper.rotateClockwise(VoxelHelper.mirror(JUNCTION, Axis.Y), Axis.Y));
		VOXEL_LIST.add(VoxelHelper.rotateClockwise(VoxelHelper.rotateClockwise(VoxelHelper.mirror(JUNCTION, Axis.Y), Axis.Y), Axis.Y));
		VOXEL_LIST.add(VoxelHelper.rotateCounterClockwise(VoxelHelper.mirror(JUNCTION, Axis.Y), Axis.Y));

		VOXEL_LIST.add(VoxelHelper.rotateCounterClockwise(JUNCTION, Axis.X));
		VOXEL_LIST.add(VoxelHelper.rotateClockwise(VoxelHelper.rotateCounterClockwise(JUNCTION, Axis.X), Axis.Z));
		VOXEL_LIST.add(VoxelHelper.rotateClockwise(VoxelHelper.rotateClockwise(VoxelHelper.rotateCounterClockwise(JUNCTION, Axis.X), Axis.Z), Axis.Z));
		VOXEL_LIST.add(VoxelHelper.rotateCounterClockwise(VoxelHelper.rotateCounterClockwise(JUNCTION, Axis.X), Axis.Z));

		VOXEL_LIST.add(VoxelHelper.mirror(VoxelHelper.rotateCounterClockwise(JUNCTION, Axis.X), Axis.Z));
		VOXEL_LIST.add(VoxelHelper.rotateClockwise(VoxelHelper.mirror(VoxelHelper.rotateCounterClockwise(JUNCTION, Axis.X), Axis.Z), Axis.Z));
		VOXEL_LIST.add(VoxelHelper.rotateClockwise(VoxelHelper.rotateClockwise(VoxelHelper.mirror(VoxelHelper.rotateCounterClockwise(JUNCTION, Axis.X), Axis.Z), Axis.Z), Axis.Z));
		VOXEL_LIST.add(VoxelHelper.rotateCounterClockwise(VoxelHelper.mirror(VoxelHelper.rotateCounterClockwise(JUNCTION, Axis.X), Axis.Z), Axis.Z));

		VOXEL_LIST.add(VoxelHelper.rotateClockwise(VoxelHelper.rotateCounterClockwise(JUNCTION, Axis.X), Axis.Y));
		VOXEL_LIST.add(VoxelHelper.rotateClockwise(VoxelHelper.rotateClockwise(VoxelHelper.rotateCounterClockwise(JUNCTION, Axis.X), Axis.Y), Axis.X));
		VOXEL_LIST.add(VoxelHelper.rotateClockwise(VoxelHelper.rotateClockwise(VoxelHelper.rotateClockwise(VoxelHelper.rotateCounterClockwise(JUNCTION, Axis.X), Axis.Y), Axis.X), Axis.X));
		VOXEL_LIST.add(VoxelHelper.rotateCounterClockwise(VoxelHelper.rotateClockwise(VoxelHelper.rotateCounterClockwise(JUNCTION, Axis.X), Axis.Y), Axis.X));

		VOXEL_LIST.add(VoxelHelper.mirror(VoxelHelper.rotateClockwise(VoxelHelper.rotateCounterClockwise(JUNCTION, Axis.X), Axis.Y), Axis.X));
		VOXEL_LIST.add(VoxelHelper.rotateClockwise(VoxelHelper.mirror(VoxelHelper.rotateClockwise(VoxelHelper.rotateCounterClockwise(JUNCTION, Axis.X), Axis.Y), Axis.X), Axis.X));
		VOXEL_LIST.add(VoxelHelper.rotateClockwise(VoxelHelper.rotateClockwise(VoxelHelper.mirror(VoxelHelper.rotateClockwise(VoxelHelper.rotateCounterClockwise(JUNCTION, Axis.X), Axis.Y), Axis.X), Axis.X), Axis.X));
		VOXEL_LIST.add(VoxelHelper.rotateCounterClockwise(VoxelHelper.mirror(VoxelHelper.rotateClockwise(VoxelHelper.rotateCounterClockwise(JUNCTION, Axis.X), Axis.Y), Axis.X), Axis.X));

		VOXEL_LIST.add(OUTER);
		VOXEL_LIST.add(VoxelHelper.rotateClockwise(OUTER, Axis.Y));
		VOXEL_LIST.add(VoxelHelper.rotateCounterClockwise(VoxelHelper.rotateCounterClockwise(OUTER, Axis.Y), Axis.Y));
		VOXEL_LIST.add(VoxelHelper.rotateCounterClockwise(OUTER, Axis.Y));

		VOXEL_LIST.add(VoxelHelper.mirror(OUTER, Axis.Y));
		VOXEL_LIST.add(VoxelHelper.rotateClockwise(VoxelHelper.mirror(OUTER, Axis.Y), Axis.Y));
		VOXEL_LIST.add(VoxelHelper.rotateCounterClockwise(VoxelHelper.rotateCounterClockwise(VoxelHelper.mirror(OUTER, Axis.Y), Axis.Y), Axis.Y));
		VOXEL_LIST.add(VoxelHelper.rotateCounterClockwise(VoxelHelper.mirror(OUTER, Axis.Y), Axis.Y));

		VOXEL_LIST.add(INNER);
		VOXEL_LIST.add(VoxelHelper.rotateClockwise(INNER, Axis.Y));
		VOXEL_LIST.add(VoxelHelper.rotateCounterClockwise(VoxelHelper.rotateCounterClockwise(INNER, Axis.Y), Axis.Y));
		VOXEL_LIST.add(VoxelHelper.rotateCounterClockwise(INNER, Axis.Y));

		VOXEL_LIST.add(VoxelHelper.mirror(INNER, Axis.Y));
		VOXEL_LIST.add(VoxelHelper.rotateClockwise(VoxelHelper.mirror(INNER, Axis.Y), Axis.Y));
		VOXEL_LIST.add(VoxelHelper.rotateCounterClockwise(VoxelHelper.rotateCounterClockwise(VoxelHelper.mirror(INNER, Axis.Y), Axis.Y), Axis.Y));
		VOXEL_LIST.add(VoxelHelper.rotateCounterClockwise(VoxelHelper.mirror(INNER, Axis.Y), Axis.Y));
	}
}
