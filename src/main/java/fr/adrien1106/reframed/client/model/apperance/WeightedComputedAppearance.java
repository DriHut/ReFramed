package fr.adrien1106.reframed.client.model.apperance;

import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.minecraft.util.collection.Weighted;
import net.minecraft.util.collection.Weighting;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WeightedComputedAppearance implements CamoAppearance {
    private final List<Weighted.Present<Appearance>> appearances;
    private final int total_weight;
    private final int id;
    private final RenderMaterial matWithAo;
    private final RenderMaterial matWithoutAo;

    public WeightedComputedAppearance(@NotNull List<Weighted.Present<Appearance>> appearances, RenderMaterial withAo, RenderMaterial withoutAo, int id) {
        this.appearances = appearances;
        this.total_weight = Weighting.getWeightSum(appearances);
        this.id = id;

        this.matWithAo = withAo;
        this.matWithoutAo = withoutAo;
    }

    @Override
    public @NotNull RenderMaterial getRenderMaterial(boolean ao) {
        return ao ? matWithAo : matWithoutAo;
    }


    public int getAppearanceIndex(long seed) {
        Random random = Random.create(seed);
        return Weighting.getAt(appearances, Math.abs((int)random.nextLong()) % total_weight)
            .map(appearances::indexOf).orElse(0);
    }

    private Appearance getAppearance(long seed) {
        Random random = Random.create(seed);
        return Weighting.getAt(appearances, Math.abs((int)random.nextLong()) % total_weight)
            .map(Weighted.Present::getData).get();
    }

    @Override
    public @NotNull List<SpriteProperties> getSprites(Direction dir, long seed) {
        return getAppearance(seed).sprites().get(dir);
    }


    @Override
    public boolean hasColor(Direction dir, long seed) {
        return (getAppearance(seed).color_mask() & (1 << dir.ordinal())) != 0;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        WeightedComputedAppearance that = (WeightedComputedAppearance) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
