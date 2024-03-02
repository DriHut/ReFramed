package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.generator.MultipartBlockStateProvider;
import fr.adrien1106.reframed.util.property.StairDirection;
import fr.adrien1106.reframed.util.property.StairShape;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.data.client.MultipartBlockStateSupplier;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static fr.adrien1106.reframed.block.ReFramedStairsBlock.*;
import static fr.adrien1106.reframed.util.property.StairShape.STRAIGHT;

public class ReFramedDoubleStairsBlock extends ReFramedDoubleBlock implements MultipartBlockStateProvider {

    private static final List<VoxelShape> COMPLEMENT_LIST = new ArrayList<>(52);

    public ReFramedDoubleStairsBlock(Settings settings) {
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
            .with(SHAPE, getPlacementShape(this, state.get(FACING), world, pos));
    }


    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getPlacement(super.getPlacementState(ctx), ctx);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        super.onStateReplaced(state, world, pos, newState, moved);

        if(!state.isOf(newState.getBlock())) world.removeBlockEntity(pos);
    }

    @Override
    public VoxelShape getShape(BlockState state, int i) {
        return i == 2 ? getComplementOutline(state) : getOutline(state);
    }

    private VoxelShape getComplementOutline(BlockState state) {
        StairShape shape = state.get(SHAPE);
        StairDirection direction = state.get(FACING);
        return switch (shape) {
            case STRAIGHT ->
                switch (direction) {
                    case DOWN_SOUTH ->                        COMPLEMENT_LIST.get(0);
                    case NORTH_DOWN ->                        COMPLEMENT_LIST.get(1);
                    case UP_NORTH ->                          COMPLEMENT_LIST.get(2);
                    case SOUTH_UP ->                          COMPLEMENT_LIST.get(3);
                    case DOWN_EAST ->                         COMPLEMENT_LIST.get(4);
                    case WEST_DOWN ->                         COMPLEMENT_LIST.get(5);
                    case UP_WEST ->                           COMPLEMENT_LIST.get(6);
                    case EAST_UP ->                           COMPLEMENT_LIST.get(7);
                    case NORTH_EAST ->                        COMPLEMENT_LIST.get(8);
                    case EAST_SOUTH ->                        COMPLEMENT_LIST.get(9);
                    case SOUTH_WEST ->                        COMPLEMENT_LIST.get(10);
                    case WEST_NORTH ->                        COMPLEMENT_LIST.get(11);
                };
            case INNER_LEFT ->
                switch (direction) {
                    case WEST_DOWN, NORTH_DOWN ->             COMPLEMENT_LIST.get(44);
                    case DOWN_EAST ->                         COMPLEMENT_LIST.get(45);
                    case DOWN_SOUTH ->                        COMPLEMENT_LIST.get(47);
                    case UP_WEST, UP_NORTH, WEST_NORTH ->     COMPLEMENT_LIST.get(48);
                    case EAST_UP, NORTH_EAST ->               COMPLEMENT_LIST.get(49);
                    case EAST_SOUTH ->                        COMPLEMENT_LIST.get(50);
                    case SOUTH_UP, SOUTH_WEST ->              COMPLEMENT_LIST.get(51);
                };
            case INNER_RIGHT ->
                switch (direction) {
                    case WEST_NORTH ->                        COMPLEMENT_LIST.get(44);
                    case NORTH_DOWN, NORTH_EAST ->            COMPLEMENT_LIST.get(45);
                    case DOWN_EAST, DOWN_SOUTH, EAST_SOUTH -> COMPLEMENT_LIST.get(46);
                    case WEST_DOWN, SOUTH_WEST ->             COMPLEMENT_LIST.get(47);
                    case UP_NORTH ->                          COMPLEMENT_LIST.get(49);
                    case EAST_UP, SOUTH_UP ->                 COMPLEMENT_LIST.get(50);
                    case UP_WEST ->                           COMPLEMENT_LIST.get(51);
                };
            case OUTER_LEFT ->
                switch (direction) {
                    case DOWN_EAST ->                         COMPLEMENT_LIST.get(43);
                    case WEST_DOWN, NORTH_DOWN ->             COMPLEMENT_LIST.get(42);
                    case DOWN_SOUTH ->                        COMPLEMENT_LIST.get(41);
                    case EAST_UP, NORTH_EAST ->               COMPLEMENT_LIST.get(39);
                    case UP_WEST, UP_NORTH, WEST_NORTH ->     COMPLEMENT_LIST.get(38);
                    case SOUTH_UP, SOUTH_WEST ->              COMPLEMENT_LIST.get(37);
                    case EAST_SOUTH ->                        COMPLEMENT_LIST.get(36);
                };
            case OUTER_RIGHT ->
                switch (direction) {
                    case NORTH_DOWN, NORTH_EAST ->            COMPLEMENT_LIST.get(43);
                    case WEST_NORTH ->                        COMPLEMENT_LIST.get(42);
                    case WEST_DOWN, SOUTH_WEST ->             COMPLEMENT_LIST.get(41);
                    case DOWN_EAST, DOWN_SOUTH, EAST_SOUTH -> COMPLEMENT_LIST.get(40);
                    case UP_NORTH ->                          COMPLEMENT_LIST.get(39);
                    case UP_WEST ->                           COMPLEMENT_LIST.get(37);
                    case EAST_UP, SOUTH_UP ->                 COMPLEMENT_LIST.get(36);
                };
            case FIRST_OUTER_LEFT ->
                switch (direction) {
                    case WEST_DOWN, NORTH_DOWN ->             COMPLEMENT_LIST.get(14);
                    case SOUTH_UP ->                          COMPLEMENT_LIST.get(17);
                    case EAST_UP ->                           COMPLEMENT_LIST.get(19);
                    case EAST_SOUTH ->                        COMPLEMENT_LIST.get(20);
                    case DOWN_SOUTH ->                        COMPLEMENT_LIST.get(22);
                    case UP_NORTH, WEST_NORTH ->              COMPLEMENT_LIST.get(25);
                    case SOUTH_WEST ->                        COMPLEMENT_LIST.get(28);
                    case UP_WEST ->                           COMPLEMENT_LIST.get(31);
                    case DOWN_EAST ->                         COMPLEMENT_LIST.get(34);
                    case NORTH_EAST ->                        COMPLEMENT_LIST.get(35);
                };
            case FIRST_OUTER_RIGHT ->
                switch (direction) {
                    case NORTH_DOWN ->                        COMPLEMENT_LIST.get(15);
                    case SOUTH_UP, EAST_UP ->                 COMPLEMENT_LIST.get(16);
                    case WEST_DOWN ->                         COMPLEMENT_LIST.get(13);
                    case DOWN_SOUTH, EAST_SOUTH ->            COMPLEMENT_LIST.get(23);
                    case UP_NORTH ->                          COMPLEMENT_LIST.get(24);
                    case WEST_NORTH ->                        COMPLEMENT_LIST.get(26);
                    case UP_WEST ->                           COMPLEMENT_LIST.get(28);
                    case SOUTH_WEST ->                        COMPLEMENT_LIST.get(29);
                    case DOWN_EAST ->                         COMPLEMENT_LIST.get(33);
                    case NORTH_EAST ->                        COMPLEMENT_LIST.get(34);
                };
            case SECOND_OUTER_LEFT ->
                switch (direction) {
                    case DOWN_EAST ->                         COMPLEMENT_LIST.get(15);
                    case DOWN_SOUTH ->                        COMPLEMENT_LIST.get(13);
                    case UP_WEST, UP_NORTH ->                 COMPLEMENT_LIST.get(18);
                    case SOUTH_UP, SOUTH_WEST ->              COMPLEMENT_LIST.get(21);
                    case NORTH_EAST ->                        COMPLEMENT_LIST.get(24);
                    case NORTH_DOWN ->                        COMPLEMENT_LIST.get(26);
                    case WEST_DOWN ->                         COMPLEMENT_LIST.get(30);
                    case WEST_NORTH ->                        COMPLEMENT_LIST.get(31);
                    case EAST_SOUTH ->                        COMPLEMENT_LIST.get(32);
                    case EAST_UP ->                           COMPLEMENT_LIST.get(35);
                };
            case SECOND_OUTER_RIGHT ->
                switch (direction) {
                    case DOWN_SOUTH, DOWN_EAST ->             COMPLEMENT_LIST.get(12);
                    case UP_WEST ->                           COMPLEMENT_LIST.get(17);
                    case UP_NORTH ->                          COMPLEMENT_LIST.get(19);
                    case SOUTH_UP ->                          COMPLEMENT_LIST.get(20);
                    case SOUTH_WEST ->                        COMPLEMENT_LIST.get(22);
                    case NORTH_EAST, NORTH_DOWN ->            COMPLEMENT_LIST.get(27);
                    case WEST_DOWN ->                         COMPLEMENT_LIST.get(29);
                    case WEST_NORTH ->                        COMPLEMENT_LIST.get(30);
                    case EAST_UP ->                           COMPLEMENT_LIST.get(32);
                    case EAST_SOUTH ->                        COMPLEMENT_LIST.get(33);
                };
        };
    }

    @Override
    public MultipartBlockStateSupplier getMultipart() {
        return getStairMultipart(this, true);
    }

    static {
        VOXEL_LIST.forEach(shape -> COMPLEMENT_LIST.add(VoxelShapes.combineAndSimplify(VoxelShapes.fullCube(), shape, BooleanBiFunction.ONLY_FIRST)));
    }
}
