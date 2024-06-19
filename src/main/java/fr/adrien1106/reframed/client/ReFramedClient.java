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

		// CUBE
		HELPER.addReFramedModel("cube"                          , HELPER.auto(new Identifier("block/cube")));
		// SMALL_CUBE
		HELPER.addReFramedModel("small_cube"                    , HELPER.auto(ReFramed.id("block/small_cube/base")));
		// SMALL_CUBES_STEP
		HELPER.addReFramedModel("small_cubes_step"              , HELPER.autoDouble(ReFramed.id("block/small_cube/base"), ReFramed.id("block/small_cube/step/base")));
		HELPER.addReFramedModel("small_cubes_step_reverse"      , HELPER.autoDouble(ReFramed.id("block/small_cube/step/base"), ReFramed.id("block/small_cube/base")));
		// SLAB
		HELPER.addReFramedModel("slab"                          , HELPER.auto(new Identifier("block/slab")));
		// SLAB_CUBE
		HELPER.addReFramedModel("double_slab"                   , HELPER.autoDouble(new Identifier("block/slab"), new Identifier("block/slab_top")));
		// STAIR
		HELPER.addReFramedModel("stair"                         , HELPER.auto(ReFramed.id("block/stair/straight")));
		HELPER.addReFramedModel("outers_stair"                  , HELPER.auto(ReFramed.id("block/stair/double_outer")));
		HELPER.addReFramedModel("inner_stair"                   , HELPER.auto(ReFramed.id("block/stair/inner")));
		HELPER.addReFramedModel("outer_stair"                   , HELPER.auto(ReFramed.id("block/stair/outer")));
		HELPER.addReFramedModel("outer_side_stair"              , HELPER.auto(ReFramed.id("block/stair/outer_side")));
		// STAIRS_CUBE
		HELPER.addReFramedModel("stairs_cube"                   , HELPER.autoDouble(ReFramed.id("block/stair/straight"), ReFramed.id("block/stair/cube/straight")));
		HELPER.addReFramedModel("outers_stairs_cube"            , HELPER.autoDouble(ReFramed.id("block/stair/double_outer"), ReFramed.id("block/stair/cube/double_outer")));
		HELPER.addReFramedModel("inner_stairs_cube"             , HELPER.autoDouble(ReFramed.id("block/stair/inner"), ReFramed.id("block/stair/cube/inner")));
		HELPER.addReFramedModel("outer_stairs_cube"             , HELPER.autoDouble(ReFramed.id("block/stair/outer"), ReFramed.id("block/stair/cube/outer")));
		HELPER.addReFramedModel("outer_side_stairs_cube"        , HELPER.autoDouble(ReFramed.id("block/stair/outer_side"), ReFramed.id("block/stair/cube/outer_side")));
		// HALF_STAIR
		HELPER.addReFramedModel("half_stair_down"               , HELPER.auto(ReFramed.id("block/half_stair/down")));
		HELPER.addReFramedModel("half_stair_side"               , HELPER.auto(ReFramed.id("block/half_stair/side")));
		// HALF_STAIRS_SLAB
		HELPER.addReFramedModel("half_stairs_slab_down"         , HELPER.autoDouble(ReFramed.id("block/half_stair/down"), ReFramed.id("block/half_stair/slab/down")));
		HELPER.addReFramedModel("half_stairs_slab_side"         , HELPER.autoDouble(ReFramed.id("block/half_stair/side"), ReFramed.id("block/half_stair/slab/side")));
		// HALF_STAIRS_STAIR
		HELPER.addReFramedModel("half_stairs_stair_down"        , HELPER.autoDouble(ReFramed.id("block/half_stair/down"), ReFramed.id("block/half_stair/stair/down")));
		HELPER.addReFramedModel("half_stairs_stair_side"        , HELPER.autoDouble(ReFramed.id("block/half_stair/side"), ReFramed.id("block/half_stair/stair/side")));
		HELPER.addReFramedModel("half_stairs_stair_reverse"     , HELPER.autoDouble(ReFramed.id("block/half_stair/stair/side"), ReFramed.id("block/half_stair/side")));
		// STEP
		HELPER.addReFramedModel("step"                          , HELPER.auto(ReFramed.id("block/step/down")));
		// STEPS_SLAB
		HELPER.addReFramedModel("steps_slab"                    , HELPER.autoDouble(ReFramed.id("block/step/down"), ReFramed.id("block/step/slab/down")));
		HELPER.addReFramedModel("steps_slab_side"               , HELPER.autoDouble(ReFramed.id("block/step/side"), ReFramed.id("block/step/slab/side")));
		// LAYER
		HELPER.addReFramedModel("layer_1"                       , HELPER.auto(new Identifier("block/snow_height2")));
		HELPER.addReFramedModel("layer_2"                       , HELPER.auto(new Identifier("block/snow_height4")));
		HELPER.addReFramedModel("layer_3"                       , HELPER.auto(new Identifier("block/snow_height6")));
		HELPER.addReFramedModel("layer_4"                       , HELPER.auto(new Identifier("block/snow_height8")));
		HELPER.addReFramedModel("layer_5"                       , HELPER.auto(new Identifier("block/snow_height10")));
		HELPER.addReFramedModel("layer_6"                       , HELPER.auto(new Identifier("block/snow_height12")));
		HELPER.addReFramedModel("layer_7"                       , HELPER.auto(new Identifier("block/snow_height14")));
		HELPER.addReFramedModel("layer_8"                       , HELPER.auto(new Identifier("block/cube")));
		// PILLAR
		HELPER.addReFramedModel("pillar"                        , HELPER.auto(ReFramed.id("block/pillar")));
		// WALL
		HELPER.addReFramedModel("wall_inventory"                , HELPER.auto(ReFramed.id("block/wall/inventory/default")));
		// --------------------- pillar
		HELPER.addReFramedModel("wall_core"                     , HELPER.auto(ReFramed.id("block/wall/pillar/core")));
		HELPER.addReFramedModel("wall_pillar_low"               , HELPER.auto(ReFramed.id("block/wall/pillar/low")));
		HELPER.addReFramedModel("wall_pillar_tall"              , HELPER.auto(ReFramed.id("block/wall/pillar/tall")));
		HELPER.addReFramedModel("wall_pillar_none"              , HELPER.auto(ReFramed.id("block/wall/pillar/none")));
		// --------------------- side
		HELPER.addReFramedModel("wall_side_low"                 , HELPER.auto(ReFramed.id("block/wall/side/low")));
		HELPER.addReFramedModel("wall_side_tall"                , HELPER.auto(ReFramed.id("block/wall/side/tall")));
		// --------------------- junction
		HELPER.addReFramedModel("wall_low_e"                    , HELPER.auto(ReFramed.id("block/wall/junction/low")));
		HELPER.addReFramedModel("wall_tall_e"                   , HELPER.auto(ReFramed.id("block/wall/junction/tall")));
		// --------------------- junction_i
		HELPER.addReFramedModel("wall_low_i"                    , HELPER.auto(ReFramed.id("block/wall/junction/low_i")));
		HELPER.addReFramedModel("wall_tall_i"                   , HELPER.auto(ReFramed.id("block/wall/junction/tall_i")));
		HELPER.addReFramedModel("wall_low_tall_i"               , HELPER.auto(ReFramed.id("block/wall/junction/low_tall_i")));
		// --------------------- junction_c
		HELPER.addReFramedModel("wall_low_c"                    , HELPER.auto(ReFramed.id("block/wall/junction/low_c")));
		HELPER.addReFramedModel("wall_tall_c"                   , HELPER.auto(ReFramed.id("block/wall/junction/tall_c")));
		HELPER.addReFramedModel("wall_low_tall_c"               , HELPER.auto(ReFramed.id("block/wall/junction/low_tall_c")));
		HELPER.addReFramedModel("wall_tall_low_c"               , HELPER.auto(ReFramed.id("block/wall/junction/tall_low_c")));
		// --------------------- junction_t
		HELPER.addReFramedModel("wall_low_t"                    , HELPER.auto(ReFramed.id("block/wall/junction/low_t")));
		HELPER.addReFramedModel("wall_tall_t"                   , HELPER.auto(ReFramed.id("block/wall/junction/tall_t")));
		HELPER.addReFramedModel("wall_tall_low_c_t"             , HELPER.auto(ReFramed.id("block/wall/junction/tall_low_c_t")));
		HELPER.addReFramedModel("wall_tall_i_low_t"             , HELPER.auto(ReFramed.id("block/wall/junction/tall_i_low_t")));
		HELPER.addReFramedModel("wall_low_i_tall_t"             , HELPER.auto(ReFramed.id("block/wall/junction/low_i_tall_t")));
		HELPER.addReFramedModel("wall_low_tall_c_t"             , HELPER.auto(ReFramed.id("block/wall/junction/low_tall_c_t")));
		HELPER.addReFramedModel("wall_low_c_tall_t"             , HELPER.auto(ReFramed.id("block/wall/junction/low_c_tall_t")));
		HELPER.addReFramedModel("wall_tall_c_low_t"             , HELPER.auto(ReFramed.id("block/wall/junction/tall_c_low_t")));
		// --------------------- junction_x
		HELPER.addReFramedModel("wall_low_x"                    , HELPER.auto(ReFramed.id("block/wall/junction/low_x")));
		HELPER.addReFramedModel("wall_tall_x"                   , HELPER.auto(ReFramed.id("block/wall/junction/tall_x")));
		HELPER.addReFramedModel("wall_tall_i_low_i_x"           , HELPER.auto(ReFramed.id("block/wall/junction/tall_i_low_i_x")));
		HELPER.addReFramedModel("wall_tall_low_t_x"             , HELPER.auto(ReFramed.id("block/wall/junction/tall_low_t_x")));
		HELPER.addReFramedModel("wall_tall_c_low_c_x"           , HELPER.auto(ReFramed.id("block/wall/junction/tall_c_low_c_x")));
		HELPER.addReFramedModel("wall_tall_t_low_x"             , HELPER.auto(ReFramed.id("block/wall/junction/tall_t_low_x")));
		// PILLAR WALL
		HELPER.addReFramedModel("pillars_wall_inventory"        , HELPER.autoDouble(ReFramed.id("block/pillar"), ReFramed.id("block/wall/full/inventory/sides")));
		HELPER.addReFramedModel("pillars_wall_low"              , HELPER.autoDouble(ReFramed.id("block/wall/full/pillar/low"), ReFramed.id("block/wall/full/side/low")));
		HELPER.addReFramedModel("pillars_wall_tall"             , HELPER.autoDouble(ReFramed.id("block/wall/full/pillar/tall"), ReFramed.id("block/wall/full/side/tall")));
        // PANE
        HELPER.addReFramedModel("pane_inventory"                , HELPER.auto(ReFramed.id("block/pane")));
        HELPER.addReFramedModel("pane_post"                     , HELPER.auto(new Identifier("block/glass_pane_post")));
        HELPER.addReFramedModel("pane_side"                     , HELPER.auto(new Identifier("block/glass_pane_side")));
        HELPER.addReFramedModel("pane_side_alt"                 , HELPER.auto(new Identifier("block/glass_pane_side_alt")));
        HELPER.addReFramedModel("pane_noside"                   , HELPER.auto(new Identifier("block/glass_pane_noside")));
        HELPER.addReFramedModel("pane_noside_alt"               , HELPER.auto(new Identifier("block/glass_pane_noside_alt")));
        // TRAPDOOR
        HELPER.addReFramedModel("trapdoor_open"                 , HELPER.auto(new Identifier("block/oak_trapdoor_open")));
        HELPER.addReFramedModel("trapdoor_bottom"               , HELPER.auto(new Identifier("block/oak_trapdoor_bottom")));
        HELPER.addReFramedModel("trapdoor_top"                  , HELPER.auto(new Identifier("block/oak_trapdoor_top")));
        // DOOR
        HELPER.addReFramedModel("door_inventory"                , HELPER.auto(ReFramed.id("block/door")));
        // BUTTON
        HELPER.addReFramedModel("button_inventory"              , HELPER.auto(new Identifier("block/button_inventory")));
        HELPER.addReFramedModel("button"                        , HELPER.auto(new Identifier("block/button")));
        HELPER.addReFramedModel("button_pressed"                , HELPER.auto(new Identifier("block/button_pressed")));
        // POST
        HELPER.addReFramedModel("post"                          , HELPER.auto(ReFramed.id("block/post")));
        // FENCE
        HELPER.addReFramedModel("fence_inventory"               , HELPER.auto(ReFramed.id("block/fence/inventory")));
        HELPER.addReFramedModel("fence_core"                    , HELPER.auto(ReFramed.id("block/fence/core")));
        HELPER.addReFramedModel("fence_side_off"                , HELPER.auto(ReFramed.id("block/fence/side_off")));
        HELPER.addReFramedModel("fence_side_on"                 , HELPER.auto(ReFramed.id("block/fence/side_on")));
        // POST FENCE
        HELPER.addReFramedModel("post_fence_inventory"          , HELPER.autoDouble(ReFramed.id("block/post"), ReFramed.id("block/fence/full/inventory")));
        HELPER.addReFramedModel("post_fence_side"               , HELPER.autoDouble(ReFramed.id("block/fence/full/side_core"), ReFramed.id("block/fence/full/side_bars")));
        // SLABS STAIR
        HELPER.addReFramedModel("slabs_stair"                   , HELPER.autoDouble(ReFramed.id("block/slabs_stair/slab"), ReFramed.id("block/slabs_stair/step")));
        HELPER.addReFramedModel("slabs_stair_side"              , HELPER.autoDouble(ReFramed.id("block/slabs_stair/side/slab"), ReFramed.id("block/slabs_stair/side/step")));
        // SLABS OUTER STAIR
        HELPER.addReFramedModel("slabs_outer_stair"             , HELPER.autoDouble(ReFramed.id("block/slabs_stair/outer/slab"), ReFramed.id("block/slabs_stair/outer/cube")));
        HELPER.addReFramedModel("slabs_outer_stair_side"        , HELPER.autoDouble(ReFramed.id("block/slabs_stair/outer/side/slab"), ReFramed.id("block/slabs_stair/outer/side/cube")));
        // SLABS OUTER STAIR
        HELPER.addReFramedModel("slabs_inner_stair"             , HELPER.autoDouble(ReFramed.id("block/slabs_stair/inner/slab"), ReFramed.id("block/slabs_stair/inner/half_stair")));
        HELPER.addReFramedModel("slabs_inner_stair_side"        , HELPER.autoDouble(ReFramed.id("block/slabs_stair/inner/side/slab"), ReFramed.id("block/slabs_stair/inner/side/half_stair")));
        // SLABS OUTER STAIR
        HELPER.addReFramedModel("steps_cross"                   , HELPER.autoDouble(ReFramed.id("block/step/down"), ReFramed.id("block/step/cross")));
        // HALF STAIRS CUBE STAIR
        HELPER.addReFramedModel("half_stairs_cube_stair"       , HELPER.autoDouble(ReFramed.id("block/half_stair/base"), ReFramed.id("block/half_stair/stair/cube")));
        HELPER.addReFramedModel("half_stairs_cube_stair_side"  , HELPER.autoDouble(ReFramed.id("block/half_stair/base_side"), ReFramed.id("block/half_stair/stair/cube_side")));
        // HALF STAIRS STEP STAIR
        HELPER.addReFramedModel("half_stairs_step_stair_1"     , HELPER.autoDouble(ReFramed.id("block/half_stair/base"), ReFramed.id("block/half_stair/stair/step_1")));
        HELPER.addReFramedModel("half_stairs_step_stair_side_1", HELPER.autoDouble(ReFramed.id("block/half_stair/base_side"), ReFramed.id("block/half_stair/stair/step_side_1")));
        HELPER.addReFramedModel("half_stairs_step_stair_2"     , HELPER.autoDouble(ReFramed.id("block/half_stair/base"), ReFramed.id("block/half_stair/stair/step_2")));
        HELPER.addReFramedModel("half_stairs_step_stair_side_2", HELPER.autoDouble(ReFramed.id("block/half_stair/base_side"), ReFramed.id("block/half_stair/stair/step_side_2")));
        // HALF LAYER
        // --------------------- east
        HELPER.addReFramedModel("half_layer_2"                 , HELPER.auto(ReFramed.id("block/half_layer/east/layer_2")));
        HELPER.addReFramedModel("half_layer_4"                 , HELPER.auto(ReFramed.id("block/half_layer/east/layer_4")));
        HELPER.addReFramedModel("half_layer_6"                 , HELPER.auto(ReFramed.id("block/half_layer/east/layer_6")));
        HELPER.addReFramedModel("half_layer_8"                 , HELPER.auto(ReFramed.id("block/half_layer/east/layer_8")));
        HELPER.addReFramedModel("half_layer_10"                , HELPER.auto(ReFramed.id("block/half_layer/east/layer_10")));
        HELPER.addReFramedModel("half_layer_12"                , HELPER.auto(ReFramed.id("block/half_layer/east/layer_12")));
        HELPER.addReFramedModel("half_layer_14"                , HELPER.auto(ReFramed.id("block/half_layer/east/layer_14")));
        HELPER.addReFramedModel("half_layer_16"                , HELPER.auto(ReFramed.id("block/half_layer/east/layer_16")));
        // --------------------- side
        HELPER.addReFramedModel("half_layer_side_2"            , HELPER.auto(ReFramed.id("block/half_layer/side/layer_2")));
        HELPER.addReFramedModel("half_layer_side_4"            , HELPER.auto(ReFramed.id("block/half_layer/side/layer_4")));
        HELPER.addReFramedModel("half_layer_side_6"            , HELPER.auto(ReFramed.id("block/half_layer/side/layer_6")));
        HELPER.addReFramedModel("half_layer_side_8"            , HELPER.auto(ReFramed.id("block/half_layer/side/layer_8")));
        HELPER.addReFramedModel("half_layer_side_10"           , HELPER.auto(ReFramed.id("block/half_layer/side/layer_10")));
        HELPER.addReFramedModel("half_layer_side_12"           , HELPER.auto(ReFramed.id("block/half_layer/side/layer_12")));
        HELPER.addReFramedModel("half_layer_side_14"           , HELPER.auto(ReFramed.id("block/half_layer/side/layer_14")));
        HELPER.addReFramedModel("half_layer_side_16"           , HELPER.auto(ReFramed.id("block/half_layer/side/layer_16")));
        // SLAB HALF LAYER
        HELPER.addReFramedModel("slabs_half_inventory"         , HELPER.autoDouble(new Identifier("block/slab"), ReFramed.id("block/half_layer/slab/east/layer_4")));
        // STEP HALF LAYER
        HELPER.addReFramedModel("steps_half_inventory"         , HELPER.autoDouble(ReFramed.id("block/step/down"), ReFramed.id("block/half_layer/slab/east/layer_4")));
        // --------------------- east
        HELPER.addReFramedModel("second_half_layer_2"          , HELPER.auto(ReFramed.id("block/half_layer/slab/east/layer_2")).setThemeIndex(2));
        HELPER.addReFramedModel("second_half_layer_4"          , HELPER.auto(ReFramed.id("block/half_layer/slab/east/layer_4")).setThemeIndex(2));
        HELPER.addReFramedModel("second_half_layer_6"          , HELPER.auto(ReFramed.id("block/half_layer/slab/east/layer_6")).setThemeIndex(2));
        HELPER.addReFramedModel("second_half_layer_8"          , HELPER.auto(ReFramed.id("block/half_layer/slab/east/layer_8")).setThemeIndex(2));
        HELPER.addReFramedModel("second_half_layer_10"         , HELPER.auto(ReFramed.id("block/half_layer/slab/east/layer_10")).setThemeIndex(2));
        HELPER.addReFramedModel("second_half_layer_12"         , HELPER.auto(ReFramed.id("block/half_layer/slab/east/layer_12")).setThemeIndex(2));
        HELPER.addReFramedModel("second_half_layer_14"         , HELPER.auto(ReFramed.id("block/half_layer/slab/east/layer_14")).setThemeIndex(2));
        HELPER.addReFramedModel("second_half_layer_16"         , HELPER.auto(ReFramed.id("block/half_layer/slab/east/layer_16")).setThemeIndex(2));
        // --------------------- side
        HELPER.addReFramedModel("second_half_layer_side_2"     , HELPER.auto(ReFramed.id("block/half_layer/slab/side/layer_2")).setThemeIndex(2));
        HELPER.addReFramedModel("second_half_layer_side_4"     , HELPER.auto(ReFramed.id("block/half_layer/slab/side/layer_4")).setThemeIndex(2));
        HELPER.addReFramedModel("second_half_layer_side_6"     , HELPER.auto(ReFramed.id("block/half_layer/slab/side/layer_6")).setThemeIndex(2));
        HELPER.addReFramedModel("second_half_layer_side_8"     , HELPER.auto(ReFramed.id("block/half_layer/slab/side/layer_8")).setThemeIndex(2));
        HELPER.addReFramedModel("second_half_layer_side_10"    , HELPER.auto(ReFramed.id("block/half_layer/slab/side/layer_10")).setThemeIndex(2));
        HELPER.addReFramedModel("second_half_layer_side_12"    , HELPER.auto(ReFramed.id("block/half_layer/slab/side/layer_12")).setThemeIndex(2));
        HELPER.addReFramedModel("second_half_layer_side_14"    , HELPER.auto(ReFramed.id("block/half_layer/slab/side/layer_14")).setThemeIndex(2));
        HELPER.addReFramedModel("second_half_layer_side_16"    , HELPER.auto(ReFramed.id("block/half_layer/slab/side/layer_16")).setThemeIndex(2));


		// item model assignments (in lieu of models/item/___.json)
		HELPER.assignItemModel("cube"                    , ReFramed.CUBE);
		HELPER.assignItemModel("small_cube"              , ReFramed.SMALL_CUBE);
		HELPER.assignItemModel("small_cubes_step"        , ReFramed.SMALL_CUBES_STEP);
		HELPER.assignItemModel("slab"                    , ReFramed.SLAB);
		HELPER.assignItemModel("double_slab"             , ReFramed.SLABS_CUBE);
		HELPER.assignItemModel("stair"                   , ReFramed.STAIR);
		HELPER.assignItemModel("stairs_cube"             , ReFramed.STAIRS_CUBE);
		HELPER.assignItemModel("half_stair_down"         , ReFramed.HALF_STAIR);
		HELPER.assignItemModel("half_stairs_slab_down"   , ReFramed.HALF_STAIRS_SLAB);
		HELPER.assignItemModel("half_stairs_stair_down"  , ReFramed.HALF_STAIRS_STAIR);
		HELPER.assignItemModel("step"                    , ReFramed.STEP);
		HELPER.assignItemModel("steps_slab"              , ReFramed.STEPS_SLAB);
		HELPER.assignItemModel("layer_1"                 , ReFramed.LAYER);
		HELPER.assignItemModel("pillar"                  , ReFramed.PILLAR);
		HELPER.assignItemModel("pillars_wall_inventory"  , ReFramed.PILLARS_WALL);
		HELPER.assignItemModel("wall_inventory"          , ReFramed.WALL);
        HELPER.assignItemModel("pane_inventory"          , ReFramed.PANE);
        HELPER.assignItemModel("trapdoor_bottom"         , ReFramed.TRAPDOOR);
        HELPER.assignItemModel("door_inventory"          , ReFramed.DOOR);
        HELPER.assignItemModel("button_inventory"        , ReFramed.BUTTON);
        HELPER.assignItemModel("post"                    , ReFramed.POST);
        HELPER.assignItemModel("fence_inventory"         , ReFramed.FENCE);
        HELPER.assignItemModel("post_fence_inventory"    , ReFramed.POST_FENCE);
        HELPER.assignItemModel("slabs_stair"             , ReFramed.SLABS_STAIR);
        HELPER.assignItemModel("slabs_outer_stair"       , ReFramed.SLABS_OUTER_STAIR);
        HELPER.assignItemModel("slabs_inner_stair"       , ReFramed.SLABS_INNER_STAIR);
        HELPER.assignItemModel("steps_cross"             , ReFramed.STEPS_CROSS);
        HELPER.assignItemModel("half_stairs_cube_stair"  , ReFramed.HALF_STAIRS_CUBE_STAIR);
        HELPER.assignItemModel("half_stairs_step_stair_1", ReFramed.HALF_STAIRS_STEP_STAIR);
        HELPER.assignItemModel("half_layer_2"            , ReFramed.HALF_LAYER);
        HELPER.assignItemModel("slabs_half_inventory"    , ReFramed.SLABS_HALF_LAYER);
        HELPER.assignItemModel("steps_half_inventory"    , ReFramed.STEPS_HALF_LAYER);
	}
	
	private void privateInit() {
		//set up some magic to force chunk re-renders when you change a template (see TemplateEntity)
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
