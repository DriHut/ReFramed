package io.github.cottonmc.templates.mixin.particles;

import io.github.cottonmc.templates.api.ThemeableBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.BlockDustParticle;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockDustParticle.class)
public class MixinBlockDustParticle {
	@Inject(
		method = "<init>(Lnet/minecraft/client/world/ClientWorld;DDDDDDLnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;)V",
		at = @At("TAIL")
	)
	void templates$init$modifyParticleSprite(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, BlockState state, BlockPos pos, CallbackInfo ci) {
		AccessorParticle a = (AccessorParticle) this;
		if(a.templates$getRandom().nextBoolean() && clientWorld.getBlockEntity(pos) instanceof ThemeableBlockEntity themeable) {
			BlockState theme = themeable.getThemeState();
			if(theme == null || theme.isAir()) return;
			
			Sprite replacement = MinecraftClient.getInstance().getBlockRenderManager().getModels().getModelParticleSprite(theme);
			((AccessorSpriteBillboardParticle) this).templates$setSprite(replacement);
			
			//basically just re-implement what the constructor does - since we mixin at tail, this already ran
			//some modifyvariable magic on the BlockState wouldn't hurt, i suppose, but, eh
			//it'd need to capture method arguments but 99% of block dust particles are not for template blocks
			int color = MinecraftClient.getInstance().getBlockColors().getColor(theme, clientWorld, pos, 0);
			a.templates$setRed(0.6f * ((color & 0xFF0000) >> 16) / 255f);
			a.templates$setGreen(0.6f * ((color & 0x00FF00) >> 8) / 255f);
			a.templates$setBlue(0.6f * (color & 0x0000FF) / 255f);
		}
	}
}
