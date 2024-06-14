package fr.adrien1106.reframed.util.mixin;

import com.moulberry.axiom.utils.IntMatrix;
import com.moulberry.axiom.world_modification.CompressedBlockEntity;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;

public interface IAxiomChunkedBlockRegionMixin {

    void setTransform(IntMatrix transform, Long2ObjectMap<CompressedBlockEntity> block_entities);

    IntMatrix getTransform();

    Long2ObjectMap<CompressedBlockEntity> getBlockEntities();
}
