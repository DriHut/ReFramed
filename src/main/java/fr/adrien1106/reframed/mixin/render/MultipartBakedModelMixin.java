package fr.adrien1106.reframed.mixin.render;

import fr.adrien1106.reframed.util.mixin.IMultipartBakedModelMixin;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.MultipartBakedModel;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

@Mixin(MultipartBakedModel.class)
public class MultipartBakedModelMixin implements IMultipartBakedModelMixin {

    @Shadow @Final private List<Pair<Predicate<BlockState>, BakedModel>> components;

    @Override
    public BakedModel getModel(BlockState state) {
        return components.stream().map(pair -> pair.getLeft().test(state) ? pair.getRight(): null).filter(Objects::nonNull).findAny().orElse(null);
    }
}
