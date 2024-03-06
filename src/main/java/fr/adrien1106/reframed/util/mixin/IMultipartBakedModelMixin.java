package fr.adrien1106.reframed.util.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;

public interface IMultipartBakedModelMixin {

    BakedModel getModel(BlockState state);
}
