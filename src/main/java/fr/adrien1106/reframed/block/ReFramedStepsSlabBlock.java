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
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

import static fr.adrien1106.reframed.block.ReFramedSlabBlock.getSlabShape;
import static fr.adrien1106.reframed.block.ReFramedStepBlock.getStepShape;
import static net.minecraft.data.client.VariantSettings.Rotation.*;
import static net.minecraft.state.property.Properties.AXIS;
import static net.minecraft.state.property.Properties.FACING;
import static net.minecraft.util.shape.VoxelShapes.empty;

public class ReFramedStepsSlabBlock extends WaterloggableReFramedDoubleBlock implements BlockStateProvider {
    private record ModelCacheKey(Direction facing, Axis axis) {}

    public ReFramedStepsSlabBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(FACING, Direction.DOWN).with(AXIS, Axis.X));
    }

    @Override
    public Object getModelCacheKey(BlockState state) {
        return new ModelCacheKey(state.get(FACING), state.get(AXIS));
    }

    @Override
    public int getModelStateCount() {
        return 18;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(FACING, AXIS));
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        Vec3d pos = BlockHelper.getHitPos(ctx.getHitPos(), ctx.getBlockPos());
        return super.getPlacementState(ctx)
            .with(FACING, ctx.getSide().getOpposite())
            .with(AXIS, BlockHelper.getHitAxis(pos, ctx.getSide()));
    }

    @Override
    public VoxelShape getCullingShape(BlockState state, BlockView view, BlockPos pos) {
        return isGhost(view, pos) ? empty() : getSlabShape(state.get(FACING));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getSlabShape(state.get(FACING));
    }

    @Override
    public VoxelShape getShape(BlockState state, int i) {
        Axis axis = state.get(AXIS);
        return getStepShape(Edge.getByDirections(
            state.get(FACING),
            switch (axis) {
                case X -> i == 1 ? Direction.WEST : Direction.EAST;
                case Y -> i == 1 ? Direction.DOWN : Direction.UP;
                case Z -> i == 1 ? Direction.NORTH : Direction.SOUTH;
            }
        ));
    }

    @Override
    public int getTopThemeIndex(BlockState state) {
        return 2;
    }

    @Override
    public MultipartBlockStateSupplier getMultipart() {
        Identifier step_id = ReFramed.id("steps_slab_special");
        Identifier step_side_id = ReFramed.id("steps_slab_side_special");
        return MultipartBlockStateSupplier.create(this)
            .with(GBlockstate.when(FACING, Direction.DOWN, AXIS, Axis.X),
                GBlockstate.variant(step_id, true, R0, R180))
            .with(GBlockstate.when(FACING, Direction.DOWN, AXIS, Axis.Z),
                GBlockstate.variant(step_id, true, R0, R270))
            .with(GBlockstate.when(FACING, Direction.UP, AXIS, Axis.X),
                GBlockstate.variant(step_id, true, R180, R180))
            .with(GBlockstate.when(FACING, Direction.UP, AXIS, Axis.Z),
                GBlockstate.variant(step_id, true, R180, R270))

            .with(GBlockstate.when(FACING, Direction.EAST, AXIS, Axis.Z),
                GBlockstate.variant(step_side_id, true, R0, R0))
            .with(GBlockstate.when(FACING, Direction.EAST, AXIS, Axis.Y),
                GBlockstate.variant(step_side_id, true, R90, R0))
            .with(GBlockstate.when(FACING, Direction.SOUTH, AXIS, Axis.X),
                GBlockstate.variant(step_side_id, true, R180, R90))
            .with(GBlockstate.when(FACING, Direction.SOUTH, AXIS, Axis.Y),
                GBlockstate.variant(step_side_id, true, R90, R90))
            .with(GBlockstate.when(FACING, Direction.WEST, AXIS, Axis.Z),
                GBlockstate.variant(step_side_id, true, R180, R180))
            .with(GBlockstate.when(FACING, Direction.WEST, AXIS, Axis.Y),
                GBlockstate.variant(step_side_id, true, R90, R180))
            .with(GBlockstate.when(FACING, Direction.NORTH, AXIS, Axis.X),
                GBlockstate.variant(step_side_id, true, R0, R270))
            .with(GBlockstate.when(FACING, Direction.NORTH, AXIS, Axis.Y),
                GBlockstate.variant(step_side_id, true, R90, R270));
    }

    @Override
    public void setRecipe(RecipeExporter exporter) {
        RecipeProvider.offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, this, ReFramed.CUBE, 2);
        ShapelessRecipeJsonBuilder
            .create(RecipeCategory.BUILDING_BLOCKS, this)
            .input(ReFramed.STEP, 2)
            .criterion(FabricRecipeProvider.hasItem(ReFramed.CUBE), FabricRecipeProvider.conditionsFromItem(ReFramed.CUBE))
            .criterion(FabricRecipeProvider.hasItem(this), FabricRecipeProvider.conditionsFromItem(this))
            .offerTo(exporter);
    }
}
