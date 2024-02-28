package fr.adrien1106.reframed.util;

import net.minecraft.block.BlockState;

public interface ThemeableBlockEntity {
	BlockState getFirstTheme();

	default BlockState getSecondTheme() {
		return getFirstTheme();
	}

	void setFirstTheme(BlockState state);

	default void setSecondTheme(BlockState state) {
		setFirstTheme(state);
	}
}
