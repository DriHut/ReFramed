package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.util.VoxelHelper;
import net.minecraft.block.*;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.state.property.Properties.*;

public class ReFramedTrapdoorBlock extends WaterloggableReFramedBlock {

    public static final VoxelShape[] TRAPDOOR_VOXELS;

    public ReFramedTrapdoorBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState()
            .with(HORIZONTAL_FACING, Direction.NORTH)
            .with(BLOCK_HALF, BlockHalf.BOTTOM)
            .with(OPEN, false)
            .with(POWERED, false)
        );
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(HORIZONTAL_FACING, BLOCK_HALF, OPEN, POWERED));
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block source, BlockPos sourcePos, boolean notify) {
        if (world.isClient) return;
        boolean powered = world.isReceivingRedstonePower(pos);
        if (powered != state.get(POWERED)) {
            if (state.get(OPEN) != powered) {
                state = state.with(OPEN, powered);
                playToggleSound(null, world, pos, powered);
            }

            world.setBlockState(pos, state.with(POWERED, powered), 2);
            if (state.get(WATERLOGGED)) {
                world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
            }
        }
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState state = super.getPlacementState(ctx);
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();
        Direction side = ctx.getSide();

        if (side.getAxis().isVertical()) state = state
            .with(HORIZONTAL_FACING, ctx.getHorizontalPlayerFacing().getOpposite())
            .with(BLOCK_HALF, side == Direction.UP ? BlockHalf.BOTTOM : BlockHalf.TOP);
        else state = state
            .with(HORIZONTAL_FACING, side)
            .with(BLOCK_HALF, ctx.getHitPos().getY() - pos.getY() > 0.5 ? BlockHalf.TOP : BlockHalf.BOTTOM);

        if (world.isReceivingRedstonePower(pos)) state = state.with(OPEN, true).with(POWERED, true);

        return state;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        super.onStateReplaced(state, world, pos, newState, moved);

        if(!state.isOf(newState.getBlock())) world.removeBlockEntity(pos);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ActionResult result = super.onUse(state, world, pos, player, hand, hit);
        if (result.isAccepted()) return result;
        flip(state, world, pos, player);
        return ActionResult.success(world.isClient);
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return switch (type) {
            case LAND, AIR -> state.get(OPEN);
            case WATER -> state.get(WATERLOGGED);
        };
    }

    private void flip(BlockState state, World world, BlockPos pos, @Nullable PlayerEntity player) {
        state = state.cycle(OPEN);
        world.setBlockState(pos, state, 2);

        this.playToggleSound(player, world, pos, state.get(OPEN));
    }

    protected void playToggleSound(@Nullable PlayerEntity player, World world, BlockPos pos, boolean open) {
        world.playSound(player, pos, open ? BlockSetType.OAK.trapdoorOpen() : BlockSetType.OAK.trapdoorClose(), SoundCategory.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.1F + 0.9F);
        world.emitGameEvent(player, open ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pos);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        int index;
        if (!state.get(OPEN)) index = state.get(BLOCK_HALF) == BlockHalf.BOTTOM ? 0 : 1;
        else index = state.get(HORIZONTAL_FACING).ordinal();
        return TRAPDOOR_VOXELS[index];
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(HORIZONTAL_FACING, rotation.rotate(state.get(HORIZONTAL_FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.with(HORIZONTAL_FACING, mirror.apply(state.get(HORIZONTAL_FACING)));
    }

    static {
        VoxelShape SHAPE = createCuboidShape(0, 0, 0, 16, 3, 16);
        TRAPDOOR_VOXELS = VoxelHelper.VoxelListBuilder.create(SHAPE, 6)
            .add(VoxelHelper::mirrorY)
            .add(VoxelHelper::rotateX)
            .add(VoxelHelper::mirrorZ)
            .add(VoxelHelper::rotateY)
            .add(VoxelHelper::mirrorX)
            .build();
    }
}
