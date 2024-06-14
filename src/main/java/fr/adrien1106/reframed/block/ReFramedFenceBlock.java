package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.util.VoxelHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.LeadItem;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class ReFramedFenceBlock extends ConnectingReFramedBlock {

    public static final VoxelShape[] FENCE_VOXELS;

    public ReFramedFenceBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected boolean connectsTo(BlockState state, boolean fs, Direction dir) {
        return fs || state.isIn(BlockTags.FENCES)
            || (state.getBlock() instanceof FenceGateBlock && FenceGateBlock.canWallConnect(state, dir));
    }

    @Override
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
    public VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getOutlineShape(state, world, pos, context);
    }

    @Override
    public VoxelShape getCullingShape(BlockState state, BlockView view, BlockPos pos) {
        return getOutlineShape(state, view, pos, ShapeContext.absent());
    }

    @Override
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

    static {
        VoxelShape POST = createCuboidShape(6, 0, 6, 10, 16, 10);
        VoxelShape POST_COLLISION = createCuboidShape(6, 0, 6, 10, 24, 10);
        VoxelShape SIDE = VoxelShapes.combineAndSimplify(
            createCuboidShape(7, 12, 0, 9, 15, 6),
            createCuboidShape(7, 6, 0, 9, 9, 6),
            BooleanBiFunction.OR
        );
        VoxelShape SIDE_COLLISION = createCuboidShape(7, 0, 0, 9, 24, 6);
        FENCE_VOXELS = VoxelHelper.VoxelListBuilder.create(POST, 5)
            .add(SIDE)
            .add(VoxelHelper::mirrorZ)
            .add(VoxelHelper::rotateY)
            .add(VoxelHelper::mirrorX)
            .add(POST_COLLISION)
            .add(SIDE_COLLISION)
            .add(VoxelHelper::mirrorZ)
            .add(VoxelHelper::rotateY)
            .add(VoxelHelper::mirrorX)
            .build();
    }
}
