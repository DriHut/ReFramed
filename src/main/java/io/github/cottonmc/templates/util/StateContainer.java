package io.github.cottonmc.templates.util;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface StateContainer {
	BlockState getContainedState(World world, BlockPos pos);
	
	void setContainedState(World world, BlockPos pos, BlockState state);
}
