package fr.adrien1106.reframedtemplates.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.WallBlock;
import net.minecraft.util.shape.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

//Used in TemplateWallBlock, since the vanilla wall block code explodes if you add more blockstates.
@Mixin(WallBlock.class)
public interface WallBlockAccessor {
	@Accessor("shapeMap") Map<BlockState, VoxelShape> templates$getShapeMap();
	@Accessor("collisionShapeMap") Map<BlockState, VoxelShape> templates$getCollisionShapeMap();
}
