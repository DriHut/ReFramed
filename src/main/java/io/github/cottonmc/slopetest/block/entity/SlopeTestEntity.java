package io.github.cottonmc.slopetest.block.entity;

import io.github.cottonmc.slopetest.SlopeTest;
import io.github.cottonmc.slopetest.util.BlockStateUtil;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.server.PlayerStream;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class SlopeTestEntity extends BlockEntity implements BlockEntityClientSerializable {
	private BlockState renderedState = Blocks.AIR.getDefaultState();

	public SlopeTestEntity() {
		super(SlopeTest.SLOPE_ENTITY);
	}

	public BlockState getRenderedState() {
		return renderedState;
	}

	public void setRenderedState(BlockState state) {
		this.renderedState = state;
		markDirty();
	}

	@Override
	public void fromTag(CompoundTag tag) {
		super.fromTag(tag);
		renderedState = BlockStateUtil.fromTag(tag);
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		super.toTag(tag);
		BlockStateUtil.toTag(tag, renderedState);
		return tag;
	}

	@Override
	public void fromClientTag(CompoundTag tag) {
		fromTag(tag);
	}

	@Override
	public CompoundTag toClientTag(CompoundTag tag) {
		return toTag(tag);
	}

	@Override
	public void markDirty() {
		super.markDirty();
		if (!this.world.isClient) {
			for (Object obj : PlayerStream.watching(this).toArray()) {
				ServerPlayerEntity player = (ServerPlayerEntity) obj;
				player.networkHandler.sendPacket(this.toUpdatePacket());
			}
		}
	}
}
