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
 * TODO make block pairable by right click                                 -> for v1.6
 * TODO add Hammer from framed ( removes theme )                           -> for v1.5.5
 * TODO add screwdriver ( iterate over theme states ) ?
 * TODO add blueprint for survival friendly copy paste of a theme.         -> for v1.5.5
 * TODO add minecraft models like wall fence etc                           -> for v1.6
 * TODO better connected textures                                          -> maybe v1.6 ?
 */
public class ReFramed implements ModInitializer {
	public static final String MODID = "reframed";

	public static final ArrayList<Block> BLOCKS = new ArrayList<>();
	public static Block CUBE, SMALL_CUBE, SMALL_CUBES_STEP, STAIR, HALF_STAIR, STAIRS_CUBE, HALF_STAIRS_SLAB, HALF_STAIRS_STAIR, SLAB, SLABS_CUBE, STEP, STEPS_SLAB, LAYER;
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
		HALF_STAIR        = registerReFramed("half_stair"        , new ReFramedHalfStairBlock(cp(Blocks.OAK_STAIRS)));
		HALF_STAIRS_SLAB  = registerReFramed("half_stairs_slab"  , new ReFramedHalfStairsSlabBlock(cp(Blocks.OAK_STAIRS)));
		HALF_STAIRS_STAIR = registerReFramed("half_stairs_stair" , new ReFramedHalfStairsStairBlock(cp(Blocks.OAK_STAIRS)));
		LAYER             = registerReFramed("layer"             , new ReFramedLayerBlock(cp(Blocks.OAK_SLAB)));
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
