package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.ReFramed;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;

import java.util.Objects;

public class ReFramedDoubleEntity extends ReFramedEntity {

    protected BlockState second_state = Blocks.AIR.getDefaultState();

    public ReFramedDoubleEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public BlockState getSecondTheme() {
        return second_state;
    }

    public void setSecondTheme(BlockState newState) {
        if(!Objects.equals(second_state, newState)) {
            second_state = newState;
            markDirtyAndDispatch();
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        BlockState rendered_state = second_state;// keep previous state to check if rerender is needed
        first_state = NbtHelper.toBlockState(Registries.BLOCK.getReadOnlyWrapper(), nbt.getCompound(BLOCKSTATE_KEY + 2));

        // Force a chunk remesh on the client if the displayed blockstate has changed
        if(world != null && world.isClient && !Objects.equals(rendered_state, second_state)) {
            ReFramed.chunkRerenderProxy.accept(world, pos);
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        if(second_state != Blocks.AIR.getDefaultState()) nbt.put(BLOCKSTATE_KEY + 2, NbtHelper.fromBlockState(second_state));
    }
}
