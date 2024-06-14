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
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Consumer;

import static net.minecraft.data.client.VariantSettings.Rotation.*;
import static net.minecraft.state.property.Properties.*;

public class PostFence implements RecipeSetter, TagGetter, BlockStateProvider {

    @Override
    public void setRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible convertible) {
        RecipeProvider.offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, convertible, ReFramed.FENCE, 1);
        ShapelessRecipeJsonBuilder
            .create(RecipeCategory.BUILDING_BLOCKS, convertible, 2)
            .input(ReFramed.FENCE, 2)
            .criterion(FabricRecipeProvider.hasItem(ReFramed.CUBE), FabricRecipeProvider.conditionsFromItem(ReFramed.CUBE))
            .criterion(FabricRecipeProvider.hasItem(convertible), FabricRecipeProvider.conditionsFromItem(convertible))
            .offerTo(exporter);
    }

    @Override
    public List<TagKey<Block>> getTags() {
        return List.of(BlockTags.FENCES, BlockTags.WOODEN_FENCES);
    }

    @Override
    public BlockStateSupplier getMultipart(Block block) {
        Identifier side_on = ReFramed.id("post_fence_side_special");
        Identifier side_off = ReFramed.id("fence_side_off_special");
        return MultipartBlockStateSupplier.create(block)
            .with(GBlockstate.variant(ReFramed.id("fence_core_special"), true, R0, R0))
            // SIDE ON
            .with(GBlockstate.when(NORTH, true),
                GBlockstate.variant(side_on, true, R0, R0))
            .with(GBlockstate.when(EAST, true),
                GBlockstate.variant(side_on, true, R0, R90))
            .with(GBlockstate.when(SOUTH, true),
                GBlockstate.variant(side_on, true, R0, R180))
            .with(GBlockstate.when(WEST, true),
                GBlockstate.variant(side_on, true, R0, R270))
            // SIDE OFF
            .with(GBlockstate.when(NORTH, false),
                GBlockstate.variant(side_off, true, R0, R0))
            .with(GBlockstate.when(EAST, false),
                GBlockstate.variant(side_off, true, R0, R90))
            .with(GBlockstate.when(SOUTH, false),
                GBlockstate.variant(side_off, true, R0, R180))
            .with(GBlockstate.when(WEST, false),
                GBlockstate.variant(side_off, true, R0, R270));
    }
}
