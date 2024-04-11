package fr.adrien1106.reframed.generator.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.generator.BlockStateProvider;
import fr.adrien1106.reframed.generator.GBlockstate;
import fr.adrien1106.reframed.generator.RecipeSetter;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.BlockStateSupplier;
import net.minecraft.data.client.MultipartBlockStateSupplier;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;

import static fr.adrien1106.reframed.util.blocks.BlockProperties.CORNER;
import static fr.adrien1106.reframed.util.blocks.Corner.*;
import static fr.adrien1106.reframed.util.blocks.Corner.NORTH_EAST_UP;
import static net.minecraft.data.client.VariantSettings.Rotation.*;

public class SmallCube implements RecipeSetter, BlockStateProvider {

    @Override
    public void setRecipe(RecipeExporter exporter, ItemConvertible convertible) {
        RecipeProvider.offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, convertible, ReFramed.CUBE, 8);
        ShapelessRecipeJsonBuilder
            .create(RecipeCategory.BUILDING_BLOCKS, convertible, 8)
            .input(ReFramed.CUBE)
            .criterion(FabricRecipeProvider.hasItem(ReFramed.CUBE), FabricRecipeProvider.conditionsFromItem(ReFramed.CUBE))
            .criterion(FabricRecipeProvider.hasItem(convertible), FabricRecipeProvider.conditionsFromItem(convertible))
            .offerTo(exporter);
    }

    @Override
    public BlockStateSupplier getMultipart(Block block) {
        Identifier small_cube_id = ReFramed.id("small_cube_special");
        return MultipartBlockStateSupplier.create(block)
            .with(GBlockstate.when(CORNER, NORTH_EAST_DOWN),
                GBlockstate.variant(small_cube_id, true, R0, R0))
            .with(GBlockstate.when(CORNER, EAST_SOUTH_DOWN),
                GBlockstate.variant(small_cube_id, true, R0, R90))
            .with(GBlockstate.when(CORNER, SOUTH_WEST_DOWN),
                GBlockstate.variant(small_cube_id, true, R0, R180))
            .with(GBlockstate.when(CORNER, WEST_NORTH_DOWN),
                GBlockstate.variant(small_cube_id, true, R0, R270))
            .with(GBlockstate.when(CORNER, EAST_SOUTH_UP),
                GBlockstate.variant(small_cube_id, true, R180, R0))
            .with(GBlockstate.when(CORNER, SOUTH_WEST_UP),
                GBlockstate.variant(small_cube_id, true, R180, R90))
            .with(GBlockstate.when(CORNER, WEST_NORTH_UP),
                GBlockstate.variant(small_cube_id, true, R180, R180))
            .with(GBlockstate.when(CORNER, NORTH_EAST_UP),
                GBlockstate.variant(small_cube_id, true, R180, R270));
    }
}
