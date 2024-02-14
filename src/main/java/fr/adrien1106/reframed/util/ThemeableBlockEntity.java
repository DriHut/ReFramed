package fr.adrien1106.reframed.util;

import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachmentBlockEntity;
import net.minecraft.block.BlockState;

public interface ThemeableBlockEntity extends RenderAttachmentBlockEntity {
	default BlockState getThemeState() {
		return (BlockState) getRenderAttachmentData();
	}
}
