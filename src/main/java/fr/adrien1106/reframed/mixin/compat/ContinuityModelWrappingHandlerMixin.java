package fr.adrien1106.reframed.mixin.compat;

import fr.adrien1106.reframed.block.ReFramedBlock;
import me.pepperbell.continuity.client.resource.ModelWrappingHandler;
import net.minecraft.block.Block;
import net.minecraft.registry.DefaultedRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Iterator;

@Mixin(ModelWrappingHandler.class)
public class ContinuityModelWrappingHandlerMixin {

    @Redirect(
        method = "createBlockStateModelIdMap",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/registry/DefaultedRegistry;iterator()Ljava/util/Iterator;"
        )
    )
    private static Iterator<Block> filterFrames(DefaultedRegistry<Block> registry) {
        return registry
            .stream()
            .filter(block -> !(block instanceof ReFramedBlock))
            .iterator();
    }
}
