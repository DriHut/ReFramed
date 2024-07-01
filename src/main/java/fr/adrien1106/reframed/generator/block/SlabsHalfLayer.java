package fr.adrien1106.reframed.generator.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.generator.BlockStateProvider;
import fr.adrien1106.reframed.generator.RecipeSetter;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.BlockStateSupplier;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;

import static fr.adrien1106.reframed.generator.GBlockstate.variant;
import static fr.adrien1106.reframed.generator.GBlockstate.when;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.EDGE;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.EDGE_FACE;
import static fr.adrien1106.reframed.util.blocks.Edge.*;
import static net.minecraft.data.client.VariantSettings.Rotation.*;
import static net.minecraft.data.client.When.anyOf;

public class SlabsHalfLayer implements RecipeSetter, BlockStateProvider {

    @Override
    public void setRecipe(RecipeExporter exporter, ItemConvertible convertible) {
        RecipeProvider.offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, convertible, ReFramed.CUBE, 2);
        ShapelessRecipeJsonBuilder
            .create(RecipeCategory.BUILDING_BLOCKS, convertible)
            .input(ReFramed.HALF_LAYER)
            .input(ReFramed.SLAB)
            .criterion(FabricRecipeProvider.hasItem(ReFramed.CUBE), FabricRecipeProvider.conditionsFromItem(ReFramed.CUBE))
            .criterion(FabricRecipeProvider.hasItem(convertible), FabricRecipeProvider.conditionsFromItem(convertible))
            .offerTo(exporter);
    }

    @Override
    public BlockStateSupplier getMultipart(Block block) {
        Identifier slab_model = ReFramed.id("slab_special");
        return HalfLayer.getMultipart(block, "second_half_layer")
            .with(
                anyOf(
                    when(EDGE, DOWN_EAST, EDGE_FACE, 0),
                    when(EDGE, DOWN_SOUTH, EDGE_FACE, 0),
                    when(EDGE, WEST_DOWN, EDGE_FACE, 1),
                    when(EDGE, NORTH_DOWN, EDGE_FACE, 1)
                ),
                variant(slab_model, true, R0, R0))
            .with(
                anyOf(
                    when(EDGE, EAST_SOUTH, EDGE_FACE, 1),
                    when(EDGE, DOWN_SOUTH, EDGE_FACE, 1),
                    when(EDGE, SOUTH_WEST, EDGE_FACE, 0),
                    when(EDGE, SOUTH_UP, EDGE_FACE, 0)
                ),
                variant(slab_model, true, R90, R0))
            .with(
                anyOf(
                    when(EDGE, UP_WEST, EDGE_FACE, 0),
                    when(EDGE, UP_NORTH, EDGE_FACE, 0),
                    when(EDGE, EAST_UP, EDGE_FACE, 1),
                    when(EDGE, SOUTH_UP, EDGE_FACE, 1)
                ),
                variant(slab_model, true, R180, R0))
            .with(
                anyOf(
                    when(EDGE, WEST_NORTH, EDGE_FACE, 1),
                    when(EDGE, UP_NORTH, EDGE_FACE, 1),
                    when(EDGE, NORTH_EAST, EDGE_FACE, 0),
                    when(EDGE, NORTH_DOWN, EDGE_FACE, 0)
                ),
                variant(slab_model, true, R270, R0))
            .with(
                anyOf(
                    when(EDGE, SOUTH_WEST, EDGE_FACE, 1),
                    when(EDGE, UP_WEST, EDGE_FACE, 1),
                    when(EDGE, WEST_NORTH, EDGE_FACE, 0),
                    when(EDGE, WEST_DOWN, EDGE_FACE, 0)
                ),
                variant(slab_model, true, R90, R90))
            .with(
                anyOf(
                    when(EDGE, NORTH_EAST, EDGE_FACE, 1),
                    when(EDGE, DOWN_EAST, EDGE_FACE, 1),
                    when(EDGE, EAST_SOUTH, EDGE_FACE, 0),
                    when(EDGE, EAST_UP, EDGE_FACE, 0)
                ),
                variant(slab_model, true, R90, R270))
        ;
    }

}
