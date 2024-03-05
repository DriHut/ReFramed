package fr.adrien1106.reframed.generator;

import fr.adrien1106.reframed.ReFramed;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.Registries;

import java.util.Arrays;
import java.util.stream.Collectors;

public class GLanguage extends FabricLanguageProvider {
    protected GLanguage(FabricDataOutput dataOutput) {
        super(dataOutput, "en_us");
    }

    @Override
    public void generateTranslations(TranslationBuilder builder) {
        builder.add(Registries.ITEM_GROUP.getKey(ReFramed.ITEM_GROUP).get(), "Frames");
        builder.add("advancements.reframed.description", "Get all the frame types.");
        ReFramed.BLOCKS.forEach(block -> {
            builder.add(block, beautify(Registries.BLOCK.getId(block).getPath()) + " Frame");
        });
    }

    private static String beautify(String name) {
        return Arrays.stream(name.split("_"))
            .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
            .collect(Collectors.joining(" "));
    }
}
