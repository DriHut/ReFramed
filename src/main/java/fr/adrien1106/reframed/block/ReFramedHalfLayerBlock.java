package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.util.VoxelHelper;
import fr.adrien1106.reframed.util.blocks.BlockHelper;
import fr.adrien1106.reframed.util.blocks.Edge;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static fr.adrien1106.reframed.util.VoxelHelper.VoxelListBuilder;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.EDGE;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.EDGE_FACE;
import static net.minecraft.state.property.Properties.*;

public class ReFramedHalfLayerBlock extends LayeredReFramedBlock {

    public static final VoxelShape[] HALF_LAYER_VOXELS;

    public ReFramedHalfLayerBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(EDGE, Edge.NORTH_DOWN).with(EDGE_FACE, 0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(EDGE, EDGE_FACE));
    }

    @Override
    public boolean canReplace(BlockState state, ItemPlacementContext context) {
        if (super.canReplace(state, context)) return true;

        if (context.getPlayer() == null
            || context.getPlayer().isSneaking()
            || !(context.getStack().getItem() instanceof BlockItem block_item)
        ) return false;

        Edge edge = state.get(EDGE);
        Direction face = edge.getDirection(state.get(EDGE_FACE));
        if (block_item.getBlock() == ReFramed.SLAB)
            return ReFramed.SLAB
                .matchesShape(
                    context.getHitPos(),
                    context.getBlockPos(),
                    ReFramed.SLAB.getDefaultState().with(FACING, edge.getOtherDirection(face).getOpposite())
                );

        if (block_item.getBlock() == ReFramed.STEP)
            return ReFramed.STEP
                .matchesShape(
                    context.getHitPos(),
                    context.getBlockPos(),
                    ReFramed.STEP.getDefaultState().with(EDGE, edge.getOpposite(face))
                );

        return false;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getHalfLayerShape(
            state.get(EDGE),
            state.get(EDGE_FACE),
            state.get(LAYERS)
        );
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState previous = ctx.getWorld().getBlockState(ctx.getBlockPos());
        BlockState state = super.getPlacementState(ctx);

        if (previous.isOf(this))
            return state;

        if (previous.isOf(ReFramed.SLABS_HALF_LAYER) || previous.isOf(ReFramed.STEPS_HALF_LAYER))
            return previous.with(LAYERS, Math.min(8, previous.get(LAYERS) + 1));

        if (previous.isOf(ReFramed.SLAB)) {
            Direction face = previous.get(FACING);
            Edge edge;
            if (face.getAxis() == ctx.getSide().getAxis()) {
                edge = BlockHelper.getPlacementEdge(ctx);
                if (face == ctx.getSide()) edge = edge.getOpposite(edge.getOtherDirection(ctx.getSide()));
            } else edge = Edge.getByDirections(face, ctx.getSide().getOpposite());

            return ReFramed.SLABS_HALF_LAYER.getDefaultState()
                .with(EDGE, edge)
                .with(EDGE_FACE, edge.getDirectionIndex(face))
                .with(WATERLOGGED, previous.get(WATERLOGGED));
        }

        if (previous.isOf(ReFramed.STEP)) {
            int face_index = 0;
            Edge edge = previous.get(EDGE);
            if (!ReFramed.STEP.matchesShape(
                ctx.getHitPos(),
                ctx.getBlockPos(),
                ReFramed.STEP.getDefaultState().with(EDGE, edge.getOpposite(1))
            )) face_index = 1;
            return ReFramed.STEPS_HALF_LAYER.getDefaultState()
                .with(EDGE, edge)
                .with(EDGE_FACE, face_index)
                .with(WATERLOGGED, previous.get(WATERLOGGED));
        }

        Edge edge = BlockHelper.getPlacementEdge(ctx);
        return state.with(EDGE, edge).with(EDGE_FACE, edge.getDirectionIndex(ctx.getSide().getOpposite()));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        Edge edge = state.get(EDGE);
        Direction face = rotation.rotate(edge.getDirection(state.get(EDGE_FACE)));
        edge = edge.rotate(rotation);
        return state.with(EDGE, edge).with(EDGE_FACE, edge.getDirectionIndex(face));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        Edge edge = state.get(EDGE);
        Direction face = mirror.apply(edge.getDirection(state.get(EDGE_FACE)));
        edge = edge.mirror(mirror);
        return state.with(EDGE, edge).with(EDGE_FACE, edge.getDirectionIndex(face));
    }

    @Override
    public Map<Integer, Integer> getThemeMap(BlockState state, BlockState new_state) {
        if (new_state.isOf(ReFramed.SLABS_HALF_LAYER)
            || new_state.isOf(ReFramed.STEPS_HALF_LAYER)
        ) return Map.of(1, 2);
        return super.getThemeMap(state, new_state);
    }

    public static VoxelShape getHalfLayerShape(Edge edge, int face, int layer) {
        return HALF_LAYER_VOXELS[edge.ordinal() * 16 + face * 8 + layer - 1];
    }

    static {
        VoxelListBuilder builder = VoxelListBuilder.create(createCuboidShape(0, 0, 0, 16, 8, 2), 192)
            .add(createCuboidShape(0, 0, 0, 16, 8, 4))
            .add(createCuboidShape(0, 0, 0, 16, 8, 6))
            .add(createCuboidShape(0, 0, 0, 16, 8, 8))
            .add(createCuboidShape(0, 0, 0, 16, 8, 10))
            .add(createCuboidShape(0, 0, 0, 16, 8, 12))
            .add(createCuboidShape(0, 0, 0, 16, 8, 14))
            .add(createCuboidShape(0, 0, 0, 16, 8, 16));

        for (int i = 0; i < 8; i++) {
            builder.add(i, VoxelHelper::rotateCX, VoxelHelper::mirrorZ);
        }
        for (int i = 0; i < 48; i++) {
            builder.add(i, VoxelHelper::rotateCX);
        }
        for (int i = 0; i < 64; i++) {
            builder.add(i, VoxelHelper::rotateCY);
        }
        for (int i = 64; i < 80; i++) {
            builder.add(i, VoxelHelper::rotateX);
        }
        for (int i = 80; i < 96; i++) {
            builder.add(i, VoxelHelper::rotateX);
        }
        for (int i = 96; i < 112; i++) {
            builder.add(i, VoxelHelper::rotateX);
        }
        for (int i = 112; i < 128; i++) {
            builder.add(i, VoxelHelper::rotateX);
        }

        HALF_LAYER_VOXELS = builder.build();
    }
}
