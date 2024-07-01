package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.util.VoxelHelper;
import fr.adrien1106.reframed.util.blocks.BlockHelper;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.DoorHinge;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.state.property.Properties.*;

public class ReFramedDoorBlock extends WaterloggableReFramedBlock {

    public static final VoxelShape[] DOOR_VOXELS;

    public ReFramedDoorBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState()
            .with(HORIZONTAL_FACING, Direction.NORTH)
            .with(DOOR_HINGE, DoorHinge.LEFT)
            .with(DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER)
            .with(OPEN, false)
            .with(POWERED, false)
        );
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(HORIZONTAL_FACING, DOOR_HINGE, DOUBLE_BLOCK_HALF, OPEN, POWERED));
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos pos_down = pos.down();
        BlockState state_down = world.getBlockState(pos_down);
        return state.get(DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER ? !state_down.isAir() : state_down.isOf(this);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block source, BlockPos sourcePos, boolean notify) {
        if (world.isClient) return;
        boolean powered = world.isReceivingRedstonePower(pos)
            || world.isReceivingRedstonePower(
                state.get(DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER
                    ? pos.up()
                    : pos.down()
        );
        if (!getDefaultState().isOf(source) && powered != state.get(POWERED)) {
            if (state.get(OPEN) != powered)
                playToggleSound(null, world, pos, powered);

            world.setBlockState(pos, state.with(POWERED, powered).with(OPEN, powered), 2);
            if (state.get(WATERLOGGED)) {
                world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
            }
        }
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();
        Direction facing = ctx.getHorizontalPlayerFacing().getOpposite();
        if (pos.getY() >= world.getTopY() - 1 || !world.getBlockState(pos.up()).canReplace(ctx)) return null;
        BlockState state = super.getPlacementState(ctx)
            .with(DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER)
            .with(HORIZONTAL_FACING, facing);

        if (world.isReceivingRedstonePower(pos) || world.isReceivingRedstonePower(pos.up()))
            state = state.with(OPEN, true).with(POWERED, true);


        return state.with(DOOR_HINGE, getHinge(facing, pos, world, BlockHelper.getRelativePos(ctx.getHitPos(), pos)));
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack, BlockState old_state, BlockEntity old_entity) {
        world.setBlockState(
            pos.up(),
            state
                .with(DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER)
                .with(WATERLOGGED, world.getFluidState(pos.up()).isOf(Fluids.WATER)),
            3
        );
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient() && (player.isCreative() || player.canHarvest(state))) {
            DoubleBlockHalf half = state.get(DOUBLE_BLOCK_HALF);
            BlockPos other_pos = half == DoubleBlockHalf.LOWER ? pos.up() : pos.down();
            BlockState other_state = world.getBlockState(other_pos);
            if (other_state.isOf(this) && other_state.get(DOUBLE_BLOCK_HALF) != half) {
                world.setBlockState(other_pos, other_state.get(WATERLOGGED) ? Blocks.WATER.getDefaultState(): Blocks.AIR.getDefaultState(), 35);
                world.syncWorldEvent(player, 2001, other_pos, Block.getRawIdFromState(other_state));
            }
        }

        super.onBreak(world, pos, state, player);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState other, WorldAccess world, BlockPos pos, BlockPos moved) {
        if (direction.getAxis() == Direction.Axis.Y
            && other.isOf(this)
            && other.get(DOUBLE_BLOCK_HALF) != state.get(DOUBLE_BLOCK_HALF)
            && other.get(OPEN) != state.get(OPEN)
        ) return state.cycle(OPEN);
        Direction facing = state.get(HORIZONTAL_FACING);
        if (direction == (
            state.get(DOOR_HINGE) == DoorHinge.RIGHT
                ? facing.rotateYClockwise()
                : facing.rotateYCounterclockwise())
            && other.isOf(this)
            && other.get(DOUBLE_BLOCK_HALF) == state.get(DOUBLE_BLOCK_HALF)
            && other.get(DOOR_HINGE) != state.get(DOOR_HINGE)
            && !state.get(POWERED)
        ) return state.with(OPEN, other.get(OPEN));
        return super.getStateForNeighborUpdate(state, direction, other, world, pos, moved);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ActionResult result = super.onUse(state, world, pos, player, hand, hit);
        if (result.isAccepted()) return result;
        flip(state, world, pos, player);
        return ActionResult.success(world.isClient);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return switch (type) {
            case LAND, AIR -> state.get(OPEN);
            case WATER -> false;
        };
    }

    private void flip(BlockState state, World world, BlockPos pos, @Nullable PlayerEntity player) {
        state = state.cycle(OPEN);
        world.setBlockState(pos, state, 10);

        this.playToggleSound(player, world, pos, state.get(OPEN));
    }

    protected void playToggleSound(@Nullable PlayerEntity player, World world, BlockPos pos, boolean open) {
        world.playSound(player, pos, open ? BlockSetType.OAK.doorOpen() : BlockSetType.OAK.doorClose(), SoundCategory.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.1F + 0.9F);
        world.emitGameEvent(player, open ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction direction = state.get(HORIZONTAL_FACING);
        if (state.get(OPEN)) direction = switch (state.get(DOOR_HINGE)) {
            case RIGHT -> direction.rotateYCounterclockwise();
            case LEFT -> direction.rotateYClockwise();
        };
        return DOOR_VOXELS[direction.ordinal() - 2];
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(HORIZONTAL_FACING, rotation.rotate(state.get(HORIZONTAL_FACING)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return mirror == BlockMirror.NONE ? state : state.with(HORIZONTAL_FACING, mirror.apply(state.get(HORIZONTAL_FACING))).cycle(DOOR_HINGE);
    }

    @Override
    @SuppressWarnings("deprecation")
    public long getRenderingSeed(BlockState state, BlockPos pos) {
        return MathHelper.hashCode(pos.getX(), pos.down(state.get(DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER ? 0 : 1).getY(), pos.getZ());
    }

    private DoorHinge getHinge(Direction facing, BlockPos pos, World world, Vec3d hit_pos) {
        Direction left = facing.rotateYClockwise();
        BlockPos left_pos = pos.offset(left);
        BlockState left_state = world.getBlockState(left_pos);
        Direction right = facing.rotateYCounterclockwise();
        BlockPos right_pos = pos.offset(right);
        BlockState right_state = world.getBlockState(right_pos);
        DoorHinge hinge = null;

        if (left_state.isSideSolidFullSquare(world, left_pos, right))
            hinge = DoorHinge.LEFT;
        if (right_state.isSideSolidFullSquare(world, right_pos, left))
            hinge = hinge == DoorHinge.LEFT ? null : DoorHinge.RIGHT;

        if (hinge != null) return hinge;

        if (left_state.isOf(this)
            && left_state.get(HORIZONTAL_FACING) == facing
            && left_state.get(DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER
            && left_state.get(DOOR_HINGE) == DoorHinge.LEFT
        ) hinge = DoorHinge.RIGHT;
        if (right_state.isOf(this)
            && right_state.get(HORIZONTAL_FACING) == facing
            && right_state.get(DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER
            && right_state.get(DOOR_HINGE) == DoorHinge.RIGHT
        ) hinge = hinge == DoorHinge.RIGHT ? null : DoorHinge.LEFT;

        if (hinge != null) return hinge;

        return switch (facing.getAxis()) {
            case Z -> {
                if (left.getDirection() == Direction.AxisDirection.POSITIVE)
                    yield hit_pos.getX() < 0.5 ? DoorHinge.RIGHT : DoorHinge.LEFT;
                else // left.getDirection() == Direction.AxisDirection.NEGATIVE
                    yield hit_pos.getX() < 0.5 ? DoorHinge.LEFT : DoorHinge.RIGHT;
            }
            case X -> {
                if (left.getDirection() == Direction.AxisDirection.POSITIVE)
                    yield hit_pos.getZ() < 0.5 ? DoorHinge.RIGHT : DoorHinge.LEFT;
                else // left.getDirection() == Direction.AxisDirection.NEGATIVE
                    yield hit_pos.getZ() < 0.5 ? DoorHinge.LEFT : DoorHinge.RIGHT;
            }
            default -> DoorHinge.LEFT;
        };
    }

    static {
        VoxelShape SHAPE = createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 3.0);
        DOOR_VOXELS = VoxelHelper.VoxelListBuilder.create(SHAPE, 4)
            .add(VoxelHelper::mirrorZ)
            .add(VoxelHelper::rotateY)
            .add(VoxelHelper::mirrorX)
            .build();
    }
}
