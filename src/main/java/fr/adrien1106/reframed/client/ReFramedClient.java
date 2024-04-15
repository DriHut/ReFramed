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

import static fr.adrien1106.reframed.util.blocks.BlockProperties.*;
import static fr.adrien1106.reframed.util.blocks.BlockProperties.STAIR_SHAPE;
import static net.minecraft.state.property.Properties.*;

public class ReFramedClient implements ClientModInitializer {
	public static final ReFramedModelProvider PROVIDER = new ReFramedModelProvider();

	public static final ReFramedClientHelper HELPER = new ReFramedClientHelper(PROVIDER);
	
	@Override
	public void onInitializeClient() {
		privateInit(); //<- Stuff you shouldn't replicate in any addon mods ;)
		
		//all frames mustn't be on the SOLID layer because they are not opaque!
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), ReFramed.BLOCKS.toArray(new Block[0]));

		// CUBE
		HELPER.addReFramedModel("cube"                      , HELPER.auto(new Identifier("block/cube"), 2));
		// SMALL_CUBE
		HELPER.addReFramedModel("small_cube"                , HELPER.auto(ReFramed.id("block/small_cube/base"), 9, CORNER));
		// SMALL_CUBES_STEP
		HELPER.addReFramedModel("small_cubes_step"          , HELPER.autoDouble(ReFramed.id("block/small_cube/base"), ReFramed.id("block/small_cube/step/base"), 9, EDGE));
		HELPER.addReFramedModel("small_cubes_step_reverse"  , HELPER.autoDouble(ReFramed.id("block/small_cube/step/base"), ReFramed.id("block/small_cube/base"), 4, EDGE));
		// SLAB
		HELPER.addReFramedModel("slab"                      , HELPER.auto(new Identifier("block/slab"), 7, FACING));
		// SLAB_CUBE
		HELPER.addReFramedModel("double_slab"               , HELPER.autoDouble(new Identifier("block/slab"), new Identifier("block/slab_top"), 4, AXIS));
		// STAIR
		HELPER.addReFramedModel("stair"                     , HELPER.auto(ReFramed.id("block/stair/straight"), 13, EDGE));
		HELPER.addReFramedModel("outers_stair"              , HELPER.auto(ReFramed.id("block/stair/double_outer"), 24, EDGE, STAIR_SHAPE));
		HELPER.addReFramedModel("inner_stair"               , HELPER.auto(ReFramed.id("block/stair/inner"), 24, EDGE, STAIR_SHAPE));
		HELPER.addReFramedModel("outer_stair"               , HELPER.auto(ReFramed.id("block/stair/outer"), 16, EDGE, STAIR_SHAPE));
		HELPER.addReFramedModel("outer_side_stair"          , HELPER.auto(ReFramed.id("block/stair/outer_side"), 32, EDGE, STAIR_SHAPE));
		// STAIRS_CUBE
		HELPER.addReFramedModel("stairs_cube"               , HELPER.autoDouble(ReFramed.id("block/stair/straight"), ReFramed.id("block/stair/cube/straight"), 13, EDGE));
		HELPER.addReFramedModel("outers_stairs_cube"        , HELPER.autoDouble(ReFramed.id("block/stair/double_outer"), ReFramed.id("block/stair/cube/double_outer"), 24, EDGE, STAIR_SHAPE));
		HELPER.addReFramedModel("inner_stairs_cube"         , HELPER.autoDouble(ReFramed.id("block/stair/inner"), ReFramed.id("block/stair/cube/inner"), 24, EDGE, STAIR_SHAPE));
		HELPER.addReFramedModel("outer_stairs_cube"         , HELPER.autoDouble(ReFramed.id("block/stair/outer"), ReFramed.id("block/stair/cube/outer"), 16, EDGE, STAIR_SHAPE));
		HELPER.addReFramedModel("outer_side_stairs_cube"    , HELPER.autoDouble(ReFramed.id("block/stair/outer_side"), ReFramed.id("block/stair/cube/outer_side"), 32, EDGE, STAIR_SHAPE));
		// HALF_STAIR
		HELPER.addReFramedModel("half_stair_down"           , HELPER.auto(ReFramed.id("block/half_stair/down"), 9, CORNER, CORNER_FACE));
		HELPER.addReFramedModel("half_stair_side"           , HELPER.auto(ReFramed.id("block/half_stair/side"), 16, CORNER, CORNER_FACE));
		// HALF_STAIRS_SLAB
		HELPER.addReFramedModel("half_stairs_slab_down"     , HELPER.autoDouble(ReFramed.id("block/half_stair/down"), ReFramed.id("block/half_stair/slab/down"), 9, CORNER, CORNER_FACE));
		HELPER.addReFramedModel("half_stairs_slab_side"     , HELPER.autoDouble(ReFramed.id("block/half_stair/side"), ReFramed.id("block/half_stair/slab/side"), 16, CORNER, CORNER_FACE));
		// HALF_STAIRS_STAIR
		HELPER.addReFramedModel("half_stairs_stair_down"    , HELPER.autoDouble(ReFramed.id("block/half_stair/down"), ReFramed.id("block/half_stair/stair/down"), 5, EDGE));
		HELPER.addReFramedModel("half_stairs_stair_side"    , HELPER.autoDouble(ReFramed.id("block/half_stair/side"), ReFramed.id("block/half_stair/stair/side"), 6, EDGE));
		HELPER.addReFramedModel("half_stairs_stair_reverse" , HELPER.autoDouble(ReFramed.id("block/half_stair/stair/side"), ReFramed.id("block/half_stair/side"), 2, EDGE));
		// STEP
		HELPER.addReFramedModel("step"                      , HELPER.auto(ReFramed.id("block/step/down"), 13, EDGE));
		// STEPS_SLAB
		HELPER.addReFramedModel("steps_slab"                , HELPER.autoDouble(ReFramed.id("block/step/down"), ReFramed.id("block/step/slab/down"), 5, FACING, AXIS));
		HELPER.addReFramedModel("steps_slab_side"           , HELPER.autoDouble(ReFramed.id("block/step/side"), ReFramed.id("block/step/slab/side"), 8, FACING, AXIS));
		// LAYER
		HELPER.addReFramedModel("layer_1"                   , HELPER.auto(new Identifier("block/snow_height2"), 7, FACING));
		HELPER.addReFramedModel("layer_2"                   , HELPER.auto(new Identifier("block/snow_height4"), 6, FACING));
		HELPER.addReFramedModel("layer_3"                   , HELPER.auto(new Identifier("block/snow_height6"), 6, FACING));
		HELPER.addReFramedModel("layer_4"                   , HELPER.auto(new Identifier("block/snow_height8"), 6, FACING));
		HELPER.addReFramedModel("layer_5"                   , HELPER.auto(new Identifier("block/snow_height10"), 6, FACING));
		HELPER.addReFramedModel("layer_6"                   , HELPER.auto(new Identifier("block/snow_height12"), 6, FACING));
		HELPER.addReFramedModel("layer_7"                   , HELPER.auto(new Identifier("block/snow_height14"), 6, FACING));
		HELPER.addReFramedModel("layer_8"                   , HELPER.auto(new Identifier("block/cube"), 1));
		// PILLAR
		HELPER.addReFramedModel("pillar"                    , HELPER.auto(ReFramed.id("block/pillar"), 4, AXIS));
		// WALL
		HELPER.addReFramedModel("wall_inventory"            , HELPER.auto(ReFramed.id("block/wall/inventory/default"), 1));
		// --------------------- pillar
		HELPER.addReFramedModel("wall_core"                 , HELPER.auto(ReFramed.id("block/wall/pillar/core"), 1, UP));
		HELPER.addReFramedModel("wall_pillar_low"           , HELPER.auto(ReFramed.id("block/wall/pillar/low"), 4, NORTH_WALL_SHAPE, EAST_WALL_SHAPE, SOUTH_WALL_SHAPE, WEST_WALL_SHAPE));
		HELPER.addReFramedModel("wall_pillar_tall"          , HELPER.auto(ReFramed.id("block/wall/pillar/tall"), 4, NORTH_WALL_SHAPE, EAST_WALL_SHAPE, SOUTH_WALL_SHAPE, WEST_WALL_SHAPE));
		HELPER.addReFramedModel("wall_pillar_none"          , HELPER.auto(ReFramed.id("block/wall/pillar/none"), 4, NORTH_WALL_SHAPE, EAST_WALL_SHAPE, SOUTH_WALL_SHAPE, WEST_WALL_SHAPE));
		// --------------------- side
		HELPER.addReFramedModel("wall_side_low"             , HELPER.auto(ReFramed.id("block/wall/side/low"), 92, NORTH_WALL_SHAPE, EAST_WALL_SHAPE, SOUTH_WALL_SHAPE, WEST_WALL_SHAPE));
		HELPER.addReFramedModel("wall_side_tall"            , HELPER.auto(ReFramed.id("block/wall/side/tall"), 92, NORTH_WALL_SHAPE, EAST_WALL_SHAPE, SOUTH_WALL_SHAPE, WEST_WALL_SHAPE));
		// --------------------- junction
		HELPER.addReFramedModel("wall_low_e"                , HELPER.auto(ReFramed.id("block/wall/junction/low"), 4, NORTH_WALL_SHAPE, EAST_WALL_SHAPE, SOUTH_WALL_SHAPE, WEST_WALL_SHAPE));
		HELPER.addReFramedModel("wall_tall_e"               , HELPER.auto(ReFramed.id("block/wall/junction/tall"), 4, NORTH_WALL_SHAPE, EAST_WALL_SHAPE, SOUTH_WALL_SHAPE, WEST_WALL_SHAPE));
		// --------------------- junction_i
		HELPER.addReFramedModel("wall_low_i"                , HELPER.auto(ReFramed.id("block/wall/junction/low_i"), 4, NORTH_WALL_SHAPE, EAST_WALL_SHAPE, SOUTH_WALL_SHAPE, WEST_WALL_SHAPE));
		HELPER.addReFramedModel("wall_tall_i"               , HELPER.auto(ReFramed.id("block/wall/junction/tall_i"), 4, NORTH_WALL_SHAPE, EAST_WALL_SHAPE, SOUTH_WALL_SHAPE, WEST_WALL_SHAPE));
		HELPER.addReFramedModel("wall_low_tall_i"           , HELPER.auto(ReFramed.id("block/wall/junction/low_tall_i"), 4, NORTH_WALL_SHAPE, EAST_WALL_SHAPE, SOUTH_WALL_SHAPE, WEST_WALL_SHAPE));
		// --------------------- junction_c
		HELPER.addReFramedModel("wall_low_c"                , HELPER.auto(ReFramed.id("block/wall/junction/low_c"), 4, NORTH_WALL_SHAPE, EAST_WALL_SHAPE, SOUTH_WALL_SHAPE, WEST_WALL_SHAPE));
		HELPER.addReFramedModel("wall_tall_c"               , HELPER.auto(ReFramed.id("block/wall/junction/tall_c"), 4, NORTH_WALL_SHAPE, EAST_WALL_SHAPE, SOUTH_WALL_SHAPE, WEST_WALL_SHAPE));
		HELPER.addReFramedModel("wall_low_tall_c"           , HELPER.auto(ReFramed.id("block/wall/junction/low_tall_c"), 4, NORTH_WALL_SHAPE, EAST_WALL_SHAPE, SOUTH_WALL_SHAPE, WEST_WALL_SHAPE));
		HELPER.addReFramedModel("wall_tall_low_c"           , HELPER.auto(ReFramed.id("block/wall/junction/tall_low_c"), 4, NORTH_WALL_SHAPE, EAST_WALL_SHAPE, SOUTH_WALL_SHAPE, WEST_WALL_SHAPE));
		// --------------------- junction_t
		HELPER.addReFramedModel("wall_low_t"                , HELPER.auto(ReFramed.id("block/wall/junction/low_t"), 4, NORTH_WALL_SHAPE, EAST_WALL_SHAPE, SOUTH_WALL_SHAPE, WEST_WALL_SHAPE));
		HELPER.addReFramedModel("wall_tall_t"               , HELPER.auto(ReFramed.id("block/wall/junction/tall_t"), 4, NORTH_WALL_SHAPE, EAST_WALL_SHAPE, SOUTH_WALL_SHAPE, WEST_WALL_SHAPE));
		HELPER.addReFramedModel("wall_tall_low_c_t"         , HELPER.auto(ReFramed.id("block/wall/junction/tall_low_c_t"), 4, NORTH_WALL_SHAPE, EAST_WALL_SHAPE, SOUTH_WALL_SHAPE, WEST_WALL_SHAPE));
		HELPER.addReFramedModel("wall_tall_i_low_t"         , HELPER.auto(ReFramed.id("block/wall/junction/tall_i_low_t"), 4, NORTH_WALL_SHAPE, EAST_WALL_SHAPE, SOUTH_WALL_SHAPE, WEST_WALL_SHAPE));
		HELPER.addReFramedModel("wall_low_i_tall_t"         , HELPER.auto(ReFramed.id("block/wall/junction/low_i_tall_t"), 4, NORTH_WALL_SHAPE, EAST_WALL_SHAPE, SOUTH_WALL_SHAPE, WEST_WALL_SHAPE));
		HELPER.addReFramedModel("wall_low_tall_c_t"         , HELPER.auto(ReFramed.id("block/wall/junction/low_tall_c_t"), 4, NORTH_WALL_SHAPE, EAST_WALL_SHAPE, SOUTH_WALL_SHAPE, WEST_WALL_SHAPE));
		HELPER.addReFramedModel("wall_low_c_tall_t"         , HELPER.auto(ReFramed.id("block/wall/junction/low_c_tall_t"), 4, NORTH_WALL_SHAPE, EAST_WALL_SHAPE, SOUTH_WALL_SHAPE, WEST_WALL_SHAPE));
		HELPER.addReFramedModel("wall_tall_c_low_t"         , HELPER.auto(ReFramed.id("block/wall/junction/tall_c_low_t"), 4, NORTH_WALL_SHAPE, EAST_WALL_SHAPE, SOUTH_WALL_SHAPE, WEST_WALL_SHAPE));
		// --------------------- junction_x
		HELPER.addReFramedModel("wall_low_x"                , HELPER.auto(ReFramed.id("block/wall/junction/low_x"), 4, NORTH_WALL_SHAPE, EAST_WALL_SHAPE, SOUTH_WALL_SHAPE, WEST_WALL_SHAPE));
		HELPER.addReFramedModel("wall_tall_x"               , HELPER.auto(ReFramed.id("block/wall/junction/tall_x"), 4, NORTH_WALL_SHAPE, EAST_WALL_SHAPE, SOUTH_WALL_SHAPE, WEST_WALL_SHAPE));
		HELPER.addReFramedModel("wall_tall_i_low_i_x"       , HELPER.auto(ReFramed.id("block/wall/junction/tall_i_low_i_x"), 4, NORTH_WALL_SHAPE, EAST_WALL_SHAPE, SOUTH_WALL_SHAPE, WEST_WALL_SHAPE));
		HELPER.addReFramedModel("wall_tall_low_t_x"         , HELPER.auto(ReFramed.id("block/wall/junction/tall_low_t_x"), 4, NORTH_WALL_SHAPE, EAST_WALL_SHAPE, SOUTH_WALL_SHAPE, WEST_WALL_SHAPE));
		HELPER.addReFramedModel("wall_tall_c_low_c_x"       , HELPER.auto(ReFramed.id("block/wall/junction/tall_c_low_c_x"), 4, NORTH_WALL_SHAPE, EAST_WALL_SHAPE, SOUTH_WALL_SHAPE, WEST_WALL_SHAPE));
		HELPER.addReFramedModel("wall_tall_t_low_x"         , HELPER.auto(ReFramed.id("block/wall/junction/tall_t_low_x"), 4, NORTH_WALL_SHAPE, EAST_WALL_SHAPE, SOUTH_WALL_SHAPE, WEST_WALL_SHAPE));


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
