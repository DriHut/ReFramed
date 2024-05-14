package fr.adrien1106.reframed.mixin.compat;

import fr.adrien1106.reframed.client.ReFramedClient;
import fr.adrien1106.reframed.client.model.DynamicBakedModel;
import fr.adrien1106.reframed.compat.ICTMQuadTransform;
import fr.adrien1106.reframed.compat.RebakedModel;
import fr.adrien1106.reframed.util.blocks.ThemeableBlockEntity;
import me.pepperbell.continuity.client.config.ContinuityConfig;
import me.pepperbell.continuity.client.model.CTMBakedModel;
import me.pepperbell.continuity.client.model.ModelObjectsContainer;
import me.pepperbell.continuity.client.model.QuadProcessors;
import me.pepperbell.continuity.client.util.RenderUtil;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

@Mixin(CTMBakedModel.class)
public abstract class ContinuityCTMBakedModelMixin extends ForwardingBakedModel implements DynamicBakedModel {

    @Shadow protected abstract Function<Sprite, QuadProcessors.Slice> getSliceFunc(BlockState state);

    @Override
    public BakedModel computeQuads(@Nullable BlockRenderView level, BlockState origin_state, @Nullable BlockPos pos, int theme_index) {
        if (wrapped instanceof DynamicBakedModel wrapped_dynamic) // support wrap of dynamic models
            return wrapped_dynamic.computeQuads(level, origin_state, pos, theme_index);


        ModelObjectsContainer container = ModelObjectsContainer.get();
        // normally baked model / wrapped or feature disabled or item (i.e. no need to compute quads)
        if (level == null || pos == null
            || !ContinuityConfig.INSTANCE.connectedTextures.get()
            || !container.featureStates.getConnectedTexturesState().isEnabled()
        ) return wrapped;

        Map<Direction, List<BakedQuad>> face_quads = new HashMap<>();

        Renderer r = ReFramedClient.HELPER.getFabricRenderer();
        QuadEmitter emitter = r.meshBuilder().getEmitter();

        // get applicable state
        BlockState state = level.getBlockEntity(pos) instanceof ThemeableBlockEntity framed_entity
            ? framed_entity.getTheme(theme_index)
            : origin_state;

        // get random supplier
        Random random = Random.create();
        Supplier<Random> random_supplier = () -> {
            random.setSeed(state.getRenderingSeed(pos));
            return random;
        };

        // get quad transform and prepare
        ICTMQuadTransform transform = ((ICTMQuadTransform) container.ctmQuadTransform);
        transform.invokePrepare(
            level,
            state,
            pos,
            random_supplier,
            ContinuityConfig.INSTANCE.useManualCulling.get(),
            getSliceFunc(state)
        );
        Arrays.stream(Direction.values()).forEach(direction -> {
            face_quads.put(direction, new ArrayList<>());

            wrapped.getQuads(state, direction, random_supplier.get()).forEach(quad -> face_quads.computeIfPresent(direction, (d, quads) -> {
                emitter.fromVanilla(quad, emitter.material(), direction);
                transform.transform(emitter);
                quads.add(emitter.toBakedQuad(RenderUtil.getSpriteFinder().find(emitter)));
                return quads;
            }));
//            transform.getProcessingContext().getExtraQuadEmitter(); // TODO start here for overlay support
        });

        transform.getProcessingContext().reset(); // reset instead of outputting to emitter
        transform.invokeReset();

        return new RebakedModel(face_quads, useAmbientOcclusion());
    }
}
