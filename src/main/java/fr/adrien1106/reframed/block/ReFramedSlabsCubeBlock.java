package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.generator.BlockStateProvider;
import fr.adrien1106.reframed.generator.GBlockstate;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.data.client.MultipartBlockStateSupplier;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.state.StateManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import static fr.adrien1106.reframed.block.ReFramedSlabBlock.*;
import static net.minecraft.data.client.VariantSettings.Rotation.R0;
import static net.minecraft.data.client.VariantSettings.Rotation.R90;
import static net.minecraft.state.property.Properties.AXIS;

public class ReFramedSlabsCubeBlock extends ReFramedDoubleBlock implements BlockStateProvider {

    public ReFramedSlabsCubeBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(AXIS, Direction.Axis.Y));
    }

    @Override
    public Object getModelCacheKey(BlockState state) {
        return state.get(AXIS);
    }

    @Override
    public int getModelStateCount() {
        return 3;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(AXIS));
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx).with(AXIS, ctx.getSide().getAxis());
    }

    @Override
    public VoxelShape getShape(BlockState state, int i) {
        return switch (state.get(AXIS)) {
            case Y -> i == 2 ? UP    : DOWN;
            case Z -> i == 2 ? NORTH : SOUTH;
            case X -> i == 2 ? EAST  : WEST;
        };
    }

    @Override
    public int getTopThemeIndex(BlockState state) {
        // when the side is shared just return one
        return state.get(AXIS) == Direction.Axis.Y ? 2: super.getTopThemeIndex(state);
    }

    @Override
    public MultipartBlockStateSupplier getMultipart() {
        Identifier model_id = ReFramed.id("double_slab_special");
        return MultipartBlockStateSupplier.create(this)
            .with(GBlockstate.when(AXIS, Direction.Axis.Y),
                GBlockstate.variant(model_id, true, R0, R0))
            .with(GBlockstate.when(AXIS, Direction.Axis.Z),
                GBlockstate.variant(model_id, true, R90, R0))
            .with(GBlockstate.when(AXIS, Direction.Axis.X),
                GBlockstate.variant(model_id, true, R90, R90));
    }

    @Override
    public void setRecipe(Consumer<RecipeJsonProvider> exporter) {
        RecipeProvider.offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, this, ReFramed.CUBE);
        ShapelessRecipeJsonBuilder
            .create(RecipeCategory.BUILDING_BLOCKS, this)
            .input(ReFramed.SLAB, 2)
            .criterion(FabricRecipeProvider.hasItem(ReFramed.CUBE), FabricRecipeProvider.conditionsFromItem(ReFramed.CUBE))
            .criterion(FabricRecipeProvider.hasItem(this), FabricRecipeProvider.conditionsFromItem(this))
            .offerTo(exporter);
    }
}
