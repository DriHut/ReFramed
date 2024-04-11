package fr.adrien1106.reframed.generator;

import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.ItemConvertible;

public interface RecipeSetter {

    void setRecipe(RecipeExporter exporter, ItemConvertible convertible);
}
