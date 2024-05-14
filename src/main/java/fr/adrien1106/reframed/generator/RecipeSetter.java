package fr.adrien1106.reframed.generator;

import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.item.ItemConvertible;

import java.util.function.Consumer;

public interface RecipeSetter {

    void setRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible convertible);
}
