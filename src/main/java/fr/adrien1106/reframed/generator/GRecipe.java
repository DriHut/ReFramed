package fr.adrien1106.reframed.generator;

import fr.adrien1106.reframed.ReFramed;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;

public class GRecipe extends FabricRecipeProvider {
    public GRecipe(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        ReFramed.BLOCKS.forEach(block -> {
            if (block instanceof RecipeSetter provider) provider.setRecipe(exporter);
        });
    }
}
