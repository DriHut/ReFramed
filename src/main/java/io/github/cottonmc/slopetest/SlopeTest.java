package io.github.cottonmc.slopetest;

import io.github.cottonmc.slopetest.block.SlopeTestBlock;
import io.github.cottonmc.slopetest.block.entity.SlopeTestEntity;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import java.util.function.Supplier;

public class SlopeTest implements ModInitializer {
	public static final String MODID = "slopetest";

	public static final Block SLOPE = register("slope", new SlopeTestBlock(), ItemGroup.DECORATIONS);
	@SuppressWarnings("unchecked")
    public static final BlockEntityType<SlopeTestEntity> SLOPE_ENTITY = register("slope", SlopeTestEntity::new, SLOPE);

	@Override
	public void onInitialize() {

	}

	public static Block register(String name, Block block, ItemGroup tab) {
		Registry.register(Registry.BLOCK, new Identifier(MODID, name), block);
		BlockItem item = new BlockItem(block, new Item.Settings().group(tab));
		register(name, item);
		return block;
	}

	@SuppressWarnings("rawtypes")
    public static BlockEntityType register(String name, Supplier<BlockEntity> be, Block...blocks) {
		return Registry.register(Registry.BLOCK_ENTITY, new Identifier(MODID, name), BlockEntityType.Builder.create(be, blocks).build(null));
	}

	public static Item register(String name, Item item) {
		Registry.register(Registry.ITEM, new Identifier(MODID, name), item);
		return item;
	}

}
