package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.generator.BlockStateProvider;
import fr.adrien1106.reframed.generator.GBlockstate;
import fr.adrien1106.reframed.util.VoxelHelper;
import fr.adrien1106.reframed.util.blocks.BlockHelper;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.data.client.BlockStateSupplier;
import net.minecraft.data.client.MultipartBlockStateSupplier;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.state.StateManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import static fr.adrien1106.reframed.util.VoxelHelper.VoxelListBuilder;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.CORNER;
import static fr.adrien1106.reframed.util.blocks.Corner.*;
import static net.minecraft.data.client.VariantSettings.Rotation.*;

public class ReFramedSmallCubeBlock extends WaterloggableReFramedBlock implements BlockStateProvider {

    public static final VoxelShape[] SMALL_CUBE_VOXELS;

    public ReFramedSmallCubeBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(CORNER, NORTH_EAST_DOWN));
    }

    @Override
    public Object getModelCacheKey(BlockState state) {
        return state.get(CORNER);
    }

    @Override
    public int getModelStateCount() {
        return 8;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(CORNER));
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx).with(CORNER, BlockHelper.getPlacementCorner(ctx));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SMALL_CUBE_VOXELS[state.get(CORNER).getID()];
    }

    @Override
    public BlockStateSupplier getMultipart() {
        Identifier small_cube_id = ReFramed.id("small_cube_special");
        return MultipartBlockStateSupplier.create(this)
            .with(GBlockstate.when(CORNER, NORTH_EAST_DOWN),
                GBlockstate.variant(small_cube_id, true, R0, R0))
            .with(GBlockstate.when(CORNER, EAST_SOUTH_DOWN),
                GBlockstate.variant(small_cube_id, true, R0, R90))
            .with(GBlockstate.when(CORNER, SOUTH_WEST_DOWN),
                GBlockstate.variant(small_cube_id, true, R0, R180))
            .with(GBlockstate.when(CORNER, WEST_NORTH_DOWN),
                GBlockstate.variant(small_cube_id, true, R0, R270))
            .with(GBlockstate.when(CORNER, EAST_SOUTH_UP),
                GBlockstate.variant(small_cube_id, true, R180, R0))
            .with(GBlockstate.when(CORNER, SOUTH_WEST_UP),
                GBlockstate.variant(small_cube_id, true, R180, R90))
            .with(GBlockstate.when(CORNER, WEST_NORTH_UP),
                GBlockstate.variant(small_cube_id, true, R180, R180))
            .with(GBlockstate.when(CORNER, NORTH_EAST_UP),
                GBlockstate.variant(small_cube_id, true, R180, R270));
    }

    @Override
    public void setRecipe(Consumer<RecipeJsonProvider> exporter) {
        RecipeProvider.offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, this, ReFramed.CUBE, 8);
        ShapelessRecipeJsonBuilder
            .create(RecipeCategory.BUILDING_BLOCKS, this, 8)
            .input(ReFramed.CUBE)
            .criterion(FabricRecipeProvider.hasItem(ReFramed.CUBE), FabricRecipeProvider.conditionsFromItem(ReFramed.CUBE))
            .criterion(FabricRecipeProvider.hasItem(this), FabricRecipeProvider.conditionsFromItem(this))
            .offerTo(exporter);
    }

    static {
        final VoxelShape SMALL_CUBE = VoxelShapes.cuboid(.5f, 0f, 0f, 1f, .5f, .5f);

        SMALL_CUBE_VOXELS = VoxelListBuilder.create(SMALL_CUBE, 8)
            .add(VoxelHelper::rotateY)
            .add(VoxelHelper::rotateY)
            .add(VoxelHelper::rotateY)

            .add(SMALL_CUBE, VoxelHelper::mirrorY)
            .add(VoxelHelper::rotateY)
            .add(VoxelHelper::rotateY)
            .add(VoxelHelper::rotateY)
            .build();
    }
}
