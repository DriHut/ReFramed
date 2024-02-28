package fr.adrien1106.reframed.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import fr.adrien1106.reframed.util.ThemeableBlockEntity;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.BlockRenderInfo;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockRenderInfo.class)
public abstract class BlockRenderInfoMixin {

    @Shadow public BlockPos blockPos;

    @Shadow public BlockState blockState;

    @Shadow public BlockRenderView blockView;

    @Shadow @Final private BlockPos.Mutable searchPos;

    @ModifyArg(method = "prepareForBlock",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayers;" +
            "getBlockLayer(Lnet/minecraft/block/BlockState;)Lnet/minecraft/client/render/RenderLayer;"))
    public BlockState prepareCamoLayer(BlockState state, @Local(argsOnly = true) BlockPos pos) {
        BlockEntity block_entity = MinecraftClient.getInstance().world.getBlockEntity(pos);
        if (!(block_entity instanceof ThemeableBlockEntity frame_entity)) return state;
        return frame_entity.getFirstTheme();
    }

    @Inject(method = "shouldDrawFace",
        at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/util/math/Direction;getId()I"),
        cancellable = true)
    private void shouldDrawCamoFace(Direction face, CallbackInfoReturnable<Boolean> cir) {
        BlockEntity block_entity = MinecraftClient.getInstance().world.getBlockEntity(blockPos);
        if (!(block_entity instanceof ThemeableBlockEntity)) return;
        cir.setReturnValue(Block.shouldDrawSide(blockState, blockView, blockPos, face, searchPos.set(blockPos, face)));
    }
}
