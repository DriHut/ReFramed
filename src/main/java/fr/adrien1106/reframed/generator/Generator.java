package fr.adrien1106.reframed.generator;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class Generator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator data_generator) {
        FabricDataGenerator.Pack my_pack = data_generator.createPack();
        my_pack.addProvider(GBlockLoot::new);
        my_pack.addProvider(GBlockstate::new);
        my_pack.addProvider(GLanguage::new);
        my_pack.addProvider(GBlockTag::new);
        my_pack.addProvider(GRecipe::new);
        my_pack.addProvider(GAdvancement::new);
    }
}
