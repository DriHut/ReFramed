package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.util.VoxelHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

import static fr.adrien1106.reframed.util.VoxelHelper.VoxelListBuilder;
import static net.minecraft.state.property.Properties.FACING;
import static net.minecraft.state.property.Properties.LAYERS;

public class ReFramedLayerBlock extends ReFramedSlabBlock {

    public static final VoxelShape[] LAYER_VOXELS;
    private record ModelCacheKey(Direction face, int layer) {}

    public ReFramedLayerBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(LAYERS, 1));
    }

    @Override
    public Object getModelCacheKey(BlockState state) {
        return new ModelCacheKey(state.get(FACING), state.get(LAYERS));
    }

    @Override
    public int getModelStateCount() {
        return 48;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(LAYERS));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return LAYER_VOXELS[state.get(FACING).getId() * 8 + state.get(LAYERS) - 1];
    }

    @Override
    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        return !(
            context.getPlayer().isSneaking()
            || !(context.getStack().getItem() instanceof BlockItem block_item)
            || !(block_item.getBlock() == this && state.get(LAYERS) < 8)
        );
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState previous = ctx.getWorld().getBlockState(ctx.getBlockPos());
        if (!previous.isOf(this)) return super.getPlacementState(ctx);
        return previous.with(LAYERS, previous.get(LAYERS) + 1);
    }

    static {
        VoxelListBuilder builder = VoxelListBuilder.create(createCuboidShape(0, 0, 0, 16, 2, 16), 48)
            .add(createCuboidShape(0, 0, 0, 16, 4, 16))
            .add(createCuboidShape(0, 0, 0, 16, 6, 16))
            .add(createCuboidShape(0, 0, 0, 16, 8, 16))
            .add(createCuboidShape(0, 0, 0, 16, 10, 16))
            .add(createCuboidShape(0, 0, 0, 16, 12, 16))
            .add(createCuboidShape(0, 0, 0, 16, 14, 16))
            .add(createCuboidShape(0, 0, 0, 16, 16, 16));

        for (int i = 0; i < 8; i++) {
            builder.add(i, VoxelHelper::mirrorY);
        }
        for (int i = 0; i < 8; i++) {
            builder.add(i, VoxelHelper::rotateX);
        }
        for (int i = 0; i < 8; i++) {
            builder.add(i, VoxelHelper::rotateCX);
        }
        for (int i = 0; i < 8; i++) {
            builder.add(i, VoxelHelper::rotateCZ);
        }
        for (int i = 0; i < 8; i++) {
            builder.add(i, VoxelHelper::rotateZ);
        }

        LAYER_VOXELS = builder.build();
    }
}
