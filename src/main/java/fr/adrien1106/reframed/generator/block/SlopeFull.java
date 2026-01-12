package fr.adrien1106.reframed.generator.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.generator.BlockStateProvider;
import fr.adrien1106.reframed.generator.GBlockstate;
import fr.adrien1106.reframed.generator.RecipeSetter;
import fr.adrien1106.reframed.util.blocks.Edge;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.MultipartBlockStateSupplier;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;

import static fr.adrien1106.reframed.util.blocks.BlockProperties.EDGE;
import static net.minecraft.data.client.VariantSettings.Rotation.*;

public class SlopeFull implements RecipeSetter, BlockStateProvider {

    @Override
    public void setRecipe(RecipeExporter exporter, ItemConvertible convertible) {
        RecipeProvider.offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, convertible, ReFramed.CUBE, 2);
        ShapedRecipeJsonBuilder
                .create(RecipeCategory.BUILDING_BLOCKS, convertible, 6)
                .pattern("  I")
                .pattern(" II")
                .pattern("III")
                .input('I', ReFramed.CUBE)
                .criterion(FabricRecipeProvider.hasItem(ReFramed.CUBE), FabricRecipeProvider.conditionsFromItem(ReFramed.CUBE))
                .criterion(FabricRecipeProvider.hasItem(convertible), FabricRecipeProvider.conditionsFromItem(convertible))
                .offerTo(exporter);
    }

    @Override
    public MultipartBlockStateSupplier getMultipart(Block block) {
        Identifier model_id = ReFramed.id("slope_full_special");
        return MultipartBlockStateSupplier.create(block)

                .with(GBlockstate.when(EDGE, Edge.NORTH_DOWN),
                        GBlockstate.variant(model_id, true, R0, R270))
                .with(GBlockstate.when(EDGE, Edge.DOWN_SOUTH),
                        GBlockstate.variant(model_id, true, R0, R90))
                .with(GBlockstate.when(EDGE, Edge.SOUTH_UP),
                        GBlockstate.variant(model_id, true, R180, R90))
                .with(GBlockstate.when(EDGE, Edge.UP_NORTH),
                        GBlockstate.variant(model_id, true, R180, R270))

                .with(GBlockstate.when(EDGE, Edge.WEST_DOWN),
                        GBlockstate.variant(model_id, true, R0, R180))
                .with(GBlockstate.when(EDGE, Edge.DOWN_EAST),
                        GBlockstate.variant(model_id, true, R0, R0))
                .with(GBlockstate.when(EDGE, Edge.EAST_UP),
                        GBlockstate.variant(model_id, true, R180, R0))
                .with(GBlockstate.when(EDGE, Edge.UP_WEST),
                        GBlockstate.variant(model_id, true, R180, R180))

                .with(GBlockstate.when(EDGE, Edge.WEST_NORTH),
                        GBlockstate.variant(model_id, true, R90, R180))
                .with(GBlockstate.when(EDGE, Edge.NORTH_EAST),
                        GBlockstate.variant(model_id, true, R90, R270))
                .with(GBlockstate.when(EDGE, Edge.EAST_SOUTH),
                        GBlockstate.variant(model_id, true, R90, R0))
                .with(GBlockstate.when(EDGE, Edge.SOUTH_WEST),
                        GBlockstate.variant(model_id, true, R90, R90));
    }
}