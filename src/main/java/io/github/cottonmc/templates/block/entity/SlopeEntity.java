package io.github.cottonmc.templates.block.entity;

import io.github.cottonmc.templates.Templates;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class SlopeEntity extends TemplateEntity {
	public SlopeEntity(BlockPos pos, BlockState state) {
		super(Templates.SLOPE_ENTITY, pos, state, Templates.SLOPE);
	}
}
