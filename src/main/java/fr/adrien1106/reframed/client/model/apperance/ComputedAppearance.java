package fr.adrien1106.reframed.client.model.apperance;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Environment(EnvType.CLIENT)
public class ComputedAppearance extends CamoAppearance {
    private final Appearance appearance;

    public ComputedAppearance(@NotNull Appearance appearance, RenderMaterial ao_material, RenderMaterial material, int id) {
        super(ao_material, material, id);
        this.appearance = appearance;
    }

    @Override
    public @NotNull List<SpriteProperties> getSprites(Direction dir, int model_id) {
        return appearance.sprites().get(dir);
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
        if(!(o instanceof ComputedAppearance that)) return false;
        return id == that.id;
    }
}
