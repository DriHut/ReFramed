package fr.adrien1106.reframed.generator;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.registry.Registries;

public class GBlockLoot extends FabricBlockLootTableProvider {

    protected GBlockLoot(FabricDataOutput data_output) {
        super(data_output);
    }

    @Override
    public void generate() {
        Generator.BLOCKS.forEach(block -> addDrop(block, Registries.ITEM.get(Registries.BLOCK.getId(block))));
    }
}
