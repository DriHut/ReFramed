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
		if(clientWorld.getBlockEntity(pos) instanceof ThemeableBlockEntity themeable && ((AccessorParticle) this).templates$getRandom().nextBoolean()) {
			BlockState theme = themeable.getThemeState();
			if(theme.isAir()) return;
			
			Sprite replacement = MinecraftClient.getInstance().getBlockRenderManager().getModels().getModelParticleSprite(theme);
			((AccessorSpriteBillboardParticle) this).templates$setSprite(replacement);
		}
	}
}
