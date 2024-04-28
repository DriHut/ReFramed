package fr.adrien1106.reframed.client.model;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.Baker;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class UnbakedDoubleRetexturedModel implements UnbakedModel {

    protected final UnbakedModel model_1;
    protected final UnbakedModel model_2;

    public UnbakedDoubleRetexturedModel(UnbakedRetexturedModel model_1, UnbakedRetexturedModel model_2) {
        this.model_1 = model_1;
        this.model_2 = model_2;
        model_2.setThemeIndex(2);
    }


    @Override
    public Collection<Identifier> getModelDependencies() {
        return List.of(((List<Identifier>) model_1.getModelDependencies()).get(0), ((List<Identifier>) model_2.getModelDependencies()).get(0));
    }

    @Override
    public void setParents(Function<Identifier, UnbakedModel> function) {
        model_1.setParents(function);
        model_2.setParents(function);
    }

    @Nullable
    @Override
    public BakedModel bake(Baker baker, Function<SpriteIdentifier, Sprite> texture_getter, ModelBakeSettings model_bake_settings, Identifier identifier) {
        return new DoubleRetexturingBakedModel(
            (RetexturingBakedModel) model_1.bake(baker, texture_getter, model_bake_settings, identifier),
            (RetexturingBakedModel) model_2.bake(baker, texture_getter, model_bake_settings, identifier)
        );
    }
}
