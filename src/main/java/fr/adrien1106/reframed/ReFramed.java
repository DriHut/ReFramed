package fr.adrien1106.reframed;

import fr.adrien1106.reframed.util.ReFramedInteractionUtil;
import fr.adrien1106.reframed.block.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSetType;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * TODO multiple camos
 */
public class ReFramed implements ModInitializer {
	public static final String MODID = "reframed";

	public static final ArrayList<Block> BLOCKS = new ArrayList<>();
	public static Block CUBE, STAIRS, SLAB, POST, FENCE, FENCE_GATE, DOOR, TRAPDOOR, IRON_DOOR, IRON_TRAPDOOR, PRESSURE_PLATE, BUTTON, LEVER, WALL, CARPET, PANE, CANDLE;

	public static BlockEntityType<ReFramedEntity> REFRAMED_BLOCK_ENTITY;

	public static BiConsumer<World, BlockPos> chunkRerenderProxy = (world, pos) -> {};
	
	@Override
	public void onInitialize() {
		//registerReFramed mutates FRAMES as a side effect, which is a List, so order is preserved
		//the ordering is used in the creative tab, so they're roughly sorted by encounter order of the
		//corresponding vanilla block in the "search" creative tab... with the non-vanilla "post" and
		//"vertical slab" inserted where they fit ...and i moved the lever way up next to the pressureplate
		//and button, because they're redstoney... hopefully this ordering makes sense lol
		CUBE           = registerReFramed("cube"          , new ReFramedBlock(ReFramedInteractionUtil.makeSettings()));
		STAIRS         = registerReFramed("stairs"        , new ReFramedStairsBlock(cp(Blocks.OAK_STAIRS)));
		SLAB           = registerReFramed("slab"          , new ReFramedSlabBlock(cp(Blocks.OAK_SLAB)));
		POST           = registerReFramed("post"          , new ReFramedPostBlock(cp(Blocks.OAK_FENCE)));
		FENCE          = registerReFramed("fence"         , new ReFramedFenceBlock(cp(Blocks.OAK_FENCE)));
		FENCE_GATE     = registerReFramed("fence_gate"    , new ReFramedFenceGateBlock(cp(Blocks.OAK_FENCE_GATE)));
		DOOR           = registerReFramed("door"          , new ReFramedDoorBlock(cp(Blocks.OAK_DOOR), BlockSetType.OAK));
		TRAPDOOR       = registerReFramed("trapdoor"      , new ReFramedTrapdoorBlock(cp(Blocks.OAK_TRAPDOOR), BlockSetType.OAK));
		IRON_DOOR      = registerReFramed("iron_door"     , new ReFramedDoorBlock(cp(Blocks.IRON_DOOR), BlockSetType.IRON));
		IRON_TRAPDOOR  = registerReFramed("iron_trapdoor" , new ReFramedTrapdoorBlock(cp(Blocks.IRON_TRAPDOOR), BlockSetType.IRON));
		PRESSURE_PLATE = registerReFramed("pressure_plate", new ReFramedPressurePlateBlock(cp(Blocks.OAK_PRESSURE_PLATE)));
		BUTTON         = registerReFramed("button"        , new ReFramedButtonBlock(cp(Blocks.OAK_BUTTON)));
		LEVER          = registerReFramed("lever"         , new ReFramedLeverBlock(cp(Blocks.LEVER)));
		WALL           = registerReFramed("wall"          , new ReFramedWallBlock(ReFramedInteractionUtil.makeSettings()));
		CARPET         = registerReFramed("carpet"        , new ReFramedCarpetBlock(cp(Blocks.WHITE_CARPET)));
		PANE           = registerReFramed("pane"          , new ReFramedPaneBlock(cp(Blocks.GLASS_PANE)));
		CANDLE         = registerReFramed("candle"        , new ReFramedCandleBlock(ReFramedCandleBlock.configureSettings(cp(Blocks.CANDLE))));

		REFRAMED_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, id("camo"),
			FabricBlockEntityTypeBuilder.create((pos, state) -> new ReFramedEntity(REFRAMED_BLOCK_ENTITY, pos, state), BLOCKS.toArray(new Block[0])).build(null)
		);
		
		Registry.register(Registries.ITEM_GROUP, id("tab"), FabricItemGroup.builder()
			.displayName(Text.translatable("itemGroup.reframed.tab"))
			.icon(() -> new ItemStack(SLAB))
			.entries((ctx, e) -> e.addAll(BLOCKS.stream().map(ItemStack::new).collect(Collectors.toList()))).build()
		);
	}

	private static AbstractBlock.Settings cp(Block base) {
		return ReFramedInteractionUtil.configureSettings(AbstractBlock.Settings.copy(base));
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
