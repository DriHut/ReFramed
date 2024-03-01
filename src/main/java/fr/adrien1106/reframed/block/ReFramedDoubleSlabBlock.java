package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.generator.GBlockstate;
import fr.adrien1106.reframed.generator.MultipartBlockStateProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.data.client.MultipartBlockStateSupplier;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import org.jetbrains.annotations.Nullable;

import static fr.adrien1106.reframed.block.ReFramedSlabBlock.*;
import static net.minecraft.data.client.VariantSettings.Rotation.*;
import static net.minecraft.state.property.Properties.AXIS;

public class ReFramedDoubleSlabBlock extends ReFramedDoubleBlock implements MultipartBlockStateProvider {
    public ReFramedDoubleSlabBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(Properties.AXIS, Direction.Axis.Y));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(Properties.AXIS));
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx).with(Properties.AXIS, ctx.getSide().getAxis());
    }

    @Override
    public VoxelShape getShape(BlockState state, int i) {
        return switch (state.get(Properties.AXIS)) {
            case Y -> i == 2 ? UP    : DOWN;
            case Z -> i == 2 ? NORTH : SOUTH;
            case X -> i == 2 ? EAST  : WEST;
        };
    }

    @Override
    public int getTopThemeIndex(BlockState state) {
        // when the side is shared just return one
        return state.get(AXIS) == Direction.Axis.Y ? 2: super.getTopThemeIndex(state);
    }

    @Override
    public MultipartBlockStateSupplier getMultipart() {
        Identifier model_id = ReFramed.id("double_slab_special");
        return MultipartBlockStateSupplier.create(this)
            .with(GBlockstate.when(AXIS, Direction.Axis.Y),
                GBlockstate.variant(model_id, true, R0, R0))
            .with(GBlockstate.when(AXIS, Direction.Axis.Z),
                GBlockstate.variant(model_id, true, R90, R0))
            .with(GBlockstate.when(AXIS, Direction.Axis.X),
                GBlockstate.variant(model_id, true, R90, R90));
    }
}
