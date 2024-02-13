package fr.adrien1106.reframedtemplates.generator;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.*;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;

import java.util.Objects;

public class GBlockstate extends FabricModelProvider {

    public GBlockstate(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator model_generator) { // TODO Find out smth for items
        Generator.BLOCKS
            .forEach(model_generator::excludeFromSimpleItemModelGeneration);
        Generator.BLOCKS.stream()
            .map(block -> block instanceof MultipartBlockStateProvider multipart_block ? multipart_block.getMultipart(): null)
            .filter(Objects::nonNull)
            .forEach(model_generator.blockStateCollector);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {}

    public static BlockStateVariant variant(Identifier model, boolean uv_lock, VariantSettings.Rotation x, VariantSettings.Rotation y) {
        BlockStateVariant variant = BlockStateVariant.create().put(VariantSettings.MODEL, model);
        if (uv_lock) variant.put(VariantSettings.UVLOCK, uv_lock);
        if (!x.equals(VariantSettings.Rotation.R0)) variant.put(VariantSettings.X, x);
        if (!y.equals(VariantSettings.Rotation.R0)) variant.put(VariantSettings.Y, y);
        return variant;
    }

    public static <T extends Comparable<T>> When when(Property<T> property_1, T value_1) {
        return When.create().set(property_1, value_1);
    }

    public static <T extends Comparable<T>, U extends Comparable<U>>  When when(Property<T> property_1, T value_1, Property<U> property_2, U value_2) {
        return When.allOf(when(property_1, value_1), when(property_2, value_2));
    }

    public static <T extends Comparable<T>, U extends Comparable<U>, V extends Comparable<V>> When when(Property<T> property_1, T value_1, Property<U> property_2, U value_2, Property<V> property_3, V value_3) {
        return When.allOf(when(property_1, value_1), when(property_2, value_2), when(property_3, value_3));
    }
}