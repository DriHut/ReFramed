package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.util.ReFramedInteractionUtil;
import fr.adrien1106.reframed.util.ThemeableBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

//Keeping the weight of this block entity down, both in terms of memory consumption and NBT sync traffic,
//is pretty important since players might place a lot of them. There were tons and tons of these at Blanketcon.
//To that end, most of the state has been crammed into a bitfield.
public class ReFramedEntity extends BlockEntity implements ThemeableBlockEntity {
	protected BlockState renderedState = Blocks.AIR.getDefaultState();
	protected byte bitfield = DEFAULT_BITFIELD;
	
	protected static final int SPENT_GLOWSTONE_DUST_MASK = 0b00000001;
	protected static final int SPENT_REDSTONE_TORCH_MASK = 0b00000010;
	protected static final int SPENT_POPPED_CHORUS_MASK  = 0b00000100;
	protected static final int EMITS_REDSTONE_MASK       = 0b00001000;
	protected static final int IS_SOLID_MASK             = 0b00010000;
	protected static final byte DEFAULT_BITFIELD = IS_SOLID_MASK; //brand-new frames shall be solid
	
	//Using one-character names is a little brash, like, what if there's a mod that adds crap to the NBT of every
	//block entity, and uses short names for the same reason I am (because there are lots and lots of block entities)?
	//Kinda doubt it?
	protected static final String BLOCKSTATE_KEY = "s";
	protected static final String BITFIELD_KEY = "b";
	
	public ReFramedEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}
	
	@Override
	public void readNbt(NbtCompound tag) {
		super.readNbt(tag);
		
		BlockState lastRenderedState = renderedState;
		
		if(tag.contains("BlockState")) { //2.0.4 and earlier
			renderedState = NbtHelper.toBlockState(Registries.BLOCK.getReadOnlyWrapper(), tag.getCompound("BlockState"));
			
			if(tag.getBoolean("spentglow")) spentGlowstoneDust();
			if(tag.getBoolean("spentredst")) spentRedstoneTorch();
			if(tag.getBoolean("spentchor")) spentPoppedChorus();
			setEmitsRedstone(tag.getBoolean("emitsredst"));
			setSolidity(!tag.contains("solid") || tag.getBoolean("solid")); //default to "true" if it's nonexistent
		} else {
			renderedState = NbtHelper.toBlockState(Registries.BLOCK.getReadOnlyWrapper(), tag.getCompound(BLOCKSTATE_KEY));
			bitfield = tag.contains(BITFIELD_KEY) ? tag.getByte(BITFIELD_KEY) : DEFAULT_BITFIELD;
		}
		
		//Force a chunk remesh on the client if the displayed blockstate has changed
		if(world != null && world.isClient && !Objects.equals(lastRenderedState, renderedState)) {
			ReFramed.chunkRerenderProxy.accept(world, pos);
		}
	}
	
	@Override
	public void writeNbt(NbtCompound tag) {
		super.writeNbt(tag);
		
		if(renderedState != Blocks.AIR.getDefaultState()) tag.put(BLOCKSTATE_KEY, NbtHelper.fromBlockState(renderedState));
		if(bitfield != DEFAULT_BITFIELD) tag.putByte(BITFIELD_KEY, bitfield);
	}
	
	public static @NotNull BlockState readStateFromItem(ItemStack stack) {
		NbtCompound blockEntityTag = BlockItem.getBlockEntityNbt(stack);
		if(blockEntityTag == null) return Blocks.AIR.getDefaultState();
		
		//slightly paranoid NBT handling cause you never know what mysteries are afoot with items
		NbtElement subElement;
		if(blockEntityTag.contains(BLOCKSTATE_KEY)) subElement = blockEntityTag.get(BLOCKSTATE_KEY); //2.0.5
		else if(blockEntityTag.contains("BlockState")) subElement = blockEntityTag.get("BlockState"); //old 2.0.4 items
		else return Blocks.AIR.getDefaultState();
		
		if(!(subElement instanceof NbtCompound subCompound)) return Blocks.AIR.getDefaultState();
		
		else return NbtHelper.toBlockState(Registries.BLOCK.getReadOnlyWrapper(), subCompound);
	}
	
	//Awkward: usually the BlockState is the source of truth for things like the "emits light" blockstate, but if you
	//ctrl-pick a glowing block and place it, it should still be glowing. This is some hacky shit that guesses the value of
	//the LIGHT blockstate based off information in the NBT tag, and also prevents bugginess like "the blockstate is not
	//glowing but the copied NBT thinks glowstone dust was already added, so it refuses to accept more dust"
	public static @Nullable BlockState weirdNbtLightLevelStuff(@Nullable BlockState state, ItemStack stack) {
		if(state == null || stack == null) return state;
		
		NbtCompound blockEntityTag = BlockItem.getBlockEntityNbt(stack);
		if(blockEntityTag == null) return state;
		
		if(state.contains(ReFramedInteractionUtil.LIGHT)) {
			state = state.with(ReFramedInteractionUtil.LIGHT,
				blockEntityTag.getBoolean("spentglow") || //2.0.4
				((blockEntityTag.contains(BITFIELD_KEY) ? blockEntityTag.getByte(BITFIELD_KEY) : DEFAULT_BITFIELD) & SPENT_GLOWSTONE_DUST_MASK) != 0 || //2.0.5
				readStateFromItem(stack).getLuminance() != 0 //glowstone dust wasn't manually added, the block just emits light
			);
		}
		
		return state;
	}
	
	//RenderAttachmentBlockEntity impl. Note that ThemeableBlockEntity depends on this returning a BlockState object.
	@Override
	public BlockState getRenderAttachmentData() {
		return renderedState;
	}
	
	public void setRenderedState(BlockState newState) {
		if(!Objects.equals(renderedState, newState)) {
			renderedState = newState;
			markDirtyAndDispatch();
		}
	}
	
	public boolean hasSpentGlowstoneDust() {
		return (bitfield & SPENT_GLOWSTONE_DUST_MASK) != 0;
	}
	
	public void spentGlowstoneDust() {
		bitfield |= SPENT_GLOWSTONE_DUST_MASK;
		markDirtyAndDispatch();
	}
	
	public boolean hasSpentRedstoneTorch() {
		return (bitfield & SPENT_REDSTONE_TORCH_MASK) != 0;
	}
	
	public void spentRedstoneTorch() {
		bitfield |= SPENT_REDSTONE_TORCH_MASK;
		markDirtyAndDispatch();
	}
	
	public boolean hasSpentPoppedChorus() {
		return (bitfield & SPENT_POPPED_CHORUS_MASK) != 0;
	}
	
	public void spentPoppedChorus() {
		bitfield |= SPENT_POPPED_CHORUS_MASK;
		markDirtyAndDispatch();
	}
	
	public boolean emitsRedstone() {
		return (bitfield & EMITS_REDSTONE_MASK) != 0;
	}
	
	public void setEmitsRedstone(boolean nextEmitsRedstone) {
		boolean currentlyEmitsRedstone = emitsRedstone();
		
		if(currentlyEmitsRedstone != nextEmitsRedstone) {
			if(currentlyEmitsRedstone) bitfield &= ~EMITS_REDSTONE_MASK;
			else bitfield |= EMITS_REDSTONE_MASK;
			markDirtyAndDispatch();
			if(world != null) world.updateNeighbors(pos, getCachedState().getBlock());
		}
	}
	
	public boolean isSolid() {
		return (bitfield & IS_SOLID_MASK) != 0;
	}
	
	public void setSolidity(boolean nextSolid) {
		boolean currentlySolid = isSolid();
		
		if(currentlySolid != nextSolid) {
			if(currentlySolid) bitfield &= ~IS_SOLID_MASK;
			else bitfield |= IS_SOLID_MASK;
			markDirtyAndDispatch();
			if(world != null) world.setBlockState(pos, getCachedState()); //do i need to invalidate any shape caches or something
		}
	}
	
	//<standard blockentity boilerplate>
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
	
	protected void dispatch() {
		if(world instanceof ServerWorld sworld) sworld.getChunkManager().markForUpdate(pos);
	}
	
	protected void markDirtyAndDispatch() {
		markDirty();
		dispatch();
	}
	//</standard blockentity boilerplate>
}
