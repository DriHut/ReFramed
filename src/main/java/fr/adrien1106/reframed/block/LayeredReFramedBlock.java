package fr.adrien1106.reframed.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static net.minecraft.state.property.Properties.LAYERS;

public abstract class LayeredReFramedBlock extends WaterloggableReFramedBlock {

    public LayeredReFramedBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(LAYERS, 1));
    }

    @Override
    @SuppressWarnings("deprecation")
    public List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
        List<ItemStack> drops = super.getDroppedStacks(state, builder);
        drops.forEach((stack) -> {
            if (stack.getItem() instanceof BlockItem bi && bi.getBlock() instanceof LayeredReFramedBlock)
                stack.setCount(state.get(LAYERS));
        });
        return drops;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(LAYERS));
    }

    @Override
    @SuppressWarnings("deprecation")
    public abstract VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context);

    @Override
    @SuppressWarnings("deprecation")
    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        if (context.getPlayer() == null) return false;
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
}
