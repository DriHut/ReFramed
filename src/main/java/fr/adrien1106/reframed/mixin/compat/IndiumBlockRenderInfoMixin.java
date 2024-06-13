package fr.adrien1106.reframed.mixin.compat;

import com.llamalad7.mixinextras.sugar.Local;
import fr.adrien1106.reframed.client.util.RenderHelper;
import fr.adrien1106.reframed.util.blocks.ThemeableBlockEntity;
import fr.adrien1106.reframed.util.mixin.IBlockRenderInfoMixin;
import link.infra.indium.renderer.render.BlockRenderInfo;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockRenderInfo.class)
public abstract class IndiumBlockRenderInfoMixin implements IBlockRenderInfoMixin {

    @Shadow public abstract void prepareForBlock(BlockState blockState, BlockPos blockPos, long seed, boolean modelAo);

    @Shadow public BlockPos blockPos;
    @Shadow public BlockRenderView blockView;
    @Shadow public BlockState blockState;
    @Shadow(remap = false) private int cullResultFlags;

    @Unique private int theme_index = 0;
    @Unique private int model_hash = 0;

    @Inject(
        method = "shouldDrawFace",
        at = @At(
            value = "INVOKE",
            target = "Llink/infra/indium/renderer/render/BlockRenderInfo;shouldDrawFaceInner(Lnet/minecraft/util/math/Direction;)Z"
        ),
        cancellable = true
    )
    private void shouldDrawInnerFace(Direction face, CallbackInfoReturnable<Boolean> cir, @Local int mask) {
        BlockPos other_pos = blockPos.offset(face);
        if (!(blockView.getBlockEntity(blockPos) instanceof ThemeableBlockEntity
            || blockView.getBlockEntity(other_pos) instanceof ThemeableBlockEntity)
        ) return;
        boolean result = RenderHelper.shouldDrawSide(blockState, blockView, blockPos, face, other_pos, theme_index);
        if (result) cullResultFlags |= mask;
        cir.setReturnValue(result);
    }

    @Override
    public void prepareForBlock(BlockState blockState, BlockPos blockPos, long seed, boolean modelAo, int theme_index, int model_hash) {
        this.theme_index = theme_index;
        this.model_hash = model_hash;
        prepareForBlock(blockState, blockPos, seed, modelAo);
    }

    @Override
    public int getThemeIndex() {
        return theme_index;
    }

    @Override
    public int getModelHash() {
        return model_hash;
    }
}
