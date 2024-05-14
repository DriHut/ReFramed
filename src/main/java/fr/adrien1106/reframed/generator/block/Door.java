package fr.adrien1106.reframed.generator.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.generator.BlockStateProvider;
import fr.adrien1106.reframed.generator.GBlockstate;
import fr.adrien1106.reframed.generator.RecipeSetter;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.BlockStateSupplier;
import net.minecraft.data.client.MultipartBlockStateSupplier;
import net.minecraft.data.client.When;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.function.Consumer;

import static net.minecraft.block.enums.DoorHinge.*;
import static net.minecraft.data.client.VariantSettings.Rotation.*;
import static net.minecraft.state.property.Properties.*;

public class Door implements RecipeSetter, BlockStateProvider {

    @Override
    public void setRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible convertible) {
        RecipeProvider.offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, convertible, ReFramed.CUBE, 1);
        ShapedRecipeJsonBuilder
            .create(RecipeCategory.BUILDING_BLOCKS, convertible, 3)
            .pattern("II")
            .pattern("II")
            .pattern("II")
            .input('I', ReFramed.CUBE)
            .criterion(FabricRecipeProvider.hasItem(ReFramed.CUBE), FabricRecipeProvider.conditionsFromItem(ReFramed.CUBE))
            .criterion(FabricRecipeProvider.hasItem(convertible), FabricRecipeProvider.conditionsFromItem(convertible))
            .offerTo(exporter);
    }

    @Override
    public BlockStateSupplier getMultipart(Block block) {
        Identifier door = ReFramed.id("trapdoor_open_special");
        return MultipartBlockStateSupplier.create(block)
            // SOUTH
            .with(When.anyOf(
                    GBlockstate.when(OPEN, false, HORIZONTAL_FACING, Direction.SOUTH),
                    GBlockstate.when(OPEN, true, HORIZONTAL_FACING, Direction.EAST, DOOR_HINGE, LEFT),
                    GBlockstate.when(OPEN, true, HORIZONTAL_FACING, Direction.WEST, DOOR_HINGE, RIGHT)
                ),
                GBlockstate.variant(door, true, R0, R0))
            // WEST
            .with(When.anyOf(
                    GBlockstate.when(OPEN, false, HORIZONTAL_FACING, Direction.WEST),
                    GBlockstate.when(OPEN, true, HORIZONTAL_FACING, Direction.SOUTH, DOOR_HINGE, LEFT),
                    GBlockstate.when(OPEN, true, HORIZONTAL_FACING, Direction.NORTH, DOOR_HINGE, RIGHT)
                ),
                GBlockstate.variant(door, true, R0, R90))
            // NORTH
            .with(When.anyOf(
                    GBlockstate.when(OPEN, false, HORIZONTAL_FACING, Direction.NORTH),
                    GBlockstate.when(OPEN, true, HORIZONTAL_FACING, Direction.WEST, DOOR_HINGE, LEFT),
                    GBlockstate.when(OPEN, true, HORIZONTAL_FACING, Direction.EAST, DOOR_HINGE, RIGHT)
                ),
                GBlockstate.variant(door, true, R0, R180))
            // EAST
            .with(When.anyOf(
                    GBlockstate.when(OPEN, false, HORIZONTAL_FACING, Direction.EAST),
                    GBlockstate.when(OPEN, true, HORIZONTAL_FACING, Direction.NORTH, DOOR_HINGE, LEFT),
                    GBlockstate.when(OPEN, true, HORIZONTAL_FACING, Direction.SOUTH, DOOR_HINGE, RIGHT)
                ),
                GBlockstate.variant(door, true, R0, R270));
    }
}
