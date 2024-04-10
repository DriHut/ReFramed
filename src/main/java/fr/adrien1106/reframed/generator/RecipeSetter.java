package fr.adrien1106.reframed.generator;

import net.minecraft.data.server.recipe.RecipeJsonProvider;

import java.util.function.Consumer;

public interface RecipeSetter {

    void setRecipe(Consumer<RecipeJsonProvider> exporter);
}
