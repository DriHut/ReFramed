package fr.adrien1106.reframed.client.model;

import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
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

    public UnbakedDoubleRetexturedModel(UnbakedModel model_1, UnbakedModel model_2) {
        this.model_1 = model_1;
        this.model_2 = model_2;
    }


    @Override
    public Collection<Identifier> getModelDependencies() {
        return List.of(model_1.getModelDependencies().iterator().next(), model_2.getModelDependencies().iterator().next());
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
            (ForwardingBakedModel) model_1.bake(baker, texture_getter, model_bake_settings, identifier),
            (ForwardingBakedModel) model_2.bake(baker, texture_getter, model_bake_settings, identifier)
        );
    }
}
