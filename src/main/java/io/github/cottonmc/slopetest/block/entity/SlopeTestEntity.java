package io.github.cottonmc.slopetest.block.entity;

import io.github.cottonmc.slopetest.SlopeTest;
import io.github.cottonmc.slopetest.util.BlockStateUtil;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;
import net.fabricmc.fabric.api.server.PlayerStream;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Direction;

public class SlopeTestEntity extends TemplateBlockEntity {
	public SlopeTestEntity() {
		super(SlopeTest.SLOPE_ENTITY, SlopeTest.SLOPE);
	}
}
