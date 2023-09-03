package io.github.cottonmc.templates;

import io.github.cottonmc.templates.api.TemplatesClientApi;
import io.github.cottonmc.templates.model.SlopeBaseMesh;
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
import org.jetbrains.annotations.ApiStatus;

public class TemplatesClient implements ClientModInitializer {
	@ApiStatus.Internal //2.2 - Please use the new TemplatesClientApi.getInstance() method.
	public static final TemplatesModelProvider provider = new TemplatesModelProvider();
	
	@ApiStatus.Internal //Please use TemplatesClientApi.getInstance() instead.
	public static final TemplatesClientApiImpl API_IMPL = new TemplatesClientApiImpl(provider);
	
	@Override
	public void onInitializeClient() {
		privateInit(); //<- Stuff you shouldn't replicate in any addon mods ;)
		
		//all templates mustn't be on the SOLID layer because they are not opaque!
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), Templates.INTERNAL_TEMPLATES.toArray(new Block[0]));
		
		//now, assign special item models
		TemplatesClientApi api = TemplatesClientApi.getInstance();
		
		api.addTemplateModel(Templates.id("button_special")               , api.auto(new Identifier("block/button")));
		api.addTemplateModel(Templates.id("button_pressed_special")       , api.auto(new Identifier("block/button_pressed")));
		api.addTemplateModel(Templates.id("one_candle_special")           , api.auto(new Identifier("block/template_candle")));
		api.addTemplateModel(Templates.id("two_candles_special")          , api.auto(new Identifier("block/template_two_candles")));
		api.addTemplateModel(Templates.id("three_candles_special")        , api.auto(new Identifier("block/template_three_candles")));
		api.addTemplateModel(Templates.id("four_candles_special")         , api.auto(new Identifier("block/template_four_candles")));
		api.addTemplateModel(Templates.id("carpet_special")               , api.auto(new Identifier("block/carpet")));
		api.addTemplateModel(Templates.id("cube_special")                 , api.auto(new Identifier("block/cube")));
		api.addTemplateModel(Templates.id("door_bottom_left_special")     , api.auto(new Identifier("block/door_bottom_left")));
		api.addTemplateModel(Templates.id("door_bottom_right_special")    , api.auto(new Identifier("block/door_bottom_right")));
		api.addTemplateModel(Templates.id("door_top_left_special")        , api.auto(new Identifier("block/door_top_left")));
		api.addTemplateModel(Templates.id("door_top_right_special")       , api.auto(new Identifier("block/door_top_right")));
		api.addTemplateModel(Templates.id("door_bottom_left_open_special"), api.auto(new Identifier("block/door_bottom_left_open")));
		api.addTemplateModel(Templates.id("door_bottom_right_open_special"), api.auto(new Identifier("block/door_bottom_right_open"))); //This is why we dont format code as tables kids
		api.addTemplateModel(Templates.id("door_top_left_open_special")   , api.auto(new Identifier("block/door_top_left_open")));
		api.addTemplateModel(Templates.id("door_top_right_open_special")  , api.auto(new Identifier("block/door_top_right_open")));
		api.addTemplateModel(Templates.id("fence_post_special")           , api.auto(new Identifier("block/fence_post")));
		api.addTemplateModel(Templates.id("fence_gate_special")           , api.auto(new Identifier("block/template_fence_gate")));
		api.addTemplateModel(Templates.id("fence_gate_open_special")      , api.auto(new Identifier("block/template_fence_gate_open")));
		api.addTemplateModel(Templates.id("fence_gate_wall_special")      , api.auto(new Identifier("block/template_fence_gate_wall")));
		api.addTemplateModel(Templates.id("fence_gate_wall_open_special") , api.auto(new Identifier("block/template_fence_gate_wall_open")));
		api.addTemplateModel(Templates.id("glass_pane_post_special")      , api.auto(new Identifier("block/glass_pane_post")));
		api.addTemplateModel(Templates.id("glass_pane_noside_special")    , api.auto(new Identifier("block/glass_pane_noside")));
		api.addTemplateModel(Templates.id("glass_pane_noside_alt_special"), api.auto(new Identifier("block/glass_pane_noside_alt")));
		api.addTemplateModel(Templates.id("pressure_plate_up_special")    , api.auto(new Identifier("block/pressure_plate_up")));
		api.addTemplateModel(Templates.id("pressure_plate_down_special")  , api.auto(new Identifier("block/pressure_plate_down")));
		api.addTemplateModel(Templates.id("slab_bottom_special")          , api.auto(new Identifier("block/slab")));
		api.addTemplateModel(Templates.id("slab_top_special")             , api.auto(new Identifier("block/slab_top")));
		api.addTemplateModel(Templates.id("stairs_special")               , api.auto(new Identifier("block/stairs")));
		api.addTemplateModel(Templates.id("inner_stairs_special")         , api.auto(new Identifier("block/inner_stairs")));
		api.addTemplateModel(Templates.id("outer_stairs_special")         , api.auto(new Identifier("block/outer_stairs")));
		api.addTemplateModel(Templates.id("trapdoor_bottom_special")      , api.auto(new Identifier("block/template_trapdoor_bottom")));
		api.addTemplateModel(Templates.id("trapdoor_top_special")         , api.auto(new Identifier("block/template_trapdoor_top")));
		api.addTemplateModel(Templates.id("vertical_slab_special")        , api.auto(Templates.id("block/vertical_slab"))); //my model not vanilla
		api.addTemplateModel(Templates.id("wall_post_special")            , api.auto(new Identifier("block/template_wall_post")));
		
		//vanilla style models (using "special-sprite replacement" method)
		api.addTemplateModel(Templates.id("lever_special")                , api.json(Templates.id("block/lever")));
		api.addTemplateModel(Templates.id("trapdoor_open_special")        , api.json(Templates.id("block/trapdoor_open")));
		api.addTemplateModel(Templates.id("lever_on_special")             , api.json(Templates.id("block/lever_on")));
		//these next five only exist because AutoRetexturedModels don't seem to rotate their textures the right way when rotated from a multipart blockstate
		api.addTemplateModel(Templates.id("fence_side_special")           , api.json(Templates.id("block/fence_side")));
		api.addTemplateModel(Templates.id("glass_pane_side_special")      , api.json(Templates.id("block/glass_pane_side")));
		api.addTemplateModel(Templates.id("glass_pane_side_alt_special")  , api.json(Templates.id("block/glass_pane_side_alt")));
		api.addTemplateModel(Templates.id("wall_side_special")            , api.json(Templates.id("block/wall_side")));
		api.addTemplateModel(Templates.id("wall_side_tall_special")       , api.json(Templates.id("block/wall_side_tall")));
		
		//mesh models
		api.addTemplateModel(Templates.id("slope_special")                , api.mesh(Templates.id("block/slope_base"), SlopeBaseMesh::makeUpright).disableAo());
		api.addTemplateModel(Templates.id("slope_side_special")           , api.mesh(Templates.id("block/slope_base"), SlopeBaseMesh::makeSide).disableAo());
		api.addTemplateModel(Templates.id("tiny_slope_special")           , api.mesh(Templates.id("block/tiny_slope_base"), SlopeBaseMesh::makeTinyUpright).disableAo());
		api.addTemplateModel(Templates.id("tiny_slope_side_special")      , api.mesh(Templates.id("block/tiny_slope_base"), SlopeBaseMesh::makeTinySide).disableAo());
		
		//item only models
		api.addTemplateModel(Templates.id("button_inventory_special")     , api.auto(new Identifier("block/button_inventory")));
		api.addTemplateModel(Templates.id("fence_inventory_special")      , api.auto(new Identifier("block/fence_inventory")));
		api.addTemplateModel(Templates.id("fence_post_inventory_special") , api.auto(Templates.id("block/fence_post_inventory")));
		api.addTemplateModel(Templates.id("wall_inventory_special")       , api.auto(new Identifier("block/wall_inventory")));
		
		//item model assignments (in lieu of models/item/___.json)
		api.assignItemModel(Templates.id("button_inventory_special")      , Templates.BUTTON);
		api.assignItemModel(Templates.id("carpet_special")                , Templates.CARPET);
		api.assignItemModel(Templates.id("cube_special")                  , Templates.CUBE);
		api.assignItemModel(Templates.id("fence_inventory_special")       , Templates.FENCE);
		api.assignItemModel(Templates.id("fence_gate_special")            , Templates.FENCE_GATE);
		api.assignItemModel(Templates.id("trapdoor_bottom_special")       , Templates.IRON_TRAPDOOR);
		api.assignItemModel(Templates.id("fence_post_inventory_special")  , Templates.POST);
		api.assignItemModel(Templates.id("pressure_plate_up_special")     , Templates.PRESSURE_PLATE);
		api.assignItemModel(Templates.id("slab_bottom_special")           , Templates.SLAB);
		api.assignItemModel(Templates.id("stairs_special")                , Templates.STAIRS);
		api.assignItemModel(Templates.id("trapdoor_bottom_special")       , Templates.TRAPDOOR);
		api.assignItemModel(Templates.id("vertical_slab_special")         , Templates.VERTICAL_SLAB);
		api.assignItemModel(Templates.id("wall_inventory_special")        , Templates.WALL);
		api.assignItemModel(Templates.id("slope_special")                 , Templates.SLOPE);
		api.assignItemModel(Templates.id("tiny_slope_special")            , Templates.TINY_SLOPE);
		
		//TODO: i could stick some kind of entrypoint here for signalling other mods that it's ok to register now?
		// Dont think it rly matters though, everything's all kept in nice hash maps
	}
	
	private void privateInit() {
		//set up some magic to force chunk rerenders when you change a template (see TemplateEntity)
		Templates.chunkRerenderProxy = (world, pos) -> {
			if(world == MinecraftClient.getInstance().world) {
				MinecraftClient.getInstance().worldRenderer.scheduleBlockRender(
					ChunkSectionPos.getSectionCoord(pos.getX()),
					ChunkSectionPos.getSectionCoord(pos.getY()),
					ChunkSectionPos.getSectionCoord(pos.getZ())
				);
			}
		};
		
		//supporting code for the TemplatesModelProvider
		ModelLoadingRegistry.INSTANCE.registerResourceProvider(rm -> provider); //block models
		ModelLoadingRegistry.INSTANCE.registerVariantProvider(rm -> provider); //item models
		
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
			@Override public Identifier getFabricId() { return Templates.id("dump-caches"); }
			@Override public void reload(ResourceManager blah) { provider.dumpCache(); }
		});
	}
}
