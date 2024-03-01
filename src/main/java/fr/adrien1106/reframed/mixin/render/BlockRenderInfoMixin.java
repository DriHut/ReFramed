package fr.adrien1106.reframed.mixin.render;

import com.llamalad7.mixinextras.sugar.Local;
import fr.adrien1106.reframed.block.ReFramedBlock;
import fr.adrien1106.reframed.util.IBlockRenderInfoMixin;
import fr.adrien1106.reframed.util.ThemeableBlockEntity;
import net.fabricmc.fabric.impl.client.indigo.renderer.render.BlockRenderInfo;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockRenderInfo.class)
public abstract class BlockRenderInfoMixin implements IBlockRenderInfoMixin {

    @Shadow public BlockPos blockPos;

    @Shadow public BlockState blockState;

    @Shadow public BlockRenderView blockView;

    @Shadow @Final private BlockPos.Mutable searchPos;

    @Shadow public abstract void prepareForBlock(BlockState blockState, BlockPos blockPos, boolean modelAo);

    @Unique
    private int theme_index = 1;

    @ModifyArg(method = "prepareForBlock",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderLayers;" +
            "getBlockLayer(Lnet/minecraft/block/BlockState;)Lnet/minecraft/client/render/RenderLayer;"))
    public BlockState prepareCamoLayer(BlockState state, @Local(argsOnly = true) BlockPos pos) {
        BlockEntity block_entity = MinecraftClient.getInstance().world.getBlockEntity(pos);
        if (!(block_entity instanceof ThemeableBlockEntity frame_entity)) return state;
        return frame_entity.getTheme(theme_index);
    }

    @Inject(method = "shouldDrawFace",
        at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/util/math/Direction;getId()I"),
        cancellable = true)
    private void shouldDrawCamoFace(Direction face, CallbackInfoReturnable<Boolean> cir) {
        // early injection for camos themselves
        BlockEntity block_entity = MinecraftClient.getInstance().world.getBlockEntity(blockPos);
        if (!(block_entity instanceof ThemeableBlockEntity)) return;
        cir.setReturnValue(ReFramedBlock.shouldDrawSide(blockState, blockView, blockPos, face, searchPos.set(blockPos, face), theme_index));
    }

    @Redirect(method = "shouldDrawFace", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;shouldDrawSide(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;Lnet/minecraft/util/math/BlockPos;)Z"))
    private boolean shouldDrawAdjacentCamoSide(BlockState state, BlockView world, BlockPos pos, Direction side, BlockPos other_pos) {
        return ReFramedBlock.shouldDrawSide(state, world, pos, side, other_pos, theme_index);
    }

    @Override
    public void prepareForBlock(BlockState state, BlockPos pos, boolean ao, int theme_index) {
        this.theme_index = theme_index;
        prepareForBlock(state, pos, ao);
    }
}
