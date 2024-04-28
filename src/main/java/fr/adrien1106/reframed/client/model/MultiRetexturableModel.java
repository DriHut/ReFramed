package fr.adrien1106.reframed.client.model;

import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;

import java.util.List;

public interface MultiRetexturableModel {

    List<RetexturingBakedModel> models();
}
