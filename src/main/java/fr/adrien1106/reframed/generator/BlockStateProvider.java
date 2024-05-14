package fr.adrien1106.reframed.generator;

import net.minecraft.block.Block;
import net.minecraft.data.client.BlockStateSupplier;

public interface BlockStateProvider {
    BlockStateSupplier getMultipart(Block block);
}
