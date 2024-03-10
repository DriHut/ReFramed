package fr.adrien1106.reframed;

import fr.adrien1106.reframed.block.*;
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

import static fr.adrien1106.reframed.util.blocks.BlockProperties.LIGHT;

/**
 * TODO add Hammer from framed ( removes theme ) for sure
 * TODO add screwdriver ( iterate over theme states ) ?
 * TODO add blueprint for survival friendly copy paste of a theme.
 * TODO fix other models ( + half stair + layers )
 * TODO get better naming for the shapes (will break a lot of already placed blocks)
 * TODO put more coherence in the double theme orders / directions
 * TODO better connected textures
 */
public class ReFramed implements ModInitializer {
	public static final String MODID = "reframed";

	public static final ArrayList<Block> BLOCKS = new ArrayList<>();
	public static Block CUBE, SMALL_CUBE, SMALL_CUBES_STEP, STAIR, STAIRS_CUBE, SLAB, SLABS_CUBE, STEP, STEPS_SLAB;
	public static ItemGroup ITEM_GROUP;

	public static BlockEntityType<ReFramedEntity> REFRAMED_BLOCK_ENTITY;
	public static BlockEntityType<ReFramedDoubleEntity> REFRAMED_DOUBLE_BLOCK_ENTITY;

	public static BiConsumer<World, BlockPos> chunkRerenderProxy = (world, pos) -> {};
	
	@Override
	public void onInitialize() {
		CUBE              = registerReFramed("cube"              , new ReFramedBlock(cp(Blocks.OAK_PLANKS)));
		SMALL_CUBE        = registerReFramed("small_cube"        , new ReFramedSmallCubeBlock(cp(Blocks.OAK_PLANKS)));
	  	SMALL_CUBES_STEP  = registerReFramed("small_cubes_step"  , new ReFramedSmallCubesStepBlock(cp(Blocks.OAK_PLANKS)));
		STAIR             = registerReFramed("stair"             , new ReFramedStairBlock(cp(Blocks.OAK_STAIRS)));
		STAIRS_CUBE       = registerReFramed("stairs_cube"       , new ReFramedStairsCubeBlock(cp(Blocks.OAK_STAIRS)));
//		CUBE              = registerReFramed("half_stair"          , new ReFramedBlock(cp(Blocks.OAK_STAIRS))); // TODO
//		CUBE              = registerReFramed("half_stairs_stair"   , new ReFramedBlock(cp(Blocks.OAK_STAIRS))); // TODO
//		CUBE              = registerReFramed("half_stairs_slab"    , new ReFramedBlock(cp(Blocks.OAK_STAIRS))); // TODO
		SLAB              = registerReFramed("slab"              , new ReFramedSlabBlock(cp(Blocks.OAK_SLAB)));
		SLABS_CUBE        = registerReFramed("slabs_cube"        , new ReFramedSlabsCubeBlock(cp(Blocks.OAK_SLAB)));
		STEP              = registerReFramed("step"              , new ReFramedStepBlock(cp(Blocks.OAK_SLAB)));
		STEPS_SLAB        = registerReFramed("steps_slab"        , new ReFramedStepsSlabBlock(cp(Blocks.OAK_SLAB)));

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
			.luminance(state -> state.contains(LIGHT) && state.get(LIGHT) ? 15 : 0)
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
