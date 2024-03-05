package fr.adrien1106.reframed.generator;

import fr.adrien1106.reframed.ReFramed;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.registry.Registries;

public class GBlockLoot extends FabricBlockLootTableProvider {

    protected GBlockLoot(FabricDataOutput data_output) {
        super(data_output);
    }

    @Override
    public void generate() {
        ReFramed.BLOCKS.forEach(block -> addDrop(block, Registries.ITEM.get(Registries.BLOCK.getId(block))));
    }
}
