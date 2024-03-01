package fr.adrien1106.reframed.mixin.render;

import fr.adrien1106.reframed.block.ReFramedDoubleBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @Shadow @Final private MinecraftClient client;

    @Redirect(method = "drawBlockOutline",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;" +
            "getOutlineShape(" +
                "Lnet/minecraft/world/BlockView;" +
                "Lnet/minecraft/util/math/BlockPos;" +
                "Lnet/minecraft/block/ShapeContext;" +
            ")Lnet/minecraft/util/shape/VoxelShape;"))
    private VoxelShape getRenderOutline(BlockState state, BlockView world, BlockPos pos, ShapeContext shape_context) {
        if (state.getBlock() instanceof ReFramedDoubleBlock double_frame_block) // cast is already checked in render
            return double_frame_block.getRenderOutline(state, (BlockHitResult) client.crosshairTarget);
        return state.getOutlineShape(world, pos, shape_context);
    }
}
