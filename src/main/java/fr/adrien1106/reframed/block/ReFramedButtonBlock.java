package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.util.VoxelHelper;
import net.minecraft.block.*;
import net.minecraft.block.enums.BlockFace;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

import static net.minecraft.state.property.Properties.*;

public class ReFramedButtonBlock extends WaterloggableReFramedBlock {

    public static final VoxelShape[] BUTTON_VOXELS;

    public ReFramedButtonBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState()
            .with(HORIZONTAL_FACING, Direction.NORTH)
            .with(BLOCK_FACE, BlockFace.WALL)
            .with(POWERED, false)
        );
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(HORIZONTAL_FACING, BLOCK_FACE, POWERED));
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return canPlaceAt(world, pos, getDirection(state).getOpposite());
    }

    public static boolean canPlaceAt(WorldView world, BlockPos pos, Direction direction) {
        BlockPos other_pos = pos.offset(direction);
        return world.getBlockState(other_pos).isSideSolidFullSquare(world, other_pos, direction.getOpposite());
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState state = super.getPlacementState(ctx);
        Direction side = ctx.getSide();
        return state
            .with(HORIZONTAL_FACING, side.getAxis() == Direction.Axis.Y
                ? ctx.getHorizontalPlayerFacing()
                : side
            )
            .with(BLOCK_FACE, side.getAxis() != Direction.Axis.Y
                ? BlockFace.WALL
                : side == Direction.UP ? BlockFace.FLOOR : BlockFace.CEILING
            );
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState other_state, WorldAccess world, BlockPos pos, BlockPos other_pos) {
        return getDirection(state).getOpposite() == direction && !state.canPlaceAt(world, pos)
            ? Blocks.AIR.getDefaultState()
            : super.getStateForNeighborUpdate(state, direction, other_state, world, pos, other_pos);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ActionResult result = super.onUse(state, world, pos, player, hand, hit);
        if (result.isAccepted()) return result;

        if (state.get(POWERED)) return ActionResult.CONSUME;
        powerOn(state, world, pos);
        playClickSound(player, world, pos, true);
        world.emitGameEvent(player, GameEvent.BLOCK_ACTIVATE, pos);

        return ActionResult.success(world.isClient);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onExploded(BlockState state, World world, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> stackMerger) {
        if (explosion.getDestructionType() == Explosion.DestructionType.TRIGGER_BLOCK && !world.isClient() && !(Boolean)state.get(POWERED)) {
            powerOn(state, world, pos);
        }

        super.onExploded(state, world, pos, explosion, stackMerger);
    }

    public void powerOn(BlockState state, World world, BlockPos pos) {
        world.setBlockState(pos, state.with(POWERED, true), 3);
        updateNeighbors(state, world, pos);
        world.scheduleBlockTick(pos, this, 30);
    }

    protected void playClickSound(@Nullable PlayerEntity player, WorldAccess world, BlockPos pos, boolean powered) {
        world.playSound(
            powered ? player : null,
            pos,
            powered ? BlockSetType.OAK.buttonClickOn() : BlockSetType.OAK.buttonClickOff(),
            SoundCategory.BLOCKS
        );
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return BUTTON_VOXELS[
            (state.get(POWERED) ? 12 : 0) +
            (4 * state.get(BLOCK_FACE).ordinal()) +
            state.get(HORIZONTAL_FACING).ordinal() - 2
        ];
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(HORIZONTAL_FACING, rotation.rotate(state.get(HORIZONTAL_FACING)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.with(HORIZONTAL_FACING, mirror.apply(state.get(HORIZONTAL_FACING)));
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState new_state, boolean moved) {
        super.onStateReplaced(state, world, pos, new_state, false);

        if(!state.isOf(new_state.getBlock())) {
            if (!moved && state.get(POWERED)) updateNeighbors(state, world, pos);
            world.removeBlockEntity(pos);
        }
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction dir) {
        return state.get(POWERED) ? 15 : super.getWeakRedstonePower(state, view, pos, dir);
    }

    @Override
    public int getStrongRedstonePower(BlockState state, BlockView view, BlockPos pos, Direction dir) {
        return dir == getDirection(state) ? state.getWeakRedstonePower(view, pos, dir) : 0;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (state.get(POWERED)) tryPowerWithProjectiles(state, world, pos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!world.isClient && !state.get(POWERED)) tryPowerWithProjectiles(state, world, pos);
    }

    protected void tryPowerWithProjectiles(BlockState state, World world, BlockPos pos) {
        PersistentProjectileEntity projectile = world.getNonSpectatingEntities(
            PersistentProjectileEntity.class,
            state.getOutlineShape(world, pos).getBoundingBox().offset(pos)
        ).stream().findFirst().orElse(null);
        boolean has_projectile = projectile != null;
        if (has_projectile != state.get(POWERED)) {
            world.setBlockState(pos, state.with(POWERED, has_projectile), 3);
            this.updateNeighbors(state, world, pos);
            this.playClickSound(null, world, pos, has_projectile);
            world.emitGameEvent(projectile, has_projectile ? GameEvent.BLOCK_ACTIVATE : GameEvent.BLOCK_DEACTIVATE, pos);
        }

        if (has_projectile) {
            world.scheduleBlockTick(pos, this, 30);
        }

    }

    private void updateNeighbors(BlockState state, World world, BlockPos pos) {
        world.updateNeighborsAlways(pos, this);
        world.updateNeighborsAlways(pos.offset(getDirection(state).getOpposite()), this);
    }

    protected static Direction getDirection(BlockState state) {
        return switch (state.get(BLOCK_FACE)) {
            case CEILING -> Direction.DOWN;
            case FLOOR -> Direction.UP;
            default -> state.get(HORIZONTAL_FACING);
        };
    }


    static {
        VoxelShape SHAPE = createCuboidShape(5, 0, 6, 11, 2, 10);
        VoxelShape POWERED_SHAPE = createCuboidShape(5, 0, 6, 11, 1, 10);
        BUTTON_VOXELS = VoxelHelper.VoxelListBuilder.create(SHAPE, 24)
            .add()
            .add(0, VoxelHelper::rotateY)
            .add()
            .add(VoxelHelper::rotateZ, VoxelHelper::rotateY)
            .add(VoxelHelper::mirrorZ)
            .add(VoxelHelper::rotateY)
            .add(VoxelHelper::mirrorX)
            .add(0, VoxelHelper::mirrorY)
            .add()
            .add(2, VoxelHelper::mirrorY)
            .add()
            .add(POWERED_SHAPE)
            .add()
            .add(12, VoxelHelper::rotateY)
            .add()
            .add(VoxelHelper::rotateZ, VoxelHelper::rotateY)
            .add(VoxelHelper::mirrorZ)
            .add(VoxelHelper::rotateY)
            .add(VoxelHelper::mirrorX)
            .add(12, VoxelHelper::mirrorY)
            .add()
            .add(13, VoxelHelper::mirrorY)
            .add()
            .build();
    }
}
