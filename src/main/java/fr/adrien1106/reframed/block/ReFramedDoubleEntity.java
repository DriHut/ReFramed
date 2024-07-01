package fr.adrien1106.reframed.block;

import fr.adrien1106.reframed.ReFramed;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Objects;

public class ReFramedDoubleEntity extends ReFramedEntity {

    protected BlockState second_state = Blocks.AIR.getDefaultState();

    public ReFramedDoubleEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public BlockState getTheme(int i) {
        return i == 2 ? second_state : super.getTheme(i);
    }

    @Override
    public List<BlockState> getThemes() {
        List<BlockState> themes = super.getThemes();
        themes.add(second_state);
        return themes;
    }

    public void setTheme(BlockState new_state, int i) {
        if(i == 2) {
            if (Objects.equals(second_state, new_state)) return;
            second_state = new_state;
            markDirtyAndDispatch();
        } else super.setTheme(new_state, i);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        BlockState rendered_state = second_state;// keep previous state_key to check if rerender is needed
        second_state = NbtHelper.toBlockState(Registries.BLOCK.getReadOnlyWrapper(), nbt.getCompound(BLOCKSTATE_KEY + 2));

        // Force a chunk remesh on the client if the displayed blockstate has changed
        if(world != null && world.isClient && !Objects.equals(rendered_state, second_state)) {
            ReFramed.chunkRerenderProxy.accept(world, pos);
        }
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        nbt.put(BLOCKSTATE_KEY + 2, NbtHelper.fromBlockState(second_state));
    }
}
