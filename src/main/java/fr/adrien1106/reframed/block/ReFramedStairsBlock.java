package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.generator.GBlockstate;
import fr.adrien1106.reframed.generator.MultipartBlockStateProvider;
import fr.adrien1106.reframed.util.VoxelHelper;
import fr.adrien1106.reframed.util.property.StairDirection;
import fr.adrien1106.reframed.util.property.StairShape;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.data.client.*;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.Identifier;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static fr.adrien1106.reframed.util.property.StairShape.*;
import static net.minecraft.data.client.VariantSettings.Rotation.*;
import static fr.adrien1106.reframed.util.property.StairDirection.*;

public class ReFramedStairsBlock extends WaterloggableReFramedBlock implements MultipartBlockStateProvider {

	public static final EnumProperty<StairDirection> FACING = EnumProperty.of("facing", StairDirection.class);
	public static final EnumProperty<StairShape> SHAPE = EnumProperty.of("shape", StairShape.class);
	private static final List<VoxelShape> VOXEL_LIST = new ArrayList<>(52);

	public ReFramedStairsBlock(Settings settings) {
		super(settings);
		setDefaultState(getDefaultState().with(FACING, StairDirection.NORTH_DOWN).with(SHAPE, STRAIGHT));
	}
	
	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder.add(FACING).add(SHAPE));
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighbor_state, WorldAccess world, BlockPos pos, BlockPos moved) {
		return super.getStateForNeighborUpdate(state, direction, neighbor_state, world, pos, moved)
			.with(SHAPE, getPlacementShape(state.get(FACING), world, pos));
	}

	@Nullable
	@Override // Pretty happy of how clean it is (also got it on first try :) )
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockState state = super.getPlacementState(ctx);
		Direction side = ctx.getSide().getOpposite();
		BlockPos block_pos = ctx.getBlockPos();
		Vec3d hit_pos = ctx.getHitPos();
		Vec3d pos = new Vec3d(
			hit_pos.getX() - block_pos.getX() - .5d,
			hit_pos.getY() - block_pos.getY() - .5d,
			hit_pos.getZ() - block_pos.getZ() - .5d
		);

		Stream<Axis> axes = Stream.of(Axis.values()).filter(axis -> !axis.equals(side.getAxis()));
		Axis axis = axes.reduce((axis_1, axis_2) ->
			Math.abs(axis_1.choose(pos.x, pos.y, pos.z)) > Math.abs(axis_2.choose(pos.x, pos.y, pos.z))
				? axis_1
				: axis_2
		).get();

		Direction part_direction = Direction.from(
			axis,
			axis.choose(pos.x, pos.y, pos.z) > 0
				? Direction.AxisDirection.POSITIVE
				: Direction.AxisDirection.NEGATIVE
		);
		StairDirection face = StairDirection.getByDirections(side, part_direction);

		return state.with(FACING, face).with(SHAPE, getPlacementShape(face, ctx.getWorld(), block_pos));
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		super.onStateReplaced(state, world, pos, newState, moved);

		//StairsBlock onStateReplaced is Weird! it doesn't delegate to regular block onStateReplaced!
		if(!state.isOf(newState.getBlock())) world.removeBlockEntity(pos);
	}

	/* ---------------------------------- DON'T GO FURTHER IF YOU LIKE HAVING EYES ---------------------------------- */
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		StairShape shape = state.get(SHAPE);
		StairDirection direction = state.get(FACING);
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

	private static String getNeighborPos(StairDirection face, Direction direction, Boolean reverse, Direction reference, BlockView world, BlockPos pos) {
		BlockState block_state = world.getBlockState(
			pos.offset(reverse ? direction.getOpposite() : direction)
		);

		if (block_state.getBlock() instanceof ReFramedStairsBlock && block_state.get(FACING).hasDirection(reference)) {
			if (block_state.get(FACING).hasDirection(face.getLeftDirection())) return "left";
			else if (block_state.get(FACING).hasDirection(face.getRightDirection())) return "right";
		}
		return "";
	}

	private static StairShape getPlacementShape(StairDirection face, BlockView world, BlockPos pos) {
		StairShape shape = STRAIGHT;

		String sol = getNeighborPos(face, face.getFirstDirection(), true, face.getSecondDirection(), world, pos);
		switch (sol) {
			case "right": return INNER_RIGHT;
			case "left": return INNER_LEFT;
		}

		sol = getNeighborPos(face, face.getSecondDirection(), true, face.getFirstDirection(), world, pos);
		switch (sol) {
			case "right": return INNER_RIGHT;
			case "left": return INNER_LEFT;
		}

		sol = getNeighborPos(face, face.getFirstDirection(), false, face.getSecondDirection(), world, pos);
		switch (sol) {
			case "right" -> shape = FIRST_OUTER_RIGHT;
			case "left" -> shape = FIRST_OUTER_LEFT;
		}

		sol = getNeighborPos(face, face.getSecondDirection(), false, face.getFirstDirection(), world, pos);
		switch (sol) {
			case "right" -> {
				if (shape.equals(STRAIGHT)) shape = SECOND_OUTER_RIGHT;
				else if (shape.equals(FIRST_OUTER_RIGHT)) shape = OUTER_RIGHT;
			}
			case "left" -> {
				if (shape.equals(STRAIGHT)) shape = SECOND_OUTER_LEFT;
				else if (shape.equals(FIRST_OUTER_LEFT)) shape = OUTER_LEFT;
			}
		}

		return shape;
	}

	@Override
	public MultipartBlockStateSupplier getMultipart() {
		Identifier straight_id = ReFramed.id("stairs_special");
		Identifier double_outer_id = ReFramed.id("double_outer_stairs_special");
		Identifier inner_id = ReFramed.id("inner_stairs_special");
		Identifier outer_id = ReFramed.id("outer_stairs_special");
		Identifier outer_side_id = ReFramed.id("outer_side_stairs_special");
		return MultipartBlockStateSupplier.create(this)
			/* STRAIGHT X AXIS */
			.with(GBlockstate.when(FACING, DOWN_EAST, SHAPE, STRAIGHT),
				GBlockstate.variant(straight_id, true, R0, R0))
			.with(GBlockstate.when(FACING, EAST_UP, SHAPE, STRAIGHT),
				GBlockstate.variant(straight_id, true, R180, R0))
			.with(GBlockstate.when(FACING, UP_WEST, SHAPE, STRAIGHT),
				GBlockstate.variant(straight_id, true, R180, R180))
			.with(GBlockstate.when(FACING, WEST_DOWN, SHAPE, STRAIGHT),
				GBlockstate.variant(straight_id, true, R0, R180))
			/* STRAIGHT Y AXIS */
			.with(GBlockstate.when(FACING, EAST_SOUTH, SHAPE, STRAIGHT),
				GBlockstate.variant(straight_id, true, R90, R0))
			.with(GBlockstate.when(FACING, SOUTH_WEST, SHAPE, STRAIGHT),
				GBlockstate.variant(straight_id, true, R90, R90))
			.with(GBlockstate.when(FACING, WEST_NORTH, SHAPE, STRAIGHT),
				GBlockstate.variant(straight_id, true, R90, R180))
			.with(GBlockstate.when(FACING, NORTH_EAST, SHAPE, STRAIGHT),
				GBlockstate.variant(straight_id, true, R90, R270))
			/* STRAIGHT Z AXIS */
			.with(GBlockstate.when(FACING, DOWN_SOUTH, SHAPE, STRAIGHT),
				GBlockstate.variant(straight_id, true, R0, R90))
			.with(GBlockstate.when(FACING, NORTH_DOWN, SHAPE, STRAIGHT),
				GBlockstate.variant(straight_id, true, R0, R270))
			.with(GBlockstate.when(FACING, UP_NORTH, SHAPE, STRAIGHT),
				GBlockstate.variant(straight_id, true, R180, R270))
			.with(GBlockstate.when(FACING, SOUTH_UP, SHAPE, STRAIGHT),
				GBlockstate.variant(straight_id, true, R180, R90))
			/* INNER BOTTOM */
			.with(When.anyOf(
				GBlockstate.when(FACING, NORTH_DOWN, SHAPE, INNER_LEFT),
				GBlockstate.when(FACING, WEST_NORTH, SHAPE, INNER_RIGHT),
			    GBlockstate.when(FACING, WEST_DOWN, SHAPE, INNER_LEFT)),
				GBlockstate.variant(inner_id, true, R0, R180))
			.with(When.anyOf(
				GBlockstate.when(FACING, NORTH_DOWN, SHAPE, INNER_RIGHT),
				GBlockstate.when(FACING, NORTH_EAST, SHAPE, INNER_RIGHT),
				GBlockstate.when(FACING, DOWN_EAST, SHAPE, INNER_LEFT)),
				GBlockstate.variant(inner_id, true, R0, R270))
			.with(When.anyOf(
				GBlockstate.when(FACING, DOWN_SOUTH, SHAPE, INNER_RIGHT),
				GBlockstate.when(FACING, EAST_SOUTH, SHAPE, INNER_RIGHT),
				GBlockstate.when(FACING, DOWN_EAST, SHAPE, INNER_RIGHT)),
				GBlockstate.variant(inner_id, true, R0, R0))
			.with(When.anyOf(
				GBlockstate.when(FACING, DOWN_SOUTH, SHAPE, INNER_LEFT),
				GBlockstate.when(FACING, SOUTH_WEST, SHAPE, INNER_RIGHT),
				GBlockstate.when(FACING, WEST_DOWN, SHAPE, INNER_RIGHT)),
				GBlockstate.variant(inner_id, true, R0, R90))
			/* INNER TOP */
			.with(When.anyOf(
				GBlockstate.when(FACING, EAST_UP, SHAPE, INNER_LEFT),
				GBlockstate.when(FACING, NORTH_EAST, SHAPE, INNER_LEFT),
				GBlockstate.when(FACING, UP_NORTH, SHAPE, INNER_RIGHT)),
				GBlockstate.variant(inner_id, true, R180, R0))
			.with(When.anyOf(
				GBlockstate.when(FACING, EAST_UP, SHAPE, INNER_RIGHT),
				GBlockstate.when(FACING, EAST_SOUTH, SHAPE, INNER_LEFT),
				GBlockstate.when(FACING, SOUTH_UP, SHAPE, INNER_RIGHT)),
				GBlockstate.variant(inner_id, true, R180, R90))
			.with(When.anyOf(
				GBlockstate.when(FACING, SOUTH_UP, SHAPE, INNER_LEFT),
				GBlockstate.when(FACING, SOUTH_WEST, SHAPE, INNER_LEFT),
				GBlockstate.when(FACING, UP_WEST, SHAPE, INNER_RIGHT)),
				GBlockstate.variant(inner_id, true, R180, R180))
			.with(When.anyOf(
				GBlockstate.when(FACING, UP_NORTH, SHAPE, INNER_LEFT),
				GBlockstate.when(FACING, WEST_NORTH, SHAPE, INNER_LEFT),
				GBlockstate.when(FACING, UP_WEST, SHAPE, INNER_LEFT)),
				GBlockstate.variant(inner_id, true, R180, R270))
			/* OUTER BOTTOM */
			.with(When.anyOf(
				GBlockstate.when(FACING, DOWN_SOUTH, SHAPE, SECOND_OUTER_RIGHT),
				GBlockstate.when(FACING, DOWN_EAST, SHAPE, SECOND_OUTER_RIGHT)),
				GBlockstate.variant(outer_id, true, R0, R0))
			.with(When.anyOf(
				GBlockstate.when(FACING, DOWN_SOUTH, SHAPE, SECOND_OUTER_LEFT),
				GBlockstate.when(FACING, WEST_DOWN, SHAPE, FIRST_OUTER_RIGHT)),
				GBlockstate.variant(outer_id, true, R0, R90))
			.with(When.anyOf(
				GBlockstate.when(FACING, NORTH_DOWN, SHAPE, FIRST_OUTER_LEFT),
				GBlockstate.when(FACING, WEST_DOWN, SHAPE, FIRST_OUTER_LEFT)),
				GBlockstate.variant(outer_id, true, R0, R180))
			.with(When.anyOf(
				GBlockstate.when(FACING, NORTH_DOWN, SHAPE, FIRST_OUTER_RIGHT),
				GBlockstate.when(FACING, DOWN_EAST, SHAPE, SECOND_OUTER_LEFT)),
				GBlockstate.variant(outer_id, true, R0, R270))
			/* OUTER TOP */
			.with(When.anyOf(
				GBlockstate.when(FACING, UP_NORTH, SHAPE, SECOND_OUTER_RIGHT),
				GBlockstate.when(FACING, EAST_UP, SHAPE, FIRST_OUTER_LEFT)),
				GBlockstate.variant(outer_id, true, R180, R0))
			.with(When.anyOf(
				GBlockstate.when(FACING, SOUTH_UP, SHAPE, FIRST_OUTER_RIGHT),
				GBlockstate.when(FACING, EAST_UP, SHAPE, FIRST_OUTER_RIGHT)),
				GBlockstate.variant(outer_id, true, R180, R90))
			.with(When.anyOf(
				GBlockstate.when(FACING, SOUTH_UP, SHAPE, FIRST_OUTER_LEFT),
				GBlockstate.when(FACING, UP_WEST, SHAPE, SECOND_OUTER_RIGHT)),
				GBlockstate.variant(outer_id, true, R180, R180))
			.with(When.anyOf(
				GBlockstate.when(FACING, UP_NORTH, SHAPE, SECOND_OUTER_LEFT),
				GBlockstate.when(FACING, UP_WEST, SHAPE, SECOND_OUTER_LEFT)),
				GBlockstate.variant(outer_id, true, R180, R270))
			/* OUTER EAST */
			.with(When.anyOf(
				GBlockstate.when(FACING, EAST_SOUTH, SHAPE, SECOND_OUTER_RIGHT),
				GBlockstate.when(FACING, DOWN_EAST, SHAPE, FIRST_OUTER_RIGHT)),
				GBlockstate.variant(outer_side_id, true, R0, R0))
			.with(When.anyOf(
				GBlockstate.when(FACING, EAST_SOUTH, SHAPE, SECOND_OUTER_LEFT),
				GBlockstate.when(FACING, EAST_UP, SHAPE, SECOND_OUTER_RIGHT)),
				GBlockstate.variant(outer_side_id, true, R90, R0))
			.with(When.anyOf(
				GBlockstate.when(FACING, NORTH_EAST, SHAPE, FIRST_OUTER_LEFT),
				GBlockstate.when(FACING, EAST_UP, SHAPE, SECOND_OUTER_LEFT)),
				GBlockstate.variant(outer_side_id, true, R180, R0))
			.with(When.anyOf(
				GBlockstate.when(FACING, NORTH_EAST, SHAPE, FIRST_OUTER_RIGHT),
				GBlockstate.when(FACING, DOWN_EAST, SHAPE, FIRST_OUTER_LEFT)),
				GBlockstate.variant(outer_side_id, true, R270, R0))
			/* OUTER SOUTH */
			.with(When.anyOf(
				GBlockstate.when(FACING, SOUTH_WEST, SHAPE, SECOND_OUTER_RIGHT),
				GBlockstate.when(FACING, DOWN_SOUTH, SHAPE, FIRST_OUTER_LEFT)),
				GBlockstate.variant(outer_side_id, true, R0, R90))
			.with(When.anyOf(
				GBlockstate.when(FACING, SOUTH_WEST, SHAPE, SECOND_OUTER_LEFT),
				GBlockstate.when(FACING, SOUTH_UP, SHAPE, SECOND_OUTER_LEFT)),
				GBlockstate.variant(outer_side_id, true, R90, R90))
			.with(When.anyOf(
				GBlockstate.when(FACING, EAST_SOUTH, SHAPE, FIRST_OUTER_LEFT),
				GBlockstate.when(FACING, SOUTH_UP, SHAPE, SECOND_OUTER_RIGHT)),
				GBlockstate.variant(outer_side_id, true, R180, R90))
			.with(When.anyOf(
				GBlockstate.when(FACING, EAST_SOUTH, SHAPE, FIRST_OUTER_RIGHT),
				GBlockstate.when(FACING, DOWN_SOUTH, SHAPE, FIRST_OUTER_RIGHT)),
				GBlockstate.variant(outer_side_id, true, R270, R90))
			/* OUTER WEST */
			.with(When.anyOf(
				GBlockstate.when(FACING, WEST_NORTH, SHAPE, SECOND_OUTER_RIGHT),
				GBlockstate.when(FACING, WEST_DOWN, SHAPE, SECOND_OUTER_LEFT)),
				GBlockstate.variant(outer_side_id, true, R0, R180))
			.with(When.anyOf(
				GBlockstate.when(FACING, WEST_NORTH, SHAPE, SECOND_OUTER_LEFT),
				GBlockstate.when(FACING, UP_WEST, SHAPE, FIRST_OUTER_LEFT)),
				GBlockstate.variant(outer_side_id, true, R90, R180))
			.with(When.anyOf(
				GBlockstate.when(FACING, SOUTH_WEST, SHAPE, FIRST_OUTER_LEFT),
				GBlockstate.when(FACING, UP_WEST, SHAPE, FIRST_OUTER_RIGHT)),
				GBlockstate.variant(outer_side_id, true, R180, R180))
			.with(When.anyOf(
				GBlockstate.when(FACING, SOUTH_WEST, SHAPE, FIRST_OUTER_RIGHT),
				GBlockstate.when(FACING, WEST_DOWN, SHAPE, SECOND_OUTER_RIGHT)),
				GBlockstate.variant(outer_side_id, true, R270, R180))
			/* OUTER NORTH */
			.with(When.anyOf(
				GBlockstate.when(FACING, NORTH_EAST, SHAPE, SECOND_OUTER_RIGHT),
				GBlockstate.when(FACING, NORTH_DOWN, SHAPE, SECOND_OUTER_RIGHT)),
				GBlockstate.variant(outer_side_id, true, R0, R270))
			.with(When.anyOf(
				GBlockstate.when(FACING, NORTH_EAST, SHAPE, SECOND_OUTER_LEFT),
				GBlockstate.when(FACING, UP_NORTH, SHAPE, FIRST_OUTER_RIGHT)),
				GBlockstate.variant(outer_side_id, true, R90, R270))
			.with(When.anyOf(
				GBlockstate.when(FACING, WEST_NORTH, SHAPE, FIRST_OUTER_LEFT),
				GBlockstate.when(FACING, UP_NORTH, SHAPE, FIRST_OUTER_LEFT)),
				GBlockstate.variant(outer_side_id, true, R180, R270))
			.with(When.anyOf(
				GBlockstate.when(FACING, WEST_NORTH, SHAPE, FIRST_OUTER_RIGHT),
				GBlockstate.when(FACING, NORTH_DOWN, SHAPE, SECOND_OUTER_LEFT)),
				GBlockstate.variant(outer_side_id, true, R270, R270))
			/* OUTER BOTTOM */
			.with(When.anyOf(
				GBlockstate.when(FACING, DOWN_SOUTH, SHAPE, OUTER_RIGHT),
				GBlockstate.when(FACING, DOWN_EAST, SHAPE, OUTER_RIGHT),
				GBlockstate.when(FACING, EAST_SOUTH, SHAPE, OUTER_RIGHT)),
				GBlockstate.variant(double_outer_id, true, R0, R0))
			.with(When.anyOf(
				GBlockstate.when(FACING, DOWN_SOUTH, SHAPE, OUTER_LEFT),
				GBlockstate.when(FACING, WEST_DOWN, SHAPE, OUTER_RIGHT),
				GBlockstate.when(FACING, SOUTH_WEST, SHAPE, OUTER_RIGHT)),
				GBlockstate.variant(double_outer_id, true, R0, R90))
			.with(When.anyOf(
				GBlockstate.when(FACING, NORTH_DOWN, SHAPE, OUTER_LEFT),
				GBlockstate.when(FACING, WEST_DOWN, SHAPE, OUTER_LEFT),
				GBlockstate.when(FACING, WEST_NORTH, SHAPE, OUTER_RIGHT)),
				GBlockstate.variant(double_outer_id, true, R0, R180))
			.with(When.anyOf(
				GBlockstate.when(FACING, NORTH_DOWN, SHAPE, OUTER_RIGHT),
				GBlockstate.when(FACING, DOWN_EAST, SHAPE, OUTER_LEFT),
				GBlockstate.when(FACING, NORTH_EAST, SHAPE, OUTER_RIGHT)),
				GBlockstate.variant(double_outer_id, true, R0, R270));
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
