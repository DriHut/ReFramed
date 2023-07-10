package io.github.cottonmc.templates.block;

import com.google.common.base.MoreObjects;
import io.github.cottonmc.templates.api.TemplateInteractionUtil;
import io.github.cottonmc.templates.util.Edge;
import io.github.cottonmc.templates.util.StairShapeMaker;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

import javax.annotation.Nullable;

public class TemplateSlopeBlock extends WaterloggableTemplateBlock {
	public static final EnumProperty<Edge> EDGE = EnumProperty.of("edge", Edge.class);
	
	private static final VoxelShape[] shapes = new VoxelShape[Edge.values().length];
	static {
		for(Edge edge : Edge.values()) {
			shapes[edge.ordinal()] = StairShapeMaker.makeStair(edge, 1, 0.125d, 0.125d, 0.125d, 8);
		}
	}
	
	public TemplateSlopeBlock(Settings settings) {
		super(settings);
		setDefaultState(getDefaultState().with(EDGE, Edge.DOWN_NORTH));
	}
	
	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder.add(EDGE));
	}
	
	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockState sup = super.getPlacementState(ctx);
		if(sup != null) sup = sup.with(EDGE, Edge.closestTo(ctx.getHitPos(), ctx.getBlockPos()));
		
		return sup;
	}
	
	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView view, BlockPos pos, ShapeContext ctx) {
		return MoreObjects.firstNonNull(
			TemplateInteractionUtil.getCollisionShape(state, view, pos, ctx),
			shapes[state.get(EDGE).ordinal()]
		);
	}
	
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext ctx) {
		return shapes[state.get(EDGE).ordinal()];
	}
}
