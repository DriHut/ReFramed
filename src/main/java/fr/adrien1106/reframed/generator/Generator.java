package fr.adrien1106.reframed.generator;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.block.Block;

import java.util.List;

import static fr.adrien1106.reframed.ReFramed.*;

public class Generator implements DataGeneratorEntrypoint {

    /**
     *  missing DOOR, IRON_DOOR, CANDLE
     */
    public static List<Block> BLOCKS = List.of(CUBE, STAIRS, SLAB, POST, FENCE, FENCE_GATE, TRAPDOOR, IRON_TRAPDOOR, PRESSURE_PLATE, BUTTON, LEVER, WALL, CARPET, PANE);
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator data_generator) {
        FabricDataGenerator.Pack myPack = data_generator.createPack();
        myPack.addProvider(GBlockLoot::new);
        myPack.addProvider(GBlockstate::new);
    }
}
