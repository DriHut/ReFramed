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
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public abstract class TemplateEntity extends BlockEntity implements RenderAttachmentBlockEntity {
	protected BlockState renderedState = Blocks.AIR.getDefaultState();
	protected boolean glowstone = false;
	protected boolean redstone = false;
	private final Block baseBlock;
	
	public TemplateEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, Block baseBlock) {
		super(type, pos, state);
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
	
	@Override
	public void markDirty() {
		super.markDirty();
		if(world != null && !world.isClient) {
			for(ServerPlayerEntity player : PlayerLookup.tracking(this)) {
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
