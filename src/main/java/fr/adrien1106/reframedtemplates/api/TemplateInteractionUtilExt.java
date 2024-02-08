package fr.adrien1106.reframedtemplates.api;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;

public interface TemplateInteractionUtilExt {
	default boolean templatesPlayerCanAddRedstoneEmission(BlockState state, BlockView view, BlockPos pos) {
		return state.getWeakRedstonePower(view, pos, Direction.UP) == 0;
	}
	
	default boolean templatesPlayerCanRemoveCollision(BlockState state, BlockView view, BlockPos pos) {
		return !state.getCollisionShape(view, pos).isEmpty();
	}
	
	class Default implements TemplateInteractionUtilExt {
		public static final Default INSTANCE = new Default();
	}
}
