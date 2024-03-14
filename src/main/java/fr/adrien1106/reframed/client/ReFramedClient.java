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
		HELPER.addReFramedModel("cube_special"                      , HELPER.auto(new Identifier("block/cube")));
		// SMALL_CUBE
		HELPER.addReFramedModel("small_cube_special"                , HELPER.auto(ReFramed.id("block/small_cube/base")));
		// SMALL_CUBES_STEP
		HELPER.addReFramedModel("small_cubes_step_special"          , HELPER.autoDouble(ReFramed.id("block/small_cube/base"), ReFramed.id("block/small_cube/step/base")));
		HELPER.addReFramedModel("small_cubes_step_reverse_special"  , HELPER.autoDouble(ReFramed.id("block/small_cube/step/base"), ReFramed.id("block/small_cube/base")));
		// SLAB
		HELPER.addReFramedModel("slab_special"                      , HELPER.auto(new Identifier("block/slab")));
		// SLAB_CUBE
		HELPER.addReFramedModel("double_slab_special"               , HELPER.autoDouble(new Identifier("block/slab"), new Identifier("block/slab_top")));
		// STAIR
		HELPER.addReFramedModel("stair_special"                     , HELPER.auto(ReFramed.id("block/stair/straight")));
		HELPER.addReFramedModel("outers_stair_special"              , HELPER.auto(ReFramed.id("block/stair/double_outer")));
		HELPER.addReFramedModel("inner_stair_special"               , HELPER.auto(ReFramed.id("block/stair/inner")));
		HELPER.addReFramedModel("outer_stair_special"               , HELPER.auto(ReFramed.id("block/stair/outer")));
		HELPER.addReFramedModel("outer_side_stair_special"          , HELPER.auto(ReFramed.id("block/stair/outer_side")));
		// STAIRS_CUBE
		HELPER.addReFramedModel("stairs_cube_special"               , HELPER.autoDouble(ReFramed.id("block/stair/straight"), ReFramed.id("block/stair/cube/straight")));
		HELPER.addReFramedModel("outers_stairs_cube_special"        , HELPER.autoDouble(ReFramed.id("block/stair/double_outer"), ReFramed.id("block/stair/cube/double_outer")));
		HELPER.addReFramedModel("inner_stairs_cube_special"         , HELPER.autoDouble(ReFramed.id("block/stair/inner"), ReFramed.id("block/stair/cube/inner")));
		HELPER.addReFramedModel("outer_stairs_cube_special"         , HELPER.autoDouble(ReFramed.id("block/stair/outer"), ReFramed.id("block/stair/cube/outer")));
		HELPER.addReFramedModel("outer_side_stairs_cube_special"    , HELPER.autoDouble(ReFramed.id("block/stair/outer_side"), ReFramed.id("block/stair/cube/outer_side")));
		// HALF_STAIR
		HELPER.addReFramedModel("half_stair_down_special"           , HELPER.auto(ReFramed.id("block/half_stair/down")));
		HELPER.addReFramedModel("half_stair_side_special"           , HELPER.auto(ReFramed.id("block/half_stair/side")));
		// HALF_STAIRS_SLAB
		HELPER.addReFramedModel("half_stairs_slab_down_special"     , HELPER.autoDouble(ReFramed.id("block/half_stair/down"), ReFramed.id("block/half_stair/slab/down")));
		HELPER.addReFramedModel("half_stairs_slab_side_special"     , HELPER.autoDouble(ReFramed.id("block/half_stair/side"), ReFramed.id("block/half_stair/slab/side")));
		// HALF_STAIRS_STAIR
		HELPER.addReFramedModel("half_stairs_stair_down_special"    , HELPER.autoDouble(ReFramed.id("block/half_stair/down"), ReFramed.id("block/half_stair/stair/down")));
		HELPER.addReFramedModel("half_stairs_stair_side_special"    , HELPER.autoDouble(ReFramed.id("block/half_stair/side"), ReFramed.id("block/half_stair/stair/side")));
		HELPER.addReFramedModel("half_stairs_stair_reverse_special" , HELPER.autoDouble(ReFramed.id("block/half_stair/stair/side"), ReFramed.id("block/half_stair/side")));
		// STEP
		HELPER.addReFramedModel("step_special"                      , HELPER.auto(ReFramed.id("block/step/down")));
		// STEPS_SLAB
		HELPER.addReFramedModel("steps_slab_special"                , HELPER.autoDouble(ReFramed.id("block/step/down"), ReFramed.id("block/step/slab/down")));
		HELPER.addReFramedModel("steps_slab_side_special"           , HELPER.autoDouble(ReFramed.id("block/step/side"), ReFramed.id("block/step/slab/side")));
		// LAYER
		HELPER.addReFramedModel("layer_1_special"                   , HELPER.auto(new Identifier("block/snow_height2")));
		HELPER.addReFramedModel("layer_2_special"                   , HELPER.auto(new Identifier("block/snow_height4")));
		HELPER.addReFramedModel("layer_3_special"                   , HELPER.auto(new Identifier("block/snow_height6")));
		HELPER.addReFramedModel("layer_4_special"                   , HELPER.auto(new Identifier("block/snow_height8")));
		HELPER.addReFramedModel("layer_5_special"                   , HELPER.auto(new Identifier("block/snow_height10")));
		HELPER.addReFramedModel("layer_6_special"                   , HELPER.auto(new Identifier("block/snow_height12")));
		HELPER.addReFramedModel("layer_7_special"                   , HELPER.auto(new Identifier("block/snow_height14")));
		HELPER.addReFramedModel("layer_8_special"                   , HELPER.auto(new Identifier("block/cube")));

		//item model assignments (in lieu of models/item/___.json)
		HELPER.assignItemModel("cube_special"                  , ReFramed.CUBE);
		HELPER.assignItemModel("small_cube_special"            , ReFramed.SMALL_CUBE);
		HELPER.assignItemModel("small_cubes_step_special"      , ReFramed.SMALL_CUBES_STEP);
		HELPER.assignItemModel("slab_special"                  , ReFramed.SLAB);
		HELPER.assignItemModel("double_slab_special"           , ReFramed.SLABS_CUBE);
		HELPER.assignItemModel("stair_special"                 , ReFramed.STAIR);
		HELPER.assignItemModel("stairs_cube_special"           , ReFramed.STAIRS_CUBE);
		HELPER.assignItemModel("half_stair_down_special"       , ReFramed.HALF_STAIR);
		HELPER.assignItemModel("half_stairs_slab_down_special" , ReFramed.HALF_STAIRS_SLAB);
		HELPER.assignItemModel("half_stairs_stair_down_special", ReFramed.HALF_STAIRS_STAIR);
		HELPER.assignItemModel("step_special"                  , ReFramed.STEP);
		HELPER.assignItemModel("steps_slab_special"            , ReFramed.STEPS_SLAB);
		HELPER.assignItemModel("layer_1_special"               , ReFramed.LAYER);
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
