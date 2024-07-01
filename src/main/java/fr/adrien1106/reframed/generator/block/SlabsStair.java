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
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;

import static fr.adrien1106.reframed.util.blocks.BlockProperties.EDGE;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.EDGE_FACE;
import static net.minecraft.data.client.VariantSettings.Rotation.*;

public class SlabsStair implements RecipeSetter, BlockStateProvider {

    @Override
    public void setRecipe(RecipeExporter exporter, ItemConvertible convertible) {
        RecipeProvider.offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, convertible, ReFramed.CUBE);
        ShapelessRecipeJsonBuilder
            .create(RecipeCategory.BUILDING_BLOCKS, convertible)
            .input(ReFramed.STEP)
            .input(ReFramed.SLAB)
            .criterion(FabricRecipeProvider.hasItem(ReFramed.CUBE), FabricRecipeProvider.conditionsFromItem(ReFramed.CUBE))
            .criterion(FabricRecipeProvider.hasItem(convertible), FabricRecipeProvider.conditionsFromItem(convertible))
            .offerTo(exporter);
    }

    @Override
    public MultipartBlockStateSupplier getMultipart(Block block) {
        Identifier model_id = ReFramed.id("slabs_stair_special");
        Identifier side_id = ReFramed.id("slabs_stair_side_special");
        return MultipartBlockStateSupplier.create(block)
            // BOTTOM
            .with(GBlockstate.when(EDGE, Edge.DOWN_EAST, EDGE_FACE, 0),
                GBlockstate.variant(model_id, true, R0, R0))
            .with(GBlockstate.when(EDGE, Edge.DOWN_SOUTH, EDGE_FACE, 0),
                GBlockstate.variant(model_id, true, R0, R90))
            .with(GBlockstate.when(EDGE, Edge.WEST_DOWN, EDGE_FACE, 1),
                GBlockstate.variant(model_id, true, R0, R180))
            .with(GBlockstate.when(EDGE, Edge.NORTH_DOWN, EDGE_FACE, 1),
                GBlockstate.variant(model_id, true, R0, R270))
            // TOP
            .with(GBlockstate.when(EDGE, Edge.EAST_UP, EDGE_FACE, 1),
                GBlockstate.variant(model_id, true, R180, R0))
            .with(GBlockstate.when(EDGE, Edge.SOUTH_UP, EDGE_FACE, 1),
                GBlockstate.variant(model_id, true, R180, R90))
            .with(GBlockstate.when(EDGE, Edge.UP_WEST, EDGE_FACE, 0),
                GBlockstate.variant(model_id, true, R180, R180))
            .with(GBlockstate.when(EDGE, Edge.UP_NORTH, EDGE_FACE, 0),
                GBlockstate.variant(model_id, true, R180, R270))
            // EAST
            .with(GBlockstate.when(EDGE, Edge.EAST_SOUTH, EDGE_FACE, 0),
                GBlockstate.variant(side_id, true, R0, R0))
            .with(GBlockstate.when(EDGE, Edge.EAST_UP, EDGE_FACE, 0),
                GBlockstate.variant(side_id, true, R90, R0))
            .with(GBlockstate.when(EDGE, Edge.NORTH_EAST, EDGE_FACE, 1),
                GBlockstate.variant(side_id, true, R180, R0))
            .with(GBlockstate.when(EDGE, Edge.DOWN_EAST, EDGE_FACE, 1),
                GBlockstate.variant(side_id, true, R270, R0))
            // SOUTH
            .with(GBlockstate.when(EDGE, Edge.SOUTH_WEST, EDGE_FACE, 0),
                GBlockstate.variant(side_id, true, R0, R90))
            .with(GBlockstate.when(EDGE, Edge.SOUTH_UP, EDGE_FACE, 0),
                GBlockstate.variant(side_id, true, R90, R90))
            .with(GBlockstate.when(EDGE, Edge.EAST_SOUTH, EDGE_FACE, 1),
                GBlockstate.variant(side_id, true, R180, R90))
            .with(GBlockstate.when(EDGE, Edge.DOWN_SOUTH, EDGE_FACE, 1),
                GBlockstate.variant(side_id, true, R270, R90))
            // WEST
            .with(GBlockstate.when(EDGE, Edge.WEST_NORTH, EDGE_FACE, 0),
                GBlockstate.variant(side_id, true, R0, R180))
            .with(GBlockstate.when(EDGE, Edge.UP_WEST, EDGE_FACE, 1),
                GBlockstate.variant(side_id, true, R90, R180))
            .with(GBlockstate.when(EDGE, Edge.SOUTH_WEST, EDGE_FACE, 1),
                GBlockstate.variant(side_id, true, R180, R180))
            .with(GBlockstate.when(EDGE, Edge.WEST_DOWN, EDGE_FACE, 0),
                GBlockstate.variant(side_id, true, R270, R180))
            // NORTH
            .with(GBlockstate.when(EDGE, Edge.NORTH_EAST, EDGE_FACE, 0),
                GBlockstate.variant(side_id, true, R0, R270))
            .with(GBlockstate.when(EDGE, Edge.UP_NORTH, EDGE_FACE, 1),
                GBlockstate.variant(side_id, true, R90, R270))
            .with(GBlockstate.when(EDGE, Edge.WEST_NORTH, EDGE_FACE, 1),
                GBlockstate.variant(side_id, true, R180, R270))
            .with(GBlockstate.when(EDGE, Edge.NORTH_DOWN, EDGE_FACE, 0),
                GBlockstate.variant(side_id, true, R270, R270))
            ;
    }
}
