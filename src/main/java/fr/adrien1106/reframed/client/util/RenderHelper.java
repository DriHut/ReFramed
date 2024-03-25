package fr.adrien1106.reframed.client.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import fr.adrien1106.reframed.block.ReFramedBlock;
import fr.adrien1106.reframed.client.ReFramedClient;
import fr.adrien1106.reframed.client.model.QuadPosBounds;
import fr.adrien1106.reframed.util.blocks.ThemeableBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;

import java.util.List;
import java.util.Objects;

import static net.minecraft.util.shape.VoxelShapes.combine;

@Environment(EnvType.CLIENT)
public class RenderHelper {


    // self culling cache of the models not made thread local so that it is only computed once
    private static final Cache<CullElement, Integer[]> INNER_CULL_MAP = CacheBuilder.newBuilder().maximumSize(1024).build();
    private record CullElement(Block block, Object state_key, int model) {}

    /**
     * compute which quad might cull with another model quad
     * @param state - the state of the model
     * @param models - list of models on the same block
     */
    public static void computeInnerCull(BlockState state, List<ForwardingBakedModel> models) {
        if (!(state.getBlock() instanceof ReFramedBlock frame_block)) return;
        Object key = frame_block.getModelCacheKey(state);
        if (INNER_CULL_MAP.asMap().containsKey(new CullElement(frame_block, key, 1))) return;

        Renderer r = ReFramedClient.HELPER.getFabricRenderer();
        QuadEmitter quad_emitter = r.meshBuilder().getEmitter();
        RenderMaterial material = r.materialFinder().clear().find();
        Random random = Random.create();

        List<List<QuadPosBounds>> model_bounds = models.stream()
            .map(ForwardingBakedModel::getWrappedModel)
            .filter(Objects::nonNull)
            .map(wrapped -> wrapped.getQuads(state, null, random))
            .map(quads -> quads.stream().map(quad -> {
                quad_emitter.fromVanilla(quad, material, null);
                return QuadPosBounds.read(quad_emitter, false);
            }).toList()).toList();

        Integer[] cull_array;
        for(int self_id = 1; self_id <= model_bounds.size(); self_id++) {
            List<QuadPosBounds> self_bounds = model_bounds.get(self_id - 1);
            cull_array = new Integer[self_bounds.size()];
            for (int self_quad = 0; self_quad < cull_array.length; self_quad++) {
                QuadPosBounds self_bound = self_bounds.get(self_quad);
                for(int other_id = 1; other_id <= model_bounds.size(); other_id++) {
                    if (other_id == self_id) continue;
                    if (model_bounds.get(other_id - 1).stream().anyMatch(other_bound -> other_bound.equals(self_bound))) {
                        cull_array[self_quad] = other_id;
                        break;
                    }
                }
            }
            INNER_CULL_MAP.put(new CullElement(frame_block, key, self_id), cull_array);
        }
    }

    public static boolean shouldDrawInnerFace(BlockState state, BlockRenderView view, BlockPos pos, int quad_index, int theme_index) {
        if ( !(state.getBlock() instanceof ReFramedBlock frame_block)
            || !(view.getBlockEntity(pos) instanceof ThemeableBlockEntity frame_entity)
        ) return true;
        CullElement key = new CullElement(frame_block, frame_block.getModelCacheKey(state), theme_index);
        if (!INNER_CULL_MAP.asMap().containsKey(key)) return true;

        // needs to be Integer object because array is initialized with null not 0
        Integer cull_theme = Objects.requireNonNull(INNER_CULL_MAP.getIfPresent(key))[quad_index];
        if (cull_theme == null) return true; // no culling possible

        BlockState self_theme = frame_entity.getTheme(theme_index);
        BlockState other_theme = frame_entity.getTheme(cull_theme);

        if (self_theme.isSideInvisible(other_theme, null)) return false;
        return !self_theme.isOpaque() || !other_theme.isOpaque();
    }

    // Doing this method from scratch as it is simpler to do than injecting everywhere
    public static boolean shouldDrawSide(BlockState self_state, BlockView world, BlockPos pos, Direction side, BlockPos other_pos, int theme_index) {
        ThemeableBlockEntity self = world.getBlockEntity(pos) instanceof ThemeableBlockEntity e ? e : null;
        ThemeableBlockEntity other = world.getBlockEntity(other_pos) instanceof ThemeableBlockEntity e ? e : null;
        BlockState other_state = world.getBlockState(other_pos);

        // normal behaviour
        if (self == null && other == null) return Block.shouldDrawSide(self_state, world, pos, side, other_pos);

        // self is a normal Block
        if (self == null && other_state.getBlock() instanceof ReFramedBlock other_block) {
            VoxelShape self_shape = self_state.getCullingShape(world, pos);
            if (self_shape.isEmpty()) return true;

            int i = 0;
            VoxelShape other_shape = VoxelShapes.empty();
            for (BlockState s: other.getThemes()) {
                i++;
                if (self_state.isSideInvisible(s, side) || s.isOpaque())
                    other_shape = combine(
                        other_shape,
                        other_block
                            .getShape(other_state, i)
                            .getFace(side.getOpposite()),
                        BooleanBiFunction.OR
                    );
            }

            // determine if side needs to be rendered
            return VoxelShapes.matchesAnywhere(self_shape, other_shape, BooleanBiFunction.ONLY_FIRST);
        }

        BlockState self_theme = self.getTheme(theme_index);
        // other is normal Block
        if (other == null && self_state.getBlock() instanceof ReFramedBlock self_block) {
            // Transparent is simple if self and the neighbor are invisible don't render side (like default)
            if (self_theme.isSideInvisible(other_state, side)) return false;

            // Opaque is also simple as each model are rendered one by one
            if (other_state.isOpaque()) {
                // no cache section :( because it differs between each instance of the frame
                VoxelShape self_shape = self_block.getShape(self_state, theme_index).getFace(side);
                if (self_shape.isEmpty()) return true;
                VoxelShape other_shape = other_state.getCullingFace(world, other_pos, side.getOpposite());

                // determine if side needs to be rendered
                return VoxelShapes.matchesAnywhere(self_shape, other_shape, BooleanBiFunction.ONLY_FIRST);
            }

            return true;
        }

        // Both are frames
        // here both are computed in the same zone as there will necessarily a shape comparison
        if (self_state.getBlock() instanceof ReFramedBlock self_block && other_state.getBlock() instanceof ReFramedBlock other_block) {
            VoxelShape self_shape = self_block.getShape(self_state, theme_index).getFace(side);
            if (self_shape.isEmpty()) return true;

            int i = 0;
            VoxelShape other_shape = VoxelShapes.empty();
            for (BlockState s: other.getThemes()) {
                i++;
                if (self_theme.isSideInvisible(s, side) || s.isOpaque())
                    other_shape = combine(
                        other_shape,
                        other_block
                            .getShape(other_state, i)
                            .getFace(side.getOpposite()),
                        BooleanBiFunction.OR
                    );
            }

            // determine if side needs to be rendered
            return VoxelShapes.matchesAnywhere(self_shape, other_shape, BooleanBiFunction.ONLY_FIRST);
        }

        return true;
    }
}
