package io.github.cottonmc.slopetest.util;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface StateContainer {
	BlockState getContainedState(World world, BlockPos pos);
}
