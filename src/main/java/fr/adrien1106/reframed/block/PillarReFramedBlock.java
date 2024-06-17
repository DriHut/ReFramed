package fr.adrien1106.reframed.block;

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

import static net.minecraft.state.property.Properties.AXIS;

public abstract class PillarReFramedBlock extends WaterloggableReFramedBlock {
    public PillarReFramedBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(AXIS, Direction.Axis.Y));
    }

    @Override
    protected void appendProperties(StateManager.Builder< Block, BlockState > builder) {
        super.appendProperties(builder.add(AXIS));
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx).with(AXIS, ctx.getSide().getAxis());
    }

    @Override
    @SuppressWarnings("deprecation")
    public abstract VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context);

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(AXIS, rotation.rotate(Direction.get(Direction.AxisDirection.POSITIVE, state.get(AXIS))).getAxis());
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.with(AXIS, mirror.apply(Direction.get(Direction.AxisDirection.POSITIVE, state.get(AXIS))).getAxis());
    }
}
