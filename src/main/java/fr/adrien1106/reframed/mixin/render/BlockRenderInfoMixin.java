package fr.adrien1106.reframed.mixin.render;

import com.llamalad7.mixinextras.sugar.Local;
import fr.adrien1106.reframed.client.util.RenderHelper;
import fr.adrien1106.reframed.util.mixin.IBlockRenderInfoMixin;
import fr.adrien1106.reframed.util.blocks.ThemeableBlockEntity;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.BlockRenderInfo;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockRenderInfo.class)
public abstract class BlockRenderInfoMixin implements IBlockRenderInfoMixin {

    @Shadow public abstract void prepareForBlock(BlockState blockState, BlockPos blockPos, boolean modelAo);

    @Shadow public BlockRenderView blockView;
    @Unique private int theme_index = 0;
    @Unique private int model_hash = 0;

    @ModifyArg(method = "prepareForBlock",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayers;" +
            "getBlockLayer(Lnet/minecraft/block/BlockState;)Lnet/minecraft/client/render/RenderLayer;"))
    public BlockState prepareCamoLayer(BlockState state, @Local(argsOnly = true) BlockPos pos) {
        BlockEntity block_entity = blockView.getBlockEntity(pos);
        if (!(block_entity instanceof ThemeableBlockEntity frame_entity)) return state;
        return frame_entity.getTheme(theme_index);
    }

    @Redirect(method = "shouldDrawFace", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;shouldDrawSide(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;Lnet/minecraft/util/math/BlockPos;)Z"))
    private boolean shouldDrawAdjacentCamoSide(BlockState state, BlockView world, BlockPos pos, Direction side, BlockPos other_pos) {
        return RenderHelper.shouldDrawSide(state, world, pos, side, other_pos, theme_index);
    }

    @Override
    @Unique
    public void prepareForBlock(BlockState state, BlockPos pos, boolean ao, int theme_index, int model_hash) {
        this.theme_index = theme_index;
        this.model_hash = model_hash;
        prepareForBlock(state, pos, ao);
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
