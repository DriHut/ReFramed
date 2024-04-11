package fr.adrien1106.reframed.generator.block;

import fr.adrien1106.reframed.generator.RecipeSetter;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;

public class Cube implements RecipeSetter {

    @Override
    public void setRecipe(RecipeExporter exporter, ItemConvertible convertible) {
        ShapedRecipeJsonBuilder
            .create(RecipeCategory.BUILDING_BLOCKS, convertible)
            .pattern("III")
            .pattern("I~I")
            .pattern("III")
            .input('I', Items.BAMBOO)
            .input('~', Items.STRING)
            .criterion(FabricRecipeProvider.hasItem(Items.BAMBOO), FabricRecipeProvider.conditionsFromItem(Items.BAMBOO))
            .criterion(FabricRecipeProvider.hasItem(convertible), FabricRecipeProvider.conditionsFromItem(convertible))
            .offerTo(exporter);
    }
}
