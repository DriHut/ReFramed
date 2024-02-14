package fr.adrien1106.reframed.util;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

public interface ReframedInteractible {
	default boolean canAddRedstoneEmission(BlockState state, BlockView view, BlockPos pos) {
		return state.getWeakRedstonePower(view, pos, Direction.UP) == 0;
	}
	
	default boolean canRemoveCollision(BlockState state, BlockView view, BlockPos pos) {
		return !state.getCollisionShape(view, pos).isEmpty();
	}
	
	class Default implements ReframedInteractible {
		public static final Default INSTANCE = new Default();
	}
}
