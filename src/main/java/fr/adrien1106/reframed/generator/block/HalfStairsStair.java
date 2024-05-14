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
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

import static fr.adrien1106.reframed.util.blocks.BlockProperties.EDGE;
import static fr.adrien1106.reframed.util.blocks.Edge.*;
import static fr.adrien1106.reframed.util.blocks.Edge.WEST_DOWN;
import static net.minecraft.data.client.VariantSettings.Rotation.*;

public class HalfStairsStair implements RecipeSetter, BlockStateProvider {

    @Override
    public void setRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible convertible) {
        RecipeProvider.offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, convertible, ReFramed.CUBE, 2);
        ShapelessRecipeJsonBuilder
            .create(RecipeCategory.BUILDING_BLOCKS, convertible)
            .input(ReFramed.HALF_STAIR, 2)
            .criterion(FabricRecipeProvider.hasItem(ReFramed.CUBE), FabricRecipeProvider.conditionsFromItem(ReFramed.CUBE))
            .criterion(FabricRecipeProvider.hasItem(convertible), FabricRecipeProvider.conditionsFromItem(convertible))
            .offerTo(exporter);
    }

    @Override
    public BlockStateSupplier getMultipart(Block block) {
        Identifier model_id = ReFramed.id("half_stairs_stair_down_special");
        Identifier side_model_id = ReFramed.id("half_stairs_stair_side_special");
        Identifier reverse_model_id = ReFramed.id("half_stairs_stair_reverse_special");
        return MultipartBlockStateSupplier.create(block)
            /* X AXIS */
            .with(GBlockstate.when(EDGE, NORTH_DOWN),
                GBlockstate.variant(side_model_id, true, R90, R180))
            .with(GBlockstate.when(EDGE, DOWN_SOUTH),
                GBlockstate.variant(side_model_id, true, R0, R180))
            .with(GBlockstate.when(EDGE, SOUTH_UP),
                GBlockstate.variant(side_model_id, true, R270, R180))
            .with(GBlockstate.when(EDGE, UP_NORTH),
                GBlockstate.variant(side_model_id, true, R180, R180))
            /* Y AXIS */
            .with(GBlockstate.when(EDGE, NORTH_EAST),
                GBlockstate.variant(model_id, true, R0, R0))
            .with(GBlockstate.when(EDGE, EAST_SOUTH),
                GBlockstate.variant(model_id, true, R0, R90))
            .with(GBlockstate.when(EDGE, SOUTH_WEST),
                GBlockstate.variant(model_id, true, R0, R180))
            .with(GBlockstate.when(EDGE, WEST_NORTH),
                GBlockstate.variant(model_id, true, R0, R270))
            /* Z AXIS */
            .with(GBlockstate.when(EDGE, DOWN_EAST),
                GBlockstate.variant(reverse_model_id, true, R0, R90))
            .with(GBlockstate.when(EDGE, EAST_UP),
                GBlockstate.variant(side_model_id, true, R180, R270))
            .with(GBlockstate.when(EDGE, UP_WEST),
                GBlockstate.variant(reverse_model_id, true, R180, R90))
            .with(GBlockstate.when(EDGE, WEST_DOWN),
                GBlockstate.variant(side_model_id, true, R0, R270));
    }
}
