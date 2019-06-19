package io.github.cottonmc.slopetest.block.entity;

import io.github.cottonmc.slopetest.util.BlockStateUtil;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;
import net.fabricmc.fabric.api.server.PlayerStream;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Direction;

public abstract class TemplateBlockEntity extends BlockEntity implements BlockEntityClientSerializable, RenderAttachmentBlockEntity {
	protected BlockState renderedState = Blocks.AIR.getDefaultState();
	protected boolean glowstone = false;
	protected boolean redstone = false;
	private Block baseBlock;

	public TemplateBlockEntity(BlockEntityType<?> type, Block baseBlock) {
		super(type);
		this.baseBlock = baseBlock;
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
		glowstone = tag.getBoolean("Glowstone");
		redstone = tag.getBoolean("Redstone");
		if (world != null && world.isClient) {
			world.scheduleBlockRender(pos);
		}
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		super.toTag(tag);
		BlockStateUtil.toTag(tag, renderedState);
		tag.putBoolean("Glowstone", glowstone);
		tag.putBoolean("Redstone", redstone);
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
		if (world != null && !world.isClient) {
			for (Object obj : PlayerStream.watching(this).toArray()) {
				ServerPlayerEntity player = (ServerPlayerEntity) obj;
				player.networkHandler.sendPacket(this.toUpdatePacket());
			}
			world.updateNeighborsAlways(pos.offset(Direction.UP), baseBlock);
			BlockState state = world.getBlockState(pos);
			world.updateListeners(pos, state, state, 1);
		}
	}

	@Override
	public BlockState getRenderAttachmentData() {
		return renderedState;
	}

	public boolean hasGlowstone() {
		return glowstone;
	}

	public void addGlowstone() {
		glowstone = true;
		markDirty();
	}

	public boolean hasRedstone() {
		return redstone;
	}

	public void addRedstone() {
		redstone = true;
		markDirty();
	}
}
