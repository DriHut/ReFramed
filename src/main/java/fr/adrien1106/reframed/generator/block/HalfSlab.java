package fr.adrien1106.reframed.generator.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.generator.BlockStateProvider;
import fr.adrien1106.reframed.generator.RecipeSetter;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.MultipartBlockStateSupplier;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.book.RecipeCategory;

public class HalfSlab implements RecipeSetter, BlockStateProvider {

    @Override
    public void setRecipe(RecipeExporter exporter, ItemConvertible convertible) {
        RecipeProvider.offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, convertible, ReFramed.CUBE, 4);
        ShapelessRecipeJsonBuilder
            .create(RecipeCategory.BUILDING_BLOCKS, convertible, 2)
            .input(ReFramed.SLAB)
            .criterion(FabricRecipeProvider.hasItem(ReFramed.CUBE), FabricRecipeProvider.conditionsFromItem(ReFramed.CUBE))
            .criterion(FabricRecipeProvider.hasItem(convertible), FabricRecipeProvider.conditionsFromItem(convertible))
            .offerTo(exporter);
    }

    @Override
    public MultipartBlockStateSupplier getMultipart(Block block) {
        return Slab.getMultipart(block, "half_slab");
    }


}
