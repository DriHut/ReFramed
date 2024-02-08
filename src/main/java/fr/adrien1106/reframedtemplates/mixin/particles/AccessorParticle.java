package fr.adrien1106.reframedtemplates.mixin.particles;

import net.minecraft.client.particle.Particle;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Particle.class)
public interface AccessorParticle {
	@Accessor("random") Random templates$getRandom();
	
	@Accessor("red") void templates$setRed(float red);
	@Accessor("green") void templates$setGreen(float green);
	@Accessor("blue") void templates$setBlue(float blue);
}
