package fr.adrien1106.reframed;

import fr.adrien1106.reframed.block.*;
import fr.adrien1106.reframed.util.BlockHelper;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * TODO self culling, fix other models, better connected textures, preload items, better cache
 */
public class ReFramed implements ModInitializer {
	public static final String MODID = "reframed";

	public static final ArrayList<Block> BLOCKS = new ArrayList<>();
	public static Block CUBE, STAIRS, DOUBLE_STAIRS, SLAB, DOUBLE_SLAB, STEP, DOUBLE_STEP;
	public static ItemGroup ITEM_GROUP;

	public static BlockEntityType<ReFramedEntity> REFRAMED_BLOCK_ENTITY;
	public static BlockEntityType<ReFramedDoubleEntity> REFRAMED_DOUBLE_BLOCK_ENTITY;

	public static BiConsumer<World, BlockPos> chunkRerenderProxy = (world, pos) -> {};
	
	@Override
	public void onInitialize() {
		//registerReFramed mutates FRAMES as a side effect, which is a List, so order is preserved
		//the ordering is used in the creative tab, so they're roughly sorted by encounter order of the
		//corresponding vanilla block in the "search" creative tab... with the non-vanilla "post" and
		//"vertical slab" inserted where they fit ...and i moved the lever way up next to the pressureplate
		//and button, because they're redstoney... hopefully this ordering makes sense lol
		CUBE           = registerReFramed("cube"          , new ReFramedBlock(cp(Blocks.OAK_PLANKS)));
		STAIRS         = registerReFramed("stairs"        , new ReFramedStairsBlock(cp(Blocks.OAK_STAIRS)));
		DOUBLE_STAIRS  = registerReFramed("double_stairs" , new ReFramedDoubleStairsBlock(cp(Blocks.OAK_STAIRS)));
		SLAB           = registerReFramed("slab"          , new ReFramedSlabBlock(cp(Blocks.OAK_SLAB)));
		DOUBLE_SLAB    = registerReFramed("double_slab"   , new ReFramedDoubleSlabBlock(cp(Blocks.OAK_SLAB)));
		STEP           = registerReFramed("step"          , new ReFramedStepBlock(cp(Blocks.OAK_SLAB)));
		DOUBLE_STEP    = registerReFramed("double_step"   , new ReFramedDoubleStepBlock(cp(Blocks.OAK_SLAB)));

		REFRAMED_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, id("camo"),
			FabricBlockEntityTypeBuilder.create(
				(pos, state) -> new ReFramedEntity(REFRAMED_BLOCK_ENTITY, pos, state),
				BLOCKS.stream()
					.filter(block -> !(block instanceof ReFramedDoubleBlock))
					.toArray(Block[]::new)).build(null)
		);

		REFRAMED_DOUBLE_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, id("double_camo"),
			FabricBlockEntityTypeBuilder.create(
				(pos, state) -> new ReFramedDoubleEntity(REFRAMED_DOUBLE_BLOCK_ENTITY, pos, state),
				BLOCKS.stream()
					.filter(block -> block instanceof ReFramedDoubleBlock)
					.toArray(Block[]::new)).build(null)
		);
		
		ITEM_GROUP = Registry.register(Registries.ITEM_GROUP, id("tab"), FabricItemGroup.builder()
			.displayName(Text.translatable("itemGroup.reframed.tab"))
			.icon(() -> new ItemStack(SLAB))
			.entries((ctx, e) -> e.addAll(BLOCKS.stream().map(ItemStack::new).collect(Collectors.toList()))).build()
		);
	}

	private static AbstractBlock.Settings cp(Block base) {
		return AbstractBlock.Settings.copy(base)
			.luminance(BlockHelper::luminance)
			.nonOpaque()
			.sounds(BlockSoundGroup.WOOD)
			.hardness(0.2f)
			.suffocates((a,b,c) -> false)
			.blockVision((a,b,c) -> false);
	}
	
	private static <B extends Block> B registerReFramed(String path, B block) {
		Identifier id = id(path);
		
		Registry.register(Registries.BLOCK, id, block);
		Registry.register(Registries.ITEM, id, new BlockItem(block, new Item.Settings()));
		BLOCKS.add(block);
		return block;
	}

	public static Identifier id(String path) {
		return new Identifier(MODID, path);
	}
}
