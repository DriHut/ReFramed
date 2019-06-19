package io.github.cottonmc.templates;

import io.github.cottonmc.templates.block.SlopeBlock;
import io.github.cottonmc.templates.block.entity.SlopeEntity;
import io.github.cottonmc.templates.model.TemplateModelVariantProvider;
import net.fabricmc.api.ModInitializer;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import java.util.function.Supplier;

public class Templates implements ModInitializer {
	public static final String MODID = "templates";
	//define/create here so that it always exists when called in a client initializer, regardless of load order
	public static TemplateModelVariantProvider provider = new TemplateModelVariantProvider();

	public static final Block SLOPE = register("slope", new SlopeBlock(), ItemGroup.DECORATIONS);
	@SuppressWarnings("unchecked")
    public static final BlockEntityType<SlopeEntity> SLOPE_ENTITY = register("slope", SlopeEntity::new, SLOPE);

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
