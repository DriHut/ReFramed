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
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.List;

import static net.minecraft.block.enums.WallShape.*;
import static net.minecraft.data.client.VariantSettings.Rotation.*;
import static net.minecraft.state.property.Properties.*;

public class PillarsWall implements RecipeSetter, TagGetter, BlockStateProvider {

    @Override
    public void setRecipe(RecipeExporter exporter, ItemConvertible convertible) {
        RecipeProvider.offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, convertible, ReFramed.CUBE, 1);
        ShapelessRecipeJsonBuilder
            .create(RecipeCategory.BUILDING_BLOCKS, convertible, 2)
            .input(ReFramed.WALL, 2)
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
            low = ReFramed.id("pillars_wall_low_special"),
            tall = ReFramed.id("pillars_wall_tall_special"),
            none = ReFramed.id("wall_pillar_none_special");
        return MultipartBlockStateSupplier.create(block)
            // PILLAR CORE
            .with(GBlockstate.variant(ReFramed.id("wall_core_special"), true, R0, R0))
            // LOW
            .with(GBlockstate.when(NORTH_WALL_SHAPE, LOW),
                GBlockstate.variant(low, true, R0, R0))
            .with(GBlockstate.when(EAST_WALL_SHAPE, LOW),
                GBlockstate.variant(low, true, R0, R90))
            .with(GBlockstate.when(SOUTH_WALL_SHAPE, LOW),
                GBlockstate.variant(low, true, R0, R180))
            .with(GBlockstate.when(WEST_WALL_SHAPE, LOW),
                GBlockstate.variant(low, true, R0, R270))
            // TALL
            .with(GBlockstate.when(NORTH_WALL_SHAPE, TALL),
                GBlockstate.variant(tall, true, R0, R0))
            .with(GBlockstate.when(EAST_WALL_SHAPE, TALL),
                GBlockstate.variant(tall, true, R0, R90))
            .with(GBlockstate.when(SOUTH_WALL_SHAPE, TALL),
                GBlockstate.variant(tall, true, R0, R180))
            .with(GBlockstate.when(WEST_WALL_SHAPE, TALL),
                GBlockstate.variant(tall, true, R0, R270))
            // PILLAR NONE
            .with(GBlockstate.when(NORTH_WALL_SHAPE, NONE),
                GBlockstate.variant(none, true, R0, R0))
            .with(GBlockstate.when(EAST_WALL_SHAPE, NONE),
                GBlockstate.variant(none, true, R0, R90))
            .with(GBlockstate.when(SOUTH_WALL_SHAPE, NONE),
                GBlockstate.variant(none, true, R0, R180))
            .with(GBlockstate.when(WEST_WALL_SHAPE, NONE),
                GBlockstate.variant(none, true, R0, R270));
    }
}
