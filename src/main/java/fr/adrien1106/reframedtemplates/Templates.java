package fr.adrien1106.reframedtemplates;

import fr.adrien1106.reframedtemplates.api.TemplateInteractionUtil;
import fr.adrien1106.reframedtemplates.block.*;
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
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * TODO handle random textures rotation and mirroring wip, handle grass side, multiple camos
 */
public class Templates implements ModInitializer {
	public static final String MODID = "reframedtemplates";
	
	//addon devs: *Don't* add your blocks to this collection, it's just for my registration convenience since Templates adds a lot of blocks...
	@ApiStatus.Internal static final ArrayList<Block> INTERNAL_TEMPLATES = new ArrayList<>();
	@ApiStatus.Internal static Block CUBE, STAIRS, SLAB, VERTICAL_SLAB, POST, FENCE, FENCE_GATE, DOOR, TRAPDOOR, IRON_DOOR, IRON_TRAPDOOR, PRESSURE_PLATE, BUTTON, LEVER, WALL, CARPET, PANE, CANDLE, SLOPE, TINY_SLOPE;
	
	//For addon devs: Please don't stuff more blocks into this BlockEntityType, and register your own.
	//You can even re-register the same TemplateEntity class under your own ID if you like. (It's an extensible block entity.)
	@ApiStatus.Internal public static BlockEntityType<TemplateEntity> TEMPLATE_BLOCK_ENTITY;
	
	//Changed in TemplatesClient (which is safe since client initializers load after common initializers)
	@ApiStatus.Internal public static BiConsumer<World, BlockPos> chunkRerenderProxy = (world, pos) -> {};
	
	@Override
	public void onInitialize() {
		//registerTemplate mutates MY_TEMPLATES as a side effect, which is a List, so order is preserved
		//the ordering is used in the creative tab, so they're roughly sorted by encounter order of the
		//corresponding vanilla block in the "search" creative tab... with the non-vanilla "post" and
		//"vertical slab" inserted where they fit ...and i moved the lever way up next to the pressureplate
		//and button, because they're redstoney... hopefully this ordering makes sense lol
		CUBE           = registerTemplate("cube"          , new TemplateBlock(TemplateInteractionUtil.makeSettings()));
		STAIRS         = registerTemplate("stairs"        , new TemplateStairsBlock(cp(Blocks.OAK_STAIRS)));
		SLAB           = registerTemplate("slab"          , new TemplateSlabBlock(cp(Blocks.OAK_SLAB)));
		VERTICAL_SLAB  = registerTemplate("vertical_slab" , new TemplateVerticalSlabBlock(cp(Blocks.OAK_SLAB)));
		POST           = registerTemplate("post"          , new TemplatePostBlock(cp(Blocks.OAK_FENCE)));
		FENCE          = registerTemplate("fence"         , new TemplateFenceBlock(cp(Blocks.OAK_FENCE)));
		FENCE_GATE     = registerTemplate("fence_gate"    , new TemplateFenceGateBlock(cp(Blocks.OAK_FENCE_GATE)));
		DOOR           = registerTemplate("door"          , new TemplateDoorBlock(cp(Blocks.OAK_DOOR), BlockSetType.OAK));
		TRAPDOOR       = registerTemplate("trapdoor"      , new TemplateTrapdoorBlock(cp(Blocks.OAK_TRAPDOOR), BlockSetType.OAK));
		IRON_DOOR      = registerTemplate("iron_door"     , new TemplateDoorBlock(cp(Blocks.IRON_DOOR), BlockSetType.IRON));
		IRON_TRAPDOOR  = registerTemplate("iron_trapdoor" , new TemplateTrapdoorBlock(cp(Blocks.IRON_TRAPDOOR), BlockSetType.IRON));
		PRESSURE_PLATE = registerTemplate("pressure_plate", new TemplatePressurePlateBlock(cp(Blocks.OAK_PRESSURE_PLATE)));
		BUTTON         = registerTemplate("button"        , new TemplateButtonBlock(cp(Blocks.OAK_BUTTON)));
		LEVER          = registerTemplate("lever"         , new TemplateLeverBlock(cp(Blocks.LEVER)));
		WALL           = registerTemplate("wall"          , new TemplateWallBlock(TemplateInteractionUtil.makeSettings()));
		CARPET         = registerTemplate("carpet"        , new TemplateCarpetBlock(cp(Blocks.WHITE_CARPET)));
		PANE           = registerTemplate("pane"          , new TemplatePaneBlock(cp(Blocks.GLASS_PANE)));
		CANDLE         = registerTemplate("candle"        , new TemplateCandleBlock(TemplateCandleBlock.configureSettings(cp(Blocks.CANDLE))));
		SLOPE          = registerTemplate("slope"         , new TemplateSlopeBlock(TemplateInteractionUtil.makeSettings()));
		TINY_SLOPE     = registerTemplate("tiny_slope"    , new TemplateSlopeBlock.Tiny(TemplateInteractionUtil.makeSettings()));
		
		//The block entity is still called templates:slope; this is a bit of a legacy mistake.
		TEMPLATE_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, id("slope"),
			FabricBlockEntityTypeBuilder.create((pos, state) -> new TemplateEntity(TEMPLATE_BLOCK_ENTITY, pos, state), INTERNAL_TEMPLATES.toArray(new Block[0])).build(null)
		);
		
		Registry.register(Registries.ITEM_GROUP, id("tab"), FabricItemGroup.builder()
			.displayName(Text.translatable("itemGroup.reframedtemplates.tab"))
			.icon(() -> new ItemStack(SLOPE))
			.entries((ctx, e) -> {
				e.addAll(INTERNAL_TEMPLATES.stream().map(ItemStack::new).collect(Collectors.toList()));
			}).build()
		);
	}
	
	//purely to shorten this call :p
	private static AbstractBlock.Settings cp(Block base) {
		return TemplateInteractionUtil.configureSettings(AbstractBlock.Settings.copy(base));
	}
	
	private static <B extends Block> B registerTemplate(String path, B block) {
		Identifier id = id(path);
		
		Registry.register(Registries.BLOCK, id, block);
		Registry.register(Registries.ITEM, id, new BlockItem(block, new Item.Settings()));
		INTERNAL_TEMPLATES.add(block);
		return block;
	}
	
	@ApiStatus.Internal
	public static Identifier id(String path) {
		return new Identifier(MODID, path);
	}
}
