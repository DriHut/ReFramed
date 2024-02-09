package fr.adrien1106.reframedtemplates.model;

import fr.adrien1106.reframedtemplates.api.TemplatesClientApi;
import fr.adrien1106.reframedtemplates.model.apperance.TemplateAppearanceManager;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.util.collection.Weighted;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class RetexturingWeightedBakedModel extends RetexturingBakedModel {

    final ConcurrentMap<BlockState, List<Weighted.Present<Mesh>>> jsonToMesh = new ConcurrentHashMap<>();

    public RetexturingWeightedBakedModel(BakedModel baseModel, TemplateAppearanceManager tam, ModelBakeSettings settings, BlockState itemModelState, boolean ao) {
        super(baseModel, tam, settings, itemModelState, ao);
    }

    @Override
    protected Mesh getBaseMesh(BlockState state) {
        return null;
//        random.setSeed(seed);
//        List<Weighted.Present<Mesh>> models = jsonToMesh.computeIfAbsent(state, this::convertModel);
//        return Weighting
//            .getAt(models, Math.abs((int)random.nextLong()) % Weighting.getWeightSum(models))
//            .map(Weighted.Present::getData).get();
    }

    private List<Weighted.Present<Mesh>> convertModel(BlockState state) {
        Renderer r = TemplatesClientApi.getInstance().getFabricRenderer();
        MeshBuilder builder = r.meshBuilder();
        QuadEmitter emitter = builder.getEmitter();
        RenderMaterial mat = tam.getCachedMaterial(state, false);

        Random rand = Random.create(42);

        for(Direction cullFace : DIRECTIONS_AND_NULL) {
            for(BakedQuad quad : wrapped.getQuads(state, cullFace, rand)) {
                emitter.fromVanilla(quad, mat, cullFace);

                QuadUvBounds bounds = QuadUvBounds.read(emitter);
//                for(int i = 0; i < specialSprites.length; i++) {
//                    if(bounds.displaysSprite(specialSprites[i])) {
//                        bounds.normalizeUv(emitter, specialSprites[i]);
//                        emitter.tag(i + 1);
//                        break;
//                    }
//                }

                emitter.emit();
            }
        }

//        builder.build();
        return null;
    }

}
