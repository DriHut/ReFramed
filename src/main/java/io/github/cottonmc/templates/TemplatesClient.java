package io.github.cottonmc.templates;

import io.github.cottonmc.templates.model.SlopeBaseMesh;
import io.github.cottonmc.templates.model.UnbakedAutoRetexturedModel;
import io.github.cottonmc.templates.model.UnbakedJsonRetexturedModel;
import io.github.cottonmc.templates.model.UnbakedMeshRetexturedModel;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkSectionPos;
import org.jetbrains.annotations.NotNull;

public class TemplatesClient implements ClientModInitializer {
	//2.2 note: Yes, this wasn't final before, but it should have been.
	//This field is considered public api by the way, feel free to use it instead of making your own copy.
	public static final TemplatesModelProvider provider = new TemplatesModelProvider();
	
	@Override
	public void onInitializeClient() {
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
		
		//all templates mustn't be on the SOLID layer because they are not opaque by default (!)
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), Templates.INTERNAL_TEMPLATES.toArray(new Block[0]));
		
		//and a big wall of fancy models
		provider.addTemplateModel(Templates.id("button_special")               , new UnbakedAutoRetexturedModel(new Identifier("block/button")));
		provider.addTemplateModel(Templates.id("button_pressed_special")       , new UnbakedAutoRetexturedModel(new Identifier("block/button_pressed")));
		provider.addTemplateModel(Templates.id("one_candle_special")           , new UnbakedAutoRetexturedModel(new Identifier("block/template_candle")));
		provider.addTemplateModel(Templates.id("two_candles_special")          , new UnbakedAutoRetexturedModel(new Identifier("block/template_two_candles")));
		provider.addTemplateModel(Templates.id("three_candles_special")        , new UnbakedAutoRetexturedModel(new Identifier("block/template_three_candles")));
		provider.addTemplateModel(Templates.id("four_candles_special")         , new UnbakedAutoRetexturedModel(new Identifier("block/template_four_candles")));
		provider.addTemplateModel(Templates.id("carpet_special")               , new UnbakedAutoRetexturedModel(new Identifier("block/carpet")));
		provider.addTemplateModel(Templates.id("cube_special")                 , new UnbakedAutoRetexturedModel(new Identifier("block/cube")));
		provider.addTemplateModel(Templates.id("door_bottom_left_special")     , new UnbakedAutoRetexturedModel(new Identifier("block/door_bottom_left")));
		provider.addTemplateModel(Templates.id("door_bottom_right_special")    , new UnbakedAutoRetexturedModel(new Identifier("block/door_bottom_right")));
		provider.addTemplateModel(Templates.id("door_top_left_special")        , new UnbakedAutoRetexturedModel(new Identifier("block/door_top_left")));
		provider.addTemplateModel(Templates.id("door_top_right_special")       , new UnbakedAutoRetexturedModel(new Identifier("block/door_top_right")));
		provider.addTemplateModel(Templates.id("door_bottom_left_open_special"), new UnbakedAutoRetexturedModel(new Identifier("block/door_bottom_left_open")));
		provider.addTemplateModel(Templates.id("door_bottom_right_open_special"), new UnbakedAutoRetexturedModel(new Identifier("block/door_bottom_right_open"))); //This is why we dont format code as tables kids
		provider.addTemplateModel(Templates.id("door_top_left_open_special")   , new UnbakedAutoRetexturedModel(new Identifier("block/door_top_left_open")));
		provider.addTemplateModel(Templates.id("door_top_right_open_special")  , new UnbakedAutoRetexturedModel(new Identifier("block/door_top_right_open")));
		provider.addTemplateModel(Templates.id("fence_post_special")           , new UnbakedAutoRetexturedModel(new Identifier("block/fence_post")));
		provider.addTemplateModel(Templates.id("fence_gate_special")           , new UnbakedAutoRetexturedModel(new Identifier("block/template_fence_gate")));
		provider.addTemplateModel(Templates.id("fence_gate_open_special")      , new UnbakedAutoRetexturedModel(new Identifier("block/template_fence_gate_open")));
		provider.addTemplateModel(Templates.id("fence_gate_wall_special")      , new UnbakedAutoRetexturedModel(new Identifier("block/template_fence_gate_wall")));
		provider.addTemplateModel(Templates.id("fence_gate_wall_open_special") , new UnbakedAutoRetexturedModel(new Identifier("block/template_fence_gate_wall_open")));
		provider.addTemplateModel(Templates.id("glass_pane_post_special")      , new UnbakedAutoRetexturedModel(new Identifier("block/glass_pane_post")));
		provider.addTemplateModel(Templates.id("glass_pane_noside_special")    , new UnbakedAutoRetexturedModel(new Identifier("block/glass_pane_noside")));
		provider.addTemplateModel(Templates.id("glass_pane_noside_alt_special"), new UnbakedAutoRetexturedModel(new Identifier("block/glass_pane_noside_alt")));
		provider.addTemplateModel(Templates.id("pressure_plate_up_special")    , new UnbakedAutoRetexturedModel(new Identifier("block/pressure_plate_up")));
		provider.addTemplateModel(Templates.id("pressure_plate_down_special")  , new UnbakedAutoRetexturedModel(new Identifier("block/pressure_plate_down")));
		provider.addTemplateModel(Templates.id("slab_bottom_special")          , new UnbakedAutoRetexturedModel(new Identifier("block/slab")));
		provider.addTemplateModel(Templates.id("slab_top_special")             , new UnbakedAutoRetexturedModel(new Identifier("block/slab_top")));
		provider.addTemplateModel(Templates.id("stairs_special")               , new UnbakedAutoRetexturedModel(new Identifier("block/stairs")));
		provider.addTemplateModel(Templates.id("inner_stairs_special")         , new UnbakedAutoRetexturedModel(new Identifier("block/inner_stairs")));
		provider.addTemplateModel(Templates.id("outer_stairs_special")         , new UnbakedAutoRetexturedModel(new Identifier("block/outer_stairs")));
		provider.addTemplateModel(Templates.id("trapdoor_bottom_special")      , new UnbakedAutoRetexturedModel(new Identifier("block/template_trapdoor_bottom")));
		provider.addTemplateModel(Templates.id("trapdoor_top_special")         , new UnbakedAutoRetexturedModel(new Identifier("block/template_trapdoor_top")));
		provider.addTemplateModel(Templates.id("vertical_slab_special")        , new UnbakedAutoRetexturedModel(Templates.id("block/vertical_slab"))); //my model not vanilla
		provider.addTemplateModel(Templates.id("wall_post_special")            , new UnbakedAutoRetexturedModel(new Identifier("block/template_wall_post")));
		
		//vanilla style models (using "special-sprite replacement" method)
		provider.addTemplateModel(Templates.id("lever_special")                , new UnbakedJsonRetexturedModel(Templates.id("block/lever")));
		provider.addTemplateModel(Templates.id("trapdoor_open_special")        , new UnbakedJsonRetexturedModel(Templates.id("block/trapdoor_open")));
		provider.addTemplateModel(Templates.id("lever_on_special")             , new UnbakedJsonRetexturedModel(Templates.id("block/lever_on")));
		//these only exist because AutoRetexturedModels don't seem to rotate their textures the right way when rotated from a multipart blockstate
		provider.addTemplateModel(Templates.id("fence_side_special")           , new UnbakedJsonRetexturedModel(Templates.id("block/fence_side")));
		provider.addTemplateModel(Templates.id("glass_pane_side_special")      , new UnbakedJsonRetexturedModel(Templates.id("block/glass_pane_side")));
		provider.addTemplateModel(Templates.id("glass_pane_side_alt_special")  , new UnbakedAutoRetexturedModel(Templates.id("block/glass_pane_side_alt")));
		provider.addTemplateModel(Templates.id("wall_side_special")            , new UnbakedJsonRetexturedModel(Templates.id("block/wall_side")));
		provider.addTemplateModel(Templates.id("wall_side_tall_special")       , new UnbakedJsonRetexturedModel(Templates.id("block/wall_side_tall")));
		
		//mesh models
		provider.addTemplateModel(Templates.id("slope_special")                , new UnbakedMeshRetexturedModel(Templates.id("block/slope_base"), SlopeBaseMesh::makeUpright).disableAo());
		provider.addTemplateModel(Templates.id("slope_side_special")           , new UnbakedMeshRetexturedModel(Templates.id("block/slope_base"), SlopeBaseMesh::makeSide).disableAo());
		provider.addTemplateModel(Templates.id("tiny_slope_special")           , new UnbakedMeshRetexturedModel(Templates.id("block/tiny_slope_base"), SlopeBaseMesh::makeTinyUpright).disableAo());
		provider.addTemplateModel(Templates.id("tiny_slope_side_special")      , new UnbakedMeshRetexturedModel(Templates.id("block/tiny_slope_base"), SlopeBaseMesh::makeTinySide).disableAo());
		
		//item only models
		provider.addTemplateModel(Templates.id("button_inventory_special")     , new UnbakedAutoRetexturedModel(new Identifier("block/button_inventory")));
		provider.addTemplateModel(Templates.id("fence_inventory_special")      , new UnbakedAutoRetexturedModel(new Identifier("block/fence_inventory")));
		provider.addTemplateModel(Templates.id("fence_post_inventory_special") , new UnbakedAutoRetexturedModel(Templates.id("block/fence_post_inventory")));
		provider.addTemplateModel(Templates.id("wall_inventory_special")       , new UnbakedAutoRetexturedModel(new Identifier("block/wall_inventory")));
		
		//item model assignments (in lieu of models/item/___.json)
		provider.assignItemModel(Templates.id("button_inventory_special")      , Templates.BUTTON);
		provider.assignItemModel(Templates.id("carpet_special")                , Templates.CARPET);
		provider.assignItemModel(Templates.id("cube_special")                  , Templates.CUBE);
		provider.assignItemModel(Templates.id("fence_inventory_special")       , Templates.FENCE);
		provider.assignItemModel(Templates.id("fence_gate_special")            , Templates.FENCE_GATE);
		provider.assignItemModel(Templates.id("trapdoor_bottom_special")       , Templates.IRON_TRAPDOOR);
		provider.assignItemModel(Templates.id("fence_post_inventory_special")  , Templates.POST);
		provider.assignItemModel(Templates.id("pressure_plate_up_special")     , Templates.PRESSURE_PLATE);
		provider.assignItemModel(Templates.id("slab_bottom_special")           , Templates.SLAB);
		provider.assignItemModel(Templates.id("stairs_special")                , Templates.STAIRS);
		provider.assignItemModel(Templates.id("trapdoor_bottom_special")       , Templates.TRAPDOOR);
		provider.assignItemModel(Templates.id("vertical_slab_special")         , Templates.VERTICAL_SLAB);
		provider.assignItemModel(Templates.id("wall_inventory_special")        , Templates.WALL);
		
		provider.assignItemModel(Templates.id("slope_special")                 , Templates.SLOPE);
		provider.assignItemModel(Templates.id("tiny_slope_special")            , Templates.TINY_SLOPE);
	}
	
	public static @NotNull Renderer getFabricRenderer() {
		Renderer obj = RendererAccess.INSTANCE.getRenderer();
		if(obj != null) return obj;
		
		//Welp, not much more we can do, this mod heavily relies on frapi
		String msg = "A Fabric Rendering API implementation is required to use Templates 2!";
		
		if(!FabricLoader.getInstance().isModLoaded("fabric-renderer-indigo"))
			msg += "\nI noticed you don't have Indigo installed, which is a part of the complete Fabric API package.";
		if(FabricLoader.getInstance().isModLoaded("sodium"))
			msg += "\nI noticed you have Sodium installed - consider also installing Indium to provide a compatible renderer implementation.";
		
		throw new NullPointerException(msg);
	}
}
