package fr.adrien1106.reframed.mixin.compat;

import earth.terrarium.athena.api.client.utils.AppearanceAndTintGetter;
import earth.terrarium.athena.api.client.utils.CtmUtils;
import earth.terrarium.athena.impl.client.models.ConnectedBlockModel;
import fr.adrien1106.reframed.util.blocks.ThemeableBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ConnectedBlockModel.class)
public class AthenaConnectedBlockModelMixin {

    @Redirect(method = "getQuads",
        at = @At(value = "INVOKE", target = "Learth/terrarium/athena/api/client/utils/CtmUtils;" +
            "checkRelative(Learth/terrarium/athena/api/client/utils/AppearanceAndTintGetter;" +
            "Lnet/minecraft/block/BlockState;" +
            "Lnet/minecraft/util/math/BlockPos;" +
            "Lnet/minecraft/util/math/Direction;)Z"))
    private boolean checkForCull(AppearanceAndTintGetter level, BlockState state, BlockPos pos, Direction direction) {
        // Always get all the textures unless its another block then use default behaviour
        if (level.getBlockEntity(pos) instanceof ThemeableBlockEntity
            || level.getBlockEntity(pos.offset(direction)) instanceof ThemeableBlockEntity)
            return false;
        return CtmUtils.checkRelative(level, state, pos, direction);
    }
}
