package fr.adrien1106.reframed.mixin.compat;

import com.moulberry.axiom.render.regions.ChunkedBlockRegion;
import com.moulberry.axiom.utils.IntMatrix;
import com.moulberry.axiom.world_modification.CompressedBlockEntity;
import fr.adrien1106.reframed.client.model.MultiRetexturableModel;
import fr.adrien1106.reframed.client.model.RetexturingBakedModel;
import fr.adrien1106.reframed.util.mixin.IAxiomChunkedBlockRegionMixin;
import fr.adrien1106.reframed.util.mixin.IMultipartBakedModelMixin;
import fr.adrien1106.reframed.util.mixin.ThemedBlockEntity;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.stream.Stream;

import static fr.adrien1106.reframed.block.ReFramedEntity.BLOCKSTATE_KEY;

@Mixin(ChunkedBlockRegion.class) // TODO: Look here for better rotation/flip support
public abstract class AxiomChunkedBlockRegionMixin implements IAxiomChunkedBlockRegionMixin {

    @Shadow
    private static void renderBlock(BufferBuilder blockBuilder, BlockRenderManager renderManager, BlockPos.Mutable blockPos, Random rand, MatrixStack matrices, BlockRenderView blockAndTintGetter, Matrix4f currentPoseMatrix, Matrix4f basePoseMatrix, int x, int y, int z, BlockState dataState, boolean useAmbientOcclusion) {}

    @Shadow public abstract BlockState getBlockState(BlockPos pos);

    @Unique
    private IntMatrix transform;
    @Unique
    private IntMatrix inverse_transform;
    @Unique
    private Long2ObjectMap<CompressedBlockEntity> block_entities;


    @Redirect(
        method = "uploadDirty",
        at = @At(
            value = "INVOKE",
            target = "Lcom/moulberry/axiom/render/regions/ChunkedBlockRegion;renderBlock(Lnet/minecraft/client/render/BufferBuilder;Lnet/minecraft/client/render/block/BlockRenderManager;Lnet/minecraft/util/math/BlockPos$Mutable;Lnet/minecraft/util/math/random/Random;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/world/BlockRenderView;Lorg/joml/Matrix4f;Lorg/joml/Matrix4f;IIILnet/minecraft/block/BlockState;Z)V"
        )
    )
    private void onRenderBlock(BufferBuilder buffer, BlockRenderManager renderer, BlockPos.Mutable pos, Random rand, MatrixStack matrices, BlockRenderView world, Matrix4f current_pos, Matrix4f base_pos, int x, int y, int z, BlockState state, boolean use_ao) {
        BakedModel model;
        List<BakedModel> models;
        if (block_entities != null
            && state.getRenderType() == BlockRenderType.MODEL
            && (model = renderer.getModel(state)) != null
            && model instanceof IMultipartBakedModelMixin mpm
            && !(models = mpm.getModels(state)
                .stream()
                .filter(m -> m instanceof RetexturingBakedModel || m instanceof MultiRetexturableModel)
                .toList()).isEmpty()
        ) {
            long key = BlockPos.asLong(
                inverse_transform.transformX(pos.getX(), pos.getY(), pos.getZ()),
                inverse_transform.transformY(pos.getX(), pos.getY(), pos.getZ()),
                inverse_transform.transformZ(pos.getX(), pos.getY(), pos.getZ())
            );
            if (block_entities.containsKey(key)) {
                NbtCompound compound = block_entities.get(key).decompress();
                models.stream()
                    .flatMap(m -> m instanceof MultiRetexturableModel mm
                        ? mm.models().stream()
                        : Stream.of((RetexturingBakedModel)m)
                    )
                    .forEach(m -> m.setCamo(
                        world,
                        compound.contains(BLOCKSTATE_KEY + m.getThemeIndex())
                            ? NbtHelper.toBlockState(Registries.BLOCK.getReadOnlyWrapper(), compound.getCompound(BLOCKSTATE_KEY + m.getThemeIndex()))
                            : null,
                        pos
                    ));
            }
        }
        renderBlock(buffer, renderer, pos, rand, matrices, world, current_pos, base_pos, x, y, z, state, use_ao);
    }

    @Inject(
        method = "getBlockEntity",
        at = @At("HEAD"),
        remap = false,
        cancellable = true
    )
    private void onGetBlockEntity(BlockPos pos, CallbackInfoReturnable<BlockEntity> cir) {
        if (inverse_transform == null) return;
        long key = BlockPos.asLong(
            inverse_transform.transformX(pos.getX(), pos.getY(), pos.getZ()),
            inverse_transform.transformY(pos.getX(), pos.getY(), pos.getZ()),
            inverse_transform.transformZ(pos.getX(), pos.getY(), pos.getZ())
        );
        NbtCompound compound;
        if (!block_entities.containsKey(key)
            || !(compound = block_entities.get(key).decompress()).contains(BLOCKSTATE_KEY + 1)
        ) return;
        cir.setReturnValue(new ThemedBlockEntity(compound, pos, getBlockState(pos)));
    }

    @Inject(
        method = "uploadDirty",
        at = @At("HEAD")
    )
    private void onUploadDirty(Camera camera, Vec3d translation, boolean canResort, boolean canUseAmbientOcclusion, CallbackInfo ci) {
        if (transform == null) inverse_transform = new IntMatrix();
        else inverse_transform = transform.copy();
        inverse_transform.invert();
    }

    @Inject(
        method = "flip",
        at = @At("RETURN")
    )
    private void onFlip(Direction.Axis axis, CallbackInfoReturnable<ChunkedBlockRegion> cir) {
        ((IAxiomChunkedBlockRegionMixin) cir.getReturnValue()).setTransform(transform, block_entities);
    }

    @Inject(
        method = "rotate",
        at = @At("RETURN")
    )
    private void onRotate(Direction.Axis axis, int count, CallbackInfoReturnable<ChunkedBlockRegion> cir) {
        ((IAxiomChunkedBlockRegionMixin) cir.getReturnValue()).setTransform(transform, block_entities);
    }

    @Override
    public void setTransform(IntMatrix transform, Long2ObjectMap<CompressedBlockEntity> block_entities) {
        this.transform = transform;
        this.block_entities = block_entities;
    }

    @Override
    public IntMatrix getTransform() {
        return transform;
    }

    @Override
    public Long2ObjectMap<CompressedBlockEntity> getBlockEntities() {
        return block_entities;
    }


}
