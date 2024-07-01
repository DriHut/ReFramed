package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.util.VoxelHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

import java.util.List;

import static fr.adrien1106.reframed.block.ReFramedLayerBlock.getLayerShape;
import static fr.adrien1106.reframed.block.ReFramedSlabBlock.getSlabShape;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.HALF_LAYERS;
import static net.minecraft.state.property.Properties.FACING;

public class ReFramedSlabsLayerBlock extends FacingDoubleReFramedBlock {

    public static VoxelShape[] HALF_LAYER_SHAPES;

    public ReFramedSlabsLayerBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(HALF_LAYERS, 1));
    }

    @Override
    @SuppressWarnings("deprecation")
    public List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
        List<ItemStack> drops = super.getDroppedStacks(state, builder);
        if (state.get(HALF_LAYERS) > 1)
            drops.add(new ItemStack(ReFramed.LAYER, state.get(HALF_LAYERS)-1));
        return drops;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        if (context.getPlayer() == null
            || context.getPlayer().isSneaking()
            || !(context.getStack().getItem() instanceof BlockItem block_item)
        ) return false;

        return block_item.getBlock() == ReFramed.LAYER && state.get(HALF_LAYERS) < 4;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(HALF_LAYERS));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getLayerShape(state.get(FACING), state.get(HALF_LAYERS) + 4);
    }

    @Override
    public VoxelShape getShape(BlockState state, int i) {
        Direction face = state.get(FACING);
        return i == 2
            ? HALF_LAYER_SHAPES[face.getId() * 4 + state.get(HALF_LAYERS)-1]
            : getSlabShape(face);
    }

    static {
        VoxelHelper.VoxelListBuilder builder = VoxelHelper.VoxelListBuilder.create(createCuboidShape(0, 8, 0, 16, 10, 16), 24)
            .add(createCuboidShape(0, 8, 0, 16, 12, 16))
            .add(createCuboidShape(0, 8, 0, 16, 14, 16))
            .add(createCuboidShape(0, 8, 0, 16, 16, 16));

        for (int i = 0; i < 4; i++) {
            builder.add(i, VoxelHelper::mirrorY);
        }
        for (int i = 0; i < 4; i++) {
            builder.add(i, VoxelHelper::rotateX);
        }
        for (int i = 0; i < 4; i++) {
            builder.add(i, VoxelHelper::rotateCX);
        }
        for (int i = 0; i < 4; i++) {
            builder.add(i, VoxelHelper::rotateCZ);
        }
        for (int i = 0; i < 4; i++) {
            builder.add(i, VoxelHelper::rotateZ);
        }

        HALF_LAYER_SHAPES = builder.build();
    }
}
