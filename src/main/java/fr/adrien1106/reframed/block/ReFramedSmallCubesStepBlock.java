package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.generator.BlockStateProvider;
import fr.adrien1106.reframed.generator.GBlockstate;
import fr.adrien1106.reframed.util.blocks.BlockHelper;
import fr.adrien1106.reframed.util.blocks.Edge;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.data.client.BlockStateSupplier;
import net.minecraft.data.client.MultipartBlockStateSupplier;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.state.StateManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

import static fr.adrien1106.reframed.block.ReFramedSmallCubeBlock.SMALL_CUBE_VOXELS;
import static fr.adrien1106.reframed.block.ReFramedStepBlock.getStepShape;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.EDGE;
import static fr.adrien1106.reframed.util.blocks.Edge.*;
import static net.minecraft.data.client.VariantSettings.Rotation.*;
import static net.minecraft.util.shape.VoxelShapes.empty;

public class ReFramedSmallCubesStepBlock extends WaterloggableReFramedDoubleBlock implements BlockStateProvider {

    public ReFramedSmallCubesStepBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(EDGE, Edge.NORTH_DOWN));
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
        return isGhost(view, pos) ? empty(): getStepShape(state.get(EDGE));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getStepShape(state.get(EDGE));
    }

    @Override
    public VoxelShape getShape(BlockState state, int i) {
        return switch (state.get(EDGE)) {
            case NORTH_DOWN -> SMALL_CUBE_VOXELS.get(i == 1 ? 3 : 0);
            case DOWN_SOUTH -> SMALL_CUBE_VOXELS.get(i == 1 ? 1 : 2);
            case SOUTH_UP ->   SMALL_CUBE_VOXELS.get(i == 1 ? 6 : 5);
            case UP_NORTH ->   SMALL_CUBE_VOXELS.get(i == 1 ? 4 : 7);
            case WEST_DOWN ->  SMALL_CUBE_VOXELS.get(i == 1 ? 2 : 3);
            case DOWN_EAST ->  SMALL_CUBE_VOXELS.get(i == 1 ? 0 : 1);
            case EAST_UP ->    SMALL_CUBE_VOXELS.get(i == 1 ? 5 : 4);
            case UP_WEST ->    SMALL_CUBE_VOXELS.get(i == 1 ? 7 : 6);
            case WEST_NORTH -> SMALL_CUBE_VOXELS.get(i == 1 ? 3 : 7);
            case NORTH_EAST -> SMALL_CUBE_VOXELS.get(i == 1 ? 0 : 4);
            case EAST_SOUTH -> SMALL_CUBE_VOXELS.get(i == 1 ? 1 : 5);
            case SOUTH_WEST -> SMALL_CUBE_VOXELS.get(i == 1 ? 2 : 6);
        };
    }

    @Override
    public int getTopThemeIndex(BlockState state) {
        return 2;
    }

    @Override
    public BlockStateSupplier getMultipart() {
        Identifier small_cube_id = ReFramed.id("double_small_cube_special");
        return MultipartBlockStateSupplier.create(this)
            /* X AXIS */
            .with(GBlockstate.when(EDGE, DOWN_EAST),
                GBlockstate.variant(small_cube_id, true, R0, R0))
            .with(GBlockstate.when(EDGE, EAST_UP),
                GBlockstate.variant(small_cube_id, true, R180, R0))
            .with(GBlockstate.when(EDGE, UP_WEST),
                GBlockstate.variant(small_cube_id, true, R180, R180))
            .with(GBlockstate.when(EDGE, WEST_DOWN),
                GBlockstate.variant(small_cube_id, true, R0, R180))
            /* Y AXIS */
            .with(GBlockstate.when(EDGE, EAST_SOUTH),
                GBlockstate.variant(small_cube_id, true, R90, R0))
            .with(GBlockstate.when(EDGE, SOUTH_WEST),
                GBlockstate.variant(small_cube_id, true, R90, R90))
            .with(GBlockstate.when(EDGE, WEST_NORTH),
                GBlockstate.variant(small_cube_id, true, R90, R180))
            .with(GBlockstate.when(EDGE, NORTH_EAST),
                GBlockstate.variant(small_cube_id, true, R90, R270))
            /* Z AXIS */
            .with(GBlockstate.when(EDGE, DOWN_SOUTH),
                GBlockstate.variant(small_cube_id, true, R0, R90))
            .with(GBlockstate.when(EDGE, NORTH_DOWN),
                GBlockstate.variant(small_cube_id, true, R0, R270))
            .with(GBlockstate.when(EDGE, UP_NORTH),
                GBlockstate.variant(small_cube_id, true, R180, R270))
            .with(GBlockstate.when(EDGE, SOUTH_UP),
                GBlockstate.variant(small_cube_id, true, R180, R90));
    }

    @Override
    public void setRecipe(RecipeExporter exporter) {
        RecipeProvider.offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, this, ReFramed.CUBE, 4);
        ShapedRecipeJsonBuilder
            .create(RecipeCategory.BUILDING_BLOCKS, this)
            .pattern("II")
            .input('I', ReFramed.SMALL_CUBE)
            .criterion(FabricRecipeProvider.hasItem(ReFramed.CUBE), FabricRecipeProvider.conditionsFromItem(ReFramed.CUBE))
            .criterion(FabricRecipeProvider.hasItem(this), FabricRecipeProvider.conditionsFromItem(this))
            .offerTo(exporter);
    }
}
