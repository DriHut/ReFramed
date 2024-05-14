package fr.adrien1106.reframed.generator.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.generator.BlockStateProvider;
import fr.adrien1106.reframed.generator.GBlockstate;
import fr.adrien1106.reframed.generator.RecipeSetter;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.MultipartBlockStateSupplier;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.function.Consumer;

import static net.minecraft.data.client.VariantSettings.Rotation.*;
import static net.minecraft.state.property.Properties.FACING;
import static net.minecraft.state.property.Properties.LAYERS;

public class Layer implements RecipeSetter, BlockStateProvider {

    @Override
    public void setRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible convertible) {
        RecipeProvider.offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, convertible, ReFramed.CUBE, 8);
        ShapedRecipeJsonBuilder
            .create(RecipeCategory.BUILDING_BLOCKS, convertible, 16)
            .pattern("II")
            .input('I', ReFramed.CUBE)
            .criterion(FabricRecipeProvider.hasItem(ReFramed.CUBE), FabricRecipeProvider.conditionsFromItem(ReFramed.CUBE))
            .criterion(FabricRecipeProvider.hasItem(convertible), FabricRecipeProvider.conditionsFromItem(convertible))
            .offerTo(exporter);
    }

    @Override
    public MultipartBlockStateSupplier getMultipart(Block block) {
        String model_pattern = "layer_x_special";
        MultipartBlockStateSupplier supplier = MultipartBlockStateSupplier.create(block);
        for (int i = 1; i <= 8; i++) {
            Identifier model = ReFramed.id(model_pattern.replace("x", i + ""));
            supplier
                .with(GBlockstate.when(FACING, Direction.DOWN, LAYERS, i),
                    GBlockstate.variant(model, true, R0, R0))
                .with(GBlockstate.when(FACING, Direction.SOUTH, LAYERS, i),
                    GBlockstate.variant(model, true, R90, R0))
                .with(GBlockstate.when(FACING, Direction.UP, LAYERS, i),
                    GBlockstate.variant(model, true, R180, R0))
                .with(GBlockstate.when(FACING, Direction.NORTH, LAYERS, i),
                    GBlockstate.variant(model, true, R270, R0))
                .with(GBlockstate.when(FACING, Direction.WEST, LAYERS, i),
                    GBlockstate.variant(model, true, R90, R90))
                .with(GBlockstate.when(FACING, Direction.EAST, LAYERS, i),
                    GBlockstate.variant(model, true, R90, R270));
        }
        return supplier;
    }
}
