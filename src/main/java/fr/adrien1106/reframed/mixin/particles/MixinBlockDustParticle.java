package fr.adrien1106.reframed.mixin.particles;

import fr.adrien1106.reframed.block.ReFramedBlock;
import fr.adrien1106.reframed.util.blocks.ThemeableBlockEntity;
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
	void modifyParticleSprite(ClientWorld clientWorld, double d, double e, double f, double g, double h, double i, BlockState state, BlockPos pos, CallbackInfo ci) {
		AccessorParticle a = (AccessorParticle) this;
		if(a.getRandom().nextBoolean()
			&& clientWorld.getBlockEntity(pos) instanceof ThemeableBlockEntity themeable
			&& state.getBlock() instanceof ReFramedBlock block
		) {
			BlockState theme = themeable.getTheme(block.getTopThemeIndex(state));
			if(theme == null || theme.isAir()) return;
			
			Sprite replacement = MinecraftClient.getInstance().getBlockRenderManager().getModels().getModelParticleSprite(theme);
			((AccessorSpriteBillboardParticle) this).setNewSprite(replacement);
			
			//basically just re-implement what the constructor does - since we mixin at tail, this already ran
			//some modifyvariable magic on the BlockState wouldn't hurt, i suppose, but, eh
			//it'd need to capture method arguments but in this version of mixin it requires using threadlocals,
			//and 99.9999% of block dust particles are not for frame blocks, so it seems like a waste of cpu cycles
			int color = MinecraftClient.getInstance().getBlockColors().getColor(theme, clientWorld, pos, 0);
			a.setRed(0.6f * ((color & 0xFF0000) >> 16) / 255f);
			a.setGreen(0.6f * ((color & 0x00FF00) >> 8) / 255f);
			a.setBlue(0.6f * (color & 0x0000FF) / 255f);
		}
	}
}
