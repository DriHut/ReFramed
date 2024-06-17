package fr.adrien1106.reframed.generator;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.block.*;
import fr.adrien1106.reframed.generator.block.*;
import fr.adrien1106.reframed.generator.item.Blueprint;
import fr.adrien1106.reframed.generator.item.Hammer;
import fr.adrien1106.reframed.generator.item.Screwdriver;
import fr.adrien1106.reframed.item.ReFramedBlueprintItem;
import fr.adrien1106.reframed.item.ReFramedHammerItem;
import fr.adrien1106.reframed.item.ReFramedScrewdriverItem;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.item.ItemConvertible;

import java.util.HashMap;
import java.util.Map;

public class GRecipe extends FabricRecipeProvider {
    private static final Map<Class<? extends ItemConvertible>, RecipeSetter> providers = new HashMap<>();
    static {
        providers.put(ReFramedBlock.class, new Cube());
        providers.put(ReFramedHalfStairBlock.class, new HalfStair());
        providers.put(ReFramedHalfStairsSlabBlock.class, new HalfStairsSlab());
        providers.put(ReFramedHalfStairsStairBlock.class, new HalfStairsStair());
        providers.put(ReFramedLayerBlock.class, new Layer());
        providers.put(ReFramedPillarBlock.class, new Pillar());
        providers.put(ReFramedSlabBlock.class, new Slab());
        providers.put(ReFramedSlabsCubeBlock.class, new SlabsCube());
        providers.put(ReFramedSlabsStairBlock.class, new SlabsStair());
        providers.put(ReFramedSlabsOuterStairBlock.class, new SlabsOuterStair());
        providers.put(ReFramedSlabsInnerStairBlock.class, new SlabsInnerStair());
        providers.put(ReFramedSmallCubeBlock.class, new SmallCube());
        providers.put(ReFramedSmallCubesStepBlock.class, new SmallCubesStep());
        providers.put(ReFramedStairBlock.class, new Stair());
        providers.put(ReFramedStairsCubeBlock.class, new StairsCube());
        providers.put(ReFramedStepBlock.class, new Step());
        providers.put(ReFramedStepsSlabBlock.class, new StepsSlab());
        providers.put(ReFramedPillarsWallBlock.class, new PillarsWall());
        providers.put(ReFramedWallBlock.class, new Wall());
        providers.put(ReFramedPaneBlock.class, new Pane());
        providers.put(ReFramedTrapdoorBlock.class, new Trapdoor());
        providers.put(ReFramedDoorBlock.class, new Door());
        providers.put(ReFramedButtonBlock.class, new Button());
        providers.put(ReFramedPostBlock.class, new Post());
        providers.put(ReFramedFenceBlock.class, new Fence());
        providers.put(ReFramedPostFenceBlock.class, new PostFence());
        providers.put(ReFramedBlueprintItem.class, new Blueprint());
        providers.put(ReFramedHammerItem.class, new Hammer());
        providers.put(ReFramedScrewdriverItem.class, new Screwdriver());
    }

    public GRecipe(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        ReFramed.BLOCKS.forEach(block -> {
            if (providers.containsKey(block.getClass())) providers.get(block.getClass()).setRecipe(exporter, block);
        });
        ReFramed.ITEMS.forEach(item -> {
            if (providers.containsKey(item.getClass())) providers.get(item.getClass()).setRecipe(exporter, item);
        });
    }
}
