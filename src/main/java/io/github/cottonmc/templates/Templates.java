package io.github.cottonmc.templates;

import io.github.cottonmc.templates.block.SlopeBlock;
import io.github.cottonmc.templates.block.entity.TemplateEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.BiConsumer;

public class Templates implements ModInitializer {
	public static final String MODID = "templates";
	
	public static final Block SLOPE = Registry.register(Registries.BLOCK, id("slope"), new SlopeBlock());
	public static final BlockEntityType<TemplateEntity> SLOPE_ENTITY = Registry.register(
		Registries.BLOCK_ENTITY_TYPE, id("slope"),
		FabricBlockEntityTypeBuilder.create(Templates::makeSlopeEntity, SLOPE).build(null)
	);
	
	//Overridden in TemplatesClient
	public static BiConsumer<World, BlockPos> chunkRerenderProxy = (world, pos) -> {};
	
	@Override
	public void onInitialize() {
		Registry.register(Registries.ITEM, id("slope"), (Item) new BlockItem(SLOPE, new Item.Settings()));
	}
	
	public static Identifier id(String path) {
		return new Identifier(MODID, path);
	}
	
	//simply for breaking the circular reference in the SLOPE_ENTITY constructor call
	private static TemplateEntity makeSlopeEntity(BlockPos pos, BlockState state) {
		return new TemplateEntity(SLOPE_ENTITY, pos, state);
	}
}
