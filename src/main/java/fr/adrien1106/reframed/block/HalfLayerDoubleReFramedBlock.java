package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.ReFramed;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.state.StateManager;

import java.util.List;

import static net.minecraft.state.property.Properties.LAYERS;

public abstract class HalfLayerDoubleReFramedBlock extends EdgeDoubleReFramedBlock {

    public HalfLayerDoubleReFramedBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(LAYERS, 1));
    }

    @Override
    @SuppressWarnings("deprecation")
    public List<ItemStack> getDroppedStacks(BlockState state, LootContextParameterSet.Builder builder) {
        List<ItemStack> drops = super.getDroppedStacks(state, builder);
        drops.add(new ItemStack(ReFramed.HALF_LAYER, state.get(LAYERS)-1));
        return drops;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(LAYERS));
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        if (context.getPlayer() == null
            || context.getPlayer().isSneaking()
            || !(context.getStack().getItem() instanceof BlockItem block_item)
        ) return false;
        return block_item.getBlock() == ReFramed.HALF_LAYER && state.get(LAYERS) < 8;
    }
}
