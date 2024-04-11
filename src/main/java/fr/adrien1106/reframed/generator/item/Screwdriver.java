package fr.adrien1106.reframed.generator.item;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.generator.RecipeSetter;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;

public class Screwdriver implements RecipeSetter {

    @Override
    public void setRecipe(RecipeExporter exporter, ItemConvertible convertible) {
        ShapedRecipeJsonBuilder
            .create(RecipeCategory.TOOLS, convertible)
            .pattern("  I")
            .pattern(" I ")
            .pattern("C  ")
            .input('I', Items.IRON_INGOT)
            .input('C', ReFramed.CUBE)
            .criterion(FabricRecipeProvider.hasItem(ReFramed.CUBE), FabricRecipeProvider.conditionsFromItem(ReFramed.CUBE))
            .criterion(FabricRecipeProvider.hasItem(convertible), FabricRecipeProvider.conditionsFromItem(convertible))
            .offerTo(exporter);
    }
}
