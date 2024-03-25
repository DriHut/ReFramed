package fr.adrien1106.reframed.mixin.compat;

import com.llamalad7.mixinextras.sugar.Local;
import earth.terrarium.athena.api.client.fabric.WrappedGetter;
import fr.adrien1106.reframed.util.blocks.ThemeableBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WrappedGetter.class)
public class AthenaWrappedGetterMixin {

    @Redirect(
        method = "query",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/BlockRenderView;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"
        )
    ) // TODO better connected textures
    private BlockState queryCamoState(BlockRenderView world, BlockPos pos, @Local(argsOnly = true) BlockState reference_state) {
        // get Any that will connect or return any other (/!\ isOf is an uncertain check)
        if (world.getBlockEntity(pos) instanceof ThemeableBlockEntity framed_entity)
            return framed_entity.getThemes()
                .stream()
                .filter(state -> reference_state.isOf(state.getBlock()))
                .findAny()
                .orElse(framed_entity.getTheme(1));
        return world.getBlockState(pos);
    }
}
