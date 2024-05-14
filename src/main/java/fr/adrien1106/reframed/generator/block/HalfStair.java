package fr.adrien1106.reframed.generator.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.generator.BlockStateProvider;
import fr.adrien1106.reframed.generator.GBlockstate;
import fr.adrien1106.reframed.generator.RecipeSetter;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.BlockStateSupplier;
import net.minecraft.data.client.MultipartBlockStateSupplier;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

import static fr.adrien1106.reframed.util.blocks.BlockProperties.CORNER;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.CORNER_FACE;
import static fr.adrien1106.reframed.util.blocks.Corner.*;
import static fr.adrien1106.reframed.util.blocks.Corner.NORTH_EAST_UP;
import static net.minecraft.data.client.VariantSettings.Rotation.*;

public class HalfStair implements RecipeSetter, BlockStateProvider {

    @Override
    public void setRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible convertible) {
        RecipeProvider.offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, convertible, ReFramed.CUBE, 2);
        ShapedRecipeJsonBuilder
            .create(RecipeCategory.BUILDING_BLOCKS, convertible, 4)
            .pattern("I ")
            .pattern("II")
            .input('I', ReFramed.CUBE)
            .criterion(FabricRecipeProvider.hasItem(ReFramed.CUBE), FabricRecipeProvider.conditionsFromItem(ReFramed.CUBE))
            .criterion(FabricRecipeProvider.hasItem(convertible), FabricRecipeProvider.conditionsFromItem(convertible))
            .offerTo(exporter);
    }

    @Override
    public BlockStateSupplier getMultipart(Block block) {
        return getMultipart(
            block,
            ReFramed.id("half_stair_down_special"),
            ReFramed.id("half_stair_side_special")
        );
    }

    public static BlockStateSupplier getMultipart(Block block, Identifier model_down, Identifier model_side) {
        return MultipartBlockStateSupplier.create(block)
            .with(GBlockstate.when(CORNER, NORTH_EAST_DOWN, CORNER_FACE, 1),
                GBlockstate.variant(model_side, true, R0, R0))
            .with(GBlockstate.when(CORNER, NORTH_EAST_DOWN, CORNER_FACE, 0),
                GBlockstate.variant(model_side, true, R90, R270))
            .with(GBlockstate.when(CORNER, NORTH_EAST_DOWN, CORNER_FACE, 2),
                GBlockstate.variant(model_down, true, R0, R0))

            .with(GBlockstate.when(CORNER, EAST_SOUTH_DOWN, CORNER_FACE, 1),
                GBlockstate.variant(model_side, true, R0, R90))
            .with(GBlockstate.when(CORNER, EAST_SOUTH_DOWN, CORNER_FACE, 0),
                GBlockstate.variant(model_side, true, R90, R0))
            .with(GBlockstate.when(CORNER, EAST_SOUTH_DOWN, CORNER_FACE, 2),
                GBlockstate.variant(model_down, true, R0, R90))

            .with(GBlockstate.when(CORNER, SOUTH_WEST_DOWN, CORNER_FACE, 1),
                GBlockstate.variant(model_side, true, R0, R180))
            .with(GBlockstate.when(CORNER, SOUTH_WEST_DOWN, CORNER_FACE, 0),
                GBlockstate.variant(model_side, true, R90, R90))
            .with(GBlockstate.when(CORNER, SOUTH_WEST_DOWN, CORNER_FACE, 2),
                GBlockstate.variant(model_down, true, R0, R180))

            .with(GBlockstate.when(CORNER, WEST_NORTH_DOWN, CORNER_FACE, 1),
                GBlockstate.variant(model_side, true, R0, R270))
            .with(GBlockstate.when(CORNER, WEST_NORTH_DOWN, CORNER_FACE, 0),
                GBlockstate.variant(model_side, true, R90, R180))
            .with(GBlockstate.when(CORNER, WEST_NORTH_DOWN, CORNER_FACE, 2),
                GBlockstate.variant(model_down, true, R0, R270))

            .with(GBlockstate.when(CORNER, EAST_SOUTH_UP, CORNER_FACE, 0),
                GBlockstate.variant(model_side, true, R180, R0))
            .with(GBlockstate.when(CORNER, EAST_SOUTH_UP, CORNER_FACE, 1),
                GBlockstate.variant(model_side, true, R270, R90))
            .with(GBlockstate.when(CORNER, EAST_SOUTH_UP, CORNER_FACE, 2),
                GBlockstate.variant(model_down, true, R180, R0))

            .with(GBlockstate.when(CORNER, SOUTH_WEST_UP, CORNER_FACE, 0),
                GBlockstate.variant(model_side, true, R180, R90))
            .with(GBlockstate.when(CORNER, SOUTH_WEST_UP, CORNER_FACE, 1),
                GBlockstate.variant(model_side, true, R270, R180))
            .with(GBlockstate.when(CORNER, SOUTH_WEST_UP, CORNER_FACE, 2),
                GBlockstate.variant(model_down, true, R180, R90))

            .with(GBlockstate.when(CORNER, WEST_NORTH_UP, CORNER_FACE, 0),
                GBlockstate.variant(model_side, true, R180, R180))
            .with(GBlockstate.when(CORNER, WEST_NORTH_UP, CORNER_FACE, 1),
                GBlockstate.variant(model_side, true, R270, R270))
            .with(GBlockstate.when(CORNER, WEST_NORTH_UP, CORNER_FACE, 2),
                GBlockstate.variant(model_down, true, R180, R180))

            .with(GBlockstate.when(CORNER, NORTH_EAST_UP, CORNER_FACE, 0),
                GBlockstate.variant(model_side, true, R180, R270))
            .with(GBlockstate.when(CORNER, NORTH_EAST_UP, CORNER_FACE, 1),
                GBlockstate.variant(model_side, true, R270, R0))
            .with(GBlockstate.when(CORNER, NORTH_EAST_UP, CORNER_FACE, 2),
                GBlockstate.variant(model_down, true, R180, R270));
    }
}
