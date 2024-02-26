package fr.adrien1106.reframed.client.model.apperance;

import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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
    public @NotNull List<SpriteProperties> getSprites(Direction dir, long seed) {
        return appearance.sprites().get(dir);
    }

    @Override
    public boolean hasColor(Direction dir, long seed, int index) {
        List<SpriteProperties> properties = getSprites(dir, seed);
        if (index != 0) index = properties.size() - index;
        return properties.get(index).has_colors();
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
}
