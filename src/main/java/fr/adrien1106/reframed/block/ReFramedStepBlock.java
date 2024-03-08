package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.generator.GBlockstate;
import fr.adrien1106.reframed.generator.BlockStateProvider;
import fr.adrien1106.reframed.util.blocks.BlockHelper;
import fr.adrien1106.reframed.util.VoxelHelper;
import fr.adrien1106.reframed.util.blocks.Edge;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.data.client.MultipartBlockStateSupplier;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.state.StateManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static fr.adrien1106.reframed.util.blocks.BlockProperties.EDGE;
import static fr.adrien1106.reframed.util.blocks.Edge.*;
import static net.minecraft.data.client.VariantSettings.Rotation.*;

public class ReFramedStepBlock extends WaterloggableReFramedBlock implements BlockStateProvider {

    public static final List<VoxelShape> STEP_VOXELS = new ArrayList<>(12);

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
        return switch (edge) {
            case DOWN_SOUTH ->                        STEP_VOXELS.get(0);
            case NORTH_DOWN ->                        STEP_VOXELS.get(1);
            case UP_NORTH ->                          STEP_VOXELS.get(2);
            case SOUTH_UP ->                          STEP_VOXELS.get(3);
            case DOWN_EAST ->                         STEP_VOXELS.get(4);
            case WEST_DOWN ->                         STEP_VOXELS.get(5);
            case UP_WEST ->                           STEP_VOXELS.get(6);
            case EAST_UP ->                           STEP_VOXELS.get(7);
            case NORTH_EAST ->                        STEP_VOXELS.get(8);
            case EAST_SOUTH ->                        STEP_VOXELS.get(9);
            case SOUTH_WEST ->                        STEP_VOXELS.get(10);
            case WEST_NORTH ->                        STEP_VOXELS.get(11);
        };
    }

    @Override
    public MultipartBlockStateSupplier getMultipart() {
        Identifier step_id = ReFramed.id("step_special");
        return MultipartBlockStateSupplier.create(this)
            /* X AXIS */
            .with(GBlockstate.when(EDGE, DOWN_EAST),
                GBlockstate.variant(step_id, true, R0, R0))
            .with(GBlockstate.when(EDGE, EAST_UP),
                GBlockstate.variant(step_id, true, R180, R0))
            .with(GBlockstate.when(EDGE, UP_WEST),
                GBlockstate.variant(step_id, true, R180, R180))
            .with(GBlockstate.when(EDGE, WEST_DOWN),
                GBlockstate.variant(step_id, true, R0, R180))
            /* Y AXIS */
            .with(GBlockstate.when(EDGE, EAST_SOUTH),
                GBlockstate.variant(step_id, true, R90, R0))
            .with(GBlockstate.when(EDGE, SOUTH_WEST),
                GBlockstate.variant(step_id, true, R90, R90))
            .with(GBlockstate.when(EDGE, WEST_NORTH),
                GBlockstate.variant(step_id, true, R90, R180))
            .with(GBlockstate.when(EDGE, NORTH_EAST),
                GBlockstate.variant(step_id, true, R90, R270))
            /* Z AXIS */
            .with(GBlockstate.when(EDGE, DOWN_SOUTH),
                GBlockstate.variant(step_id, true, R0, R90))
            .with(GBlockstate.when(EDGE, NORTH_DOWN),
                GBlockstate.variant(step_id, true, R0, R270))
            .with(GBlockstate.when(EDGE, UP_NORTH),
                GBlockstate.variant(step_id, true, R180, R270))
            .with(GBlockstate.when(EDGE, SOUTH_UP),
                GBlockstate.variant(step_id, true, R180, R90));
    }

    @Override
    public void setRecipe(RecipeExporter exporter) {
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
        final VoxelShape STEP = VoxelShapes.cuboid(0f, 0f, .5f, 1f, .5f, 1f);

        STEP_VOXELS.add(STEP);
        STEP_VOXELS.add(VoxelHelper.mirror(STEP, Direction.Axis.Z));
        STEP_VOXELS.add(VoxelHelper.mirror(VoxelHelper.rotateCounterClockwise(STEP, Direction.Axis.X), Direction.Axis.Z));
        STEP_VOXELS.add(VoxelHelper.rotateCounterClockwise(STEP, Direction.Axis.X));

        STEP_VOXELS.add(VoxelHelper.rotateCounterClockwise(STEP, Direction.Axis.Y));
        STEP_VOXELS.add(VoxelHelper.mirror(VoxelHelper.rotateCounterClockwise(STEP, Direction.Axis.Y), Direction.Axis.X));
        STEP_VOXELS.add(VoxelHelper.mirror(VoxelHelper.rotateClockwise(VoxelHelper.rotateCounterClockwise(STEP, Direction.Axis.Y), Direction.Axis.Z), Direction.Axis.X));
        STEP_VOXELS.add(VoxelHelper.rotateClockwise(VoxelHelper.rotateCounterClockwise(STEP, Direction.Axis.Y), Direction.Axis.Z));

        STEP_VOXELS.add(VoxelHelper.rotateCounterClockwise(VoxelHelper.rotateClockwise(STEP, Direction.Axis.Z), Direction.Axis.Y));
        STEP_VOXELS.add(VoxelHelper.rotateClockwise(STEP, Direction.Axis.Z));
        STEP_VOXELS.add(VoxelHelper.rotateClockwise(VoxelHelper.rotateClockwise(STEP, Direction.Axis.Z), Direction.Axis.Y));
        STEP_VOXELS.add(VoxelHelper.mirror(VoxelHelper.rotateClockwise(VoxelHelper.rotateClockwise(STEP, Direction.Axis.Z), Direction.Axis.Y), Direction.Axis.Z));
    }
}
