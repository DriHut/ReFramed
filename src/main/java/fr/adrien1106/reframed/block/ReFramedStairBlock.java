package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.generator.BlockStateProvider;
import fr.adrien1106.reframed.generator.GBlockstate;
import fr.adrien1106.reframed.util.VoxelHelper;
import fr.adrien1106.reframed.util.blocks.BlockHelper;
import fr.adrien1106.reframed.util.blocks.Edge;
import fr.adrien1106.reframed.util.blocks.StairShape;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.data.client.MultipartBlockStateSupplier;
import net.minecraft.data.client.When;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.state.StateManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.stream.Stream;

import static fr.adrien1106.reframed.util.VoxelHelper.VoxelListBuilder;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.EDGE;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.STAIR_SHAPE;
import static fr.adrien1106.reframed.util.blocks.Edge.*;
import static fr.adrien1106.reframed.util.blocks.StairShape.*;
import static net.minecraft.data.client.VariantSettings.Rotation.*;

public class ReFramedStairBlock extends WaterloggableReFramedBlock implements BlockStateProvider {
	public static final VoxelShape[] STAIR_VOXELS;
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
			.with(STAIR_SHAPE, BlockHelper.getStairsShape(state.get(EDGE), world, pos));
	}

	@Nullable
	@Override // Pretty happy of how clean it is (also got it on first try :) )
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		Edge face = BlockHelper.getPlacementEdge(ctx);
		StairShape shape = BlockHelper.getStairsShape(face, ctx.getWorld(), ctx.getBlockPos());
		return super.getPlacementState(ctx).with(EDGE, face).with(STAIR_SHAPE, shape);
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		super.onStateReplaced(state, world, pos, newState, moved);

		if(!state.isOf(newState.getBlock())) world.removeBlockEntity(pos);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return getStairShape(state.get(EDGE), state.get(STAIR_SHAPE));
	}

	public static VoxelShape getStairShape(Edge edge, StairShape shape) {
		return STAIR_VOXELS[edge.getID() * 9 + shape.getID()];
	}

	@Override
	public MultipartBlockStateSupplier getMultipart() {
		return getStairMultipart(this, false);
	}

	public static MultipartBlockStateSupplier getStairMultipart(Block block, boolean is_double) {
		String infix = is_double ? "s_cube" : "";
		Identifier straight_id = ReFramed.id("stair" + infix + "_special");
		Identifier double_outer_id = ReFramed.id("outers_stair" + infix + "_special");
		Identifier inner_id = ReFramed.id("inner_stair" + infix + "_special");
		Identifier outer_id = ReFramed.id("outer_stair" + infix + "_special");
		Identifier outer_side_id = ReFramed.id("outer_side_stair" + infix + "_special");
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
				GBlockstate.when(EDGE, NORTH_DOWN, STAIR_SHAPE, INNER_RIGHT),
				GBlockstate.when(EDGE, WEST_NORTH, STAIR_SHAPE, INNER_RIGHT),
			    GBlockstate.when(EDGE, WEST_DOWN, STAIR_SHAPE, INNER_LEFT)),
				GBlockstate.variant(inner_id, true, R0, R180))
			.with(When.anyOf(
				GBlockstate.when(EDGE, NORTH_DOWN, STAIR_SHAPE, INNER_LEFT),
				GBlockstate.when(EDGE, NORTH_EAST, STAIR_SHAPE, INNER_RIGHT),
				GBlockstate.when(EDGE, DOWN_EAST, STAIR_SHAPE, INNER_LEFT)),
				GBlockstate.variant(inner_id, true, R0, R270))
			.with(When.anyOf(
				GBlockstate.when(EDGE, DOWN_SOUTH, STAIR_SHAPE, INNER_LEFT),
				GBlockstate.when(EDGE, EAST_SOUTH, STAIR_SHAPE, INNER_RIGHT),
				GBlockstate.when(EDGE, DOWN_EAST, STAIR_SHAPE, INNER_RIGHT)),
				GBlockstate.variant(inner_id, true, R0, R0))
			.with(When.anyOf(
				GBlockstate.when(EDGE, DOWN_SOUTH, STAIR_SHAPE, INNER_RIGHT),
				GBlockstate.when(EDGE, SOUTH_WEST, STAIR_SHAPE, INNER_RIGHT),
				GBlockstate.when(EDGE, WEST_DOWN, STAIR_SHAPE, INNER_RIGHT)),
				GBlockstate.variant(inner_id, true, R0, R90))
			/* INNER TOP */
			.with(When.anyOf(
				GBlockstate.when(EDGE, EAST_UP, STAIR_SHAPE, INNER_LEFT),
				GBlockstate.when(EDGE, NORTH_EAST, STAIR_SHAPE, INNER_LEFT),
				GBlockstate.when(EDGE, UP_NORTH, STAIR_SHAPE, INNER_LEFT)),
				GBlockstate.variant(inner_id, true, R180, R0))
			.with(When.anyOf(
				GBlockstate.when(EDGE, EAST_UP, STAIR_SHAPE, INNER_RIGHT),
				GBlockstate.when(EDGE, EAST_SOUTH, STAIR_SHAPE, INNER_LEFT),
				GBlockstate.when(EDGE, SOUTH_UP, STAIR_SHAPE, INNER_LEFT)),
				GBlockstate.variant(inner_id, true, R180, R90))
			.with(When.anyOf(
				GBlockstate.when(EDGE, SOUTH_UP, STAIR_SHAPE, INNER_RIGHT),
				GBlockstate.when(EDGE, SOUTH_WEST, STAIR_SHAPE, INNER_LEFT),
				GBlockstate.when(EDGE, UP_WEST, STAIR_SHAPE, INNER_RIGHT)),
				GBlockstate.variant(inner_id, true, R180, R180))
			.with(When.anyOf(
				GBlockstate.when(EDGE, UP_NORTH, STAIR_SHAPE, INNER_RIGHT),
				GBlockstate.when(EDGE, WEST_NORTH, STAIR_SHAPE, INNER_LEFT),
				GBlockstate.when(EDGE, UP_WEST, STAIR_SHAPE, INNER_LEFT)),
				GBlockstate.variant(inner_id, true, R180, R270))
			/* OUTER BOTTOM */
			.with(When.anyOf(
				GBlockstate.when(EDGE, DOWN_SOUTH, STAIR_SHAPE, SECOND_OUTER_LEFT),
				GBlockstate.when(EDGE, DOWN_EAST, STAIR_SHAPE, SECOND_OUTER_RIGHT)),
				GBlockstate.variant(outer_id, true, R0, R0))
			.with(When.anyOf(
				GBlockstate.when(EDGE, DOWN_SOUTH, STAIR_SHAPE, SECOND_OUTER_RIGHT),
				GBlockstate.when(EDGE, WEST_DOWN, STAIR_SHAPE, FIRST_OUTER_RIGHT)),
				GBlockstate.variant(outer_id, true, R0, R90))
			.with(When.anyOf(
				GBlockstate.when(EDGE, NORTH_DOWN, STAIR_SHAPE, FIRST_OUTER_RIGHT),
				GBlockstate.when(EDGE, WEST_DOWN, STAIR_SHAPE, FIRST_OUTER_LEFT)),
				GBlockstate.variant(outer_id, true, R0, R180))
			.with(When.anyOf(
				GBlockstate.when(EDGE, NORTH_DOWN, STAIR_SHAPE, FIRST_OUTER_LEFT),
				GBlockstate.when(EDGE, DOWN_EAST, STAIR_SHAPE, SECOND_OUTER_LEFT)),
				GBlockstate.variant(outer_id, true, R0, R270))
			/* OUTER TOP */
			.with(When.anyOf(
				GBlockstate.when(EDGE, UP_NORTH, STAIR_SHAPE, SECOND_OUTER_LEFT),
				GBlockstate.when(EDGE, EAST_UP, STAIR_SHAPE, FIRST_OUTER_LEFT)),
				GBlockstate.variant(outer_id, true, R180, R0))
			.with(When.anyOf(
				GBlockstate.when(EDGE, SOUTH_UP, STAIR_SHAPE, FIRST_OUTER_LEFT),
				GBlockstate.when(EDGE, EAST_UP, STAIR_SHAPE, FIRST_OUTER_RIGHT)),
				GBlockstate.variant(outer_id, true, R180, R90))
			.with(When.anyOf(
				GBlockstate.when(EDGE, SOUTH_UP, STAIR_SHAPE, FIRST_OUTER_RIGHT),
				GBlockstate.when(EDGE, UP_WEST, STAIR_SHAPE, SECOND_OUTER_RIGHT)),
				GBlockstate.variant(outer_id, true, R180, R180))
			.with(When.anyOf(
				GBlockstate.when(EDGE, UP_NORTH, STAIR_SHAPE, SECOND_OUTER_RIGHT),
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
				GBlockstate.when(EDGE, DOWN_SOUTH, STAIR_SHAPE, FIRST_OUTER_RIGHT)),
				GBlockstate.variant(outer_side_id, true, R0, R90))
			.with(When.anyOf(
				GBlockstate.when(EDGE, SOUTH_WEST, STAIR_SHAPE, SECOND_OUTER_LEFT),
				GBlockstate.when(EDGE, SOUTH_UP, STAIR_SHAPE, SECOND_OUTER_RIGHT)),
				GBlockstate.variant(outer_side_id, true, R90, R90))
			.with(When.anyOf(
				GBlockstate.when(EDGE, EAST_SOUTH, STAIR_SHAPE, FIRST_OUTER_LEFT),
				GBlockstate.when(EDGE, SOUTH_UP, STAIR_SHAPE, SECOND_OUTER_LEFT)),
				GBlockstate.variant(outer_side_id, true, R180, R90))
			.with(When.anyOf(
				GBlockstate.when(EDGE, EAST_SOUTH, STAIR_SHAPE, FIRST_OUTER_RIGHT),
				GBlockstate.when(EDGE, DOWN_SOUTH, STAIR_SHAPE, FIRST_OUTER_LEFT)),
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
				GBlockstate.when(EDGE, NORTH_DOWN, STAIR_SHAPE, SECOND_OUTER_LEFT)),
				GBlockstate.variant(outer_side_id, true, R0, R270))
			.with(When.anyOf(
				GBlockstate.when(EDGE, NORTH_EAST, STAIR_SHAPE, SECOND_OUTER_LEFT),
				GBlockstate.when(EDGE, UP_NORTH, STAIR_SHAPE, FIRST_OUTER_LEFT)),
				GBlockstate.variant(outer_side_id, true, R90, R270))
			.with(When.anyOf(
				GBlockstate.when(EDGE, WEST_NORTH, STAIR_SHAPE, FIRST_OUTER_LEFT),
				GBlockstate.when(EDGE, UP_NORTH, STAIR_SHAPE, FIRST_OUTER_RIGHT)),
				GBlockstate.variant(outer_side_id, true, R180, R270))
			.with(When.anyOf(
				GBlockstate.when(EDGE, WEST_NORTH, STAIR_SHAPE, FIRST_OUTER_RIGHT),
				GBlockstate.when(EDGE, NORTH_DOWN, STAIR_SHAPE, SECOND_OUTER_RIGHT)),
				GBlockstate.variant(outer_side_id, true, R270, R270))
			/* OUTER BOTTOM */
			.with(When.anyOf(
				GBlockstate.when(EDGE, DOWN_SOUTH, STAIR_SHAPE, OUTER_LEFT),
				GBlockstate.when(EDGE, DOWN_EAST, STAIR_SHAPE, OUTER_RIGHT),
				GBlockstate.when(EDGE, EAST_SOUTH, STAIR_SHAPE, OUTER_RIGHT)),
				GBlockstate.variant(double_outer_id, true, R0, R0))
			.with(When.anyOf(
				GBlockstate.when(EDGE, DOWN_SOUTH, STAIR_SHAPE, OUTER_RIGHT),
				GBlockstate.when(EDGE, WEST_DOWN, STAIR_SHAPE, OUTER_RIGHT),
				GBlockstate.when(EDGE, SOUTH_WEST, STAIR_SHAPE, OUTER_RIGHT)),
				GBlockstate.variant(double_outer_id, true, R0, R90))
			.with(When.anyOf(
				GBlockstate.when(EDGE, NORTH_DOWN, STAIR_SHAPE, OUTER_RIGHT),
				GBlockstate.when(EDGE, WEST_DOWN, STAIR_SHAPE, OUTER_LEFT),
				GBlockstate.when(EDGE, WEST_NORTH, STAIR_SHAPE, OUTER_RIGHT)),
				GBlockstate.variant(double_outer_id, true, R0, R180))
			.with(When.anyOf(
				GBlockstate.when(EDGE, NORTH_DOWN, STAIR_SHAPE, OUTER_LEFT),
				GBlockstate.when(EDGE, DOWN_EAST, STAIR_SHAPE, OUTER_LEFT),
				GBlockstate.when(EDGE, NORTH_EAST, STAIR_SHAPE, OUTER_RIGHT)),
				GBlockstate.variant(double_outer_id, true, R0, R270))
			/* OUTER TOP */
			.with(When.anyOf(
				GBlockstate.when(EDGE, UP_NORTH, STAIR_SHAPE, OUTER_LEFT),
				GBlockstate.when(EDGE, EAST_UP, STAIR_SHAPE, OUTER_LEFT),
				GBlockstate.when(EDGE, NORTH_EAST, STAIR_SHAPE, OUTER_LEFT)),
				GBlockstate.variant(double_outer_id, true, R180, R0))
			.with(When.anyOf(
				GBlockstate.when(EDGE, SOUTH_UP, STAIR_SHAPE, OUTER_LEFT),
				GBlockstate.when(EDGE, EAST_UP, STAIR_SHAPE, OUTER_RIGHT),
				GBlockstate.when(EDGE, EAST_SOUTH, STAIR_SHAPE, OUTER_LEFT)),
				GBlockstate.variant(double_outer_id, true, R180, R90))
			.with(When.anyOf(
				GBlockstate.when(EDGE, SOUTH_UP, STAIR_SHAPE, OUTER_RIGHT),
				GBlockstate.when(EDGE, UP_WEST, STAIR_SHAPE, OUTER_RIGHT),
				GBlockstate.when(EDGE, SOUTH_WEST, STAIR_SHAPE, OUTER_LEFT)),
				GBlockstate.variant(double_outer_id, true, R180, R180))
			.with(When.anyOf(
				GBlockstate.when(EDGE, UP_NORTH, STAIR_SHAPE, OUTER_RIGHT),
				GBlockstate.when(EDGE, UP_WEST, STAIR_SHAPE, OUTER_LEFT),
				GBlockstate.when(EDGE, WEST_NORTH, STAIR_SHAPE, OUTER_LEFT)),
				GBlockstate.variant(double_outer_id, true, R180, R270));
	}

	@Override
	public void setRecipe(Consumer<RecipeJsonProvider> exporter) {
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
		final VoxelShape STRAIGHT = VoxelShapes.combineAndSimplify(
			createCuboidShape(0, 8, 0, 16, 16, 8),
			createCuboidShape(0, 0, 0, 16, 8, 16),
			BooleanBiFunction.OR
		);
		final VoxelShape INNER = Stream.of(
			createCuboidShape(0, 8, 0, 16, 16, 8),
			createCuboidShape(0, 8, 8, 8, 16, 16),
			createCuboidShape(0, 0, 0, 16, 8, 16)
		).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();
		final VoxelShape OUTER = Stream.of(
			createCuboidShape(0, 8, 0, 8, 16, 8),
			createCuboidShape(8, 0, 0, 16, 8, 8),
			createCuboidShape(0, 0, 0, 8, 8, 16)
		).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();
		final VoxelShape JUNCTION = VoxelShapes.combineAndSimplify(
			createCuboidShape(0, 8, 0, 8, 16, 8),
			createCuboidShape(0, 0, 0, 16, 8, 16),
			BooleanBiFunction.OR
		);

		STAIR_VOXELS = VoxelListBuilder.create(STRAIGHT, 108)
			.add(INNER).add(VoxelHelper::rotateY)
			.add(OUTER).add(VoxelHelper::rotateY)
			.add(JUNCTION).add(VoxelHelper::rotateY)
			.add(JUNCTION, VoxelHelper::rotateX, VoxelHelper::rotateZ).add(VoxelHelper::rotateZ)
			// DOWN_SOUTH
			.add(0, VoxelHelper::rotateCX)
			.add(1, VoxelHelper::rotateCX).add(2, VoxelHelper::rotateCX)
			.add(3, VoxelHelper::rotateCX).add(4, VoxelHelper::rotateCX)
			.add(5, VoxelHelper::rotateCX).add(6, VoxelHelper::rotateCX)
			.add(7, VoxelHelper::rotateCX).add(8, VoxelHelper::rotateCX)
			// SOUTH_UP
			.add(9, VoxelHelper::rotateCX)
			.add(10, VoxelHelper::rotateCX).add(11, VoxelHelper::rotateCX)
			.add(12, VoxelHelper::rotateCX).add(13, VoxelHelper::rotateCX)
			.add(14, VoxelHelper::rotateCX).add(15, VoxelHelper::rotateCX)
			.add(16, VoxelHelper::rotateCX).add(17, VoxelHelper::rotateCX)
			// UP_NORTH
			.add(18, VoxelHelper::rotateCX)
			.add(19, VoxelHelper::rotateCX).add(20, VoxelHelper::rotateCX)
			.add(21, VoxelHelper::rotateCX).add(22, VoxelHelper::rotateCX)
			.add(23, VoxelHelper::rotateCX).add(24, VoxelHelper::rotateCX)
			.add(25, VoxelHelper::rotateCX).add(26, VoxelHelper::rotateCX)
			// WEST_DOWN
			.add(0, VoxelHelper::rotateCY)
			.add(10).add(1)
			.add(12).add(3)
			.add(16).add(5)
			.add(7, VoxelHelper::rotateCY).add(8, VoxelHelper::rotateCY)
			// DOWN_EAST
			.add(36, VoxelHelper::rotateZ)
			.add(11).add(2)
			.add(13).add(4)
			.add(41, VoxelHelper::rotateZ).add(42, VoxelHelper::rotateZ)
			.add(17).add(6)
			// EAST_UP
			.add(45, VoxelHelper::rotateZ)
			.add(20).add(29)
			.add(22).add(31)
			.add(24).add(35)
			.add(52, VoxelHelper::rotateZ).add(53, VoxelHelper::rotateZ)
			// UP_WEST
			.add(54, VoxelHelper::rotateZ)
			.add(19).add(28)
			.add(21).add(30)
			.add(59, VoxelHelper::rotateZ).add(60, VoxelHelper::rotateZ)
			.add(23).add(34)
			// WEST_NORTH
			.add(0, VoxelHelper::rotateCZ)
			.add(1).add(28)
			.add(3).add(30)
			.add(7).add(32)
			.add(44).add(69)
			// NORTH_EAST
			.add(72, VoxelHelper::rotateY)
			.add(2).add(29)
			.add(4).add(31)
			.add(51).add(62)
			.add(8).add(33)
			// EAST_SOUTH
			.add(81, VoxelHelper::rotateY)
			.add(11).add(20)
			.add(13).add(22)
			.add(15).add(26)
			.add(50).add(61)
			// SOUTH_WEST
			.add(90, VoxelHelper::rotateY)
			.add(10).add(19)
			.add(12).add(21)
			.add(43).add(68)
			.add(14).add(25)
			.build();
	}
}
