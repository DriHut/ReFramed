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

		HELPER.addReFramedModel("cube_special"                    , HELPER.auto(new Identifier("block/cube")));
		HELPER.addReFramedModel("small_cube_special"              , HELPER.auto(ReFramed.id("block/small_cube")));
		HELPER.addReFramedModel("double_small_cube_special"       , HELPER.autoDouble(ReFramed.id("block/small_cube"), ReFramed.id("block/small_cube_complement")));
		HELPER.addReFramedModel("slab_special"                    , HELPER.auto(new Identifier("block/slab")));
		HELPER.addReFramedModel("double_slab_special"             , HELPER.autoDouble(new Identifier("block/slab"), new Identifier("block/slab_top")));
		HELPER.addReFramedModel("stairs_special"                  , HELPER.auto(ReFramed.id("block/stairs")));
		HELPER.addReFramedModel("outers_stairs_special"           , HELPER.auto(ReFramed.id("block/double_outer_stairs")));
		HELPER.addReFramedModel("inner_stairs_special"            , HELPER.auto(ReFramed.id("block/inner_stairs")));
		HELPER.addReFramedModel("outer_stairs_special"            , HELPER.auto(ReFramed.id("block/outer_stairs")));
		HELPER.addReFramedModel("outer_side_stairs_special"       , HELPER.auto(ReFramed.id("block/outer_side_stairs")));
		HELPER.addReFramedModel("double_stairs_special"           , HELPER.autoDouble(ReFramed.id("block/stairs"), ReFramed.id("block/stairs_complement")));
		HELPER.addReFramedModel("double_outers_stairs_special"    , HELPER.autoDouble(ReFramed.id("block/double_outer_stairs"), ReFramed.id("block/double_outer_stairs_complement")));
		HELPER.addReFramedModel("double_inner_stairs_special"     , HELPER.autoDouble(ReFramed.id("block/inner_stairs"), ReFramed.id("block/inner_stairs_complement")));
		HELPER.addReFramedModel("double_outer_stairs_special"     , HELPER.autoDouble(ReFramed.id("block/outer_stairs"), ReFramed.id("block/outer_stairs_complement")));
		HELPER.addReFramedModel("double_outer_side_stairs_special", HELPER.autoDouble(ReFramed.id("block/outer_side_stairs"), ReFramed.id("block/outer_side_stairs_complement")));
		HELPER.addReFramedModel("step_special"                    , HELPER.auto(ReFramed.id("block/step")));
		HELPER.addReFramedModel("double_step_special"             , HELPER.autoDouble(ReFramed.id("block/step"), ReFramed.id("block/step_complement_slab")));
		HELPER.addReFramedModel("double_step_side_special"        , HELPER.autoDouble(ReFramed.id("block/step_side"), ReFramed.id("block/step_side_complement_slab")));

		//item model assignments (in lieu of models/item/___.json)
		HELPER.assignItemModel("cube_special"                  , ReFramed.CUBE);
		HELPER.assignItemModel("small_cube_special"            , ReFramed.SMALL_CUBE);
		HELPER.assignItemModel("double_small_cube_special"     , ReFramed.SMALL_CUBES_STEP);
		HELPER.assignItemModel("slab_special"                  , ReFramed.SLAB);
		HELPER.assignItemModel("double_slab_special"           , ReFramed.SLABS_CUBE);
		HELPER.assignItemModel("stairs_special"                , ReFramed.STAIR);
		HELPER.assignItemModel("double_stairs_special"         , ReFramed.STAIRS_CUBE);
		HELPER.assignItemModel("step_special"                  , ReFramed.STEP);
		HELPER.assignItemModel("double_step_special"           , ReFramed.STEPS_SLAB);
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
