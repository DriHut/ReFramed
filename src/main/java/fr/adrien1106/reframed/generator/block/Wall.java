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
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.util.List;

import static net.minecraft.block.enums.WallShape.*;
import static net.minecraft.data.client.VariantSettings.Rotation.*;
import static net.minecraft.state.property.Properties.*;

public class Wall implements RecipeSetter, TagGetter, BlockStateProvider {

    @Override
    public void setRecipe(RecipeExporter exporter, ItemConvertible convertible) {
        RecipeProvider.offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, convertible, ReFramed.CUBE, 1);
        ShapedRecipeJsonBuilder
            .create(RecipeCategory.BUILDING_BLOCKS, convertible, 4)
            .pattern("III")
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
            side_low = ReFramed.id("wall_side_low_special"),
            side_tall = ReFramed.id("wall_side_tall_special"),
            pillar_low = ReFramed.id("wall_pillar_low_special"),
            pillar_tall = ReFramed.id("wall_pillar_tall_special"),
            pillar_none = ReFramed.id("wall_pillar_none_special"),
            low_e = ReFramed.id("wall_low_e_special"),
            tall_e = ReFramed.id("wall_tall_e_special"),
            low_i = ReFramed.id("wall_low_i_special"),
            tall_i = ReFramed.id("wall_tall_i_special"),
            low_tall_i = ReFramed.id("wall_low_tall_i_special"),
            low_c = ReFramed.id("wall_low_c_special"),
            tall_c = ReFramed.id("wall_tall_c_special"),
            low_tall_c = ReFramed.id("wall_low_tall_c_special"),
            tall_low_c = ReFramed.id("wall_tall_low_c_special"),
            low_t = ReFramed.id("wall_low_t_special"),
            tall_t = ReFramed.id("wall_tall_t_special"),
            tall_low_c_t = ReFramed.id("wall_tall_low_c_t_special"),
            tall_i_low_t = ReFramed.id("wall_tall_i_low_t_special"),
            low_i_tall_t = ReFramed.id("wall_low_i_tall_t_special"),
            low_tall_c_t = ReFramed.id("wall_low_tall_c_t_special"),
            low_c_tall_t = ReFramed.id("wall_low_c_tall_t_special"),
            tall_c_low_t = ReFramed.id("wall_tall_c_low_t_special"),
            tall_i_low_i_x = ReFramed.id("wall_tall_i_low_i_x_special"),
            tall_low_t_x = ReFramed.id("wall_tall_low_t_x_special"),
            tall_c_low_c_x = ReFramed.id("wall_tall_c_low_c_x_special"),
            tall_t_low_x = ReFramed.id("wall_tall_t_low_x_special");
        return MultipartBlockStateSupplier.create(block)
            // PILLAR CORE
            .with(GBlockstate.when(UP, true),
                GBlockstate.variant(ReFramed.id("wall_core_special"), true, R0, R0))
            // LOW
            .with(GBlockstate.when(NORTH_WALL_SHAPE, LOW),
                GBlockstate.variant(side_low, true, R0, R0))
            .with(GBlockstate.when(EAST_WALL_SHAPE, LOW),
                GBlockstate.variant(side_low, true, R0, R90))
            .with(GBlockstate.when(SOUTH_WALL_SHAPE, LOW),
                GBlockstate.variant(side_low, true, R0, R180))
            .with(GBlockstate.when(WEST_WALL_SHAPE, LOW),
                GBlockstate.variant(side_low, true, R0, R270))
            // TALL
            .with(GBlockstate.when(NORTH_WALL_SHAPE, TALL),
                GBlockstate.variant(side_tall, true, R0, R0))
            .with(GBlockstate.when(EAST_WALL_SHAPE, TALL),
                GBlockstate.variant(side_tall, true, R0, R90))
            .with(GBlockstate.when(SOUTH_WALL_SHAPE, TALL),
                GBlockstate.variant(side_tall, true, R0, R180))
            .with(GBlockstate.when(WEST_WALL_SHAPE, TALL),
                GBlockstate.variant(side_tall, true, R0, R270))
            // PILLAR LOW
            .with(GBlockstate.when(NORTH_WALL_SHAPE, LOW, UP, true),
                GBlockstate.variant(pillar_low, true, R0, R0))
            .with(GBlockstate.when(EAST_WALL_SHAPE, LOW, UP, true),
                GBlockstate.variant(pillar_low, true, R0, R90))
            .with(GBlockstate.when(SOUTH_WALL_SHAPE, LOW, UP, true),
                GBlockstate.variant(pillar_low, true, R0, R180))
            .with(GBlockstate.when(WEST_WALL_SHAPE, LOW, UP, true),
                GBlockstate.variant(pillar_low, true, R0, R270))
            // PILLAR TALL
            .with(GBlockstate.when(NORTH_WALL_SHAPE, TALL, UP, true),
                GBlockstate.variant(pillar_tall, true, R0, R0))
            .with(GBlockstate.when(EAST_WALL_SHAPE, TALL, UP, true),
                GBlockstate.variant(pillar_tall, true, R0, R90))
            .with(GBlockstate.when(SOUTH_WALL_SHAPE, TALL, UP, true),
                GBlockstate.variant(pillar_tall, true, R0, R180))
            .with(GBlockstate.when(WEST_WALL_SHAPE, TALL, UP, true),
                GBlockstate.variant(pillar_tall, true, R0, R270))
            // PILLAR NONE
            .with(GBlockstate.when(NORTH_WALL_SHAPE, NONE, UP, true),
                GBlockstate.variant(pillar_none, true, R0, R0))
            .with(GBlockstate.when(EAST_WALL_SHAPE, NONE, UP, true),
                GBlockstate.variant(pillar_none, true, R0, R90))
            .with(GBlockstate.when(SOUTH_WALL_SHAPE, NONE, UP, true),
                GBlockstate.variant(pillar_none, true, R0, R180))
            .with(GBlockstate.when(WEST_WALL_SHAPE, NONE, UP, true),
                GBlockstate.variant(pillar_none, true, R0, R270))
            // JUNCTION LOW
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, LOW,
                    EAST_WALL_SHAPE, NONE,
                    SOUTH_WALL_SHAPE, NONE,
                    WEST_WALL_SHAPE, NONE,
                    UP, false
                ),
                GBlockstate.variant(low_e, true, R0, R0))
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, NONE,
                    EAST_WALL_SHAPE, LOW,
                    SOUTH_WALL_SHAPE, NONE,
                    WEST_WALL_SHAPE, NONE,
                    UP, false
                ),
                GBlockstate.variant(low_e, true, R0, R90))
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, NONE,
                    EAST_WALL_SHAPE, NONE,
                    SOUTH_WALL_SHAPE, LOW,
                    WEST_WALL_SHAPE, NONE,
                    UP, false
                ),
                GBlockstate.variant(low_e, true, R0, R180))
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, NONE,
                    EAST_WALL_SHAPE, NONE,
                    SOUTH_WALL_SHAPE, NONE,
                    WEST_WALL_SHAPE, LOW,
                    UP, false
                ),
                GBlockstate.variant(low_e, true, R0, R270))
            // JUNCTION TALL
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, TALL,
                    EAST_WALL_SHAPE, NONE,
                    SOUTH_WALL_SHAPE, NONE,
                    WEST_WALL_SHAPE, NONE,
                    UP, false
                ),
                GBlockstate.variant(tall_e, true, R0, R0))
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, NONE,
                    EAST_WALL_SHAPE, TALL,
                    SOUTH_WALL_SHAPE, NONE,
                    WEST_WALL_SHAPE, NONE,
                    UP, false
                ),
                GBlockstate.variant(tall_e, true, R0, R90))
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, NONE,
                    EAST_WALL_SHAPE, NONE,
                    SOUTH_WALL_SHAPE, TALL,
                    WEST_WALL_SHAPE, NONE,
                    UP, false
                ),
                GBlockstate.variant(tall_e, true, R0, R180))
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, NONE,
                    EAST_WALL_SHAPE, NONE,
                    SOUTH_WALL_SHAPE, NONE,
                    WEST_WALL_SHAPE, TALL,
                    UP, false
                ),
                GBlockstate.variant(tall_e, true, R0, R270))
            // JUNCTION LOW I
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, LOW,
                    EAST_WALL_SHAPE, NONE,
                    SOUTH_WALL_SHAPE, LOW,
                    WEST_WALL_SHAPE, NONE,
                    UP, false
                ),
                GBlockstate.variant(low_i, true, R0, R0))
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, NONE,
                    EAST_WALL_SHAPE, LOW,
                    SOUTH_WALL_SHAPE, NONE,
                    WEST_WALL_SHAPE, LOW,
                    UP, false
                ),
                GBlockstate.variant(low_i, true, R0, R90))
            // JUNCTION TALL I
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, TALL,
                    EAST_WALL_SHAPE, NONE,
                    SOUTH_WALL_SHAPE, TALL,
                    WEST_WALL_SHAPE, NONE,
                    UP, false
                ),
                GBlockstate.variant(tall_i, true, R0, R0))
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, NONE,
                    EAST_WALL_SHAPE, TALL,
                    SOUTH_WALL_SHAPE, NONE,
                    WEST_WALL_SHAPE, TALL,
                    UP, false
                ),
                GBlockstate.variant(tall_i, true, R0, R90))
            // JUNCTION LOW TALL I
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, LOW,
                    EAST_WALL_SHAPE, NONE,
                    SOUTH_WALL_SHAPE, TALL,
                    WEST_WALL_SHAPE, NONE,
                    UP, false
                ),
                GBlockstate.variant(low_tall_i, true, R0, R0))
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, NONE,
                    EAST_WALL_SHAPE, LOW,
                    SOUTH_WALL_SHAPE, NONE,
                    WEST_WALL_SHAPE, TALL,
                    UP, false
                ),
                GBlockstate.variant(low_tall_i, true, R0, R90))
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, TALL,
                    EAST_WALL_SHAPE, NONE,
                    SOUTH_WALL_SHAPE, LOW,
                    WEST_WALL_SHAPE, NONE,
                    UP, false
                ),
                GBlockstate.variant(low_tall_i, true, R0, R180))
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, NONE,
                    EAST_WALL_SHAPE, TALL,
                    SOUTH_WALL_SHAPE, NONE,
                    WEST_WALL_SHAPE, LOW,
                    UP, false
                ),
                GBlockstate.variant(low_tall_i, true, R0, R270))
            // JUNCTION LOW C
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, LOW,
                    EAST_WALL_SHAPE, NONE,
                    SOUTH_WALL_SHAPE, NONE,
                    WEST_WALL_SHAPE, LOW,
                    UP, false
                ),
                GBlockstate.variant(low_c, true, R0, R0))
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, LOW,
                    EAST_WALL_SHAPE, LOW,
                    SOUTH_WALL_SHAPE, NONE,
                    WEST_WALL_SHAPE, NONE,
                    UP, false
                ),
                GBlockstate.variant(low_c, true, R0, R90))
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, NONE,
                    EAST_WALL_SHAPE, LOW,
                    SOUTH_WALL_SHAPE, LOW,
                    WEST_WALL_SHAPE, NONE,
                    UP, false
                ),
                GBlockstate.variant(low_c, true, R0, R180))
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, NONE,
                    EAST_WALL_SHAPE, NONE,
                    SOUTH_WALL_SHAPE, LOW,
                    WEST_WALL_SHAPE, LOW,
                    UP, false
                ),
                GBlockstate.variant(low_c, true, R0, R270))
            // JUNCTION TALL C
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, TALL,
                    EAST_WALL_SHAPE, NONE,
                    SOUTH_WALL_SHAPE, NONE,
                    WEST_WALL_SHAPE, TALL,
                    UP, false
                ),
                GBlockstate.variant(tall_c, true, R0, R0))
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, TALL,
                    EAST_WALL_SHAPE, TALL,
                    SOUTH_WALL_SHAPE, NONE,
                    WEST_WALL_SHAPE, NONE,
                    UP, false
                ),
                GBlockstate.variant(tall_c, true, R0, R90))
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, NONE,
                    EAST_WALL_SHAPE, TALL,
                    SOUTH_WALL_SHAPE, TALL,
                    WEST_WALL_SHAPE, NONE,
                    UP, false
                ),
                GBlockstate.variant(tall_c, true, R0, R180))
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, NONE,
                    EAST_WALL_SHAPE, NONE,
                    SOUTH_WALL_SHAPE, TALL,
                    WEST_WALL_SHAPE, TALL,
                    UP, false
                ),
                GBlockstate.variant(tall_c, true, R0, R270))
            // JUNCTION LOW TALL C
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, LOW,
                    EAST_WALL_SHAPE, NONE,
                    SOUTH_WALL_SHAPE, NONE,
                    WEST_WALL_SHAPE, TALL,
                    UP, false
                ),
                GBlockstate.variant(low_tall_c, true, R0, R0))
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, TALL,
                    EAST_WALL_SHAPE, LOW,
                    SOUTH_WALL_SHAPE, NONE,
                    WEST_WALL_SHAPE, NONE,
                    UP, false
                ),
                GBlockstate.variant(low_tall_c, true, R0, R90))
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, NONE,
                    EAST_WALL_SHAPE, TALL,
                    SOUTH_WALL_SHAPE, LOW,
                    WEST_WALL_SHAPE, NONE,
                    UP, false
                ),
                GBlockstate.variant(low_tall_c, true, R0, R180))
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, NONE,
                    EAST_WALL_SHAPE, NONE,
                    SOUTH_WALL_SHAPE, TALL,
                    WEST_WALL_SHAPE, LOW,
                    UP, false
                ),
                GBlockstate.variant(low_tall_c, true, R0, R270))
            // JUNCTION TALL LOW C
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, TALL,
                    EAST_WALL_SHAPE, NONE,
                    SOUTH_WALL_SHAPE, NONE,
                    WEST_WALL_SHAPE, LOW,
                    UP, false
                ),
                GBlockstate.variant(tall_low_c, true, R0, R0))
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, LOW,
                    EAST_WALL_SHAPE, TALL,
                    SOUTH_WALL_SHAPE, NONE,
                    WEST_WALL_SHAPE, NONE,
                    UP, false
                ),
                GBlockstate.variant(tall_low_c, true, R0, R90))
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, NONE,
                    EAST_WALL_SHAPE, LOW,
                    SOUTH_WALL_SHAPE, TALL,
                    WEST_WALL_SHAPE, NONE,
                    UP, false
                ),
                GBlockstate.variant(tall_low_c, true, R0, R180))
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, NONE,
                    EAST_WALL_SHAPE, NONE,
                    SOUTH_WALL_SHAPE, LOW,
                    WEST_WALL_SHAPE, TALL,
                    UP, false
                ),
                GBlockstate.variant(tall_low_c, true, R0, R270))
            // JUNCTION LOW T
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, LOW,
                    EAST_WALL_SHAPE, LOW,
                    SOUTH_WALL_SHAPE, NONE,
                    WEST_WALL_SHAPE, LOW,
                    UP, false
                ),
                GBlockstate.variant(low_t, true, R0, R0))
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, LOW,
                    EAST_WALL_SHAPE, LOW,
                    SOUTH_WALL_SHAPE, LOW,
                    WEST_WALL_SHAPE, NONE,
                    UP, false
                ),
                GBlockstate.variant(low_t, true, R0, R90))
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, NONE,
                    EAST_WALL_SHAPE, LOW,
                    SOUTH_WALL_SHAPE, LOW,
                    WEST_WALL_SHAPE, LOW,
                    UP, false
                ),
                GBlockstate.variant(low_t, true, R0, R180))
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, LOW,
                    EAST_WALL_SHAPE, NONE,
                    SOUTH_WALL_SHAPE, LOW,
                    WEST_WALL_SHAPE, LOW,
                    UP, false
                ),
                GBlockstate.variant(low_t, true, R0, R270))
            // JUNCTION TALL T
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, TALL,
                    EAST_WALL_SHAPE, TALL,
                    SOUTH_WALL_SHAPE, NONE,
                    WEST_WALL_SHAPE, TALL,
                    UP, false
                ),
                GBlockstate.variant(tall_t, true, R0, R0))
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, TALL,
                    EAST_WALL_SHAPE, TALL,
                    SOUTH_WALL_SHAPE, TALL,
                    WEST_WALL_SHAPE, NONE,
                    UP, false
                ),
                GBlockstate.variant(tall_t, true, R0, R90))
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, NONE,
                    EAST_WALL_SHAPE, TALL,
                    SOUTH_WALL_SHAPE, TALL,
                    WEST_WALL_SHAPE, TALL,
                    UP, false
                ),
                GBlockstate.variant(tall_t, true, R0, R180))
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, TALL,
                    EAST_WALL_SHAPE, NONE,
                    SOUTH_WALL_SHAPE, TALL,
                    WEST_WALL_SHAPE, TALL,
                    UP, false
                ),
                GBlockstate.variant(tall_t, true, R0, R270))
            // JUNCTION TALL LOW C T
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, LOW,
                    EAST_WALL_SHAPE, TALL,
                    SOUTH_WALL_SHAPE, NONE,
                    WEST_WALL_SHAPE, LOW,
                    UP, false
                ),
                GBlockstate.variant(tall_low_c_t, true, R0, R0))
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, LOW,
                    EAST_WALL_SHAPE, LOW,
                    SOUTH_WALL_SHAPE, TALL,
                    WEST_WALL_SHAPE, NONE,
                    UP, false
                ),
                GBlockstate.variant(tall_low_c_t, true, R0, R90))
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, NONE,
                    EAST_WALL_SHAPE, LOW,
                    SOUTH_WALL_SHAPE, LOW,
                    WEST_WALL_SHAPE, TALL,
                    UP, false
                ),
                GBlockstate.variant(tall_low_c_t, true, R0, R180))
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, TALL,
                    EAST_WALL_SHAPE, NONE,
                    SOUTH_WALL_SHAPE, LOW,
                    WEST_WALL_SHAPE, LOW,
                    UP, false
                ),
                GBlockstate.variant(tall_low_c_t, true, R0, R270))
            // JUNCTION TALL I LOW T
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, LOW,
                    EAST_WALL_SHAPE, TALL,
                    SOUTH_WALL_SHAPE, NONE,
                    WEST_WALL_SHAPE, TALL,
                    UP, false
                ),
                GBlockstate.variant(tall_i_low_t, true, R0, R0))
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, TALL,
                    EAST_WALL_SHAPE, LOW,
                    SOUTH_WALL_SHAPE, TALL,
                    WEST_WALL_SHAPE, NONE,
                    UP, false
                ),
                GBlockstate.variant(tall_i_low_t, true, R0, R90))
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, NONE,
                    EAST_WALL_SHAPE, TALL,
                    SOUTH_WALL_SHAPE, LOW,
                    WEST_WALL_SHAPE, TALL,
                    UP, false
                ),
                GBlockstate.variant(tall_i_low_t, true, R0, R180))
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, TALL,
                    EAST_WALL_SHAPE, NONE,
                    SOUTH_WALL_SHAPE, TALL,
                    WEST_WALL_SHAPE, LOW,
                    UP, false
                ),
                GBlockstate.variant(tall_i_low_t, true, R0, R270))
            // JUNCTION LOW I TALL T
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, TALL,
                    EAST_WALL_SHAPE, LOW,
                    SOUTH_WALL_SHAPE, NONE,
                    WEST_WALL_SHAPE, LOW,
                    UP, false
                ),
                GBlockstate.variant(low_i_tall_t, true, R0, R0))
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, LOW,
                    EAST_WALL_SHAPE, TALL,
                    SOUTH_WALL_SHAPE, LOW,
                    WEST_WALL_SHAPE, NONE,
                    UP, false
                ),
                GBlockstate.variant(low_i_tall_t, true, R0, R90))
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, NONE,
                    EAST_WALL_SHAPE, LOW,
                    SOUTH_WALL_SHAPE, TALL,
                    WEST_WALL_SHAPE, LOW,
                    UP, false
                ),
                GBlockstate.variant(low_i_tall_t, true, R0, R180))
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, LOW,
                    EAST_WALL_SHAPE, NONE,
                    SOUTH_WALL_SHAPE, LOW,
                    WEST_WALL_SHAPE, TALL,
                    UP, false
                ),
                GBlockstate.variant(low_i_tall_t, true, R0, R270))
            // JUNCTION LOW TALL C T
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, TALL,
                    EAST_WALL_SHAPE, LOW,
                    SOUTH_WALL_SHAPE, NONE,
                    WEST_WALL_SHAPE, TALL,
                    UP, false
                ),
                GBlockstate.variant(low_tall_c_t, true, R0, R0))
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, TALL,
                    EAST_WALL_SHAPE, TALL,
                    SOUTH_WALL_SHAPE, LOW,
                    WEST_WALL_SHAPE, NONE,
                    UP, false
                ),
                GBlockstate.variant(low_tall_c_t, true, R0, R90))
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, NONE,
                    EAST_WALL_SHAPE, TALL,
                    SOUTH_WALL_SHAPE, TALL,
                    WEST_WALL_SHAPE, LOW,
                    UP, false
                ),
                GBlockstate.variant(low_tall_c_t, true, R0, R180))
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, LOW,
                    EAST_WALL_SHAPE, NONE,
                    SOUTH_WALL_SHAPE, TALL,
                    WEST_WALL_SHAPE, TALL,
                    UP, false
                ),
                GBlockstate.variant(low_tall_c_t, true, R0, R270))
            // JUNCTION LOW C TALL T
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, LOW,
                    EAST_WALL_SHAPE, LOW,
                    SOUTH_WALL_SHAPE, NONE,
                    WEST_WALL_SHAPE, TALL,
                    UP, false
                ),
                GBlockstate.variant(low_c_tall_t, true, R0, R0))
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, TALL,
                    EAST_WALL_SHAPE, LOW,
                    SOUTH_WALL_SHAPE, LOW,
                    WEST_WALL_SHAPE, NONE,
                    UP, false
                ),
                GBlockstate.variant(low_c_tall_t, true, R0, R90))
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, NONE,
                    EAST_WALL_SHAPE, TALL,
                    SOUTH_WALL_SHAPE, LOW,
                    WEST_WALL_SHAPE, LOW,
                    UP, false
                ),
                GBlockstate.variant(low_c_tall_t, true, R0, R180))
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, LOW,
                    EAST_WALL_SHAPE, NONE,
                    SOUTH_WALL_SHAPE, TALL,
                    WEST_WALL_SHAPE, LOW,
                    UP, false
                ),
                GBlockstate.variant(low_c_tall_t, true, R0, R270))
            // JUNCTION TALL C LOW T
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, TALL,
                    EAST_WALL_SHAPE, TALL,
                    SOUTH_WALL_SHAPE, NONE,
                    WEST_WALL_SHAPE, LOW,
                    UP, false
                ),
                GBlockstate.variant(tall_c_low_t, true, R0, R0))
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, LOW,
                    EAST_WALL_SHAPE, TALL,
                    SOUTH_WALL_SHAPE, TALL,
                    WEST_WALL_SHAPE, NONE,
                    UP, false
                ),
                GBlockstate.variant(tall_c_low_t, true, R0, R90))
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, NONE,
                    EAST_WALL_SHAPE, LOW,
                    SOUTH_WALL_SHAPE, TALL,
                    WEST_WALL_SHAPE, TALL,
                    UP, false
                ),
                GBlockstate.variant(tall_c_low_t, true, R0, R180))
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, TALL,
                    EAST_WALL_SHAPE, NONE,
                    SOUTH_WALL_SHAPE, LOW,
                    WEST_WALL_SHAPE, TALL,
                    UP, false
                ),
                GBlockstate.variant(tall_c_low_t, true, R0, R270))
            // JUNCTION X
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, LOW,
                    EAST_WALL_SHAPE, LOW,
                    SOUTH_WALL_SHAPE, LOW,
                    WEST_WALL_SHAPE, LOW,
                    UP, false
                ),
                GBlockstate.variant(ReFramed.id("wall_low_x_special"), true, R0, R0))
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, TALL,
                    EAST_WALL_SHAPE, TALL,
                    SOUTH_WALL_SHAPE, TALL,
                    WEST_WALL_SHAPE, TALL,
                    UP, false
                ),
                GBlockstate.variant(ReFramed.id("wall_tall_x_special"), true, R0, R0))
            // JUNCTION I X
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, LOW,
                    EAST_WALL_SHAPE, TALL,
                    SOUTH_WALL_SHAPE, LOW,
                    WEST_WALL_SHAPE, TALL,
                    UP, false
                ),
                GBlockstate.variant(tall_i_low_i_x, true, R0, R0))
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, TALL,
                    EAST_WALL_SHAPE, LOW,
                    SOUTH_WALL_SHAPE, TALL,
                    WEST_WALL_SHAPE, LOW,
                    UP, false
                ),
                GBlockstate.variant(tall_i_low_i_x, true, R0, R90))
            // JUNCTION TALL LOW T X
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, LOW,
                    EAST_WALL_SHAPE, TALL,
                    SOUTH_WALL_SHAPE, LOW,
                    WEST_WALL_SHAPE, LOW,
                    UP, false
                ),
                GBlockstate.variant(tall_low_t_x, true, R0, R0))
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, LOW,
                    EAST_WALL_SHAPE, LOW,
                    SOUTH_WALL_SHAPE, TALL,
                    WEST_WALL_SHAPE, LOW,
                    UP, false
                ),
                GBlockstate.variant(tall_low_t_x, true, R0, R90))
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, LOW,
                    EAST_WALL_SHAPE, LOW,
                    SOUTH_WALL_SHAPE, LOW,
                    WEST_WALL_SHAPE, TALL,
                    UP, false
                ),
                GBlockstate.variant(tall_low_t_x, true, R0, R180))
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, TALL,
                    EAST_WALL_SHAPE, LOW,
                    SOUTH_WALL_SHAPE, LOW,
                    WEST_WALL_SHAPE, LOW,
                    UP, false
                ),
                GBlockstate.variant(tall_low_t_x, true, R0, R270))
            // JUNCTION TALL C LOW C X
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, TALL,
                    EAST_WALL_SHAPE, TALL,
                    SOUTH_WALL_SHAPE, LOW,
                    WEST_WALL_SHAPE, LOW,
                    UP, false
                ),
                GBlockstate.variant(tall_c_low_c_x, true, R0, R0))
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, LOW,
                    EAST_WALL_SHAPE, TALL,
                    SOUTH_WALL_SHAPE, TALL,
                    WEST_WALL_SHAPE, LOW,
                    UP, false
                ),
                GBlockstate.variant(tall_c_low_c_x, true, R0, R90))
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, LOW,
                    EAST_WALL_SHAPE, LOW,
                    SOUTH_WALL_SHAPE, TALL,
                    WEST_WALL_SHAPE, TALL,
                    UP, false
                ),
                GBlockstate.variant(tall_c_low_c_x, true, R0, R180))
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, TALL,
                    EAST_WALL_SHAPE, LOW,
                    SOUTH_WALL_SHAPE, LOW,
                    WEST_WALL_SHAPE, TALL,
                    UP, false
                ),
                GBlockstate.variant(tall_c_low_c_x, true, R0, R270))
            // JUNCTION TALL C LOW C X
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, TALL,
                    EAST_WALL_SHAPE, TALL,
                    SOUTH_WALL_SHAPE, LOW,
                    WEST_WALL_SHAPE, TALL,
                    UP, false
                ),
                GBlockstate.variant(tall_t_low_x, true, R0, R0))
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, TALL,
                    EAST_WALL_SHAPE, TALL,
                    SOUTH_WALL_SHAPE, TALL,
                    WEST_WALL_SHAPE, LOW,
                    UP, false
                ),
                GBlockstate.variant(tall_t_low_x, true, R0, R90))
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, LOW,
                    EAST_WALL_SHAPE, TALL,
                    SOUTH_WALL_SHAPE, TALL,
                    WEST_WALL_SHAPE, TALL,
                    UP, false
                ),
                GBlockstate.variant(tall_t_low_x, true, R0, R180))
            .with(GBlockstate.when(
                    NORTH_WALL_SHAPE, TALL,
                    EAST_WALL_SHAPE, LOW,
                    SOUTH_WALL_SHAPE, TALL,
                    WEST_WALL_SHAPE, TALL,
                    UP, false
                ),
                GBlockstate.variant(tall_t_low_x, true, R0, R270));
    }
}
