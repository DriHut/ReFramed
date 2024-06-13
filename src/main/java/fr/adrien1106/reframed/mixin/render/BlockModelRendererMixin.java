package fr.adrien1106.reframed.mixin.render;

import com.llamalad7.mixinextras.sugar.Local;
import fr.adrien1106.reframed.client.model.RetexturingBakedModel;
import fr.adrien1106.reframed.client.util.RenderHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockModelRenderer.class)
public class BlockModelRendererMixin {

    @Redirect(
        method = "renderSmooth",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/Block;shouldDrawSide(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;Lnet/minecraft/util/math/BlockPos;)Z"
        )
    )
    private boolean shouldDrawFrameSideS(BlockState state, BlockView world, BlockPos pos, Direction side, BlockPos other_pos, @Local(argsOnly = true) BakedModel model) {
        return RenderHelper.shouldDrawSide(state, world, pos, side, other_pos, model instanceof RetexturingBakedModel rm ? rm.getThemeIndex() : 0);
    }

    @Redirect(
        method = "renderFlat",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/block/Block;shouldDrawSide(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;Lnet/minecraft/util/math/BlockPos;)Z"
        )
    )
    private boolean shouldDrawFrameSideF(BlockState state, BlockView world, BlockPos pos, Direction side, BlockPos other_pos, @Local(argsOnly = true) BakedModel model) {
        return RenderHelper.shouldDrawSide(state, world, pos, side, other_pos, model instanceof RetexturingBakedModel rm ? rm.getThemeIndex() : 0);
    }
}
