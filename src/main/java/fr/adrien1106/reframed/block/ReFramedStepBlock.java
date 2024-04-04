package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.generator.BlockStateProvider;
import fr.adrien1106.reframed.generator.GBlockstate;
import fr.adrien1106.reframed.util.VoxelHelper;
import fr.adrien1106.reframed.util.blocks.BlockHelper;
import fr.adrien1106.reframed.util.blocks.Edge;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.data.client.BlockStateSupplier;
import net.minecraft.data.client.MultipartBlockStateSupplier;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
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

import java.util.function.Consumer;

import static fr.adrien1106.reframed.util.VoxelHelper.VoxelListBuilder;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.EDGE;
import static fr.adrien1106.reframed.util.blocks.Edge.*;
import static net.minecraft.data.client.VariantSettings.Rotation.*;

public class ReFramedStepBlock extends WaterloggableReFramedBlock implements BlockStateProvider {

    public static final VoxelShape[] STEP_VOXELS;

    public ReFramedStepBlock(Settings settings) {
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
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getStepShape(state.get(EDGE));
    }

    public static VoxelShape getStepShape(Edge edge) {
        return STEP_VOXELS[edge.getID()];
    }

    @Override
    public BlockStateSupplier getMultipart() {
        Identifier model_id = ReFramed.id("step_special");
        return MultipartBlockStateSupplier.create(this)
            /* X AXIS */
            .with(GBlockstate.when(EDGE, DOWN_EAST),
                GBlockstate.variant(model_id, true, R0, R0))
            .with(GBlockstate.when(EDGE, EAST_UP),
                GBlockstate.variant(model_id, true, R180, R0))
            .with(GBlockstate.when(EDGE, UP_WEST),
                GBlockstate.variant(model_id, true, R180, R180))
            .with(GBlockstate.when(EDGE, WEST_DOWN),
                GBlockstate.variant(model_id, true, R0, R180))
            /* Y AXIS */
            .with(GBlockstate.when(EDGE, EAST_SOUTH),
                GBlockstate.variant(model_id, true, R90, R0))
            .with(GBlockstate.when(EDGE, SOUTH_WEST),
                GBlockstate.variant(model_id, true, R90, R90))
            .with(GBlockstate.when(EDGE, WEST_NORTH),
                GBlockstate.variant(model_id, true, R90, R180))
            .with(GBlockstate.when(EDGE, NORTH_EAST),
                GBlockstate.variant(model_id, true, R90, R270))
            /* Z AXIS */
            .with(GBlockstate.when(EDGE, DOWN_SOUTH),
                GBlockstate.variant(model_id, true, R0, R90))
            .with(GBlockstate.when(EDGE, NORTH_DOWN),
                GBlockstate.variant(model_id, true, R0, R270))
            .with(GBlockstate.when(EDGE, UP_NORTH),
                GBlockstate.variant(model_id, true, R180, R270))
            .with(GBlockstate.when(EDGE, SOUTH_UP),
                GBlockstate.variant(model_id, true, R180, R90));
    }

    @Override
    public void setRecipe(Consumer<RecipeJsonProvider> exporter) {
        RecipeProvider.offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, this, ReFramed.CUBE, 4);
        ShapedRecipeJsonBuilder
            .create(RecipeCategory.BUILDING_BLOCKS, this, 8)
            .pattern("II")
            .input('I', ReFramed.CUBE)
            .criterion(FabricRecipeProvider.hasItem(ReFramed.CUBE), FabricRecipeProvider.conditionsFromItem(ReFramed.CUBE))
            .criterion(FabricRecipeProvider.hasItem(this), FabricRecipeProvider.conditionsFromItem(this))
            .offerTo(exporter);
    }

    static {
        final VoxelShape STEP = createCuboidShape(0, 0, 0, 16, 8, 8);

        STEP_VOXELS = VoxelListBuilder.create(STEP, 12)
            .add(VoxelHelper::rotateCX)
            .add(VoxelHelper::rotateCX)
            .add(VoxelHelper::rotateCX)

            .add(STEP, VoxelHelper::rotateCY)
            .add(VoxelHelper::rotateZ)
            .add(VoxelHelper::rotateZ)
            .add(VoxelHelper::rotateZ)

            .add(STEP, VoxelHelper::rotateCZ)
            .add(VoxelHelper::rotateY)
            .add(VoxelHelper::rotateY)
            .add(VoxelHelper::rotateY)
            .build();
    }
}
