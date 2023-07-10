package io.github.cottonmc.templates.block;

import com.google.common.base.MoreObjects;
import io.github.cottonmc.templates.util.EdgeDirection;
import io.github.cottonmc.templates.util.StairShapeMaker;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

import javax.annotation.Nullable;

public class TemplateSlopeBlock extends WaterloggableTemplateBlock {
	public static final EnumProperty<EdgeDirection> FACING = EnumProperty.of("facing", EdgeDirection.class);
	
	private static final VoxelShape[] shapes = new VoxelShape[8];
	private static int shapeIndex(Direction dir, BlockHalf half) {
		return dir.getHorizontal() + (half == BlockHalf.TOP ? 4 : 0);
	}
	static {
		//TODO
		for(BlockHalf half : BlockHalf.values()) {
			for(Direction d : Direction.values()) {
				if(d.getHorizontal() == -1) continue;
				shapes[shapeIndex(d, half)] = StairShapeMaker.createHorizontal(d, half, 8, 0.125d, 0.125d);
			}
		}
	}
	
	public TemplateSlopeBlock(Settings settings) {
		super(settings);
		setDefaultState(getDefaultState().with(FACING, EdgeDirection.DOWN_NORTH));
	}
	
	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder.add(FACING));
	}
	
	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		EdgeDirection.guessFromHitResult(ctx.getHitPos(), ctx.getBlockPos());
		
		BlockState sup = super.getPlacementState(ctx);
		if(sup != null) sup = sup.with(FACING, EdgeDirection.guessFromHitResult(ctx.getHitPos(), ctx.getBlockPos()));
		
		return sup;
	}
	
	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView view, BlockPos pos, ShapeContext ctx) {
		return MoreObjects.firstNonNull(
			super.getCollisionShape(state, view, pos, ctx),
			//shapes[shapeIndex(state.get(FACING_OLD), state.get(HALF_OLD))]
			shapes[0] //TODO
		);
	}
	
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext ctx) {
		//return shapes[shapeIndex(state.get(FACING_OLD), state.get(HALF_OLD))];
		return shapes[0]; //TODO
	}
}
