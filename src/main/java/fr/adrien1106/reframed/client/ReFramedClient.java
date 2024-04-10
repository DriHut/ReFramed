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
		HELPER.addReFramedModel("cube"                      , HELPER.auto(new Identifier("block/cube")));
		// SMALL_CUBE
		HELPER.addReFramedModel("small_cube"                , HELPER.auto(ReFramed.id("block/small_cube/base")));
		// SMALL_CUBES_STEP
		HELPER.addReFramedModel("small_cubes_step"          , HELPER.autoDouble(ReFramed.id("block/small_cube/base"), ReFramed.id("block/small_cube/step/base")));
		HELPER.addReFramedModel("small_cubes_step_reverse"  , HELPER.autoDouble(ReFramed.id("block/small_cube/step/base"), ReFramed.id("block/small_cube/base")));
		// SLAB
		HELPER.addReFramedModel("slab"                      , HELPER.auto(new Identifier("block/slab")));
		// SLAB_CUBE
		HELPER.addReFramedModel("double_slab"               , HELPER.autoDouble(new Identifier("block/slab"), new Identifier("block/slab_top")));
		// STAIR
		HELPER.addReFramedModel("stair"                     , HELPER.auto(ReFramed.id("block/stair/straight")));
		HELPER.addReFramedModel("outers_stair"              , HELPER.auto(ReFramed.id("block/stair/double_outer")));
		HELPER.addReFramedModel("inner_stair"               , HELPER.auto(ReFramed.id("block/stair/inner")));
		HELPER.addReFramedModel("outer_stair"               , HELPER.auto(ReFramed.id("block/stair/outer")));
		HELPER.addReFramedModel("outer_side_stair"          , HELPER.auto(ReFramed.id("block/stair/outer_side")));
		// STAIRS_CUBE
		HELPER.addReFramedModel("stairs_cube"               , HELPER.autoDouble(ReFramed.id("block/stair/straight"), ReFramed.id("block/stair/cube/straight")));
		HELPER.addReFramedModel("outers_stairs_cube"        , HELPER.autoDouble(ReFramed.id("block/stair/double_outer"), ReFramed.id("block/stair/cube/double_outer")));
		HELPER.addReFramedModel("inner_stairs_cube"         , HELPER.autoDouble(ReFramed.id("block/stair/inner"), ReFramed.id("block/stair/cube/inner")));
		HELPER.addReFramedModel("outer_stairs_cube"         , HELPER.autoDouble(ReFramed.id("block/stair/outer"), ReFramed.id("block/stair/cube/outer")));
		HELPER.addReFramedModel("outer_side_stairs_cube"    , HELPER.autoDouble(ReFramed.id("block/stair/outer_side"), ReFramed.id("block/stair/cube/outer_side")));
		// HALF_STAIR
		HELPER.addReFramedModel("half_stair_down"           , HELPER.auto(ReFramed.id("block/half_stair/down")));
		HELPER.addReFramedModel("half_stair_side"           , HELPER.auto(ReFramed.id("block/half_stair/side")));
		// HALF_STAIRS_SLAB
		HELPER.addReFramedModel("half_stairs_slab_down"     , HELPER.autoDouble(ReFramed.id("block/half_stair/down"), ReFramed.id("block/half_stair/slab/down")));
		HELPER.addReFramedModel("half_stairs_slab_side"     , HELPER.autoDouble(ReFramed.id("block/half_stair/side"), ReFramed.id("block/half_stair/slab/side")));
		// HALF_STAIRS_STAIR
		HELPER.addReFramedModel("half_stairs_stair_down"    , HELPER.autoDouble(ReFramed.id("block/half_stair/down"), ReFramed.id("block/half_stair/stair/down")));
		HELPER.addReFramedModel("half_stairs_stair_side"    , HELPER.autoDouble(ReFramed.id("block/half_stair/side"), ReFramed.id("block/half_stair/stair/side")));
		HELPER.addReFramedModel("half_stairs_stair_reverse" , HELPER.autoDouble(ReFramed.id("block/half_stair/stair/side"), ReFramed.id("block/half_stair/side")));
		// STEP
		HELPER.addReFramedModel("step"                      , HELPER.auto(ReFramed.id("block/step/down")));
		// STEPS_SLAB
		HELPER.addReFramedModel("steps_slab"                , HELPER.autoDouble(ReFramed.id("block/step/down"), ReFramed.id("block/step/slab/down")));
		HELPER.addReFramedModel("steps_slab_side"           , HELPER.autoDouble(ReFramed.id("block/step/side"), ReFramed.id("block/step/slab/side")));
		// LAYER
		HELPER.addReFramedModel("layer_1"                   , HELPER.auto(new Identifier("block/snow_height2")));
		HELPER.addReFramedModel("layer_2"                   , HELPER.auto(new Identifier("block/snow_height4")));
		HELPER.addReFramedModel("layer_3"                   , HELPER.auto(new Identifier("block/snow_height6")));
		HELPER.addReFramedModel("layer_4"                   , HELPER.auto(new Identifier("block/snow_height8")));
		HELPER.addReFramedModel("layer_5"                   , HELPER.auto(new Identifier("block/snow_height10")));
		HELPER.addReFramedModel("layer_6"                   , HELPER.auto(new Identifier("block/snow_height12")));
		HELPER.addReFramedModel("layer_7"                   , HELPER.auto(new Identifier("block/snow_height14")));
		HELPER.addReFramedModel("layer_8"                   , HELPER.auto(new Identifier("block/cube")));
		// PILLAR
		HELPER.addReFramedModel("pillar"                    , HELPER.auto(ReFramed.id("block/pillar")));
		// WALL
		HELPER.addReFramedModel("wall_inventory"            , HELPER.auto(ReFramed.id("block/wall/inventory/default")));
		// --------------------- pillar
		HELPER.addReFramedModel("wall_core"                 , HELPER.auto(ReFramed.id("block/wall/pillar/core"))); // shares AXIS only
		HELPER.addReFramedModel("wall_pillar_none"          , HELPER.auto(ReFramed.id("block/wall/pillar/none"))); // only shape_none (4 * 3 axis)
		HELPER.addReFramedModel("wall_pillar_negative"      , HELPER.auto(ReFramed.id("block/wall/pillar/bottom")));
		HELPER.addReFramedModel("wall_pillar_both"          , HELPER.auto(ReFramed.id("block/wall/pillar/both")));
		HELPER.addReFramedModel("wall_pillar_positive"      , HELPER.auto(ReFramed.id("block/wall/pillar/top")));
		HELPER.addReFramedModel("wall_pillar_middle"        , HELPER.auto(ReFramed.id("block/wall/pillar/middle")));
		// --------------------- side
		HELPER.addReFramedModel("wall_side_negative"        , HELPER.auto(ReFramed.id("block/wall/side/bottom")));
		HELPER.addReFramedModel("wall_side_both"            , HELPER.auto(ReFramed.id("block/wall/side/both")));
		HELPER.addReFramedModel("wall_side_positive"        , HELPER.auto(ReFramed.id("block/wall/side/top")));
		HELPER.addReFramedModel("wall_side_middle"          , HELPER.auto(ReFramed.id("block/wall/side/middle")));
		// --------------------- junction
		HELPER.addReFramedModel("wall_junction_negative"    , HELPER.auto(ReFramed.id("block/wall/junction/bottom"))); // (4 * 3) possible
		HELPER.addReFramedModel("wall_junction_both"        , HELPER.auto(ReFramed.id("block/wall/junction/both")));
		HELPER.addReFramedModel("wall_junction_positive"    , HELPER.auto(ReFramed.id("block/wall/junction/top")));
		HELPER.addReFramedModel("wall_junction_middle"      , HELPER.auto(ReFramed.id("block/wall/junction/middle")));
		// --------------------- junction_c
		HELPER.addReFramedModel("wall_junction_negative_c"  , HELPER.auto(ReFramed.id("block/wall/junction/bottom_c")));
		HELPER.addReFramedModel("wall_junction_both_c"      , HELPER.auto(ReFramed.id("block/wall/junction/both_c")));
		HELPER.addReFramedModel("wall_junction_positive_c"  , HELPER.auto(ReFramed.id("block/wall/junction/top_c")));
		HELPER.addReFramedModel("wall_junction_middle_c"    , HELPER.auto(ReFramed.id("block/wall/junction/middle_c")));
		// --------------------- junction_i
		HELPER.addReFramedModel("wall_junction_negative_i"  , HELPER.auto(ReFramed.id("block/wall/junction/bottom_i")));
		HELPER.addReFramedModel("wall_junction_both_i"      , HELPER.auto(ReFramed.id("block/wall/junction/both_i")));
		HELPER.addReFramedModel("wall_junction_positive_i"  , HELPER.auto(ReFramed.id("block/wall/junction/top_i")));
		HELPER.addReFramedModel("wall_junction_middle_i"    , HELPER.auto(ReFramed.id("block/wall/junction/middle_i")));
		// --------------------- junction_t
		HELPER.addReFramedModel("wall_junction_negative_t"  , HELPER.auto(ReFramed.id("block/wall/junction/bottom_t")));
		HELPER.addReFramedModel("wall_junction_both_t"      , HELPER.auto(ReFramed.id("block/wall/junction/both_t")));
		HELPER.addReFramedModel("wall_junction_positive_t"  , HELPER.auto(ReFramed.id("block/wall/junction/top_t")));
		HELPER.addReFramedModel("wall_junction_middle_t"    , HELPER.auto(ReFramed.id("block/wall/junction/middle_t")));
		// --------------------- junction_x
		HELPER.addReFramedModel("wall_junction_negative_x"  , HELPER.auto(ReFramed.id("block/wall/junction/bottom_x"))); // (Axis only)
		HELPER.addReFramedModel("wall_junction_both_x"      , HELPER.auto(ReFramed.id("block/wall/junction/both_x")));
		HELPER.addReFramedModel("wall_junction_positive_x"  , HELPER.auto(ReFramed.id("block/wall/junction/top_x")));
		HELPER.addReFramedModel("wall_junction_middle_x"    , HELPER.auto(ReFramed.id("block/wall/junction/middle_x")));


		//item model assignments (in lieu of models/item/___.json)
		HELPER.assignItemModel("cube"                  , ReFramed.CUBE);
		HELPER.assignItemModel("small_cube"            , ReFramed.SMALL_CUBE);
		HELPER.assignItemModel("small_cubes_step"      , ReFramed.SMALL_CUBES_STEP);
		HELPER.assignItemModel("slab"                  , ReFramed.SLAB);
		HELPER.assignItemModel("double_slab"           , ReFramed.SLABS_CUBE);
		HELPER.assignItemModel("stair"                 , ReFramed.STAIR);
		HELPER.assignItemModel("stairs_cube"           , ReFramed.STAIRS_CUBE);
		HELPER.assignItemModel("half_stair_down"       , ReFramed.HALF_STAIR);
		HELPER.assignItemModel("half_stairs_slab_down" , ReFramed.HALF_STAIRS_SLAB);
		HELPER.assignItemModel("half_stairs_stair_down", ReFramed.HALF_STAIRS_STAIR);
		HELPER.assignItemModel("step"                  , ReFramed.STEP);
		HELPER.assignItemModel("steps_slab"            , ReFramed.STEPS_SLAB);
		HELPER.assignItemModel("layer_1"               , ReFramed.LAYER);
		HELPER.assignItemModel("pillar"                , ReFramed.PILLAR);
		HELPER.assignItemModel("wall_inventory"        , ReFramed.WALL);
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
