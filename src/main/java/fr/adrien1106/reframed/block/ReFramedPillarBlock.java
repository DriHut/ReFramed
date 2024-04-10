package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.generator.BlockStateProvider;
import fr.adrien1106.reframed.generator.GBlockstate;
import fr.adrien1106.reframed.util.VoxelHelper;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.data.client.BlockStateSupplier;
import net.minecraft.data.client.MultipartBlockStateSupplier;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.state.StateManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static net.minecraft.data.client.VariantSettings.Rotation.*;
import static net.minecraft.state.property.Properties.AXIS;

public class ReFramedPillarBlock extends WaterloggableReFramedBlock implements BlockStateProvider {

    public static final VoxelShape[] PILLAR_VOXELS;

    public ReFramedPillarBlock(Settings settings) {
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

    @Override
    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        return !(context.getPlayer().isSneaking()
            || !(context.getStack().getItem() instanceof BlockItem block_item)
            || !(
                block_item.getBlock() == this
                && state.get(AXIS) != context.getSide().getAxis()
            )
        );
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        // TODO: PILLARS WALL
        return super.getPlacementState(ctx).with(AXIS, ctx.getSide().getAxis());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getPillarShape(state.get(AXIS));
    }

    public static VoxelShape getPillarShape(Direction.Axis axis) {
        return PILLAR_VOXELS[axis.ordinal()];
    }

    @Override
    public Map<Integer, Integer> getThemeMap(BlockState state, BlockState new_state) {
//        if (new_state.getBlock() == ReFramed.PILLARS_WALL) return Map.of(1, 1); // TODO: PILLARS WALL
        return super.getThemeMap(state, new_state);
    }

    @Override
    public BlockStateSupplier getMultipart() {
        Identifier model_id = ReFramed.id("pillar_special");
        return MultipartBlockStateSupplier.create(this)
            .with(GBlockstate.when(AXIS, Direction.Axis.X),
                GBlockstate.variant(model_id, true, R90, R90))
            .with(GBlockstate.when(AXIS, Direction.Axis.Y),
                GBlockstate.variant(model_id, true, R0, R0))
            .with(GBlockstate.when(AXIS, Direction.Axis.Z),
                GBlockstate.variant(model_id, true, R90, R0));
    }

    @Override
    public void setRecipe(RecipeExporter exporter) {
        RecipeProvider.offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, this, ReFramed.CUBE, 4);
        ShapedRecipeJsonBuilder
            .create(RecipeCategory.BUILDING_BLOCKS, this, 8)
            .pattern("I")
            .pattern("I")
            .input('I', ReFramed.CUBE)
            .criterion(FabricRecipeProvider.hasItem(ReFramed.CUBE), FabricRecipeProvider.conditionsFromItem(ReFramed.CUBE))
            .criterion(FabricRecipeProvider.hasItem(this), FabricRecipeProvider.conditionsFromItem(this))
            .offerTo(exporter);
    }

    static {
        final VoxelShape PILLAR = createCuboidShape(0, 4, 4, 16, 12, 12);
        PILLAR_VOXELS = VoxelHelper.VoxelListBuilder.create(PILLAR, 3)
            .add(VoxelHelper::rotateZ)
            .add(VoxelHelper::rotateX)
            .build();
    }
}
