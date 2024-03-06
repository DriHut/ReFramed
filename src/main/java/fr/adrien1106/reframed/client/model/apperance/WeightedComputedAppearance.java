package fr.adrien1106.reframed.client.model.apperance;

import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.minecraft.util.collection.Weighted;
import net.minecraft.util.collection.Weighting;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WeightedComputedAppearance extends CamoAppearance {
    private final List<Weighted.Present<Appearance>> appearances;
    private final int total_weight;

    public WeightedComputedAppearance(@NotNull List<Weighted.Present<Appearance>> appearances, RenderMaterial ao_material, RenderMaterial material, int id) {
        super(ao_material, material, id);
        this.appearances = appearances;
        this.total_weight = Weighting.getWeightSum(appearances);
    }


    public int getAppearanceIndex(long seed) {
        Random random = Random.create(seed);
        return Weighting.getAt(appearances, Math.abs((int)random.nextLong()) % total_weight)
            .map(appearances::indexOf).orElse(0);
    }

    private Appearance getAppearance(int model_id) {
        return appearances.get(model_id).getData();
    }

    @Override
    public @NotNull List<SpriteProperties> getSprites(Direction dir, int model_id) {
        return getAppearance(model_id).sprites().get(dir);
    }


    @Override
    public boolean hasColor(Direction dir, int model_id, int index) {
        List<SpriteProperties> properties = getSprites(dir, model_id);
        if (index != 0) index = properties.size() - index;
        return properties.get(index).has_colors();
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof WeightedComputedAppearance that)) return false;
        return id == that.id;
    }
}
