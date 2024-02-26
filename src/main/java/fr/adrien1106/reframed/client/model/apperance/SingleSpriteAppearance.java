package fr.adrien1106.reframed.client.model.apperance;

import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SingleSpriteAppearance implements CamoAppearance {
    private final @NotNull Sprite defaultSprite;
    private final RenderMaterial mat;
    private final int id;

    public SingleSpriteAppearance(@NotNull Sprite defaultSprite, RenderMaterial mat, int id) {
        this.defaultSprite = defaultSprite;
        this.mat = mat;
        this.id = id;
    }

    @Override
    public @NotNull RenderMaterial getRenderMaterial(boolean ao) {
        return mat;
    }

    @Override
    public @NotNull List<SpriteProperties> getSprites(Direction dir, long seed) {
        return List.of(new SpriteProperties(defaultSprite, 0, null));
    }

    @Override
    public boolean hasColor(Direction dir, long seed) {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        SingleSpriteAppearance that = (SingleSpriteAppearance) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "SingleSpriteAppearance[defaultSprite=%s, mat=%s, id=%d]".formatted(defaultSprite, mat, id);
    }
}
