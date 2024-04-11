package fr.adrien1106.reframed.generator.item;

import fr.adrien1106.reframed.generator.RecipeSetter;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;

public class Blueprint implements RecipeSetter {

    @Override
    public void setRecipe(RecipeExporter exporter, ItemConvertible convertible) {
        ShapedRecipeJsonBuilder
            .create(RecipeCategory.TOOLS, convertible, 3)
            .pattern("PI")
            .pattern("PP")
            .input('P', Items.PAPER)
            .input('I', Items.INK_SAC)
            .criterion(FabricRecipeProvider.hasItem(Items.PAPER), FabricRecipeProvider.conditionsFromItem(Items.PAPER))
            .criterion(FabricRecipeProvider.hasItem(convertible), FabricRecipeProvider.conditionsFromItem(convertible))
            .offerTo(exporter);
    }
}
