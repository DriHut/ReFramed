package fr.adrien1106.reframed.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.item.ItemColors;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MinecraftClient.class)
public interface MinecraftAccessor {
	//Yeah there's a fabric API for this, but do we really need it, no.
	@Accessor("itemColors") ItemColors getItemColors();
}
