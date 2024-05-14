package fr.adrien1106.reframed;

import fr.adrien1106.reframed.block.*;
import fr.adrien1106.reframed.item.ReFramedHammerItem;
import fr.adrien1106.reframed.item.ReFramedBlueprintItem;
import fr.adrien1106.reframed.item.ReFramedBlueprintWrittenItem;
import fr.adrien1106.reframed.item.ReFramedScrewdriverItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import static fr.adrien1106.reframed.util.blocks.BlockProperties.LIGHT;

/**
 * TODO Dynamic Ambient Occlusion                                            -> for v1.6
 * TODO add minecraft models like wall fence etc                             -> for v1.6
 * TODO better connected textures                                            -> maybe v1.6 ?
 * TODO support continuity overlays                                          -> not scheduled
 */
public class ReFramed implements ModInitializer {
	public static final String MODID = "reframed";

	public static final ArrayList<Block> BLOCKS = new ArrayList<>();
	public static Block CUBE, SMALL_CUBE, SMALL_CUBES_STEP, STAIR, HALF_STAIR, STAIRS_CUBE, HALF_STAIRS_SLAB, HALF_STAIRS_STAIR, SLAB, SLABS_CUBE, STEP, STEPS_SLAB, LAYER, PILLAR, PILLARS_WALL, WALL, PANE, TRAPDOOR, DOOR;

	public static final ArrayList<Item> ITEMS = new ArrayList<>();
	public static Item HAMMER, SCREWDRIVER, BLUEPRINT, BLUEPRINT_WRITTEN;

	public static ItemGroup ITEM_GROUP;

	public static BlockEntityType<ReFramedEntity> REFRAMED_BLOCK_ENTITY;
	public static BlockEntityType<ReFramedDoubleEntity> REFRAMED_DOUBLE_BLOCK_ENTITY;

	public static BiConsumer<World, BlockPos> chunkRerenderProxy = (world, pos) -> {};
	
	@Override
	public void onInitialize() {
		CUBE              = registerBlock("cube"              , new ReFramedBlock(cp(Blocks.OAK_PLANKS)));
		SMALL_CUBE        = registerBlock("small_cube"        , new ReFramedSmallCubeBlock(cp(Blocks.OAK_PLANKS)));
	  	SMALL_CUBES_STEP  = registerBlock("small_cubes_step"  , new ReFramedSmallCubesStepBlock(cp(Blocks.OAK_PLANKS)));
		STAIR             = registerBlock("stair"             , new ReFramedStairBlock(cp(Blocks.OAK_STAIRS)));
		STAIRS_CUBE       = registerBlock("stairs_cube"       , new ReFramedStairsCubeBlock(cp(Blocks.OAK_STAIRS)));
		HALF_STAIR        = registerBlock("half_stair"        , new ReFramedHalfStairBlock(cp(Blocks.OAK_STAIRS)));
		HALF_STAIRS_SLAB  = registerBlock("half_stairs_slab"  , new ReFramedHalfStairsSlabBlock(cp(Blocks.OAK_STAIRS)));
		HALF_STAIRS_STAIR = registerBlock("half_stairs_stair" , new ReFramedHalfStairsStairBlock(cp(Blocks.OAK_STAIRS)));
		LAYER             = registerBlock("layer"             , new ReFramedLayerBlock(cp(Blocks.OAK_SLAB)));
		SLAB              = registerBlock("slab"              , new ReFramedSlabBlock(cp(Blocks.OAK_SLAB)));
		SLABS_CUBE        = registerBlock("slabs_cube"        , new ReFramedSlabsCubeBlock(cp(Blocks.OAK_SLAB)));
		STEP              = registerBlock("step"              , new ReFramedStepBlock(cp(Blocks.OAK_SLAB)));
		STEPS_SLAB        = registerBlock("steps_slab"        , new ReFramedStepsSlabBlock(cp(Blocks.OAK_SLAB)));
		PILLAR            = registerBlock("pillar"            , new ReFramedPillarBlock(cp(Blocks.OAK_FENCE)));
		PILLARS_WALL      = registerBlock("pillars_wall"      , new ReFramedPillarsWallBlock(cp(Blocks.OAK_FENCE)));
		WALL              = registerBlock("wall"              , new ReFramedWallBlock(cp(Blocks.OAK_FENCE)));
        PANE              = registerBlock("pane"              , new ReFramedPaneBlock(cp(Blocks.OAK_FENCE)));
        TRAPDOOR          = registerBlock("trapdoor"          , new ReFramedTrapdoorBlock(cp(Blocks.OAK_TRAPDOOR)));
        DOOR              = registerBlock("door"              , new ReFramedDoorBlock(cp(Blocks.OAK_DOOR)));

		HAMMER            = registerItem("hammer"             , new ReFramedHammerItem(new Item.Settings().maxCount(1)));
		SCREWDRIVER       = registerItem("screwdriver"        , new ReFramedScrewdriverItem(new Item.Settings().maxCount(1)));
		BLUEPRINT         = registerItem("blueprint"          , new ReFramedBlueprintItem(new Item.Settings()));
		BLUEPRINT_WRITTEN = registerItem("blueprint_written"  , new ReFramedBlueprintWrittenItem(new Item.Settings().maxCount(1)));

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
			.entries((ctx, e) -> e.addAll(
				Stream.concat(
					ITEMS.stream().filter(item -> item != BLUEPRINT_WRITTEN),
					BLOCKS.stream().map(Block::asItem)
				).map(Item::getDefaultStack).toList())
			).build()
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

	private static <I extends Item> I registerItem(String path, I item) {
		Identifier id = id(path);
		Registry.register(Registries.ITEM, id, item);
		ITEMS.add(item);
		return item;
	}
	
	private static <B extends Block> B registerBlock(String path, B block) {
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
