package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.util.VoxelHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static net.minecraft.state.property.Properties.AXIS;

public class ReFramedPillarBlock extends WaterloggableReFramedBlock {

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

    static {
        final VoxelShape PILLAR = createCuboidShape(0, 4, 4, 16, 12, 12);
        PILLAR_VOXELS = VoxelHelper.VoxelListBuilder.create(PILLAR, 3)
            .add(VoxelHelper::rotateZ)
            .add(VoxelHelper::rotateX)
            .build();
    }
}
