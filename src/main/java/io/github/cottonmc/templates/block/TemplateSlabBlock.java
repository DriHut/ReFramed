package io.github.cottonmc.templates.block;

import io.github.cottonmc.templates.Templates;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class TemplateSlabBlock extends SlabBlock implements BlockEntityProvider {
	public TemplateSlabBlock(Settings settings) {
		super(settings);
	}
	
	public TemplateSlabBlock() {
		//super(TemplateBlock.configureSettings(Settings.create()) //TODO
		super(Settings.create().nonOpaque()
			.sounds(BlockSoundGroup.WOOD)
			.hardness(0.2f)); //TODO: Material.WOOD
	}
	
	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return Templates.SLAB_ENTITY.instantiate(pos, state);
	}
}
