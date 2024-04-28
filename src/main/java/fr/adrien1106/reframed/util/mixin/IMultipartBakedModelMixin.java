package fr.adrien1106.reframed.util.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;

import java.util.List;

public interface IMultipartBakedModelMixin {

    List<BakedModel> getModels(BlockState state);
}
