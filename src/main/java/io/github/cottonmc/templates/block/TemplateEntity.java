package io.github.cottonmc.templates.block;

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
	
	protected boolean emitsRedstone;
	protected boolean isSolid = true;
	
	public TemplateEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}
	
	@Override
	public void readNbt(NbtCompound tag) {
		super.readNbt(tag);
		
		BlockState lastRenderedState = renderedState;
		
		renderedState = NbtHelper.toBlockState(Registries.BLOCK.getReadOnlyWrapper(), tag.getCompound("BlockState"));
		spentGlowstoneDust = tag.getBoolean("spentglow");
		spentRedstoneTorch = tag.getBoolean("spentredst");
		spentPoppedChorus = tag.getBoolean("spentchor");
		
		emitsRedstone = tag.getBoolean("emitsredst");
		isSolid = !tag.contains("solid") || tag.getBoolean("solid"); //default to "true" if it's nonexistent
		
		//Force a chunk remesh on the client if the displayed blockstate has changed
		if(world != null && world.isClient && !Objects.equals(lastRenderedState, renderedState)) {
			Templates.chunkRerenderProxy.accept(world, pos);
		}
	}
	
	@Override
	public void writeNbt(NbtCompound tag) {
		super.writeNbt(tag);
		tag.put("BlockState", NbtHelper.fromBlockState(renderedState));
		tag.putBoolean("spentglow", spentGlowstoneDust);
		tag.putBoolean("spentredst", spentRedstoneTorch);
		tag.putBoolean("spentchor", spentPoppedChorus);
		
		tag.putBoolean("emitsredst", emitsRedstone);
		tag.putBoolean("solid", isSolid);
	}
	
	@Nullable
	@Override
	public Packet<ClientPlayPacketListener> toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create(this);
	}
	
	@Override
	public NbtCompound toInitialChunkDataNbt() {
		//TERRIBLE yarn name, this is "getUpdateTag", it's the nbt that will be sent to clients
		//and it just calls "writeNbt"
		return createNbt();
	}
	
	@Override
	public BlockState getRenderAttachmentData() {
		return renderedState;
	}
	
	private void dispatch() {
		if(world instanceof ServerWorld sworld) sworld.getChunkManager().markForUpdate(pos);
	}
	
	public void setRenderedState(BlockState newState) {
		if(!Objects.equals(renderedState, newState)) {
			renderedState = newState;
			markDirty();
			dispatch();
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
	
	public boolean emitsRedstone() {
		return emitsRedstone;
	}
	
	public void setEmitsRedstone(boolean emitsRedstone) {
		if(this.emitsRedstone != emitsRedstone) {
			this.emitsRedstone = emitsRedstone;
			markDirty();
			
			if(world != null) world.updateNeighbors(pos, getCachedState().getBlock());
		}
	}
	
	public boolean isSolid() {
		return isSolid;
	}
	
	public void setSolidity(boolean isSolid) {
		if(this.isSolid != isSolid) {
			this.isSolid = isSolid;
			markDirty();
			
			//do i need to invalidate any shape caches or something
			if(world != null) world.setBlockState(pos, getCachedState());
			
			dispatch();
		}
	}
}
