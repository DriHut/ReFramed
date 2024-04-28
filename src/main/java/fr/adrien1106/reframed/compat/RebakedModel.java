package fr.adrien1106.reframed.compat;

import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;

import java.util.List;
import java.util.Map;

public class RebakedModel implements BakedModel {
    protected final Map<Direction, List<BakedQuad>> face_quads;
    protected boolean ambient_occlusion;

    public RebakedModel(Map<Direction, List<BakedQuad>> face_quads, boolean ambient_occlusion) {
        this.face_quads = face_quads;
        this.ambient_occlusion = ambient_occlusion;
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction direction, Random random) {
        return face_quads.getOrDefault(direction, List.of());
    }

    @Override
    public boolean useAmbientOcclusion() {
        return ambient_occlusion;
    }

    @Override
    public boolean hasDepth() {
        return false;
    }

    @Override
    public boolean isSideLit() {
        return false;
    }

    @Override
    public boolean isBuiltin() {
        return false;
    }

    @Override
    public Sprite getParticleSprite() {
        return null;
    }

    @Override
    public ModelTransformation getTransformation() {
        return ModelTransformation.NONE;
    }

    @Override
    public ModelOverrideList getOverrides() {
        return ModelOverrideList.EMPTY;
    }
}
