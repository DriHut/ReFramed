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

public class Slab implements RecipeSetter, BlockStateProvider {

    @Override
    public void setRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible convertible) {
        RecipeProvider.offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, convertible, ReFramed.CUBE, 2);
        ShapedRecipeJsonBuilder
            .create(RecipeCategory.BUILDING_BLOCKS, convertible, 6)
            .pattern("III")
            .input('I', ReFramed.CUBE)
            .criterion(FabricRecipeProvider.hasItem(ReFramed.CUBE), FabricRecipeProvider.conditionsFromItem(ReFramed.CUBE))
            .criterion(FabricRecipeProvider.hasItem(convertible), FabricRecipeProvider.conditionsFromItem(convertible))
            .offerTo(exporter);
    }

    @Override
    public MultipartBlockStateSupplier getMultipart(Block block) {
        return getMultipart(block, "slab");
    }

    public static MultipartBlockStateSupplier getMultipart(Block block, String model_name) {
        Identifier model_id = ReFramed.id(model_name + "_special");
        return MultipartBlockStateSupplier.create(block)
            .with(GBlockstate.when(FACING, Direction.DOWN),
                GBlockstate.variant(model_id, true, R0, R0))
            .with(GBlockstate.when(FACING, Direction.SOUTH),
                GBlockstate.variant(model_id, true, R90, R0))
            .with(GBlockstate.when(FACING, Direction.UP),
                GBlockstate.variant(model_id, true, R180, R0))
            .with(GBlockstate.when(FACING, Direction.NORTH),
                GBlockstate.variant(model_id, true, R270, R0))
            .with(GBlockstate.when(FACING, Direction.WEST),
                GBlockstate.variant(model_id, true, R90, R90))
            .with(GBlockstate.when(FACING, Direction.EAST),
                GBlockstate.variant(model_id, true, R90, R270));
    }


}
