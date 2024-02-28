package fr.adrien1106.reframed.client.model;

import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.texture.Sprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;

import java.util.function.Supplier;

public class DoubleRetexturingBakedModel extends ForwardingBakedModel {

    private final ForwardingBakedModel model_1, model_2;
    public DoubleRetexturingBakedModel(ForwardingBakedModel model_1, ForwardingBakedModel model_2) {
        this.model_1 = model_1;
        this.model_2 = model_2;
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public Sprite getParticleSprite() {
        return model_1.getParticleSprite(); // TODO determine which face is on top
    }

    @Override
    public void emitBlockQuads(BlockRenderView world, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
        model_1.emitBlockQuads(world, state, pos, randomSupplier, context);
        model_2.emitBlockQuads(world, state, pos, randomSupplier, context);
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
        model_1.emitItemQuads(stack, randomSupplier, context);
        model_2.emitItemQuads(stack, randomSupplier, context);
    }
}
