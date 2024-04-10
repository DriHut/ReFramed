package fr.adrien1106.reframed.client.model;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public abstract class UnbakedRetexturedModel implements UnbakedModel {

    protected final Identifier parent;

    protected int theme_index = 1;
    protected BlockState item_state;
    protected final boolean ao = true;

    public UnbakedRetexturedModel(Identifier parent) {
        this.parent = parent;
    }

    public UnbakedRetexturedModel setThemeIndex(int theme_index) {
        this.theme_index = theme_index;
        return this;
    }

    @Override
    public Collection<Identifier> getModelDependencies() {
        return List.of(parent);
    }

    @Override
    public void setParents(Function<Identifier, UnbakedModel> function) {
        function.apply(parent).setParents(function);
    }
}
