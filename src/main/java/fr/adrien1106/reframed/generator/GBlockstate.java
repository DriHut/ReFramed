package fr.adrien1106.reframed.generator;

import fr.adrien1106.reframed.ReFramed;
import fr.adrien1106.reframed.block.*;
import fr.adrien1106.reframed.generator.block.*;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.*;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static net.minecraft.data.client.VariantSettings.Rotation.R0;

public class GBlockstate extends FabricModelProvider {
    private static final Map<Class<? extends Block>, BlockStateProvider> providers = new HashMap<>();
    static {
        providers.put(ReFramedHalfStairBlock.class, new HalfStair());
        providers.put(ReFramedHalfStairsSlabBlock.class, new HalfStairsSlab());
        providers.put(ReFramedHalfStairsStairBlock.class, new HalfStairsStair());
        providers.put(ReFramedHalfStairsCubeStairBlock.class, new HalfStairsCubeStair());
        providers.put(ReFramedHalfStairsStepStairBlock.class, new HalfStairsStepStair());
        providers.put(ReFramedLayerBlock.class, new Layer());
        providers.put(ReFramedHalfLayerBlock.class, new HalfLayer());
        providers.put(ReFramedPillarBlock.class, new Pillar());
        providers.put(ReFramedSlabBlock.class, new Slab());
        providers.put(ReFramedSlabsCubeBlock.class, new SlabsCube());
        providers.put(ReFramedSlabsStairBlock.class, new SlabsStair());
        providers.put(ReFramedSlabsOuterStairBlock.class, new SlabsOuterStair());
        providers.put(ReFramedSlabsInnerStairBlock.class, new SlabsInnerStair());
        providers.put(ReFramedSlabsHalfLayerBlock.class, new SlabsHalfLayer());
        providers.put(ReFramedSlabsLayerBlock.class, new SlabsLayer());
        providers.put(ReFramedHalfSlabBlock.class, new HalfSlab());
        providers.put(ReFramedHalfSlabsSlabBlock.class, new HalfSlabsSlab());
        providers.put(ReFramedSmallCubeBlock.class, new SmallCube());
        providers.put(ReFramedSmallCubesStepBlock.class, new SmallCubesStep());
        providers.put(ReFramedStairBlock.class, new Stair());
        providers.put(ReFramedStairsCubeBlock.class, new StairsCube());
        providers.put(ReFramedStepBlock.class, new Step());
        providers.put(ReFramedStepsSlabBlock.class, new StepsSlab());
        providers.put(ReFramedStepsCrossBlock.class, new StepsCross());
        providers.put(ReFramedStepsHalfLayerBlock.class, new StepsHalfLayer());
        providers.put(ReFramedPillarsWallBlock.class, new PillarsWall());
        providers.put(ReFramedWallBlock.class, new Wall());
        providers.put(ReFramedPaneBlock.class, new Pane());
        providers.put(ReFramedTrapdoorBlock.class, new Trapdoor());
        providers.put(ReFramedDoorBlock.class, new Door());
        providers.put(ReFramedButtonBlock.class, new Button());
        providers.put(ReFramedPostBlock.class, new Post());
        providers.put(ReFramedFenceBlock.class, new Fence());
        providers.put(ReFramedPostFenceBlock.class, new PostFence());
    }

    public GBlockstate(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator model_generator) {
        ReFramed.BLOCKS
            .forEach(model_generator::excludeFromSimpleItemModelGeneration);
        ReFramed.BLOCKS.stream()
            .map(block -> {
                if (providers.containsKey(block.getClass())) return providers.get(block.getClass()).getMultipart(block);
                return VariantsBlockStateSupplier.create(
                    block,
                    GBlockstate.variant(
                        ReFramed.id("cube_special"),
                        true,
                        R0, R0
                    )
                );
            })
            .filter(Objects::nonNull)
            .forEach(model_generator.blockStateCollector);
    }

    @Override
    public void generateItemModels(ItemModelGenerator model_generator) {
        ReFramed.ITEMS.forEach(item -> model_generator.register(item, Models.GENERATED));
    }

    public static BlockStateVariant variant(Identifier model, boolean uv_lock, VariantSettings.Rotation x, VariantSettings.Rotation y) {
        BlockStateVariant variant = BlockStateVariant.create().put(VariantSettings.MODEL, model);
        if (uv_lock) variant.put(VariantSettings.UVLOCK, uv_lock);
        if (!x.equals(R0)) variant.put(VariantSettings.X, x);
        if (!y.equals(R0)) variant.put(VariantSettings.Y, y);
        return variant;
    }

    public static <T extends Comparable<T>> When when(Property<T> property_1, T value_1) {
        return When.create().set(property_1, value_1);
    }

    public static <T extends Comparable<T>,
        U extends Comparable<U>>  When when(Property<T> property_1, T value_1,
                                            Property<U> property_2, U value_2) {
        return When.allOf(
            when(property_1, value_1),
            when(property_2, value_2)
        );
    }

    public static <T extends Comparable<T>,
        U extends Comparable<U>,
        V extends Comparable<V>> When when(Property<T> property_1, T value_1,
                                           Property<U> property_2, U value_2,
                                           Property<V> property_3, V value_3) {
        return When.allOf(
            when(property_1, value_1),
            when(property_2, value_2),
            when(property_3, value_3)
        );
    }

    public static <T extends Comparable<T>,
        U extends Comparable<U>,
        V extends Comparable<V>,
        W extends Comparable<W>> When when(Property<T> property_1, T value_1,
                                           Property<U> property_2, U value_2,
                                           Property<V> property_3, V value_3,
                                           Property<W> property_4, W value_4) {
        return When.allOf(
            when(property_1, value_1),
            when(property_2, value_2),
            when(property_3, value_3),
            when(property_4, value_4)
        );
    }

    public static <T extends Comparable<T>,
        U extends Comparable<U>,
        V extends Comparable<V>,
        W extends Comparable<W>,
        X extends Comparable<X>> When when(Property<T> property_1, T value_1,
                                           Property<U> property_2, U value_2,
                                           Property<V> property_3, V value_3,
                                           Property<W> property_4, W value_4,
                                           Property<X> property_5, X value_5) {
        return When.allOf(
            when(property_1, value_1),
            when(property_2, value_2),
            when(property_3, value_3),
            when(property_4, value_4),
            when(property_5, value_5)
        );
    }
}