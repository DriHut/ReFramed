package fr.adrien1106.reframed.client;

import fr.adrien1106.reframed.ReFramed;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkSectionPos;

public class ReFramedClient implements ClientModInitializer {
	public static final ReFramedModelProvider PROVIDER = new ReFramedModelProvider();

	public static final ReFramedClientHelper HELPER = new ReFramedClientHelper(PROVIDER);
	
	@Override
	public void onInitializeClient() {
		privateInit(); //<- Stuff you shouldn't replicate in any addon mods ;)
		
		//all frames mustn't be on the SOLID layer because they are not opaque!
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), ReFramed.BLOCKS.toArray(new Block[0]));
		
		HELPER.addReFramedModel(ReFramed.id("button_special")               , HELPER.auto(new Identifier("block/button")));
		HELPER.addReFramedModel(ReFramed.id("button_pressed_special")       , HELPER.auto(new Identifier("block/button_pressed")));
		HELPER.addReFramedModel(ReFramed.id("one_candle_special")           , HELPER.auto(new Identifier("block/template_candle")));
		HELPER.addReFramedModel(ReFramed.id("two_candles_special")          , HELPER.auto(new Identifier("block/template_two_candles")));
		HELPER.addReFramedModel(ReFramed.id("three_candles_special")        , HELPER.auto(new Identifier("block/template_three_candles")));
		HELPER.addReFramedModel(ReFramed.id("four_candles_special")         , HELPER.auto(new Identifier("block/template_four_candles")));
		HELPER.addReFramedModel(ReFramed.id("carpet_special")               , HELPER.auto(new Identifier("block/carpet")));
		HELPER.addReFramedModel(ReFramed.id("cube_special")                 , HELPER.auto(new Identifier("block/cube")));
		HELPER.addReFramedModel(ReFramed.id("door_bottom_left_special")     , HELPER.auto(new Identifier("block/door_bottom_left")));
		HELPER.addReFramedModel(ReFramed.id("door_bottom_right_special")    , HELPER.auto(new Identifier("block/door_bottom_right")));
		HELPER.addReFramedModel(ReFramed.id("door_top_left_special")        , HELPER.auto(new Identifier("block/door_top_left")));
		HELPER.addReFramedModel(ReFramed.id("door_top_right_special")       , HELPER.auto(new Identifier("block/door_top_right")));
		HELPER.addReFramedModel(ReFramed.id("door_bottom_left_open_special"), HELPER.auto(new Identifier("block/door_bottom_left_open")));
		HELPER.addReFramedModel(ReFramed.id("door_bottom_right_open_special"), HELPER.auto(new Identifier("block/door_bottom_right_open"))); //This is why we dont format code as tables kids
		HELPER.addReFramedModel(ReFramed.id("door_top_left_open_special")   , HELPER.auto(new Identifier("block/door_top_left_open")));
		HELPER.addReFramedModel(ReFramed.id("door_top_right_open_special")  , HELPER.auto(new Identifier("block/door_top_right_open")));
		HELPER.addReFramedModel(ReFramed.id("fence_post_special")           , HELPER.auto(new Identifier("block/fence_post")));
		HELPER.addReFramedModel(ReFramed.id("fence_gate_special")           , HELPER.auto(new Identifier("block/template_fence_gate")));
		HELPER.addReFramedModel(ReFramed.id("fence_gate_open_special")      , HELPER.auto(new Identifier("block/template_fence_gate_open")));
		HELPER.addReFramedModel(ReFramed.id("fence_gate_wall_special")      , HELPER.auto(new Identifier("block/template_fence_gate_wall")));
		HELPER.addReFramedModel(ReFramed.id("fence_gate_wall_open_special") , HELPER.auto(new Identifier("block/template_fence_gate_wall_open")));
		HELPER.addReFramedModel(ReFramed.id("glass_pane_post_special")      , HELPER.auto(new Identifier("block/glass_pane_post")));
		HELPER.addReFramedModel(ReFramed.id("glass_pane_noside_special")    , HELPER.auto(new Identifier("block/glass_pane_noside")));
		HELPER.addReFramedModel(ReFramed.id("glass_pane_noside_alt_special"), HELPER.auto(new Identifier("block/glass_pane_noside_alt")));
		HELPER.addReFramedModel(ReFramed.id("pressure_plate_up_special")    , HELPER.auto(new Identifier("block/pressure_plate_up")));
		HELPER.addReFramedModel(ReFramed.id("pressure_plate_down_special")  , HELPER.auto(new Identifier("block/pressure_plate_down")));
		HELPER.addReFramedModel(ReFramed.id("slab_special")                 , HELPER.auto(new Identifier("block/slab")));
		HELPER.addReFramedModel(ReFramed.id("double_slab_special")          , HELPER.autoDouble(new Identifier("block/slab"), new Identifier("block/slab_top")));
		HELPER.addReFramedModel(ReFramed.id("stairs_special")               , HELPER.auto(ReFramed.id("block/stairs")));
		HELPER.addReFramedModel(ReFramed.id("double_outer_stairs_special")  , HELPER.auto(ReFramed.id("block/double_outer_stairs")));
		HELPER.addReFramedModel(ReFramed.id("inner_stairs_special")         , HELPER.auto(ReFramed.id("block/inner_stairs")));
		HELPER.addReFramedModel(ReFramed.id("outer_stairs_special")         , HELPER.auto(ReFramed.id("block/outer_stairs")));
		HELPER.addReFramedModel(ReFramed.id("outer_side_stairs_special")    , HELPER.auto(ReFramed.id("block/outer_side_stairs")));
		HELPER.addReFramedModel(ReFramed.id("trapdoor_bottom_special")      , HELPER.auto(new Identifier("block/template_trapdoor_bottom")));
		HELPER.addReFramedModel(ReFramed.id("trapdoor_top_special")         , HELPER.auto(new Identifier("block/template_trapdoor_top")));
		HELPER.addReFramedModel(ReFramed.id("wall_post_special")            , HELPER.auto(new Identifier("block/template_wall_post")));
		
		//vanilla style models (using "special-sprite replacement" method)
		HELPER.addReFramedModel(ReFramed.id("lever_special")                , HELPER.json(ReFramed.id("block/lever")));
		HELPER.addReFramedModel(ReFramed.id("trapdoor_open_special")        , HELPER.json(ReFramed.id("block/trapdoor_open")));
		HELPER.addReFramedModel(ReFramed.id("lever_on_special")             , HELPER.json(ReFramed.id("block/lever_on")));
		//these next five only exist because AutoRetexturedModels don't seem to rotate their textures the right way when rotated from a multipart blockstate
		HELPER.addReFramedModel(ReFramed.id("fence_side_special")           , HELPER.json(ReFramed.id("block/fence_side")));
		HELPER.addReFramedModel(ReFramed.id("glass_pane_side_special")      , HELPER.json(ReFramed.id("block/glass_pane_side")));
		HELPER.addReFramedModel(ReFramed.id("glass_pane_side_alt_special")  , HELPER.json(ReFramed.id("block/glass_pane_side_alt")));
		HELPER.addReFramedModel(ReFramed.id("wall_side_special")            , HELPER.json(ReFramed.id("block/wall_side")));
		HELPER.addReFramedModel(ReFramed.id("wall_side_tall_special")       , HELPER.json(ReFramed.id("block/wall_side_tall")));
		
		//item only models
		HELPER.addReFramedModel(ReFramed.id("button_inventory_special")     , HELPER.auto(new Identifier("block/button_inventory")));
		HELPER.addReFramedModel(ReFramed.id("fence_inventory_special")      , HELPER.auto(new Identifier("block/fence_inventory")));
		HELPER.addReFramedModel(ReFramed.id("fence_post_inventory_special") , HELPER.auto(ReFramed.id("block/fence_post_inventory")));
		HELPER.addReFramedModel(ReFramed.id("wall_inventory_special")       , HELPER.auto(new Identifier("block/wall_inventory")));
		
		//item model assignments (in lieu of models/item/___.json)
		HELPER.assignItemModel(ReFramed.id("button_inventory_special")      , ReFramed.BUTTON);
		HELPER.assignItemModel(ReFramed.id("carpet_special")                , ReFramed.CARPET);
		HELPER.assignItemModel(ReFramed.id("cube_special")                  , ReFramed.CUBE);
		HELPER.assignItemModel(ReFramed.id("fence_inventory_special")       , ReFramed.FENCE);
		HELPER.assignItemModel(ReFramed.id("fence_gate_special")            , ReFramed.FENCE_GATE);
		HELPER.assignItemModel(ReFramed.id("trapdoor_bottom_special")       , ReFramed.IRON_TRAPDOOR);
		HELPER.assignItemModel(ReFramed.id("fence_post_inventory_special")  , ReFramed.POST);
		HELPER.assignItemModel(ReFramed.id("pressure_plate_up_special")     , ReFramed.PRESSURE_PLATE);
		HELPER.assignItemModel(ReFramed.id("slab_special")                  , ReFramed.SLAB);
		HELPER.assignItemModel(ReFramed.id("double_slab_special")           , ReFramed.DOUBLE_SLAB);
		HELPER.assignItemModel(ReFramed.id("stairs_special")                , ReFramed.STAIRS);
		HELPER.assignItemModel(ReFramed.id("trapdoor_bottom_special")       , ReFramed.TRAPDOOR);
		HELPER.assignItemModel(ReFramed.id("wall_inventory_special")        , ReFramed.WALL);
	}
	
	private void privateInit() {
		//set up some magic to force chunk rerenders when you change a template (see TemplateEntity)
		ReFramed.chunkRerenderProxy = (world, pos) -> {
			if(world == MinecraftClient.getInstance().world) {
				MinecraftClient.getInstance().worldRenderer.scheduleBlockRender(
					ChunkSectionPos.getSectionCoord(pos.getX()),
					ChunkSectionPos.getSectionCoord(pos.getY()),
					ChunkSectionPos.getSectionCoord(pos.getZ())
				);
			}
		};
		
		//supporting code for the TemplatesModelProvider
		ModelLoadingRegistry.INSTANCE.registerResourceProvider(rm -> PROVIDER); //block models
		ModelLoadingRegistry.INSTANCE.registerVariantProvider(rm -> PROVIDER); //item models
		
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
			@Override public Identifier getFabricId() { return ReFramed.id("dump-caches"); }
			@Override public void reload(ResourceManager blah) { PROVIDER.dumpCache(); }
		});
	}
}
