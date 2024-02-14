package fr.adrien1106.reframed.client.model.apperance;

import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class ComputedAppearance implements CamoAppearance {
    private final Appearance appearance;
    private final int id;
    private final RenderMaterial matWithAo;
    private final RenderMaterial matWithoutAo;

    public ComputedAppearance(@NotNull Appearance appearance, RenderMaterial withAo, RenderMaterial withoutAo, int id) {
        this.appearance = appearance;
        this.id = id;

        this.matWithAo = withAo;
        this.matWithoutAo = withoutAo;
    }

    @Override
    public @NotNull RenderMaterial getRenderMaterial(boolean ao) {
        return ao ? matWithAo : matWithoutAo;
    }

    @Override
    public @NotNull Sprite getSprite(Direction dir, long seed) {
        return appearance.sprites()[dir.ordinal()];
    }

    @Override
    public int getBakeFlags(Direction dir, long seed) {
        return appearance.flags()[dir.ordinal()];
    }

    @Override
    public boolean hasColor(Direction dir, long seed) {
        return (appearance.color_mask() & (1 << dir.ordinal())) != 0;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        ComputedAppearance that = (ComputedAppearance) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "ComputedApperance{sprites=%s, bakeFlags=%s, hasColorMask=%s, matWithoutAo=%s, matWithAo=%s, id=%d}"
            .formatted(Arrays.toString(appearance.sprites()), Arrays.toString(appearance.flags()), appearance.color_mask(), matWithoutAo, matWithAo, id);
    }
}
