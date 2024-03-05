package fr.adrien1106.reframed.generator;

import fr.adrien1106.reframed.ReFramed;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider.BlockTagProvider;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.registry.tag.BlockTags;

import java.util.concurrent.CompletableFuture;

public class GBlockTag extends BlockTagProvider {

    public GBlockTag(FabricDataOutput output, CompletableFuture<WrapperLookup> registries) {
        super(output, registries);
    }

    @Override
    protected void configure(WrapperLookup arg) {
        FabricTagBuilder builder = getOrCreateTagBuilder(BlockTags.AXE_MINEABLE);
        ReFramed.BLOCKS.forEach(builder::add);
    }
}
