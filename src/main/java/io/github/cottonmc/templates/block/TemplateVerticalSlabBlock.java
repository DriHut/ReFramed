package io.github.cottonmc.templates.block;

import io.github.cottonmc.templates.api.TemplateInteractionUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.enums.SlabType;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

//Extending SlabBlock from this is a little bit bold - let's see how this goes
public class TemplateVerticalSlabBlock extends TemplateSlabBlock {
	public TemplateVerticalSlabBlock(Settings settings) {
		super(settings);
		setDefaultState(getDefaultState().with(AFFINITY, Affinity.X));
	}
	
	protected static final EnumProperty<Affinity> AFFINITY = EnumProperty.of("affinity", Affinity.class);
	protected static final VoxelShape NORTH_SHAPE = createCuboidShape(0, 0, 0, 16, 16, 8);
	protected static final VoxelShape EAST_SHAPE = createCuboidShape(8, 0, 0, 16, 16, 16);
	protected static final VoxelShape SOUTH_SHAPE = createCuboidShape(0, 0, 8, 16, 16, 16);
	protected static final VoxelShape WEST_SHAPE = createCuboidShape(0, 0, 0, 8, 16, 16);
	
	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder.add(AFFINITY));
	}
	
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
		Direction d = stateToDirection(state);
		if(d == null) return VoxelShapes.fullCube(); //double slab
		else return switch(d) {
			case NORTH -> NORTH_SHAPE;
			case EAST -> EAST_SHAPE;
			case SOUTH -> SOUTH_SHAPE;
			case WEST -> WEST_SHAPE;
			default -> VoxelShapes.fullCube(); //unreachable
		};
	}
	
	@Override
	public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockPos pos = ctx.getBlockPos();
		BlockState existingState = ctx.getWorld().getBlockState(pos);
		BlockState state;
		
		if(existingState.isOf(this)) {
			//Player clicked inside of an existing vertical slab. Complete the double slab.
			state = existingState.with(TYPE, SlabType.DOUBLE).with(WATERLOGGED, false);
		} else {
			state = getDefaultState().with(WATERLOGGED, ctx.getWorld().getFluidState(pos).getFluid() == Fluids.WATER);
			
			//chosen by fair dice roll, guaranteed to be intuitive
			if(ctx.getPlayer() != null && ctx.getPlayer().isSneaky() && ctx.getSide().getAxis().isHorizontal()) {
				state = directionToState(state, ctx.getSide().getOpposite());
			} else {
				state = directionToState(state, ctx.getHorizontalPlayerFacing());
			}
		}
		
		return TemplateInteractionUtil.modifyPlacementState(state, ctx);
	}
	
	@Override
	public boolean canReplace(BlockState state, ItemPlacementContext ctx) {
		SlabType type = state.get(TYPE);
		if(type == SlabType.DOUBLE) return false;
		
		ItemStack stack = ctx.getStack();
		if(!stack.isOf(asItem())) return false;
		
		//This looks wrong, right? if !ctx.canReplaceExisting, return "true"?
		//canReplaceExisting seems to return false when the placement was "bumped"
		//into this blockspace, like when you click the side of an end rod that's facing my block.
		//If that happens I dont care what orientation you're facing, let's just complete the slab.
		if(!ctx.canReplaceExisting()) return true;
		
		Direction d = stateToDirection(state);
		return d != null && d == ctx.getSide().getOpposite();
	}
	
	protected enum Affinity implements StringIdentifiable {
		X, Z;
		
		@Override
		public String asString() {
			return this == X ? "x" : "z";
		}
	}
	
	//This only exists because I'm being dumb and extending SlabBlock.
	//Really I should fold out into a six-way N/S/E/W/double_x/double_z enum.
	protected @Nullable Direction stateToDirection(BlockState state) {
		SlabType type = state.get(TYPE);
		if(type == SlabType.DOUBLE) return null;
		
		return state.get(AFFINITY) == Affinity.X ?
			(type == SlabType.BOTTOM ? Direction.WEST : Direction.EAST) :
			(type == SlabType.BOTTOM ? Direction.NORTH : Direction.SOUTH);
	}
	
	protected BlockState directionToState(BlockState state, Direction dir) {
		return state.with(AFFINITY, (dir == Direction.EAST || dir == Direction.WEST) ? Affinity.X : Affinity.Z)
			.with(TYPE, (dir == Direction.NORTH || dir == Direction.WEST) ? SlabType.BOTTOM : SlabType.TOP);
	}
}
