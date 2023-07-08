package io.github.cottonmc.templates;

import io.github.cottonmc.templates.api.TemplateInteractionUtil;
import io.github.cottonmc.templates.block.TemplateBlock;
import io.github.cottonmc.templates.block.TemplateButtonBlock;
import io.github.cottonmc.templates.block.TemplateCarpetBlock;
import io.github.cottonmc.templates.block.TemplateFenceBlock;
import io.github.cottonmc.templates.block.TemplateFenceGateBlock;
import io.github.cottonmc.templates.block.TemplateLeverBlock;
import io.github.cottonmc.templates.block.TemplatePostBlock;
import io.github.cottonmc.templates.block.TemplatePressurePlateBlock;
import io.github.cottonmc.templates.block.TemplateSlabBlock;
import io.github.cottonmc.templates.block.TemplateSlopeBlock;
import io.github.cottonmc.templates.block.TemplateEntity;
import io.github.cottonmc.templates.block.TemplateWallBlock;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class Templates implements ModInitializer {
	public static final String MODID = "templates";
	
	public static final List<Block> BLOCKS = new ArrayList<>();
	private static <B extends Block> B reg(String id, B block) {
		B b = Registry.register(Registries.BLOCK, id(id), block);
		BLOCKS.add(b);
		return b;
	}
	
	public static final Block BUTTON = reg("button", new TemplateButtonBlock(TemplateInteractionUtil.makeSettings().pistonBehavior(PistonBehavior.DESTROY)));
	//candle? (lol)
	public static final Block CARPET = reg("carpet", new TemplateCarpetBlock(TemplateInteractionUtil.makeSettings().sounds(BlockSoundGroup.WOOL).pistonBehavior(PistonBehavior.DESTROY)));
	public static final Block CUBE = reg("cube", new TemplateBlock(TemplateInteractionUtil.makeSettings()));
	//door? (hard cause its a multiblock)
	public static final Block FENCE = reg("fence", new TemplateFenceBlock(TemplateInteractionUtil.makeSettings()));
	public static final Block FENCE_GATE = reg("fence_gate", new TemplateFenceGateBlock(TemplateInteractionUtil.makeSettings()));
	public static final Block LEVER = reg("lever", new TemplateLeverBlock(TemplateInteractionUtil.makeSettings().pistonBehavior(PistonBehavior.DESTROY)));
	//pane
	public static final Block POST = reg("post", new TemplatePostBlock(TemplateInteractionUtil.makeSettings()));
	public static final Block PRESSURE_PLATE = reg("pressure_plate", new TemplatePressurePlateBlock(TemplateInteractionUtil.makeSettings().noCollision().pistonBehavior(PistonBehavior.DESTROY)));
	public static final Block SLAB = reg("slab", new TemplateSlabBlock(TemplateInteractionUtil.makeSettings()));
	//stair
	//trapdoor
	public static final Block WALL = reg("wall", new TemplateWallBlock(TemplateInteractionUtil.makeSettings()));
	
	public static final Block SLOPE = reg("slope", new TemplateSlopeBlock(TemplateInteractionUtil.makeSettings()));
	//30 degree slope (shallow/deep)
	//60 degree slope
	//wall slopes
	//corner slopes
	//quarter slabs????
	
	//for addon devs: it's fine to make your own block entity type instead of gluing additional blocks to this one
	public static final BlockEntityType<TemplateEntity> TEMPLATE_BLOCK_ENTITY = Registry.register(
		Registries.BLOCK_ENTITY_TYPE, id("slope"),
		FabricBlockEntityTypeBuilder.create(Templates::makeTemplateBlockEntity, BLOCKS.toArray(new Block[0])).build(null)
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
				.entries((ctx, ent) -> ent.addAll(BLOCKS.stream().map(ItemStack::new).collect(Collectors.toList())))
				.build()
		);
		
		for(Block b : BLOCKS) {
			Identifier id = Registries.BLOCK.getId(b);
			Registry.register(Registries.ITEM, id, new BlockItem(b, new Item.Settings()));
		}
	}
	
	public static Identifier id(String path) {
		return new Identifier(MODID, path);
	}
	
	//simply for breaking circular reference in the registration call
	private static TemplateEntity makeTemplateBlockEntity(BlockPos pos, BlockState state) {
		return new TemplateEntity(TEMPLATE_BLOCK_ENTITY, pos, state);
	}
}
