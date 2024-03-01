package fr.adrien1106.reframed.util;

import net.minecraft.block.BlockState;

import java.util.List;

public interface ThemeableBlockEntity {
	BlockState getTheme(int i);

	void setTheme(BlockState state, int i);

	List<BlockState> getThemes();
}
