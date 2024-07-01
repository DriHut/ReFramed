package fr.adrien1106.reframed.generator.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.generator.BlockStateProvider;
import fr.adrien1106.reframed.generator.GBlockstate;
import fr.adrien1106.reframed.generator.RecipeSetter;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.MultipartBlockStateSupplier;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import static fr.adrien1106.reframed.util.blocks.BlockProperties.HALF_LAYERS;
import static net.minecraft.data.client.VariantSettings.Rotation.*;
import static net.minecraft.state.property.Properties.FACING;

public class SlabsLayer implements RecipeSetter, BlockStateProvider {

    @Override
    public void setRecipe(RecipeExporter exporter, ItemConvertible convertible) {
        RecipeProvider.offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, convertible, ReFramed.CUBE, 2);
        ShapelessRecipeJsonBuilder
            .create(RecipeCategory.BUILDING_BLOCKS, convertible, 1)
            .input(ReFramed.SLAB)
            .input(ReFramed.LAYER)
            .criterion(FabricRecipeProvider.hasItem(ReFramed.CUBE), FabricRecipeProvider.conditionsFromItem(ReFramed.CUBE))
            .criterion(FabricRecipeProvider.hasItem(convertible), FabricRecipeProvider.conditionsFromItem(convertible))
            .offerTo(exporter);
    }

    @Override
    public MultipartBlockStateSupplier getMultipart(Block block) {
        String model_pattern = "slabs_layer_x_special";
        MultipartBlockStateSupplier supplier = MultipartBlockStateSupplier.create(block);
        for (int i = 1; i <= 4; i++) {
            Identifier model = ReFramed.id(model_pattern.replace("x", i * 2 + ""));
            supplier
                .with(GBlockstate.when(FACING, Direction.DOWN, HALF_LAYERS, i),
                    GBlockstate.variant(model, true, R0, R0))
                .with(GBlockstate.when(FACING, Direction.SOUTH, HALF_LAYERS, i),
                    GBlockstate.variant(model, true, R90, R0))
                .with(GBlockstate.when(FACING, Direction.UP, HALF_LAYERS, i),
                    GBlockstate.variant(model, true, R180, R0))
                .with(GBlockstate.when(FACING, Direction.NORTH, HALF_LAYERS, i),
                    GBlockstate.variant(model, true, R270, R0))
                .with(GBlockstate.when(FACING, Direction.WEST, HALF_LAYERS, i),
                    GBlockstate.variant(model, true, R90, R90))
                .with(GBlockstate.when(FACING, Direction.EAST, HALF_LAYERS, i),
                    GBlockstate.variant(model, true, R90, R270));
        }
        return supplier;
    }
}
