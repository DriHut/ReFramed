package io.github.cottonmc.templates;

import io.github.cottonmc.templates.api.TemplateInteractionUtil;
import io.github.cottonmc.templates.block.TemplateBlock;
import io.github.cottonmc.templates.block.TemplateSlabBlock;
import io.github.cottonmc.templates.block.TemplateSlopeBlock;
import io.github.cottonmc.templates.block.TemplateEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.BiConsumer;

public class Templates implements ModInitializer {
	public static final String MODID = "templates";
	
	public static final Block CUBE = Registry.register(Registries.BLOCK, id("cube"), new TemplateBlock(TemplateInteractionUtil.makeSettings()));
	public static final Block SLOPE = Registry.register(Registries.BLOCK, id("slope"), new TemplateSlopeBlock(TemplateInteractionUtil.makeSettings()));
	public static final Block SLAB = Registry.register(Registries.BLOCK, id("slab"), new TemplateSlabBlock(TemplateInteractionUtil.makeSettings()));
	
	//N.B. it's fine to make your own block entity type instead of gluing additional blocks to this one
	public static final BlockEntityType<TemplateEntity> TEMPLATE_BLOCK_ENTITY = Registry.register(
		Registries.BLOCK_ENTITY_TYPE, id("slope"),
		FabricBlockEntityTypeBuilder.create(Templates::makeTemplateBlockEntity, CUBE, SLOPE, SLAB).build(null)
	);
	
	@SuppressWarnings("unused")
	public static final ItemGroup TAB = Registry.register(
		Registries.ITEM_GROUP, id("tab"),
		FabricItemGroup.builder()
			.displayName(Text.translatable("itemGroup.templates.tab"))
			.icon(() -> new ItemStack(SLOPE))
			.entries(Templates::fillItemGroup)
			.build()
	);
	
	//Overridden in TemplatesClient
	public static BiConsumer<World, BlockPos> chunkRerenderProxy = (world, pos) -> {};
	
	@Override
	public void onInitialize() {
		Registry.register(Registries.ITEM, id("cube"), new BlockItem(CUBE, new Item.Settings()));
		Registry.register(Registries.ITEM, id("slope"), new BlockItem(SLOPE, new Item.Settings()));
		Registry.register(Registries.ITEM, id("slab"), new BlockItem(SLAB, new Item.Settings()));
	}
	
	public static Identifier id(String path) {
		return new Identifier(MODID, path);
	}
	
	//simply for breaking circular reference in the registration call
	private static TemplateEntity makeTemplateBlockEntity(BlockPos pos, BlockState state) {
		return new TemplateEntity(TEMPLATE_BLOCK_ENTITY, pos, state);
	}
	
	private static void fillItemGroup(ItemGroup.DisplayContext ctx, ItemGroup.Entries ent) {
		ent.add(CUBE);
		ent.add(SLOPE);
		ent.add(SLAB);
	}
}
