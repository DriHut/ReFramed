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
import static net.minecraft.data.client.VariantSettings.Rotation.R270;
import static net.minecraft.state.property.Properties.AXIS;
import static net.minecraft.state.property.Properties.FACING;

public class StepsSlab implements RecipeSetter, BlockStateProvider {

    @Override
    public void setRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible convertible) {
        RecipeProvider.offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, convertible, ReFramed.CUBE, 2);
        ShapedRecipeJsonBuilder
            .create(RecipeCategory.BUILDING_BLOCKS, convertible)
            .pattern("II")
            .input('I', ReFramed.STEP)
            .criterion(FabricRecipeProvider.hasItem(ReFramed.CUBE), FabricRecipeProvider.conditionsFromItem(ReFramed.CUBE))
            .criterion(FabricRecipeProvider.hasItem(convertible), FabricRecipeProvider.conditionsFromItem(convertible))
            .offerTo(exporter);
    }

    @Override
    public MultipartBlockStateSupplier getMultipart(Block block) {
        Identifier step_id = ReFramed.id("steps_slab_special");
        Identifier step_side_id = ReFramed.id("steps_slab_side_special");
        return MultipartBlockStateSupplier.create(block)
            .with(GBlockstate.when(FACING, Direction.DOWN, AXIS, Direction.Axis.X),
                GBlockstate.variant(step_id, true, R0, R180))
            .with(GBlockstate.when(FACING, Direction.DOWN, AXIS, Direction.Axis.Z),
                GBlockstate.variant(step_id, true, R0, R270))
            .with(GBlockstate.when(FACING, Direction.UP, AXIS, Direction.Axis.X),
                GBlockstate.variant(step_id, true, R180, R180))
            .with(GBlockstate.when(FACING, Direction.UP, AXIS, Direction.Axis.Z),
                GBlockstate.variant(step_id, true, R180, R270))
            .with(GBlockstate.when(FACING, Direction.EAST, AXIS, Direction.Axis.Z),
                GBlockstate.variant(step_side_id, true, R0, R0))
            .with(GBlockstate.when(FACING, Direction.EAST, AXIS, Direction.Axis.Y),
                GBlockstate.variant(step_side_id, true, R90, R0))
            .with(GBlockstate.when(FACING, Direction.SOUTH, AXIS, Direction.Axis.X),
                GBlockstate.variant(step_side_id, true, R180, R90))
            .with(GBlockstate.when(FACING, Direction.SOUTH, AXIS, Direction.Axis.Y),
                GBlockstate.variant(step_side_id, true, R90, R90))
            .with(GBlockstate.when(FACING, Direction.WEST, AXIS, Direction.Axis.Z),
                GBlockstate.variant(step_side_id, true, R180, R180))
            .with(GBlockstate.when(FACING, Direction.WEST, AXIS, Direction.Axis.Y),
                GBlockstate.variant(step_side_id, true, R90, R180))
            .with(GBlockstate.when(FACING, Direction.NORTH, AXIS, Direction.Axis.X),
                GBlockstate.variant(step_side_id, true, R0, R270))
            .with(GBlockstate.when(FACING, Direction.NORTH, AXIS, Direction.Axis.Y),
                GBlockstate.variant(step_side_id, true, R90, R270));
    }
}
