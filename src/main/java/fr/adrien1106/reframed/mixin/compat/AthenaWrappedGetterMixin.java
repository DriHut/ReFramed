package fr.adrien1106.reframed.mixin.compat;

import earth.terrarium.athena.api.client.fabric.WrappedGetter;
import fr.adrien1106.reframed.util.ThemeableBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WrappedGetter.class)
public class AthenaWrappedGetterMixin {

    // TODO return only the state that might be of interest
    @Shadow @Final private BlockRenderView getter;

    @Inject(method = "getBlockState", at = @At(value = "HEAD"), cancellable = true)
    private void getCamoState(BlockPos pos, CallbackInfoReturnable<BlockState> cir) {
        if (!(getter.getBlockEntity(pos) instanceof ThemeableBlockEntity framed_entity)) return;
        cir.setReturnValue(framed_entity.getTheme(1)); // TODO theme
    }

    @Redirect(method = "getAppearance(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;)" +
        "Lnet/minecraft/block/BlockState;",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/BlockRenderView;" +
            "getBlockState(Lnet/minecraft/util/math/BlockPos;)" +
            "Lnet/minecraft/block/BlockState;"))
    private BlockState appearanceCamoState(BlockRenderView world, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof ThemeableBlockEntity framed_entity) return framed_entity.getTheme(1); // TODO theme
        return world.getBlockState(pos);
    }

    @Redirect(method = "query",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/BlockRenderView;" +
            "getBlockState(Lnet/minecraft/util/math/BlockPos;)" +
            "Lnet/minecraft/block/BlockState;"))
    private BlockState queryCamoState(BlockRenderView world, BlockPos pos) {
        if (world.getBlockEntity(pos) instanceof ThemeableBlockEntity framed_entity) return framed_entity.getTheme(1); // TODO theme
        return world.getBlockState(pos);
    }
}
