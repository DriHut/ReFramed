package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.ReFramed;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//Keeping the weight of this block entity down, both in terms of memory consumption and NBT sync traffic,
//is pretty important since players might place a lot of them. There were tons and tons of these at Blanketcon.
//To that end, most of the state has been crammed into a bitfield.
public class ReFramedEntity extends BlockEntity implements ThemeableBlockEntity {
	protected BlockState first_state = Blocks.AIR.getDefaultState();
	protected byte bit_field = SOLIDITY_MASK;
	
	protected static final byte LIGHT_MASK    = 0b001;
	protected static final byte REDSTONE_MASK = 0b010;
	protected static final byte SOLIDITY_MASK = 0b100;

	protected static final String BLOCKSTATE_KEY = "s";
	protected static final String BITFIELD_KEY = "b";
	
	public ReFramedEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}
	
	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		
		BlockState rendered_state = first_state; // keep previous state to check if rerender is needed
		first_state = NbtHelper.toBlockState(Registries.BLOCK.getReadOnlyWrapper(), nbt.getCompound(BLOCKSTATE_KEY + 1));
		if (nbt.contains(BITFIELD_KEY)) bit_field = nbt.getByte(BITFIELD_KEY);
		
		// Force a chunk remesh on the client if the displayed blockstate has changed
		if(world != null && world.isClient && !Objects.equals(rendered_state, first_state)) {
			ReFramed.chunkRerenderProxy.accept(world, pos);
		}
	}
	
	@Override
	public void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		
		if(first_state != Blocks.AIR.getDefaultState()) nbt.put(BLOCKSTATE_KEY + 1, NbtHelper.fromBlockState(first_state));
		if(bit_field != SOLIDITY_MASK) nbt.putByte(BITFIELD_KEY, bit_field);
	}

	public static @NotNull BlockState readStateFromItem(ItemStack stack, int state) {
		NbtCompound nbt = BlockItem.getBlockEntityNbt(stack);
		if(nbt == null) return Blocks.AIR.getDefaultState();
		
		//slightly paranoid NBT handling cause you never know what mysteries are afoot with items
		NbtElement element;
		if(nbt.contains(BLOCKSTATE_KEY + state)) element = nbt.get(BLOCKSTATE_KEY + state);
		else return Blocks.AIR.getDefaultState();
		
		if(!(element instanceof NbtCompound compound)) return Blocks.AIR.getDefaultState();
		else return NbtHelper.toBlockState(Registries.BLOCK.getReadOnlyWrapper(), compound);
	}
	
	//Awkward: usually the BlockState is the source of truth for things like the "emits light" blockstate, but if you
	//ctrl-pick a glowing block and place it, it should still be glowing. This is some hacky shit that guesses the value of
	//the LIGHT blockstate based off information in the NBT tag, and also prevents bugginess like "the blockstate is not
	//glowing but the copied NBT thinks glowstone dust was already added, so it refuses to accept more dust"
	public static @Nullable BlockState getNbtLightLevel(@Nullable BlockState state, ItemStack stack) {
		if(state == null || stack == null) return state;
		
		NbtCompound nbt = BlockItem.getBlockEntityNbt(stack);
		if(nbt == null) return state;
		
		if(state.contains(ReFramedBlock.LIGHT)) {
			state = state.with(ReFramedBlock.LIGHT,
				((nbt.contains(BITFIELD_KEY)
					? nbt.getByte(BITFIELD_KEY)
					: SOLIDITY_MASK)
					& LIGHT_MASK) != 0
			);
		}
		
		return state;
	}

	@Override
	public BlockState getTheme(int i) {
		return first_state;
	}

	@Override
	public List<BlockState> getThemes() {
		List<BlockState> themes = new ArrayList<>();
		themes.add(first_state);
		return themes;
	}

	public void setTheme(BlockState new_state, int i) {
		if(!Objects.equals(first_state, new_state)) {
			first_state = new_state;
			markDirtyAndDispatch();
		}
	}

	/* --------------------------------------------------- ADDONS --------------------------------------------------- */
	public boolean emitsLight() {
		return (bit_field & LIGHT_MASK) != 0;
	}
	
	public void toggleLight() {
		if (emitsLight()) bit_field &= ~LIGHT_MASK;
		else bit_field |= LIGHT_MASK;
		markDirtyAndDispatch();
	}
	
	public void toggleRedstone() {
		if (emitsRedstone()) bit_field &= ~REDSTONE_MASK;
		else bit_field |= REDSTONE_MASK;

		if(world != null) world.updateNeighbors(pos, getCachedState().getBlock());
		markDirtyAndDispatch();
	}

	public boolean emitsRedstone() {
		return (bit_field & REDSTONE_MASK) != 0;
	}
	
	public void toggleSolidity() {
		if (isSolid()) bit_field &= ~SOLIDITY_MASK;
		else bit_field |= SOLIDITY_MASK;

		if(world != null) world.setBlockState(pos, getCachedState());
		markDirtyAndDispatch();
	}
	
	public boolean isSolid() {
		return (bit_field & SOLIDITY_MASK) != 0;
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
	
	protected void dispatch() {
		if(world instanceof ServerWorld sworld) sworld.getChunkManager().markForUpdate(pos);
	}
	
	protected void markDirtyAndDispatch() {
		markDirty();
		dispatch();
	}
}
