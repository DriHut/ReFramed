package fr.adrien1106.reframed.generator.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.generator.BlockStateProvider;
import fr.adrien1106.reframed.generator.RecipeSetter;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.MultipartBlockStateSupplier;
import net.minecraft.data.client.VariantsBlockStateSupplier;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.book.RecipeCategory;

import static fr.adrien1106.reframed.generator.GBlockstate.variant;
import static net.minecraft.data.client.VariantSettings.Rotation.R0;

public class Carpet implements RecipeSetter, BlockStateProvider {

    @Override
    public void setRecipe(RecipeExporter exporter, ItemConvertible convertible) {
        ShapedRecipeJsonBuilder
                .create(RecipeCategory.BUILDING_BLOCKS, convertible, 3)
                .pattern("II")
                .input('I', ReFramed.CUBE)
                .criterion(FabricRecipeProvider.hasItem(ReFramed.CUBE), FabricRecipeProvider.conditionsFromItem(ReFramed.CUBE))
                .criterion(FabricRecipeProvider.hasItem(convertible), FabricRecipeProvider.conditionsFromItem(convertible))
                .offerTo(exporter);
    }

    @Override
    public MultipartBlockStateSupplier getMultipart(Block block) {
        return null;
    }

    public VariantsBlockStateSupplier getVariant(Block block) {
        return VariantsBlockStateSupplier.create(
                block,
                variant(ReFramed.id("carpet_special"), true, R0, R0)
        );
    }
}