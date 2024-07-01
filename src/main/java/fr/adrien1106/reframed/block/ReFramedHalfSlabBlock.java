package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.util.VoxelHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static net.minecraft.state.property.Properties.*;

public class ReFramedHalfSlabBlock extends ReFramedSlabBlock {

    public static VoxelShape[] HALF_SLAB_SHAPES;

	public ReFramedHalfSlabBlock(Settings settings) {
		super(settings);
	}

	@Override
	public boolean canReplace(BlockState state, ItemPlacementContext context) {
        if (context.getPlayer() == null
            || context.getPlayer().isSneaking()
            || !(context.getStack().getItem() instanceof BlockItem block_item)
        ) return false;

        // allow replacing with slab, step, small cube and half stair
        Block block = block_item.getBlock();
        if (block != this) return false;

        // check if the player is clicking on the inner part of the block
		return ReFramed.HALF_SLABS_SLAB
            .matchesShape(
                context.getHitPos(),
                context.getBlockPos(),
                ReFramed.HALF_SLABS_SLAB.getDefaultState().with(FACING, state.get(FACING)),
                2
            );
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockState current_state = ctx.getWorld().getBlockState(ctx.getBlockPos());

		if (current_state.isOf(this))
			return ReFramed.HALF_SLABS_SLAB.getDefaultState()
				.with(FACING, current_state.get(FACING))
                .with(WATERLOGGED, current_state.get(WATERLOGGED));

		return super.getPlacementState(ctx).with(FACING, ctx.getSide().getOpposite());
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return getHalfSlabShape(state.get(FACING));
	}

    public static VoxelShape getHalfSlabShape(Direction direction) {
        return HALF_SLAB_SHAPES[direction.getId()];
    }

    @Override
	public Map<Integer, Integer> getThemeMap(BlockState state, BlockState new_state) {
        if (new_state.isOf(ReFramed.HALF_SLABS_SLAB)) return Map.of(1, 1);
		return super.getThemeMap(state, new_state);
	}

    static {
        HALF_SLAB_SHAPES = VoxelHelper.VoxelListBuilder.create(createCuboidShape(0, 0, 0, 16, 4, 16),6)
            .add(createCuboidShape(0, 12, 0, 16, 16, 16))
            .add(createCuboidShape(0, 0, 0, 16, 16, 4))
            .add(createCuboidShape(0, 0, 12, 16, 16, 16))
            .add(createCuboidShape(0, 0, 0, 4, 16, 16))
            .add(createCuboidShape(12, 0, 0, 16, 16, 16))
            .build();
    }
}
