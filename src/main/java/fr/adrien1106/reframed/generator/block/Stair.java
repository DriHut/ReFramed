package fr.adrien1106.reframed.generator.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.generator.BlockStateProvider;
import fr.adrien1106.reframed.generator.GBlockstate;
import fr.adrien1106.reframed.generator.RecipeSetter;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.MultipartBlockStateSupplier;
import net.minecraft.data.client.When;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

import static fr.adrien1106.reframed.util.blocks.BlockProperties.EDGE;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.STAIR_SHAPE;
import static fr.adrien1106.reframed.util.blocks.Edge.*;
import static fr.adrien1106.reframed.util.blocks.StairShape.*;
import static net.minecraft.data.client.VariantSettings.Rotation.*;

public class Stair implements RecipeSetter, BlockStateProvider {

    @Override
    public void setRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible convertible) {
        RecipeProvider.offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, convertible, ReFramed.CUBE);
        ShapedRecipeJsonBuilder
            .create(RecipeCategory.BUILDING_BLOCKS, convertible, 4)
            .pattern("I  ")
            .pattern("II ")
            .pattern("III")
            .input('I', ReFramed.CUBE)
            .criterion(FabricRecipeProvider.hasItem(ReFramed.CUBE), FabricRecipeProvider.conditionsFromItem(ReFramed.CUBE))
            .criterion(FabricRecipeProvider.hasItem(convertible), FabricRecipeProvider.conditionsFromItem(convertible))
            .offerTo(exporter);
    }

    @Override
    public MultipartBlockStateSupplier getMultipart(Block block) {
        return getMultipart(block, false);
    }

    public static MultipartBlockStateSupplier getMultipart(Block block, boolean is_double) {
        String infix = is_double ? "s_cube" : "";
        Identifier straight_id = ReFramed.id("stair" + infix + "_special");
        Identifier double_outer_id = ReFramed.id("outers_stair" + infix + "_special");
        Identifier inner_id = ReFramed.id("inner_stair" + infix + "_special");
        Identifier outer_id = ReFramed.id("outer_stair" + infix + "_special");
        Identifier outer_side_id = ReFramed.id("outer_side_stair" + infix + "_special");
        return MultipartBlockStateSupplier.create(block)
            /* STRAIGHT X AXIS */
            .with(GBlockstate.when(EDGE, DOWN_EAST, STAIR_SHAPE, STRAIGHT),
                GBlockstate.variant(straight_id, true, R0, R0))
            .with(GBlockstate.when(EDGE, EAST_UP, STAIR_SHAPE, STRAIGHT),
                GBlockstate.variant(straight_id, true, R180, R0))
            .with(GBlockstate.when(EDGE, UP_WEST, STAIR_SHAPE, STRAIGHT),
                GBlockstate.variant(straight_id, true, R180, R180))
            .with(GBlockstate.when(EDGE, WEST_DOWN, STAIR_SHAPE, STRAIGHT),
                GBlockstate.variant(straight_id, true, R0, R180))
            /* STRAIGHT Y AXIS */
            .with(GBlockstate.when(EDGE, EAST_SOUTH, STAIR_SHAPE, STRAIGHT),
                GBlockstate.variant(straight_id, true, R90, R0))
            .with(GBlockstate.when(EDGE, SOUTH_WEST, STAIR_SHAPE, STRAIGHT),
                GBlockstate.variant(straight_id, true, R90, R90))
            .with(GBlockstate.when(EDGE, WEST_NORTH, STAIR_SHAPE, STRAIGHT),
                GBlockstate.variant(straight_id, true, R90, R180))
            .with(GBlockstate.when(EDGE, NORTH_EAST, STAIR_SHAPE, STRAIGHT),
                GBlockstate.variant(straight_id, true, R90, R270))
            /* STRAIGHT Z AXIS */
            .with(GBlockstate.when(EDGE, DOWN_SOUTH, STAIR_SHAPE, STRAIGHT),
                GBlockstate.variant(straight_id, true, R0, R90))
            .with(GBlockstate.when(EDGE, NORTH_DOWN, STAIR_SHAPE, STRAIGHT),
                GBlockstate.variant(straight_id, true, R0, R270))
            .with(GBlockstate.when(EDGE, UP_NORTH, STAIR_SHAPE, STRAIGHT),
                GBlockstate.variant(straight_id, true, R180, R270))
            .with(GBlockstate.when(EDGE, SOUTH_UP, STAIR_SHAPE, STRAIGHT),
                GBlockstate.variant(straight_id, true, R180, R90))
            /* INNER BOTTOM */
            .with(When.anyOf(
                    GBlockstate.when(EDGE, NORTH_DOWN, STAIR_SHAPE, INNER_RIGHT),
                    GBlockstate.when(EDGE, WEST_NORTH, STAIR_SHAPE, INNER_RIGHT),
                    GBlockstate.when(EDGE, WEST_DOWN, STAIR_SHAPE, INNER_RIGHT)),
                GBlockstate.variant(inner_id, true, R0, R180))
            .with(When.anyOf(
                    GBlockstate.when(EDGE, NORTH_DOWN, STAIR_SHAPE, INNER_LEFT),
                    GBlockstate.when(EDGE, NORTH_EAST, STAIR_SHAPE, INNER_RIGHT),
                    GBlockstate.when(EDGE, DOWN_EAST, STAIR_SHAPE, INNER_RIGHT)),
                GBlockstate.variant(inner_id, true, R0, R270))
            .with(When.anyOf(
                    GBlockstate.when(EDGE, DOWN_SOUTH, STAIR_SHAPE, INNER_LEFT),
                    GBlockstate.when(EDGE, EAST_SOUTH, STAIR_SHAPE, INNER_RIGHT),
                    GBlockstate.when(EDGE, DOWN_EAST, STAIR_SHAPE, INNER_LEFT)),
                GBlockstate.variant(inner_id, true, R0, R0))
            .with(When.anyOf(
                    GBlockstate.when(EDGE, DOWN_SOUTH, STAIR_SHAPE, INNER_RIGHT),
                    GBlockstate.when(EDGE, SOUTH_WEST, STAIR_SHAPE, INNER_RIGHT),
                    GBlockstate.when(EDGE, WEST_DOWN, STAIR_SHAPE, INNER_LEFT)),
                GBlockstate.variant(inner_id, true, R0, R90))
            /* INNER TOP */
            .with(When.anyOf(
                    GBlockstate.when(EDGE, EAST_UP, STAIR_SHAPE, INNER_RIGHT),
                    GBlockstate.when(EDGE, NORTH_EAST, STAIR_SHAPE, INNER_LEFT),
                    GBlockstate.when(EDGE, UP_NORTH, STAIR_SHAPE, INNER_LEFT)),
                GBlockstate.variant(inner_id, true, R180, R0))
            .with(When.anyOf(
                    GBlockstate.when(EDGE, EAST_UP, STAIR_SHAPE, INNER_LEFT),
                    GBlockstate.when(EDGE, EAST_SOUTH, STAIR_SHAPE, INNER_LEFT),
                    GBlockstate.when(EDGE, SOUTH_UP, STAIR_SHAPE, INNER_LEFT)),
                GBlockstate.variant(inner_id, true, R180, R90))
            .with(When.anyOf(
                    GBlockstate.when(EDGE, SOUTH_UP, STAIR_SHAPE, INNER_RIGHT),
                    GBlockstate.when(EDGE, SOUTH_WEST, STAIR_SHAPE, INNER_LEFT),
                    GBlockstate.when(EDGE, UP_WEST, STAIR_SHAPE, INNER_LEFT)),
                GBlockstate.variant(inner_id, true, R180, R180))
            .with(When.anyOf(
                    GBlockstate.when(EDGE, UP_NORTH, STAIR_SHAPE, INNER_RIGHT),
                    GBlockstate.when(EDGE, WEST_NORTH, STAIR_SHAPE, INNER_LEFT),
                    GBlockstate.when(EDGE, UP_WEST, STAIR_SHAPE, INNER_RIGHT)),
                GBlockstate.variant(inner_id, true, R180, R270))
            /* OUTER BOTTOM */
            .with(When.anyOf(
                    GBlockstate.when(EDGE, DOWN_SOUTH, STAIR_SHAPE, SECOND_OUTER_LEFT),
                    GBlockstate.when(EDGE, DOWN_EAST, STAIR_SHAPE, SECOND_OUTER_LEFT)),
                GBlockstate.variant(outer_id, true, R0, R0))
            .with(When.anyOf(
                    GBlockstate.when(EDGE, DOWN_SOUTH, STAIR_SHAPE, SECOND_OUTER_RIGHT),
                    GBlockstate.when(EDGE, WEST_DOWN, STAIR_SHAPE, FIRST_OUTER_LEFT)),
                GBlockstate.variant(outer_id, true, R0, R90))
            .with(When.anyOf(
                    GBlockstate.when(EDGE, NORTH_DOWN, STAIR_SHAPE, FIRST_OUTER_RIGHT),
                    GBlockstate.when(EDGE, WEST_DOWN, STAIR_SHAPE, FIRST_OUTER_RIGHT)),
                GBlockstate.variant(outer_id, true, R0, R180))
            .with(When.anyOf(
                    GBlockstate.when(EDGE, NORTH_DOWN, STAIR_SHAPE, FIRST_OUTER_LEFT),
                    GBlockstate.when(EDGE, DOWN_EAST, STAIR_SHAPE, SECOND_OUTER_RIGHT)),
                GBlockstate.variant(outer_id, true, R0, R270))
            /* OUTER TOP */
            .with(When.anyOf(
                    GBlockstate.when(EDGE, UP_NORTH, STAIR_SHAPE, SECOND_OUTER_LEFT),
                    GBlockstate.when(EDGE, EAST_UP, STAIR_SHAPE, FIRST_OUTER_RIGHT)),
                GBlockstate.variant(outer_id, true, R180, R0))
            .with(When.anyOf(
                    GBlockstate.when(EDGE, SOUTH_UP, STAIR_SHAPE, FIRST_OUTER_LEFT),
                    GBlockstate.when(EDGE, EAST_UP, STAIR_SHAPE, FIRST_OUTER_LEFT)),
                GBlockstate.variant(outer_id, true, R180, R90))
            .with(When.anyOf(
                    GBlockstate.when(EDGE, SOUTH_UP, STAIR_SHAPE, FIRST_OUTER_RIGHT),
                    GBlockstate.when(EDGE, UP_WEST, STAIR_SHAPE, SECOND_OUTER_LEFT)),
                GBlockstate.variant(outer_id, true, R180, R180))
            .with(When.anyOf(
                    GBlockstate.when(EDGE, UP_NORTH, STAIR_SHAPE, SECOND_OUTER_RIGHT),
                    GBlockstate.when(EDGE, UP_WEST, STAIR_SHAPE, SECOND_OUTER_RIGHT)),
                GBlockstate.variant(outer_id, true, R180, R270))
            /* OUTER EAST */
            .with(When.anyOf(
                    GBlockstate.when(EDGE, EAST_SOUTH, STAIR_SHAPE, SECOND_OUTER_RIGHT),
                    GBlockstate.when(EDGE, DOWN_EAST, STAIR_SHAPE, FIRST_OUTER_LEFT)),
                GBlockstate.variant(outer_side_id, true, R0, R0))
            .with(When.anyOf(
                    GBlockstate.when(EDGE, EAST_SOUTH, STAIR_SHAPE, SECOND_OUTER_LEFT),
                    GBlockstate.when(EDGE, EAST_UP, STAIR_SHAPE, SECOND_OUTER_LEFT)),
                GBlockstate.variant(outer_side_id, true, R90, R0))
            .with(When.anyOf(
                    GBlockstate.when(EDGE, NORTH_EAST, STAIR_SHAPE, FIRST_OUTER_LEFT),
                    GBlockstate.when(EDGE, EAST_UP, STAIR_SHAPE, SECOND_OUTER_RIGHT)),
                GBlockstate.variant(outer_side_id, true, R180, R0))
            .with(When.anyOf(
                    GBlockstate.when(EDGE, NORTH_EAST, STAIR_SHAPE, FIRST_OUTER_RIGHT),
                    GBlockstate.when(EDGE, DOWN_EAST, STAIR_SHAPE, FIRST_OUTER_RIGHT)),
                GBlockstate.variant(outer_side_id, true, R270, R0))
            /* OUTER SOUTH */
            .with(When.anyOf(
                    GBlockstate.when(EDGE, SOUTH_WEST, STAIR_SHAPE, SECOND_OUTER_RIGHT),
                    GBlockstate.when(EDGE, DOWN_SOUTH, STAIR_SHAPE, FIRST_OUTER_RIGHT)),
                GBlockstate.variant(outer_side_id, true, R0, R90))
            .with(When.anyOf(
                    GBlockstate.when(EDGE, SOUTH_WEST, STAIR_SHAPE, SECOND_OUTER_LEFT),
                    GBlockstate.when(EDGE, SOUTH_UP, STAIR_SHAPE, SECOND_OUTER_RIGHT)),
                GBlockstate.variant(outer_side_id, true, R90, R90))
            .with(When.anyOf(
                    GBlockstate.when(EDGE, EAST_SOUTH, STAIR_SHAPE, FIRST_OUTER_LEFT),
                    GBlockstate.when(EDGE, SOUTH_UP, STAIR_SHAPE, SECOND_OUTER_LEFT)),
                GBlockstate.variant(outer_side_id, true, R180, R90))
            .with(When.anyOf(
                    GBlockstate.when(EDGE, EAST_SOUTH, STAIR_SHAPE, FIRST_OUTER_RIGHT),
                    GBlockstate.when(EDGE, DOWN_SOUTH, STAIR_SHAPE, FIRST_OUTER_LEFT)),
                GBlockstate.variant(outer_side_id, true, R270, R90))
            /* OUTER WEST */
            .with(When.anyOf(
                    GBlockstate.when(EDGE, WEST_NORTH, STAIR_SHAPE, SECOND_OUTER_RIGHT),
                    GBlockstate.when(EDGE, WEST_DOWN, STAIR_SHAPE, SECOND_OUTER_RIGHT)),
                GBlockstate.variant(outer_side_id, true, R0, R180))
            .with(When.anyOf(
                    GBlockstate.when(EDGE, WEST_NORTH, STAIR_SHAPE, SECOND_OUTER_LEFT),
                    GBlockstate.when(EDGE, UP_WEST, STAIR_SHAPE, FIRST_OUTER_RIGHT)),
                GBlockstate.variant(outer_side_id, true, R90, R180))
            .with(When.anyOf(
                    GBlockstate.when(EDGE, SOUTH_WEST, STAIR_SHAPE, FIRST_OUTER_LEFT),
                    GBlockstate.when(EDGE, UP_WEST, STAIR_SHAPE, FIRST_OUTER_LEFT)),
                GBlockstate.variant(outer_side_id, true, R180, R180))
            .with(When.anyOf(
                    GBlockstate.when(EDGE, SOUTH_WEST, STAIR_SHAPE, FIRST_OUTER_RIGHT),
                    GBlockstate.when(EDGE, WEST_DOWN, STAIR_SHAPE, SECOND_OUTER_LEFT)),
                GBlockstate.variant(outer_side_id, true, R270, R180))
            /* OUTER NORTH */
            .with(When.anyOf(
                    GBlockstate.when(EDGE, NORTH_EAST, STAIR_SHAPE, SECOND_OUTER_RIGHT),
                    GBlockstate.when(EDGE, NORTH_DOWN, STAIR_SHAPE, SECOND_OUTER_LEFT)),
                GBlockstate.variant(outer_side_id, true, R0, R270))
            .with(When.anyOf(
                    GBlockstate.when(EDGE, NORTH_EAST, STAIR_SHAPE, SECOND_OUTER_LEFT),
                    GBlockstate.when(EDGE, UP_NORTH, STAIR_SHAPE, FIRST_OUTER_LEFT)),
                GBlockstate.variant(outer_side_id, true, R90, R270))
            .with(When.anyOf(
                    GBlockstate.when(EDGE, WEST_NORTH, STAIR_SHAPE, FIRST_OUTER_LEFT),
                    GBlockstate.when(EDGE, UP_NORTH, STAIR_SHAPE, FIRST_OUTER_RIGHT)),
                GBlockstate.variant(outer_side_id, true, R180, R270))
            .with(When.anyOf(
                    GBlockstate.when(EDGE, WEST_NORTH, STAIR_SHAPE, FIRST_OUTER_RIGHT),
                    GBlockstate.when(EDGE, NORTH_DOWN, STAIR_SHAPE, SECOND_OUTER_RIGHT)),
                GBlockstate.variant(outer_side_id, true, R270, R270))
            /* OUTER BOTTOM */
            .with(When.anyOf(
                    GBlockstate.when(EDGE, DOWN_SOUTH, STAIR_SHAPE, OUTER_LEFT),
                    GBlockstate.when(EDGE, DOWN_EAST, STAIR_SHAPE, OUTER_LEFT),
                    GBlockstate.when(EDGE, EAST_SOUTH, STAIR_SHAPE, OUTER_RIGHT)),
                GBlockstate.variant(double_outer_id, true, R0, R0))
            .with(When.anyOf(
                    GBlockstate.when(EDGE, DOWN_SOUTH, STAIR_SHAPE, OUTER_RIGHT),
                    GBlockstate.when(EDGE, WEST_DOWN, STAIR_SHAPE, OUTER_LEFT),
                    GBlockstate.when(EDGE, SOUTH_WEST, STAIR_SHAPE, OUTER_RIGHT)),
                GBlockstate.variant(double_outer_id, true, R0, R90))
            .with(When.anyOf(
                    GBlockstate.when(EDGE, NORTH_DOWN, STAIR_SHAPE, OUTER_RIGHT),
                    GBlockstate.when(EDGE, WEST_DOWN, STAIR_SHAPE, OUTER_RIGHT),
                    GBlockstate.when(EDGE, WEST_NORTH, STAIR_SHAPE, OUTER_RIGHT)),
                GBlockstate.variant(double_outer_id, true, R0, R180))
            .with(When.anyOf(
                    GBlockstate.when(EDGE, NORTH_DOWN, STAIR_SHAPE, OUTER_LEFT),
                    GBlockstate.when(EDGE, DOWN_EAST, STAIR_SHAPE, OUTER_RIGHT),
                    GBlockstate.when(EDGE, NORTH_EAST, STAIR_SHAPE, OUTER_RIGHT)),
                GBlockstate.variant(double_outer_id, true, R0, R270))
            /* OUTER TOP */
            .with(When.anyOf(
                    GBlockstate.when(EDGE, UP_NORTH, STAIR_SHAPE, OUTER_LEFT),
                    GBlockstate.when(EDGE, EAST_UP, STAIR_SHAPE, OUTER_RIGHT),
                    GBlockstate.when(EDGE, NORTH_EAST, STAIR_SHAPE, OUTER_LEFT)),
                GBlockstate.variant(double_outer_id, true, R180, R0))
            .with(When.anyOf(
                    GBlockstate.when(EDGE, SOUTH_UP, STAIR_SHAPE, OUTER_LEFT),
                    GBlockstate.when(EDGE, EAST_UP, STAIR_SHAPE, OUTER_LEFT),
                    GBlockstate.when(EDGE, EAST_SOUTH, STAIR_SHAPE, OUTER_LEFT)),
                GBlockstate.variant(double_outer_id, true, R180, R90))
            .with(When.anyOf(
                    GBlockstate.when(EDGE, SOUTH_UP, STAIR_SHAPE, OUTER_RIGHT),
                    GBlockstate.when(EDGE, UP_WEST, STAIR_SHAPE, OUTER_LEFT),
                    GBlockstate.when(EDGE, SOUTH_WEST, STAIR_SHAPE, OUTER_LEFT)),
                GBlockstate.variant(double_outer_id, true, R180, R180))
            .with(When.anyOf(
                    GBlockstate.when(EDGE, UP_NORTH, STAIR_SHAPE, OUTER_RIGHT),
                    GBlockstate.when(EDGE, UP_WEST, STAIR_SHAPE, OUTER_RIGHT),
                    GBlockstate.when(EDGE, WEST_NORTH, STAIR_SHAPE, OUTER_LEFT)),
                GBlockstate.variant(double_outer_id, true, R180, R270));
    }
}
