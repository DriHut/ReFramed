package fr.adrien1106.reframed.generator.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.generator.BlockStateProvider;
import fr.adrien1106.reframed.generator.GBlockstate;
import fr.adrien1106.reframed.generator.RecipeSetter;
import fr.adrien1106.reframed.util.blocks.Corner;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.MultipartBlockStateSupplier;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;

import static fr.adrien1106.reframed.util.blocks.BlockProperties.*;
import static net.minecraft.data.client.VariantSettings.Rotation.*;
import static net.minecraft.data.client.VariantSettings.Rotation.R270;

public class HalfStairsStepStair implements RecipeSetter, BlockStateProvider {

    @Override
    public void setRecipe(RecipeExporter exporter, ItemConvertible convertible) {
        RecipeProvider.offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, convertible, ReFramed.CUBE);
        ShapelessRecipeJsonBuilder
            .create(RecipeCategory.BUILDING_BLOCKS, convertible)
            .input(ReFramed.SMALL_CUBE)
            .input(ReFramed.HALF_STAIR)
            .criterion(FabricRecipeProvider.hasItem(ReFramed.CUBE), FabricRecipeProvider.conditionsFromItem(ReFramed.CUBE))
            .criterion(FabricRecipeProvider.hasItem(convertible), FabricRecipeProvider.conditionsFromItem(convertible))
            .offerTo(exporter);
    }

    @Override
    public MultipartBlockStateSupplier getMultipart(Block block) {
        Identifier model_1_id = ReFramed.id("half_stairs_step_stair_1_special");
        Identifier side_1_id = ReFramed.id("half_stairs_step_stair_side_1_special");
        Identifier model_2_id = ReFramed.id("half_stairs_step_stair_2_special");
        Identifier side_2_id = ReFramed.id("half_stairs_step_stair_side_2_special");
        return MultipartBlockStateSupplier.create(block)
            // BOTTOM
            // --- 1 ---
            .with(GBlockstate.when(CORNER, Corner.EAST_SOUTH_DOWN, CORNER_FACE, 2, CORNER_FEATURE, 0),
                GBlockstate.variant(model_1_id, true, R0, R0))
            .with(GBlockstate.when(CORNER, Corner.SOUTH_WEST_DOWN, CORNER_FACE, 2, CORNER_FEATURE, 0),
                GBlockstate.variant(model_1_id, true, R0, R90))
            .with(GBlockstate.when(CORNER, Corner.WEST_NORTH_DOWN, CORNER_FACE, 2, CORNER_FEATURE, 0),
                GBlockstate.variant(model_1_id, true, R0, R180))
            .with(GBlockstate.when(CORNER, Corner.NORTH_EAST_DOWN, CORNER_FACE, 2, CORNER_FEATURE, 0),
                GBlockstate.variant(model_1_id, true, R0, R270))
            // --- 2 ---
            .with(GBlockstate.when(CORNER, Corner.EAST_SOUTH_DOWN, CORNER_FACE, 2, CORNER_FEATURE, 1),
                GBlockstate.variant(model_2_id, true, R0, R0))
            .with(GBlockstate.when(CORNER, Corner.SOUTH_WEST_DOWN, CORNER_FACE, 2, CORNER_FEATURE, 1),
                GBlockstate.variant(model_2_id, true, R0, R90))
            .with(GBlockstate.when(CORNER, Corner.WEST_NORTH_DOWN, CORNER_FACE, 2, CORNER_FEATURE, 1),
                GBlockstate.variant(model_2_id, true, R0, R180))
            .with(GBlockstate.when(CORNER, Corner.NORTH_EAST_DOWN, CORNER_FACE, 2, CORNER_FEATURE, 1),
                GBlockstate.variant(model_2_id, true, R0, R270))
            // TOP
            // --- 1 ---
            .with(GBlockstate.when(CORNER, Corner.NORTH_EAST_UP, CORNER_FACE, 2, CORNER_FEATURE, 1),
                GBlockstate.variant(model_1_id, true, R180, R0))
            .with(GBlockstate.when(CORNER, Corner.EAST_SOUTH_UP, CORNER_FACE, 2, CORNER_FEATURE, 1),
                GBlockstate.variant(model_1_id, true, R180, R90))
            .with(GBlockstate.when(CORNER, Corner.SOUTH_WEST_UP, CORNER_FACE, 2, CORNER_FEATURE, 1),
                GBlockstate.variant(model_1_id, true, R180, R180))
            .with(GBlockstate.when(CORNER, Corner.WEST_NORTH_UP, CORNER_FACE, 2, CORNER_FEATURE, 1),
                GBlockstate.variant(model_1_id, true, R180, R270))
            // --- 2 ---
            .with(GBlockstate.when(CORNER, Corner.NORTH_EAST_UP, CORNER_FACE, 2, CORNER_FEATURE, 0),
                GBlockstate.variant(model_2_id, true, R180, R0))
            .with(GBlockstate.when(CORNER, Corner.EAST_SOUTH_UP, CORNER_FACE, 2, CORNER_FEATURE, 0),
                GBlockstate.variant(model_2_id, true, R180, R90))
            .with(GBlockstate.when(CORNER, Corner.SOUTH_WEST_UP, CORNER_FACE, 2, CORNER_FEATURE, 0),
                GBlockstate.variant(model_2_id, true, R180, R180))
            .with(GBlockstate.when(CORNER, Corner.WEST_NORTH_UP, CORNER_FACE, 2, CORNER_FEATURE, 0),
                GBlockstate.variant(model_2_id, true, R180, R270))
            // EAST
            // --- 1 ---
            .with(GBlockstate.when(CORNER, Corner.EAST_SOUTH_DOWN, CORNER_FACE, 0, CORNER_FEATURE, 0),
                GBlockstate.variant(side_1_id, true, R0, R0))
            .with(GBlockstate.when(CORNER, Corner.EAST_SOUTH_UP, CORNER_FACE, 0, CORNER_FEATURE, 1),
                GBlockstate.variant(side_1_id, true, R90, R0))
            .with(GBlockstate.when(CORNER, Corner.NORTH_EAST_UP, CORNER_FACE, 1, CORNER_FEATURE, 0),
                GBlockstate.variant(side_1_id, true, R180, R0))
            .with(GBlockstate.when(CORNER, Corner.NORTH_EAST_DOWN, CORNER_FACE, 1, CORNER_FEATURE, 1),
                GBlockstate.variant(side_1_id, true, R270, R0))
            // --- 2 ---
            .with(GBlockstate.when(CORNER, Corner.EAST_SOUTH_DOWN, CORNER_FACE, 0, CORNER_FEATURE, 1),
                GBlockstate.variant(side_2_id, true, R0, R0))
            .with(GBlockstate.when(CORNER, Corner.EAST_SOUTH_UP, CORNER_FACE, 0, CORNER_FEATURE, 0),
                GBlockstate.variant(side_2_id, true, R90, R0))
            .with(GBlockstate.when(CORNER, Corner.NORTH_EAST_UP, CORNER_FACE, 1, CORNER_FEATURE, 1),
                GBlockstate.variant(side_2_id, true, R180, R0))
            .with(GBlockstate.when(CORNER, Corner.NORTH_EAST_DOWN, CORNER_FACE, 1, CORNER_FEATURE, 0),
                GBlockstate.variant(side_2_id, true, R270, R0))
            // SOUTH
            // --- 1 ---
            .with(GBlockstate.when(CORNER, Corner.SOUTH_WEST_DOWN, CORNER_FACE, 0, CORNER_FEATURE, 0),
                GBlockstate.variant(side_1_id, true, R0, R90))
            .with(GBlockstate.when(CORNER, Corner.SOUTH_WEST_UP, CORNER_FACE, 0, CORNER_FEATURE, 1),
                GBlockstate.variant(side_1_id, true, R90, R90))
            .with(GBlockstate.when(CORNER, Corner.EAST_SOUTH_UP, CORNER_FACE, 1, CORNER_FEATURE, 0),
                GBlockstate.variant(side_1_id, true, R180, R90))
            .with(GBlockstate.when(CORNER, Corner.EAST_SOUTH_DOWN, CORNER_FACE, 1, CORNER_FEATURE, 1),
                GBlockstate.variant(side_1_id, true, R270, R90))
            // --- 2 ---
            .with(GBlockstate.when(CORNER, Corner.SOUTH_WEST_DOWN, CORNER_FACE, 0, CORNER_FEATURE, 1),
                GBlockstate.variant(side_2_id, true, R0, R90))
            .with(GBlockstate.when(CORNER, Corner.SOUTH_WEST_UP, CORNER_FACE, 0, CORNER_FEATURE, 0),
                GBlockstate.variant(side_2_id, true, R90, R90))
            .with(GBlockstate.when(CORNER, Corner.EAST_SOUTH_UP, CORNER_FACE, 1, CORNER_FEATURE, 1),
                GBlockstate.variant(side_2_id, true, R180, R90))
            .with(GBlockstate.when(CORNER, Corner.EAST_SOUTH_DOWN, CORNER_FACE, 1, CORNER_FEATURE, 0),
                GBlockstate.variant(side_2_id, true, R270, R90))
            // WEST
            // --- 1 ---
            .with(GBlockstate.when(CORNER, Corner.WEST_NORTH_DOWN, CORNER_FACE, 0, CORNER_FEATURE, 0),
                GBlockstate.variant(side_1_id, true, R0, R180))
            .with(GBlockstate.when(CORNER, Corner.WEST_NORTH_UP, CORNER_FACE, 0, CORNER_FEATURE, 1),
                GBlockstate.variant(side_1_id, true, R90, R180))
            .with(GBlockstate.when(CORNER, Corner.SOUTH_WEST_UP, CORNER_FACE, 1, CORNER_FEATURE, 0),
                GBlockstate.variant(side_1_id, true, R180, R180))
            .with(GBlockstate.when(CORNER, Corner.SOUTH_WEST_DOWN, CORNER_FACE, 1, CORNER_FEATURE, 1),
                GBlockstate.variant(side_1_id, true, R270, R180))
            // --- 2 ---
            .with(GBlockstate.when(CORNER, Corner.WEST_NORTH_DOWN, CORNER_FACE, 0, CORNER_FEATURE, 1),
                GBlockstate.variant(side_2_id, true, R0, R180))
            .with(GBlockstate.when(CORNER, Corner.WEST_NORTH_UP, CORNER_FACE, 0, CORNER_FEATURE, 0),
                GBlockstate.variant(side_2_id, true, R90, R180))
            .with(GBlockstate.when(CORNER, Corner.SOUTH_WEST_UP, CORNER_FACE, 1, CORNER_FEATURE, 1),
                GBlockstate.variant(side_2_id, true, R180, R180))
            .with(GBlockstate.when(CORNER, Corner.SOUTH_WEST_DOWN, CORNER_FACE, 1, CORNER_FEATURE, 0),
                GBlockstate.variant(side_2_id, true, R270, R180))
            // NORTH
            // --- 1 ---
            .with(GBlockstate.when(CORNER, Corner.NORTH_EAST_DOWN, CORNER_FACE, 0, CORNER_FEATURE, 0),
                GBlockstate.variant(side_1_id, true, R0, R270))
            .with(GBlockstate.when(CORNER, Corner.NORTH_EAST_UP, CORNER_FACE, 0, CORNER_FEATURE, 1),
                GBlockstate.variant(side_1_id, true, R90, R270))
            .with(GBlockstate.when(CORNER, Corner.WEST_NORTH_UP, CORNER_FACE, 1, CORNER_FEATURE, 0),
                GBlockstate.variant(side_1_id, true, R180, R270))
            .with(GBlockstate.when(CORNER, Corner.WEST_NORTH_DOWN, CORNER_FACE, 1, CORNER_FEATURE, 1),
                GBlockstate.variant(side_1_id, true, R270, R270))
            // --- 2 ---
            .with(GBlockstate.when(CORNER, Corner.NORTH_EAST_DOWN, CORNER_FACE, 0, CORNER_FEATURE, 1),
                GBlockstate.variant(side_2_id, true, R0, R270))
            .with(GBlockstate.when(CORNER, Corner.NORTH_EAST_UP, CORNER_FACE, 0, CORNER_FEATURE, 0),
                GBlockstate.variant(side_2_id, true, R90, R270))
            .with(GBlockstate.when(CORNER, Corner.WEST_NORTH_UP, CORNER_FACE, 1, CORNER_FEATURE, 1),
                GBlockstate.variant(side_2_id, true, R180, R270))
            .with(GBlockstate.when(CORNER, Corner.WEST_NORTH_DOWN, CORNER_FACE, 1, CORNER_FEATURE, 0),
                GBlockstate.variant(side_2_id, true, R270, R270))
            ;
    }
}
