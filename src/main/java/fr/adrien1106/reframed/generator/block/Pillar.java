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
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import static net.minecraft.data.client.VariantSettings.Rotation.R0;
import static net.minecraft.data.client.VariantSettings.Rotation.R90;
import static net.minecraft.state.property.Properties.AXIS;

public class Pillar implements RecipeSetter, BlockStateProvider {

    @Override
    public void setRecipe(RecipeExporter exporter, ItemConvertible convertible) {
        RecipeProvider.offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, convertible, ReFramed.CUBE, 4);
        ShapedRecipeJsonBuilder
            .create(RecipeCategory.BUILDING_BLOCKS, convertible, 8)
            .pattern("I")
            .pattern("I")
            .input('I', ReFramed.CUBE)
            .criterion(FabricRecipeProvider.hasItem(ReFramed.CUBE), FabricRecipeProvider.conditionsFromItem(ReFramed.CUBE))
            .criterion(FabricRecipeProvider.hasItem(convertible), FabricRecipeProvider.conditionsFromItem(convertible))
            .offerTo(exporter);
    }

    @Override
    public BlockStateSupplier getMultipart(Block block) {
        Identifier model_id = ReFramed.id("pillar_special");
        return MultipartBlockStateSupplier.create(block)
            .with(GBlockstate.when(AXIS, Direction.Axis.X),
                GBlockstate.variant(model_id, true, R90, R90))
            .with(GBlockstate.when(AXIS, Direction.Axis.Y),
                GBlockstate.variant(model_id, true, R0, R0))
            .with(GBlockstate.when(AXIS, Direction.Axis.Z),
                GBlockstate.variant(model_id, true, R90, R0));
    }
}
