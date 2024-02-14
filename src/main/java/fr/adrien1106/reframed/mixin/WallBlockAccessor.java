package fr.adrien1106.reframed.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.WallBlock;
import net.minecraft.util.shape.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

//Used in ReFramedWallBlock, since the vanilla wall block code explodes if you add more blockstates.
@Mixin(WallBlock.class)
public interface WallBlockAccessor {
	@Accessor("shapeMap") Map<BlockState, VoxelShape> getShapeMap();
	@Accessor("collisionShapeMap") Map<BlockState, VoxelShape> getCollisionShapeMap();
}
