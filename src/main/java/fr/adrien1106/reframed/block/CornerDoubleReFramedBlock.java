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

import static fr.adrien1106.reframed.util.blocks.BlockProperties.CORNER;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.CORNER_FACE;

public abstract class CornerDoubleReFramedBlock extends WaterloggableReFramedDoubleBlock {

    public CornerDoubleReFramedBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(CORNER, Corner.NORTH_EAST_DOWN).with(CORNER_FACE, 0));
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
    @SuppressWarnings("deprecation")
    public abstract VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context);


    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        Corner corner = state.get(CORNER);
        Direction face = corner.getDirection(state.get(CORNER_FACE));
        return state
            .with(CORNER, corner.rotate(rotation))
            .with(CORNER_FACE, corner.getDirectionIndex(rotation.rotate(face)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        Corner corner = state.get(CORNER);
        Direction face = corner.getDirection(state.get(CORNER_FACE));
        return state
            .with(CORNER, corner.mirror(mirror))
            .with(CORNER_FACE, corner.getDirectionIndex(mirror.apply(face)));
    }

    @Override
    public abstract VoxelShape getShape(BlockState state, int i);
}
