package fr.adrien1106.reframed.client.model;

import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.minecraft.client.render.model.BakedModel;

import java.util.List;

public interface MultiRetexturableModel {

    List<ForwardingBakedModel> models();
}
