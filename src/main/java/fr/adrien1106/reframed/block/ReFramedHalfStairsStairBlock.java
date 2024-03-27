package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.generator.BlockStateProvider;
import fr.adrien1106.reframed.generator.GBlockstate;
import fr.adrien1106.reframed.util.blocks.BlockHelper;
import fr.adrien1106.reframed.util.blocks.Corner;
import fr.adrien1106.reframed.util.blocks.Edge;
import fr.adrien1106.reframed.util.blocks.StairShape;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.data.client.BlockStateSupplier;
import net.minecraft.data.client.MultipartBlockStateSupplier;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.state.StateManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

import static fr.adrien1106.reframed.block.ReFramedHalfStairBlock.HALF_STAIR_VOXELS;
import static fr.adrien1106.reframed.block.ReFramedStairBlock.getStairShape;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.EDGE;
import static fr.adrien1106.reframed.util.blocks.Edge.*;
import static net.minecraft.data.client.VariantSettings.Rotation.*;
import static net.minecraft.util.shape.VoxelShapes.empty;

public class ReFramedHalfStairsStairBlock extends WaterloggableReFramedDoubleBlock implements BlockStateProvider {
    public ReFramedHalfStairsStairBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(EDGE, NORTH_DOWN));
    }

    @Override
    public Object getModelCacheKey(BlockState state) {
        return state.get(EDGE);
    }

    @Override
    public int getModelStateCount() {
        return 12;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(EDGE));
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx).with(EDGE, BlockHelper.getPlacementEdge(ctx));
    }

    @Override
    public VoxelShape getCullingShape(BlockState state, BlockView view, BlockPos pos) {
        return isGhost(view, pos) ? empty(): getStairShape(state.get(EDGE), StairShape.STRAIGHT);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getStairShape(state.get(EDGE), StairShape.STRAIGHT);
    }

    @Override
    public VoxelShape getShape(BlockState state, int i) {
        Edge edge = state.get(EDGE);
        Direction side = i == 1
            ? edge.getRightDirection()
            : edge.getLeftDirection();
        Corner corner = Corner.getByDirections(
            edge.getFirstDirection(),
            edge.getSecondDirection(),
            side

        );
        return HALF_STAIR_VOXELS[corner.getID() * 3 + corner.getDirectionIndex(side)];
    }

    @Override
    public int getTopThemeIndex(BlockState state) {
        return 2;
    }

    @Override
    public BlockStateSupplier getMultipart() {
        Identifier model_id = ReFramed.id("half_stairs_stair_down_special");
        Identifier side_model_id = ReFramed.id("half_stairs_stair_side_special");
        Identifier reverse_model_id = ReFramed.id("half_stairs_stair_reverse_special");
        return MultipartBlockStateSupplier.create(this)
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

    @Override
    public void setRecipe(RecipeExporter exporter) {
        RecipeProvider.offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, this, ReFramed.CUBE, 2);
        ShapelessRecipeJsonBuilder
            .create(RecipeCategory.BUILDING_BLOCKS, this)
            .input(ReFramed.HALF_STAIR, 2)
            .criterion(FabricRecipeProvider.hasItem(ReFramed.CUBE), FabricRecipeProvider.conditionsFromItem(ReFramed.CUBE))
            .criterion(FabricRecipeProvider.hasItem(this), FabricRecipeProvider.conditionsFromItem(this))
            .offerTo(exporter);
    }
}
