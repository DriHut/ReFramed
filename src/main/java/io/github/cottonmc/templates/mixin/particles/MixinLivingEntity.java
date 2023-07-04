package io.github.cottonmc.templates.mixin.particles;

import io.github.cottonmc.templates.api.ThemeableBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {
	@Unique private BlockPos lastFallCheckPos;
	
	@Inject(method = "fall", at = @At("HEAD"))
	private void templates$onFall(double d, boolean bl, BlockState blockState, BlockPos blockPos, CallbackInfo ci) {
		lastFallCheckPos = blockPos;
	}
	
	@ModifyArg(
		method = "fall",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/particle/BlockStateParticleEffect;<init>(Lnet/minecraft/particle/ParticleType;Lnet/minecraft/block/BlockState;)V")
	)
	private BlockState templates$fall$modifyParticleState(BlockState origState) {
		World world = ((Entity) (Object) this).getWorld();
		
		if(lastFallCheckPos != null && world.getBlockEntity(lastFallCheckPos) instanceof ThemeableBlockEntity themeable) {
			BlockState theme = themeable.getThemeState();
			if(!theme.isAir()) return theme;
		}
		
		return origState;
	}
}
