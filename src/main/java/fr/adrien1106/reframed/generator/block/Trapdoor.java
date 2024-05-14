package fr.adrien1106.reframed.generator.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.generator.BlockStateProvider;
import fr.adrien1106.reframed.generator.GBlockstate;
import fr.adrien1106.reframed.generator.RecipeSetter;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Block;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.data.client.BlockStateSupplier;
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
import static net.minecraft.state.property.Properties.*;

public class Trapdoor implements RecipeSetter, BlockStateProvider {

    @Override
    public void setRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible convertible) {
        RecipeProvider.offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, convertible, ReFramed.CUBE, 2);
        ShapedRecipeJsonBuilder
            .create(RecipeCategory.BUILDING_BLOCKS, convertible, 2)
            .pattern("  I")
            .pattern("III")
            .pattern("II ")
            .input('I', ReFramed.CUBE)
            .criterion(FabricRecipeProvider.hasItem(ReFramed.CUBE), FabricRecipeProvider.conditionsFromItem(ReFramed.CUBE))
            .criterion(FabricRecipeProvider.hasItem(convertible), FabricRecipeProvider.conditionsFromItem(convertible))
            .offerTo(exporter);
    }

    @Override
    public BlockStateSupplier getMultipart(Block block) {
        Identifier open = ReFramed.id("trapdoor_open_special");
        return MultipartBlockStateSupplier.create(block)
            .with(GBlockstate.when(OPEN, false, BLOCK_HALF, BlockHalf.BOTTOM),
                GBlockstate.variant(ReFramed.id("trapdoor_bottom_special"), true, R0, R0))
            .with(GBlockstate.when(OPEN, false, BLOCK_HALF, BlockHalf.TOP),
                GBlockstate.variant(ReFramed.id("trapdoor_top_special"), true, R0, R0))
            .with(GBlockstate.when(OPEN, true, HORIZONTAL_FACING, Direction.NORTH),
                GBlockstate.variant(open, true, R0, R0))
            .with(GBlockstate.when(OPEN, true, HORIZONTAL_FACING, Direction.EAST),
                GBlockstate.variant(open, true, R0, R90))
            .with(GBlockstate.when(OPEN, true, HORIZONTAL_FACING, Direction.SOUTH),
                GBlockstate.variant(open, true, R0, R180))
            .with(GBlockstate.when(OPEN, true, HORIZONTAL_FACING, Direction.WEST),
                GBlockstate.variant(open, true, R0, R270));
    }
}
