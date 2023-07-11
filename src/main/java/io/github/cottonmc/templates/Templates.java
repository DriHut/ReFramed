package io.github.cottonmc.templates;

import io.github.cottonmc.templates.api.TemplateInteractionUtil;
import io.github.cottonmc.templates.block.TemplateBlock;
import io.github.cottonmc.templates.block.TemplateButtonBlock;
import io.github.cottonmc.templates.block.TemplateCandleBlock;
import io.github.cottonmc.templates.block.TemplateCarpetBlock;
import io.github.cottonmc.templates.block.TemplateFenceBlock;
import io.github.cottonmc.templates.block.TemplateFenceGateBlock;
import io.github.cottonmc.templates.block.TemplateLeverBlock;
import io.github.cottonmc.templates.block.TemplatePaneBlock;
import io.github.cottonmc.templates.block.TemplatePostBlock;
import io.github.cottonmc.templates.block.TemplatePressurePlateBlock;
import io.github.cottonmc.templates.block.TemplateSlabBlock;
import io.github.cottonmc.templates.block.TemplateSlopeBlock;
import io.github.cottonmc.templates.block.TemplateEntity;
import io.github.cottonmc.templates.block.TemplateStairsBlock;
import io.github.cottonmc.templates.block.TemplateTrapdoorBlock;
import io.github.cottonmc.templates.block.TemplateWallBlock;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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
	
	private static AbstractBlock.Settings cp(Block base) {
		return TemplateInteractionUtil.configureSettings(AbstractBlock.Settings.copy(base));
	}
	
	public static final Block BUTTON         = Registry.register(Registries.BLOCK, id("button")        , new TemplateButtonBlock(cp(Blocks.OAK_BUTTON)));
	public static final Block CANDLE         = Registry.register(Registries.BLOCK, id("candle")        , new TemplateCandleBlock(TemplateCandleBlock.configureSettings(cp(Blocks.CANDLE))));
	public static final Block CARPET         = Registry.register(Registries.BLOCK, id("carpet")        , new TemplateCarpetBlock(cp(Blocks.WHITE_CARPET)));
	public static final Block CUBE           = Registry.register(Registries.BLOCK, id("cube")          , new TemplateBlock(TemplateInteractionUtil.makeSettings()));
	//door? (hard cause its a multiblock)
	public static final Block FENCE          = Registry.register(Registries.BLOCK, id("fence")         , new TemplateFenceBlock(cp(Blocks.OAK_FENCE)));
	public static final Block FENCE_GATE     = Registry.register(Registries.BLOCK, id("fence_gate")    , new TemplateFenceGateBlock(cp(Blocks.OAK_FENCE_GATE)));
	public static final Block LEVER          = Registry.register(Registries.BLOCK, id("lever")         , new TemplateLeverBlock(cp(Blocks.LEVER)));
	public static final Block PANE           = Registry.register(Registries.BLOCK, id("pane")          , new TemplatePaneBlock(cp(Blocks.GLASS_PANE)));
	public static final Block POST           = Registry.register(Registries.BLOCK, id("post")          , new TemplatePostBlock(cp(Blocks.OAK_FENCE)));
	public static final Block PRESSURE_PLATE = Registry.register(Registries.BLOCK, id("pressure_plate"), new TemplatePressurePlateBlock(cp(Blocks.OAK_PRESSURE_PLATE)));
	public static final Block SLAB           = Registry.register(Registries.BLOCK, id("slab")          , new TemplateSlabBlock(cp(Blocks.OAK_SLAB)));
	public static final Block STAIRS         = Registry.register(Registries.BLOCK, id("stairs")        , new TemplateStairsBlock(cp(Blocks.OAK_STAIRS)));
	public static final Block TRAPDOOR       = Registry.register(Registries.BLOCK, id("trapdoor")      , new TemplateTrapdoorBlock(cp(Blocks.OAK_TRAPDOOR)));
	public static final Block WALL           = Registry.register(Registries.BLOCK, id("wall")          , new TemplateWallBlock(cp(Blocks.COBBLESTONE_WALL)));
	public static final Block SLOPE          = Registry.register(Registries.BLOCK, id("slope")         , new TemplateSlopeBlock(TemplateInteractionUtil.makeSettings()));
	//30 degree slope (shallow/deep) 
	//corner slopes
	//quarter slabs????
	
	//for addon devs: it's fine to make your own block entity type instead of gluing additional blocks to this one
	public static final BlockEntityType<TemplateEntity> TEMPLATE_BLOCK_ENTITY = Registry.register(
		Registries.BLOCK_ENTITY_TYPE, id("slope"),
		FabricBlockEntityTypeBuilder.create(Templates::makeTemplateBlockEntity,
			BUTTON,
			CANDLE,
			CARPET,
			CUBE,
			FENCE,
			FENCE_GATE,
			LEVER,
			PANE,
			POST,
			PRESSURE_PLATE,
			SLAB,
			STAIRS,
			TRAPDOOR,
			WALL,
			SLOPE
		).build(null)
	);
	
	//Overridden in TemplatesClient
	public static BiConsumer<World, BlockPos> chunkRerenderProxy = (world, pos) -> {};
	
	@Override
	public void onInitialize() {
		Registry.register(
			Registries.ITEM_GROUP, id("tab"),
			FabricItemGroup.builder()
				.displayName(Text.translatable("itemGroup.templates.tab"))
				.icon(() -> new ItemStack(SLOPE))
				.entries(this::fillCreativeTab)
				.build()
		);
		
		Registry.register(Registries.ITEM, id("button")        , new BlockItem(BUTTON, new Item.Settings()));
		Registry.register(Registries.ITEM, id("candle")        , new BlockItem(CANDLE, new Item.Settings()));
		Registry.register(Registries.ITEM, id("carpet")        , new BlockItem(CARPET, new Item.Settings()));
		Registry.register(Registries.ITEM, id("cube")          , new BlockItem(CUBE, new Item.Settings()));
		Registry.register(Registries.ITEM, id("fence")         , new BlockItem(FENCE, new Item.Settings()));
		Registry.register(Registries.ITEM, id("fence_gate")    , new BlockItem(FENCE_GATE, new Item.Settings()));
		Registry.register(Registries.ITEM, id("lever")         , new BlockItem(LEVER, new Item.Settings()));
		Registry.register(Registries.ITEM, id("pane")          , new BlockItem(PANE, new Item.Settings()));
		Registry.register(Registries.ITEM, id("post")          , new BlockItem(POST, new Item.Settings()));
		Registry.register(Registries.ITEM, id("pressure_plate"), new BlockItem(PRESSURE_PLATE, new Item.Settings()));
		Registry.register(Registries.ITEM, id("slab")          , new BlockItem(SLAB, new Item.Settings()));
		Registry.register(Registries.ITEM, id("stairs")        , new BlockItem(STAIRS, new Item.Settings()));
		Registry.register(Registries.ITEM, id("trapdoor")      , new BlockItem(TRAPDOOR, new Item.Settings()));
		Registry.register(Registries.ITEM, id("wall")          , new BlockItem(WALL, new Item.Settings()));
		Registry.register(Registries.ITEM, id("slope")         , new BlockItem(SLOPE, new Item.Settings()));
	}
	
	public static Identifier id(String path) {
		return new Identifier(MODID, path);
	}
	
	//simply for breaking circular reference in the registration call
	private static TemplateEntity makeTemplateBlockEntity(BlockPos pos, BlockState state) {
		return new TemplateEntity(TEMPLATE_BLOCK_ENTITY, pos, state);
	}
	
	private void fillCreativeTab(ItemGroup.DisplayContext ctx, ItemGroup.Entries e) {
		//sorted by encounter order of the vanilla block in the "search" creative tab
		//with the non-vanilla "post" inserted of course
		//and i moved the lever next to the pressureplate and button cause theyre redstoney
		e.add(CUBE);
		e.add(STAIRS);
		e.add(SLAB);
		e.add(POST);
		e.add(FENCE);
		e.add(FENCE_GATE);
		// <-- insert door here
		e.add(TRAPDOOR);
		e.add(PRESSURE_PLATE);
		e.add(BUTTON);
		e.add(LEVER);
		e.add(WALL);
		e.add(CARPET);
		e.add(PANE);
		e.add(CANDLE);
	}
}
