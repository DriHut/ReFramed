package io.github.cottonmc.templates.block.entity;

import io.github.cottonmc.templates.Templates;
import io.github.cottonmc.templates.api.ThemeableBlockEntity;
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

public class TemplateEntity extends BlockEntity implements ThemeableBlockEntity {
	protected BlockState renderedState = Blocks.AIR.getDefaultState();
	
	//Whether the player has manually spent a redstone/glowstone item to upgrade the template.
	//It's possible to get templates that, e.g. glow, without manually spending a glowstone on them
	//(put a froglight in a template!) Same for redstone activation. We need to separately store
	//whether a redstone/glowstone should be refunded when the player breaks the template, and wasting a
	//blockstate for it is a little silly, so, here you go.
	protected boolean spentGlowstoneDust = false;
	protected boolean spentRedstoneTorch = false;
	protected boolean spentPoppedChorus = false;
	
	public TemplateEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}
	
	@Override
	public void readNbt(NbtCompound tag) {
		super.readNbt(tag);
		
		BlockState lastRenderedState = renderedState;
		
		renderedState = NbtHelper.toBlockState(Registries.BLOCK.getReadOnlyWrapper(), tag.getCompound("BlockState"));
		spentGlowstoneDust = tag.getBoolean("Glowstone");
		spentRedstoneTorch = tag.getBoolean("Redstone");
		spentPoppedChorus = tag.getBoolean("Chorus");
		
		//Force a chunk remesh on the client if the displayed blockstate has changed
		if(world != null && world.isClient && !Objects.equals(lastRenderedState, renderedState)) {
			Templates.chunkRerenderProxy.accept(world, pos);
		}
	}
	
	@Override
	public void writeNbt(NbtCompound tag) {
		super.writeNbt(tag);
		tag.put("BlockState", NbtHelper.fromBlockState(renderedState));
		tag.putBoolean("Glowstone", spentGlowstoneDust);
		tag.putBoolean("Redstone", spentRedstoneTorch);
		tag.putBoolean("Chorus", spentPoppedChorus);
	}
	
	@Nullable
	@Override
	public Packet<ClientPlayPacketListener> toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create(this);
	}
	
	@Override
	public NbtCompound toInitialChunkDataNbt() {
		//TERRIBLE yarn name, this is "getUpdateTag", it's the nbt that will be sent to clients
		return createNbt();
	}
	
	@Override
	public BlockState getRenderAttachmentData() {
		return renderedState;
	}
	
	public void setRenderedState(BlockState newState) {
		if(!Objects.equals(renderedState, newState)) {
			renderedState = newState;
			markDirty();
			if(world instanceof ServerWorld sworld) sworld.getChunkManager().markForUpdate(pos); //dispatch to clients
		}
	}
	
	public boolean hasSpentGlowstoneDust() {
		return spentGlowstoneDust;
	}
	
	public void spentGlowstoneDust() {
		spentGlowstoneDust = true;
		markDirty();
	}
	
	public boolean hasSpentRedstoneTorch() {
		return spentRedstoneTorch;
	}
	
	public void spentRedstoneTorch() {
		spentRedstoneTorch = true;
		markDirty();
	}
	
	public boolean hasSpentPoppedChorus() {
		return spentPoppedChorus;
	}
	
	public void spentPoppedChorus() {
		spentPoppedChorus = true;
		markDirty();
	}
}
