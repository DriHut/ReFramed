package fr.adrien1106.reframed.mixin.particles;

import fr.adrien1106.reframed.util.ThemeableBlockEntity;
import net.minecraft.block.BlockState;
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
	private BlockState modifyParticleState(BlockState origState) {
		World world = ((Entity) (Object) this).getWorld();
		
		if(world.getBlockEntity(getLandingPos()) instanceof ThemeableBlockEntity themeable) {
			BlockState theme = themeable.getFirstTheme();
			if(!theme.isAir()) return theme;
		}
		
		return origState;
	}
}
