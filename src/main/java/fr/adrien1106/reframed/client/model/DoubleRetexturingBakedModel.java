package fr.adrien1106.reframed.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.Sprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class DoubleRetexturingBakedModel extends ForwardingBakedModel implements MultiRetexturableModel {

    private final RetexturingBakedModel model_1, model_2;
    public DoubleRetexturingBakedModel(RetexturingBakedModel model_1, RetexturingBakedModel model_2) {
        this.wrapped = model_1.getWrappedModel();
        this.model_1 = model_1;
        this.model_2 = model_2;
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public Sprite getParticleSprite() {
        return model_1.getParticleSprite();
    }

    @Override
    public List<BakedQuad> getQuads(BlockState blockState, Direction face, Random rand) {
        List<BakedQuad> quads = new ArrayList<>(model_1.getQuads(blockState, face, rand));
        quads.addAll(model_2.getQuads(blockState, face, rand));
        return quads;
    }

    @Override
    public void emitBlockQuads(BlockRenderView world, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
        model_1.emitBlockQuads(world, state, pos, randomSupplier, context);
        model_2.emitBlockQuads(world, state, pos, randomSupplier, context);
    }

    @Override // models are emitted here because no checks are done on items
    public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
        model_1.emitItemQuads(stack, randomSupplier, context);
        model_2.emitItemQuads(stack, randomSupplier, context);
    }

    @Override
    public List<RetexturingBakedModel> models() {
        return List.of(model_1, model_2);
    }
}
