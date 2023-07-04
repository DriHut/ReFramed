package io.github.cottonmc.templates.mixin.particles;

import io.github.cottonmc.templates.api.ThemeableBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Entity.class)
public abstract class MixinEntity {
	@Shadow @Deprecated public abstract BlockPos getLandingPos(); //TODO, somewhat expensive method
	
	@ModifyArg(
		method = "spawnSprintingParticles",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/particle/BlockStateParticleEffect;<init>(Lnet/minecraft/particle/ParticleType;Lnet/minecraft/block/BlockState;)V")
	)
	private BlockState templates$spawnSprintingParticles$modifyParticleState(BlockState origState) {
		World world = ((Entity) (Object) this).getWorld();
		
		if(world.getBlockEntity(getLandingPos()) instanceof ThemeableBlockEntity themeable) {
			BlockState theme = themeable.getThemeState();
			if(!theme.isAir()) return theme;
		}
		
		return origState;
	}
}
