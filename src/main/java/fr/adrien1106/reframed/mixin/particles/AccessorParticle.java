package fr.adrien1106.reframed.mixin.particles;

import net.minecraft.client.particle.Particle;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Particle.class)
public interface AccessorParticle {
	@Accessor("random") Random getRandom();
	
	@Accessor("red") void setRed(float red);
	@Accessor("green") void setGreen(float green);
	@Accessor("blue") void setBlue(float blue);
}
