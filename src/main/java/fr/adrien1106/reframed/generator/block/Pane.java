package fr.adrien1106.reframed.generator.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.generator.BlockStateProvider;
import fr.adrien1106.reframed.generator.GBlockstate;
import fr.adrien1106.reframed.generator.RecipeSetter;
import fr.adrien1106.reframed.generator.TagGetter;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.BlockStateSupplier;
import net.minecraft.data.client.MultipartBlockStateSupplier;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Consumer;

import static net.minecraft.data.client.VariantSettings.Rotation.*;
import static net.minecraft.state.property.Properties.*;

public class Pane implements RecipeSetter, TagGetter, BlockStateProvider {

    @Override
    public void setRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible convertible) {
        RecipeProvider.offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, convertible, ReFramed.CUBE, 4);
        ShapedRecipeJsonBuilder
            .create(RecipeCategory.BUILDING_BLOCKS, convertible, 32)
            .pattern("III")
            .pattern("I I")
            .pattern("III")
            .input('I', ReFramed.CUBE)
            .criterion(FabricRecipeProvider.hasItem(ReFramed.CUBE), FabricRecipeProvider.conditionsFromItem(ReFramed.CUBE))
            .criterion(FabricRecipeProvider.hasItem(convertible), FabricRecipeProvider.conditionsFromItem(convertible))
            .offerTo(exporter);
    }

    @Override
    public List<TagKey<Block>> getTags() {
        return List.of(BlockTags.WALLS);
    }

    @Override
    public BlockStateSupplier getMultipart(Block block) {
        Identifier
            pane_side = ReFramed.id("pane_side_special"),
            pane_side_alt = ReFramed.id("pane_side_alt_special"),
            pane_noside = ReFramed.id("pane_noside_special"),
            pane_noside_alt = ReFramed.id("pane_noside_alt_special");
        return MultipartBlockStateSupplier.create(block)
            // PILLAR CORE
            .with(GBlockstate.variant(ReFramed.id("pane_post_special"), true, R0, R0))
            // SIDE
            .with(GBlockstate.when(NORTH, true),
                GBlockstate.variant(pane_side, true, R0, R0))
            .with(GBlockstate.when(EAST, true),
                GBlockstate.variant(pane_side, true, R0, R90))
            .with(GBlockstate.when(SOUTH, true),
                GBlockstate.variant(pane_side_alt, true, R0, R0))
            .with(GBlockstate.when(WEST, true),
                GBlockstate.variant(pane_side_alt, true, R0, R90))
            // NOSIDE
            .with(GBlockstate.when(NORTH, false),
                GBlockstate.variant(pane_noside, true, R0, R0))
            .with(GBlockstate.when(EAST, false),
                GBlockstate.variant(pane_noside_alt, true, R0, R0))
            .with(GBlockstate.when(SOUTH, false),
                GBlockstate.variant(pane_noside_alt, true, R0, R90))
            .with(GBlockstate.when(WEST, false),
                GBlockstate.variant(pane_noside, true, R0, R270));
    }
}
