package fr.adrien1106.reframed.util.mixin;

import fr.adrien1106.reframed.util.blocks.ThemeableBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

import static fr.adrien1106.reframed.block.ReFramedEntity.BLOCKSTATE_KEY;

public class ThemedBlockEntity extends BlockEntity implements ThemeableBlockEntity {
    private final List<BlockState> themes;

    public ThemedBlockEntity(NbtCompound compound, BlockPos pos, BlockState state) {
        super(null, pos, state);
        themes = new ArrayList<>();
        for (int i = 1; compound.contains(BLOCKSTATE_KEY + i ); i++) {
            themes.add(NbtHelper.toBlockState(
                Registries.BLOCK.getReadOnlyWrapper(),
                compound.getCompound(BLOCKSTATE_KEY + i)
            ));
        }
    }

    @Override
    public BlockState getTheme(int i) {
        return themes.get(Math.max(0, i-1));
    }

    @Override
    public void setTheme(BlockState state, int i) {
        themes.set(Math.max(0, i-1), state);
    }

    @Override
    public List<BlockState> getThemes() {
        return themes;
    }
}
