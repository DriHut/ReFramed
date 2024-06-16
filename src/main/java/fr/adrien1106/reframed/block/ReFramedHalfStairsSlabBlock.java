package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.util.blocks.BlockHelper;
import fr.adrien1106.reframed.util.blocks.Corner;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

import static fr.adrien1106.reframed.block.ReFramedHalfStairBlock.getHalfStairShape;
import static fr.adrien1106.reframed.block.ReFramedSlabBlock.getSlabShape;
import static fr.adrien1106.reframed.block.ReFramedSmallCubeBlock.getSmallCubeShape;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.CORNER;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.CORNER_FACE;
import static fr.adrien1106.reframed.util.blocks.Corner.NORTH_EAST_DOWN;
import static net.minecraft.util.shape.VoxelShapes.empty;

public class ReFramedHalfStairsSlabBlock extends WaterloggableReFramedDoubleBlock {

    public ReFramedHalfStairsSlabBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(CORNER, NORTH_EAST_DOWN).with(CORNER_FACE, 0));
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
    public VoxelShape getCollisionShape(BlockState state, BlockView view, BlockPos pos, ShapeContext ctx) {
        return isGhost(view, pos) ? empty(): getOutlineShape(state, view, pos, ctx);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getSlabShape(state.get(CORNER).getDirection(state.get(CORNER_FACE)));
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        Corner corner = state.get(CORNER).rotate(rotation);
        Direction face = state.get(CORNER).getDirection(state.get(CORNER_FACE));
        return state.with(CORNER, corner).with(CORNER_FACE, corner.getDirectionIndex(rotation.rotate(face)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        Corner corner = state.get(CORNER).mirror(mirror);
        Direction face = state.get(CORNER).getDirection(state.get(CORNER_FACE));
        return state.with(CORNER, corner).with(CORNER_FACE, corner.getDirectionIndex(mirror.apply(face)));
    }

    @Override
    public VoxelShape getShape(BlockState state, int i) {
        Corner corner = state.get(CORNER);
        int face = state.get(CORNER_FACE);
        return i == 2
            ? getSmallCubeShape(corner.getOpposite(face))
            : getHalfStairShape(corner, face);
    }
}
