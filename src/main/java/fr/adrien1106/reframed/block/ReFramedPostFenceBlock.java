package fr.adrien1106.reframed.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.LeadItem;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.state.StateManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import static fr.adrien1106.reframed.block.ConnectingReFramedBlock.getConnectionProperty;
import static fr.adrien1106.reframed.block.ConnectingReFramedBlock.placementState;
import static fr.adrien1106.reframed.block.ReFramedFenceBlock.FENCE_VOXELS;
import static net.minecraft.state.property.Properties.*;
import static net.minecraft.state.property.Properties.WEST;

public class ReFramedPostFenceBlock extends WaterloggableReFramedDoubleBlock {

    public ReFramedPostFenceBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState()
            .with(EAST, false)
            .with(NORTH, false)
            .with(WEST, false)
            .with(SOUTH, false)
        );
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(EAST, NORTH, SOUTH, WEST));
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState new_state, boolean moved) {
        super.onStateReplaced(state, world, pos, new_state, moved);

        if(!state.isOf(new_state.getBlock())) world.removeBlockEntity(pos);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction dir, BlockState other_state, WorldAccess world, BlockPos pos, BlockPos moved) {
        BlockState new_state = super.getStateForNeighborUpdate(state, dir, other_state, world, pos, moved);
        if (dir == Direction.DOWN) return new_state;

        return placementState(new_state, world, pos, this::connectsTo);
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState state = super.getPlacementState(ctx);
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();

        return placementState(state, world, pos, this::connectsTo);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return Direction.Type.HORIZONTAL.stream().reduce(state, (s, dir) ->
                s.with(getConnectionProperty(rotation.rotate(dir)), state.get(getConnectionProperty(dir)))
            , (prev, next) -> next);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return Direction.Type.HORIZONTAL.stream().reduce(state, (s, dir) ->
                s.with(getConnectionProperty(mirror.apply(dir)), state.get(getConnectionProperty(dir)))
            , (prev, next) -> next);
    }

    private boolean connectsTo(BlockState state, boolean fs, Direction dir) {
        return fs || state.isIn(BlockTags.FENCES)
            || (state.getBlock() instanceof FenceGateBlock && FenceGateBlock.canWallConnect(state, dir));
    }

    @Override
    public VoxelShape getShape(BlockState state, int i) {
        if (i == 1) return FENCE_VOXELS[0];
        VoxelShape shape = VoxelShapes.empty();
        for (Direction dir: Direction.Type.HORIZONTAL) {
            if (state.get(getConnectionProperty(dir)))
                shape = VoxelShapes.union(shape, FENCE_VOXELS[dir.ordinal() - 1]);
        }
        return shape;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        VoxelShape shape = FENCE_VOXELS[0];
        for (Direction dir: Direction.Type.HORIZONTAL) {
            if (state.get(getConnectionProperty(dir)))
                shape = VoxelShapes.union(shape, FENCE_VOXELS[dir.ordinal() - 1]);
        }
        return shape;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView view, BlockPos pos, ShapeContext ctx) {
        VoxelShape shape = FENCE_VOXELS[5];
        for (Direction dir: Direction.Type.HORIZONTAL) {
            if (state.get(getConnectionProperty(dir)))
                shape = VoxelShapes.union(shape, FENCE_VOXELS[dir.ordinal() + 4]);
        }
        return shape;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getOutlineShape(state, world, pos, context);
    }

    @Override
    public VoxelShape getCullingShape(BlockState state, BlockView view, BlockPos pos) {
        return getOutlineShape(state, view, pos, ShapeContext.absent());
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }

    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ActionResult result = super.onUse(state, world, pos, player, hand, hit);
        if (result.isAccepted()) return result;
        if (world.isClient) {
            ItemStack itemStack = player.getStackInHand(hand);
            return itemStack.isOf(Items.LEAD) ? ActionResult.SUCCESS : ActionResult.PASS;
        } else {
            return LeadItem.attachHeldMobsToBlock(player, world, pos);
        }
    }

}
