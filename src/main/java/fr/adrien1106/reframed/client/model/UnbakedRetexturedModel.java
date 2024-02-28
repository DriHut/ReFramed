package fr.adrien1106.reframed.client.model;

import fr.adrien1106.reframed.util.ThemeableBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public abstract class UnbakedRetexturedModel implements UnbakedModel {

    protected final Identifier parent;
    protected final Function<ThemeableBlockEntity, BlockState> state_getter;

    protected BlockState item_state;
    protected boolean ao = true;

    public UnbakedRetexturedModel(Identifier parent, Function<ThemeableBlockEntity, BlockState> state_getter) {
        this.parent = parent;
        this.state_getter = state_getter;
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
