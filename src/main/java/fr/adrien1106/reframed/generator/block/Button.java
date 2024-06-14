package fr.adrien1106.reframed.generator.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.generator.BlockStateProvider;
import fr.adrien1106.reframed.generator.GBlockstate;
import fr.adrien1106.reframed.generator.RecipeSetter;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Block;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.data.client.BlockStateSupplier;
import net.minecraft.data.client.MultipartBlockStateSupplier;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.function.Consumer;

import static net.minecraft.block.WallMountedBlock.FACE;
import static net.minecraft.data.client.VariantSettings.Rotation.*;
import static net.minecraft.state.property.Properties.*;

public class Button implements RecipeSetter, BlockStateProvider {

    @Override
    public void setRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible convertible) {
        RecipeProvider.offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, convertible, ReFramed.CUBE, 8);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.BUILDING_BLOCKS, convertible, 1)
            .input(ReFramed.CUBE, 1)
            .criterion(FabricRecipeProvider.hasItem(ReFramed.CUBE), FabricRecipeProvider.conditionsFromItem(ReFramed.CUBE))
            .criterion(FabricRecipeProvider.hasItem(convertible), FabricRecipeProvider.conditionsFromItem(convertible))
            .offerTo(exporter);
    }

    @Override
    public BlockStateSupplier getMultipart(Block block) {
        Identifier button = ReFramed.id("button_special");
        Identifier button_pressed = ReFramed.id("button_pressed_special");
        return MultipartBlockStateSupplier.create(block)
            // FLOOR OFF
            .with(GBlockstate.when(POWERED, false, HORIZONTAL_FACING, Direction.NORTH, FACE, WallMountLocation.FLOOR),
                GBlockstate.variant(button, true, R0, R0))
            .with(GBlockstate.when(POWERED, false, HORIZONTAL_FACING, Direction.EAST, FACE, WallMountLocation.FLOOR),
                GBlockstate.variant(button, true, R0, R90))
            .with(GBlockstate.when(POWERED, false, HORIZONTAL_FACING, Direction.SOUTH, FACE, WallMountLocation.FLOOR),
                GBlockstate.variant(button, true, R0, R180))
            .with(GBlockstate.when(POWERED, false, HORIZONTAL_FACING, Direction.WEST, FACE, WallMountLocation.FLOOR),
                GBlockstate.variant(button, true, R0, R270))
            // CEILING OFF
            .with(GBlockstate.when(POWERED, false, HORIZONTAL_FACING, Direction.SOUTH, FACE, WallMountLocation.CEILING),
                GBlockstate.variant(button, true, R180, R0))
            .with(GBlockstate.when(POWERED, false, HORIZONTAL_FACING, Direction.WEST, FACE, WallMountLocation.CEILING),
                GBlockstate.variant(button, true, R180, R90))
            .with(GBlockstate.when(POWERED, false, HORIZONTAL_FACING, Direction.NORTH, FACE, WallMountLocation.CEILING),
                GBlockstate.variant(button, true, R180, R180))
            .with(GBlockstate.when(POWERED, false, HORIZONTAL_FACING, Direction.EAST, FACE, WallMountLocation.CEILING),
                GBlockstate.variant(button, true, R180, R270))
            // WALL OFF
            .with(GBlockstate.when(POWERED, false, HORIZONTAL_FACING, Direction.NORTH, FACE, WallMountLocation.WALL),
                GBlockstate.variant(button, true, R90, R0))
            .with(GBlockstate.when(POWERED, false, HORIZONTAL_FACING, Direction.EAST, FACE, WallMountLocation.WALL),
                GBlockstate.variant(button, true, R90, R90))
            .with(GBlockstate.when(POWERED, false, HORIZONTAL_FACING, Direction.SOUTH, FACE, WallMountLocation.WALL),
                GBlockstate.variant(button, true, R90, R180))
            .with(GBlockstate.when(POWERED, false, HORIZONTAL_FACING, Direction.WEST, FACE, WallMountLocation.WALL),
                GBlockstate.variant(button, true, R90, R270))
            // FLOOR ON
            .with(GBlockstate.when(POWERED, true, HORIZONTAL_FACING, Direction.NORTH, FACE, WallMountLocation.FLOOR),
                GBlockstate.variant(button_pressed, true, R0, R0))
            .with(GBlockstate.when(POWERED, true, HORIZONTAL_FACING, Direction.EAST, FACE, WallMountLocation.FLOOR),
                GBlockstate.variant(button_pressed, true, R0, R90))
            .with(GBlockstate.when(POWERED, true, HORIZONTAL_FACING, Direction.SOUTH, FACE, WallMountLocation.FLOOR),
                GBlockstate.variant(button_pressed, true, R0, R180))
            .with(GBlockstate.when(POWERED, true, HORIZONTAL_FACING, Direction.WEST, FACE, WallMountLocation.FLOOR),
                GBlockstate.variant(button_pressed, true, R0, R270))
            // CEILING ON
            .with(GBlockstate.when(POWERED, true, HORIZONTAL_FACING, Direction.SOUTH, FACE, WallMountLocation.CEILING),
                GBlockstate.variant(button_pressed, true, R180, R0))
            .with(GBlockstate.when(POWERED, true, HORIZONTAL_FACING, Direction.WEST, FACE, WallMountLocation.CEILING),
                GBlockstate.variant(button_pressed, true, R180, R90))
            .with(GBlockstate.when(POWERED, true, HORIZONTAL_FACING, Direction.NORTH, FACE, WallMountLocation.CEILING),
                GBlockstate.variant(button_pressed, true, R180, R180))
            .with(GBlockstate.when(POWERED, true, HORIZONTAL_FACING, Direction.EAST, FACE, WallMountLocation.CEILING),
                GBlockstate.variant(button_pressed, true, R180, R270))
            // WALL ON
            .with(GBlockstate.when(POWERED, true, HORIZONTAL_FACING, Direction.NORTH, FACE, WallMountLocation.WALL),
                GBlockstate.variant(button_pressed, true, R90, R0))
            .with(GBlockstate.when(POWERED, true, HORIZONTAL_FACING, Direction.EAST, FACE, WallMountLocation.WALL),
                GBlockstate.variant(button_pressed, true, R90, R90))
            .with(GBlockstate.when(POWERED, true, HORIZONTAL_FACING, Direction.SOUTH, FACE, WallMountLocation.WALL),
                GBlockstate.variant(button_pressed, true, R90, R180))
            .with(GBlockstate.when(POWERED, true, HORIZONTAL_FACING, Direction.WEST, FACE, WallMountLocation.WALL),
                GBlockstate.variant(button_pressed, true, R90, R270));
    }
}
