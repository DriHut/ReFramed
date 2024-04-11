package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.util.blocks.BlockHelper;
import fr.adrien1106.reframed.util.blocks.Corner;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

import static fr.adrien1106.reframed.block.ReFramedHalfStairBlock.HALF_STAIR_VOXELS;
import static fr.adrien1106.reframed.block.ReFramedSlabBlock.getSlabShape;
import static fr.adrien1106.reframed.block.ReFramedSmallCubeBlock.SMALL_CUBE_VOXELS;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.CORNER;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.CORNER_FACE;
import static fr.adrien1106.reframed.util.blocks.Corner.NORTH_EAST_DOWN;
import static net.minecraft.util.shape.VoxelShapes.empty;

public class ReFramedHalfStairsSlabBlock extends WaterloggableReFramedDoubleBlock {

    private record ModelCacheKey(Corner corner, int face) {}

    public ReFramedHalfStairsSlabBlock(Settings settings) {
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
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        Corner corner = BlockHelper.getPlacementCorner(ctx);
        return super.getPlacementState(ctx)
            .with(CORNER, corner)
            .with(CORNER_FACE, corner.getDirectionIndex(ctx.getSide().getOpposite()));
    }

    @Override
    public VoxelShape getCullingShape(BlockState state, BlockView view, BlockPos pos) {
        return isGhost(view, pos) ? empty(): getSlabShape(state.get(CORNER).getDirection(state.get(CORNER_FACE)));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getSlabShape(state.get(CORNER).getDirection(state.get(CORNER_FACE)));
    }

    @Override
    public VoxelShape getShape(BlockState state, int i) {
        Corner corner = state.get(CORNER);
        int face = state.get(CORNER_FACE);
        return i == 2
            ? SMALL_CUBE_VOXELS[corner.getOpposite(face).getID()]
            : HALF_STAIR_VOXELS[face + corner.getID() * 3];
    }
}
