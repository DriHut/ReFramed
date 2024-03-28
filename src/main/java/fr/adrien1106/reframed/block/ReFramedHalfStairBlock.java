package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.generator.BlockStateProvider;
import fr.adrien1106.reframed.generator.GBlockstate;
import fr.adrien1106.reframed.util.VoxelHelper;
import fr.adrien1106.reframed.util.blocks.BlockHelper;
import fr.adrien1106.reframed.util.blocks.Corner;
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
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static fr.adrien1106.reframed.util.VoxelHelper.VoxelListBuilder;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.*;
import static fr.adrien1106.reframed.util.blocks.Corner.*;
import static net.minecraft.data.client.VariantSettings.Rotation.*;
import static net.minecraft.state.property.Properties.WATERLOGGED;

public class ReFramedHalfStairBlock extends WaterloggableReFramedBlock implements BlockStateProvider {

    public static final VoxelShape[] HALF_STAIR_VOXELS;

    private record ModelCacheKey(Corner corner, int face) {}

    public ReFramedHalfStairBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(CORNER, NORTH_EAST_DOWN).with(CORNER_FACE, 0));
    }

    @Override
    public Object getModelCacheKey(BlockState state) {
        return new ModelCacheKey(state.get(CORNER), state.get(CORNER_FACE));
    }

    @Override
    public int getModelStateCount() {
        return 24;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(CORNER,CORNER_FACE));
    }

    @Override
    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        Direction dir = state.get(CORNER).getDirection(state.get(CORNER_FACE));
        return !(
            context.getPlayer().isSneaking()
                || !(context.getStack().getItem() instanceof BlockItem block_item)
                || (
                    !(
                        block_item.getBlock() == this
                        && ((ReFramedHalfStairsStairBlock) ReFramed.HALF_STAIRS_STAIR)
                            .matchesShape(
                                context.getHitPos(),
                                context.getBlockPos(),
                                ReFramed.HALF_STAIRS_STAIR.getDefaultState()
                                    .with(EDGE, state.get(CORNER).getEdge(dir)),
                                dir.getDirection() == Direction.AxisDirection.POSITIVE ? 1 : 2
                            )
                    )
                    && !(
                        block_item.getBlock() == ReFramed.SMALL_CUBE
                        && BlockHelper.cursorMatchesFace(
                            ReFramed.SMALL_CUBE.getOutlineShape(
                                ReFramed.SMALL_CUBE.getDefaultState()
                                    .with(CORNER, state.get(CORNER).getOpposite(state.get(CORNER_FACE))),
                                context.getWorld(),
                                context.getBlockPos(),
                                ShapeContext.absent()
                            ),
                            BlockHelper.getRelativePos(context.getHitPos(), context.getBlockPos())
                        )
                    )
                )
        );
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState current_state = ctx.getWorld().getBlockState(ctx.getBlockPos());
        if (current_state.isOf(ReFramed.SMALL_CUBE)) {
            Corner corner = current_state.get(CORNER).getOpposite(ctx.getSide().getOpposite());
            return ReFramed.HALF_STAIRS_SLAB.getDefaultState()
                .with(CORNER, corner)
                .with(CORNER_FACE, corner.getDirectionIndex(ctx.getSide().getOpposite()))
                .with(WATERLOGGED, current_state.get(WATERLOGGED));
        }

        if (current_state.isOf(this))
            return ReFramed.HALF_STAIRS_STAIR.getDefaultState()
                .with(EDGE, current_state.get(CORNER).getEdge(current_state.get(CORNER).getDirection(current_state.get(CORNER_FACE))))
                .with(WATERLOGGED, current_state.get(WATERLOGGED));

        Corner corner = BlockHelper.getPlacementCorner(ctx);
        return super.getPlacementState(ctx)
            .with(CORNER, corner)
            .with(CORNER_FACE, corner.getDirectionIndex(ctx.getSide().getOpposite()));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return HALF_STAIR_VOXELS[state.get(CORNER_FACE) + state.get(CORNER).getID() * 3];
    }

    @Override
    public Map<Integer, Integer> getThemeMap(BlockState state, BlockState new_state) {
        if (new_state.isOf(ReFramed.HALF_STAIRS_SLAB)) return Map.of(1, 1);
        if (new_state.isOf(ReFramed.HALF_STAIRS_STAIR))
            return Map.of(
                1,
                state.get(CORNER)
                    .getDirection(state.get(CORNER_FACE))
                    .getDirection() == Direction.AxisDirection.POSITIVE ? 2 : 1
            );
        return super.getThemeMap(state, new_state);
    }

    @Override
    public BlockStateSupplier getMultipart() {
        return getHalfStairMultipart(
            this,
            ReFramed.id("half_stair_down_special"),
            ReFramed.id("half_stair_side_special")
        );
    }

    public static BlockStateSupplier getHalfStairMultipart(Block block, Identifier model_down, Identifier model_side) {
        return MultipartBlockStateSupplier.create(block)
            .with(GBlockstate.when(CORNER, NORTH_EAST_DOWN, CORNER_FACE, 1),
                GBlockstate.variant(model_side, true, R0, R0))
            .with(GBlockstate.when(CORNER, NORTH_EAST_DOWN, CORNER_FACE, 0),
                GBlockstate.variant(model_side, true, R90, R270))
            .with(GBlockstate.when(CORNER, NORTH_EAST_DOWN, CORNER_FACE, 2),
                GBlockstate.variant(model_down, true, R0, R0))

            .with(GBlockstate.when(CORNER, EAST_SOUTH_DOWN, CORNER_FACE, 1),
                GBlockstate.variant(model_side, true, R0, R90))
            .with(GBlockstate.when(CORNER, EAST_SOUTH_DOWN, CORNER_FACE, 0),
                GBlockstate.variant(model_side, true, R90, R0))
            .with(GBlockstate.when(CORNER, EAST_SOUTH_DOWN, CORNER_FACE, 2),
                GBlockstate.variant(model_down, true, R0, R90))

            .with(GBlockstate.when(CORNER, SOUTH_WEST_DOWN, CORNER_FACE, 1),
                GBlockstate.variant(model_side, true, R0, R180))
            .with(GBlockstate.when(CORNER, SOUTH_WEST_DOWN, CORNER_FACE, 0),
                GBlockstate.variant(model_side, true, R90, R90))
            .with(GBlockstate.when(CORNER, SOUTH_WEST_DOWN, CORNER_FACE, 2),
                GBlockstate.variant(model_down, true, R0, R180))

            .with(GBlockstate.when(CORNER, WEST_NORTH_DOWN, CORNER_FACE, 1),
                GBlockstate.variant(model_side, true, R0, R270))
            .with(GBlockstate.when(CORNER, WEST_NORTH_DOWN, CORNER_FACE, 0),
                GBlockstate.variant(model_side, true, R90, R180))
            .with(GBlockstate.when(CORNER, WEST_NORTH_DOWN, CORNER_FACE, 2),
                GBlockstate.variant(model_down, true, R0, R270))

            .with(GBlockstate.when(CORNER, EAST_SOUTH_UP, CORNER_FACE, 0),
                GBlockstate.variant(model_side, true, R180, R0))
            .with(GBlockstate.when(CORNER, EAST_SOUTH_UP, CORNER_FACE, 1),
                GBlockstate.variant(model_side, true, R270, R90))
            .with(GBlockstate.when(CORNER, EAST_SOUTH_UP, CORNER_FACE, 2),
                GBlockstate.variant(model_down, true, R180, R0))

            .with(GBlockstate.when(CORNER, SOUTH_WEST_UP, CORNER_FACE, 0),
                GBlockstate.variant(model_side, true, R180, R90))
            .with(GBlockstate.when(CORNER, SOUTH_WEST_UP, CORNER_FACE, 1),
                GBlockstate.variant(model_side, true, R270, R180))
            .with(GBlockstate.when(CORNER, SOUTH_WEST_UP, CORNER_FACE, 2),
                GBlockstate.variant(model_down, true, R180, R90))

            .with(GBlockstate.when(CORNER, WEST_NORTH_UP, CORNER_FACE, 0),
                GBlockstate.variant(model_side, true, R180, R180))
            .with(GBlockstate.when(CORNER, WEST_NORTH_UP, CORNER_FACE, 1),
                GBlockstate.variant(model_side, true, R270, R270))
            .with(GBlockstate.when(CORNER, WEST_NORTH_UP, CORNER_FACE, 2),
                GBlockstate.variant(model_down, true, R180, R180))

            .with(GBlockstate.when(CORNER, NORTH_EAST_UP, CORNER_FACE, 0),
                GBlockstate.variant(model_side, true, R180, R270))
            .with(GBlockstate.when(CORNER, NORTH_EAST_UP, CORNER_FACE, 1),
                GBlockstate.variant(model_side, true, R270, R0))
            .with(GBlockstate.when(CORNER, NORTH_EAST_UP, CORNER_FACE, 2),
                GBlockstate.variant(model_down, true, R180, R270));
    }

    @Override
    public void setRecipe(RecipeExporter exporter) {
        RecipeProvider.offerStonecuttingRecipe(exporter, RecipeCategory.BUILDING_BLOCKS, this, ReFramed.CUBE, 2);
        ShapedRecipeJsonBuilder
            .create(RecipeCategory.BUILDING_BLOCKS, this, 4)
            .pattern("I  ")
            .pattern("II ")
            .input('I', ReFramed.CUBE)
            .criterion(FabricRecipeProvider.hasItem(ReFramed.CUBE), FabricRecipeProvider.conditionsFromItem(ReFramed.CUBE))
            .criterion(FabricRecipeProvider.hasItem(this), FabricRecipeProvider.conditionsFromItem(this))
            .offerTo(exporter);
    }

    static {
        final VoxelShape HALF_STAIR = VoxelShapes.combineAndSimplify(
            createCuboidShape(8, 0, 0, 16, 16, 8),
            createCuboidShape(0, 0, 0, 8, 8, 8),
            BooleanBiFunction.OR
        );
        HALF_STAIR_VOXELS = VoxelListBuilder.create(HALF_STAIR, 24)
            .add(0 , VoxelHelper::rotateY, VoxelHelper::mirrorZ)
            .add(0 , VoxelHelper::rotateCX, VoxelHelper::mirrorZ)

            .add(0 , VoxelHelper::rotateY)
            .add(1 , VoxelHelper::rotateY)
            .add(2 , VoxelHelper::rotateY)

            .add(3 , VoxelHelper::rotateY)
            .add(4 , VoxelHelper::rotateY)
            .add(5 , VoxelHelper::rotateY)

            .add(6 , VoxelHelper::rotateY)
            .add(7 , VoxelHelper::rotateY)
            .add(8 , VoxelHelper::rotateY)

            .add(0 , VoxelHelper::mirrorY)
            .add(1 , VoxelHelper::mirrorY)
            .add(2 , VoxelHelper::mirrorY)

            .add(12, VoxelHelper::rotateY)
            .add(13, VoxelHelper::rotateY)
            .add(14, VoxelHelper::rotateY)

            .add(15, VoxelHelper::rotateY)
            .add(16, VoxelHelper::rotateY)
            .add(17, VoxelHelper::rotateY)

            .add(18, VoxelHelper::rotateY)
            .add(19, VoxelHelper::rotateY)
            .add(20, VoxelHelper::rotateY)
            .build();
    }
}
