package fr.adrien1106.reframed.client.model.apperance;

import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SingleSpriteAppearance extends CamoAppearance {
    private final @NotNull Sprite defaultSprite;

    public SingleSpriteAppearance(@NotNull Sprite defaultSprite, RenderMaterial mat, int id) {
        super(null, mat, id);
        this.defaultSprite = defaultSprite;
    }

    @Override
    public @NotNull List<SpriteProperties> getSprites(Direction dir, int model_id) {
        return List.of(new SpriteProperties(defaultSprite, 0, null, false));
    }

    @Override
    public boolean hasColor(Direction dir, int model_id, int index) {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof SingleSpriteAppearance that)) return false;
        return id == that.id;
    }

    @Override
    public String toString() {
        return "SingleSpriteAppearance[defaultSprite=%s, mat=%s, id=%d]".formatted(defaultSprite, material, id);
    }
}
