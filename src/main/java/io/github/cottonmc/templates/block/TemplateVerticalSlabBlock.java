package io.github.cottonmc.templates.block;

import io.github.cottonmc.templates.api.TemplateInteractionUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.enums.SlabType;
import net.minecraft.fluid.FluidState;
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
		SlabType type = state.get(TYPE);
		if(type == SlabType.DOUBLE) return VoxelShapes.fullCube();
		
		Affinity aff = state.get(AFFINITY);
		if(type == SlabType.BOTTOM && aff == Affinity.X) return WEST_SHAPE;
		if(type == SlabType.BOTTOM && aff == Affinity.Z) return NORTH_SHAPE;
		if(type == SlabType.TOP && aff == Affinity.X) return EAST_SHAPE;
		if(type == SlabType.TOP && aff == Affinity.Z) return SOUTH_SHAPE;
		
		return VoxelShapes.fullCube(); //unreachable
	}
	
	@Override
	public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockPos pos = ctx.getBlockPos();
		BlockState existingState = ctx.getWorld().getBlockState(pos);
		if(existingState.isOf(this)) {
			return TemplateInteractionUtil.modifyPlacementState(existingState.with(TYPE, SlabType.DOUBLE).with(WATERLOGGED, false), ctx);
		} else {
//			double dx = ctx.getHitPos().x - ctx.getBlockPos().getX();
//			double dz = ctx.getHitPos().z - ctx.getBlockPos().getZ();
//			
//			Direction hmm = switch(ctx.getHorizontalPlayerFacing()) {
//				case NORTH, SOUTH -> dx < 0.5 ? Direction.WEST : Direction.EAST;
//				case EAST, WEST -> dz < 0.5 ? Direction.NORTH : Direction.SOUTH;
//				default -> Direction.NORTH; //unreachable
//			};
			
			Direction hmm = ctx.getHorizontalPlayerFacing();
			
			return TemplateInteractionUtil.modifyPlacementState(getDefaultState()
				.with(WATERLOGGED, ctx.getWorld().getFluidState(pos).getFluid() == Fluids.WATER)
				.with(TYPE, (hmm == Direction.NORTH || hmm == Direction.WEST) ? SlabType.BOTTOM : SlabType.TOP)
				.with(AFFINITY, (hmm == Direction.NORTH || hmm == Direction.SOUTH) ? Affinity.Z : Affinity.X), ctx);
		}
	}
	
	@Override
	public boolean canReplace(BlockState state, ItemPlacementContext ctx) {
		SlabType type = state.get(TYPE);
		if(type == SlabType.DOUBLE) return false;
		
		ItemStack stack = ctx.getStack();
		if(!stack.isOf(asItem())) return false;
		
		if(ctx.canReplaceExisting()) {
			//Easy check -> clicking on the floor or ceiling should complete the slab.
			//I don't think this check actually works lol, but vanilla has something like it
			//If you click on the floor or ceiling of the next block, you get !canReplaceExisting
			Direction dir = ctx.getSide();
			if(dir.getAxis().isVertical()) return true;
			
			//Hard check -> clicking the west face of a slab occupying the east half of the block should complete the slab.
			Affinity aff = state.get(AFFINITY);
			return (type == SlabType.BOTTOM && aff == Affinity.X && dir == Direction.EAST) ||
				(type == SlabType.BOTTOM && aff == Affinity.Z && dir == Direction.SOUTH) ||
				(type == SlabType.TOP && aff == Affinity.X && dir == Direction.WEST) ||
				(type == SlabType.TOP && aff == Affinity.Z && dir == Direction.NORTH);
		} else {
			//This looks wrong, right? if !ctx.canReplaceExisting, return "true"?
			//I'll chalk this up to a bad Yarn name. This method seems to return false when the placement was "bumped"
			//into this blockspace, like when you click the side of an end rod that's facing my block
			return true;
		}
	}
	
	enum Affinity implements StringIdentifiable {
		X, Z;
		
		@Override
		public String asString() {
			return this == X ? "x" : "z";
		}
	}
}
