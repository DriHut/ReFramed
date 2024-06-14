package fr.adrien1106.reframed.generator;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.block.*;
import fr.adrien1106.reframed.generator.block.*;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider.BlockTagProvider;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.registry.tag.BlockTags;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class GBlockTag extends BlockTagProvider {
    private static final Map<Class<? extends Block>, TagGetter> providers = new HashMap<>();
    static {
        providers.put(ReFramedPillarsWallBlock.class, new PillarsWall());
        providers.put(ReFramedWallBlock.class, new Wall());
        providers.put(ReFramedPaneBlock.class, new Pane());
        providers.put(ReFramedFenceBlock.class, new Fence());
        providers.put(ReFramedPostFenceBlock.class, new PostFence());
    }

    public GBlockTag(FabricDataOutput output, CompletableFuture<WrapperLookup> registries) {
        super(output, registries);
    }

    @Override
    protected void configure(WrapperLookup arg) {
        FabricTagBuilder builder = getOrCreateTagBuilder(BlockTags.AXE_MINEABLE);
        ReFramed.BLOCKS.forEach((block) -> {
            if (providers.containsKey(block.getClass()))
                providers.get(block.getClass()).getTags().forEach((tag) -> getOrCreateTagBuilder(tag).add(block));
            builder.add(block);
        });
    }
}
