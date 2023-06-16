package io.github.cottonmc.templates.block.entity;

import io.github.cottonmc.templates.Templates;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class TemplateEntity extends BlockEntity implements RenderAttachmentBlockEntity {
	protected BlockState renderedState = Blocks.AIR.getDefaultState();
	protected boolean glowstone = false;
	protected boolean redstone = false;
	
	public TemplateEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}
	
	@Override
	public void readNbt(NbtCompound tag) {
		super.readNbt(tag);
		
		BlockState lastRenderedState = renderedState;
		
		renderedState = NbtHelper.toBlockState(Registries.BLOCK.getReadOnlyWrapper(), tag.getCompound("BlockState"));
		glowstone = tag.getBoolean("Glowstone");
		redstone = tag.getBoolean("Redstone");
		
		//Force a chunk remesh on the client, if the displayed blockstate has changed
		if(world != null && world.isClient && !Objects.equals(lastRenderedState, renderedState)) {
			Templates.chunkRerenderProxy.accept(world, pos);
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
		if(world instanceof ServerWorld sworld) sworld.getChunkManager().markForUpdate(pos); //dispatch to clients
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
		if(lastRedstone != newRedstone) {
			world.updateNeighbors(pos, getCachedState().getBlock());
			change();
		}
	}
}
