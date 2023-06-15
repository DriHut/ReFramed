package io.github.cottonmc.templates.block.entity;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class TemplateEntity extends BlockEntity implements RenderAttachmentBlockEntity {
	protected BlockState renderedState = Blocks.AIR.getDefaultState();
	protected boolean glowstone = false;
	protected boolean redstone = false;
	private final Block baseBlock;
	
	public TemplateEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, Block baseBlock) {
		super(type, pos, state);
		this.baseBlock = baseBlock;
	}
	
	@Override
	public void readNbt(NbtCompound tag) {
		super.readNbt(tag);
		renderedState = NbtHelper.toBlockState(Registries.BLOCK.getReadOnlyWrapper(), tag.getCompound("BlockState"));
		glowstone = tag.getBoolean("Glowstone");
		redstone = tag.getBoolean("Redstone");
		if(world != null && world.isClient) {
			//TODO probably unsafe, i think the method was removed in 1.14.4 or something though
			// i cant find any relevant method that takes only 1 blockpos argument
			((ClientWorld) world).scheduleBlockRenders(pos.getX(), pos.getY(), pos.getZ());
			//world.scheduleBlockRender(pos);
		}
	}
	
	@Override
	public void writeNbt(NbtCompound tag) {
		super.writeNbt(tag);
		tag.put("BlockState", NbtHelper.fromBlockState(renderedState));
		tag.putBoolean("Glowstone", glowstone);
		tag.putBoolean("Redstone", redstone);
	}
	
	@Nullable
	@Override
	public Packet<ClientPlayPacketListener> toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create(this);
	}
	
	@Override
	public NbtCompound toInitialChunkDataNbt() {
		return createNbt();
	}
	
	@Override
	public BlockState getRenderAttachmentData() {
		return renderedState;
	}
	
	public void change() {
		markDirty();
		if(world != null && !world.isClient) {
			//for(ServerPlayerEntity player : PlayerLookup.tracking(this)) player.networkHandler.sendPacket(this.toUpdatePacket());
			
			//TODO is this needed
			//world.updateNeighborsAlways(pos.offset(Direction.UP), baseBlock);
			world.updateListeners(pos, getCachedState(), getCachedState(), 3);
		}
	}
	
	public BlockState getRenderedState() {
		return renderedState;
	}
	
	public void setRenderedState(BlockState newState) {
		BlockState lastState = renderedState;
		renderedState = newState;
		if(!Objects.equals(lastState, newState)) change();
	}
	
	public boolean hasGlowstone() {
		return glowstone;
	}
	
	public void setGlowstone(boolean newGlowstone) {
		boolean lastGlowstone = glowstone;
		glowstone = newGlowstone;
		if(lastGlowstone != newGlowstone) change();
	}
	
	public boolean hasRedstone() {
		return redstone;
	}
	
	public void setRedstone(boolean newRedstone) {
		boolean lastRedstone = redstone;
		redstone = newRedstone;
		if(lastRedstone != newRedstone) change();
	}
}
