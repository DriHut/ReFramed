package fr.adrien1106.reframed.mixin.compat;

import earth.terrarium.athena.api.client.fabric.AthenaBakedModel;
import earth.terrarium.athena.api.client.fabric.WrappedGetter;
import earth.terrarium.athena.api.client.models.AthenaBlockModel;
import fr.adrien1106.reframed.client.ReFramedClient;
import fr.adrien1106.reframed.client.model.DynamicBakedModel;
import fr.adrien1106.reframed.compat.RebakedAthenaModel;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.*;

@Mixin(AthenaBakedModel.class)
public abstract class AthenaBakedModelMixin implements DynamicBakedModel, BakedModel {

    @Shadow @Final private AthenaBlockModel model;

    @Shadow @Final private Int2ObjectMap<Sprite> textures;

    /**
     * Reuses the emitQuad method to compute the quads to be used by the frame
     *
     * @param level - the world
     * @param state - the current block camo
     * @param pos   - the block position
     * @return - the rebakedmodel containing the computed quads
     */
    @Override
    public BakedModel computeQuads(BlockRenderView level, BlockState state, BlockPos pos) {
        Map<Direction, List<BakedQuad>> face_quads = new HashMap<>();
        Renderer r = ReFramedClient.HELPER.getFabricRenderer();
        QuadEmitter emitter = r.meshBuilder().getEmitter();

        WrappedGetter getter = new WrappedGetter(level);
        Arrays.stream(Direction.values()).forEach(direction -> {
            face_quads.put(direction, new ArrayList<>());

            (level == null || pos == null
                ? model.getDefaultQuads(direction).get(direction)
                : model.getQuads(getter, state, pos, direction))
                .forEach(sprite -> face_quads.computeIfPresent(direction, (d, quads) -> {
                    Sprite texture = textures.get(sprite.sprite());
                    if (texture == null) return quads;
                    emitter.square(direction, sprite.left(), sprite.bottom(), sprite.right(), sprite.top(), sprite.depth());

                    int flag = MutableQuadView.BAKE_LOCK_UV;

                    switch (sprite.rotation()) {
                        case CLOCKWISE_90 -> flag |= MutableQuadView.BAKE_ROTATE_90;
                        case CLOCKWISE_180 -> flag |= MutableQuadView.BAKE_ROTATE_180;
                        case COUNTERCLOCKWISE_90 -> flag |= MutableQuadView.BAKE_ROTATE_270;
                    }

                    emitter.spriteBake(texture, flag);
                    emitter.color(-1, -1, -1, -1);
                    quads.add(emitter.toBakedQuad(texture));
                    return quads;
                }));
        });

        return new RebakedAthenaModel(face_quads);
    }
}
