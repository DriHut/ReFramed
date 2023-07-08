package io.github.cottonmc.templates.block;

import com.google.common.base.MoreObjects;
import io.github.cottonmc.templates.api.TemplateInteractionUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

public class TemplatePostBlock extends WaterloggableTemplateBlock {
	public TemplatePostBlock(Settings settings) {
		super(settings);
		
		setDefaultState(getDefaultState().with(Properties.AXIS, Direction.Axis.Y));
	}
	
	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder.add(Properties.AXIS));
	}
	
	@Override
	public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockState sup = super.getPlacementState(ctx);
		if(sup != null) sup = sup.with(Properties.AXIS, ctx.getSide().getAxis());
		return sup;
	}
	
	protected static final VoxelShape SHAPE_X = createCuboidShape(0, 6, 6, 16, 10, 10);
	protected static final VoxelShape SHAPE_Y = createCuboidShape(6, 0, 6, 10, 16, 10);
	protected static final VoxelShape SHAPE_Z = createCuboidShape(6, 6, 0, 10, 10, 16);
	
	protected VoxelShape shap(BlockState state) {
		return switch(state.get(Properties.AXIS)) {
			case X -> SHAPE_X;
			case Y -> SHAPE_Y;
			case Z -> SHAPE_Z;
		};
	}
	
	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView view, BlockPos pos, ShapeContext ctx) {
		return MoreObjects.firstNonNull(TemplateInteractionUtil.getCollisionShape(state, view, pos, ctx), shap(state));
	}
	
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext ctx) {
		return shap(state);
	}
}
