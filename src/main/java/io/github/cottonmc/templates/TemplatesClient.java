package io.github.cottonmc.templates;

import io.github.cottonmc.templates.block.SlopeBlock;
import io.github.cottonmc.templates.model.SlopeUnbakedModel;
import io.github.cottonmc.templates.model.TemplateModelVariantProvider;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;

public class TemplatesClient implements ClientModInitializer {
	public static TemplateModelVariantProvider provider = new TemplateModelVariantProvider();
	
	@Override
	public void onInitializeClient() {
		Templates.chunkRerenderProxy = (world, pos) -> {
			if(world == MinecraftClient.getInstance().world) {
				MinecraftClient.getInstance().worldRenderer.scheduleBlockRender(
					ChunkSectionPos.getSectionCoord(pos.getX()),
					ChunkSectionPos.getSectionCoord(pos.getY()),
					ChunkSectionPos.getSectionCoord(pos.getZ())
				);
			}
		};
		
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
			@Override
			public Identifier getFabricId() {
				return Templates.id("dump-caches");
			}
			
			@Override
			public void reload(ResourceManager resourceManager) {
				provider.dumpCache();
			}
		});
		
		ModelLoadingRegistry.INSTANCE.registerVariantProvider(rm -> provider);
		BlockRenderLayerMap.INSTANCE.putBlock(Templates.SLOPE, RenderLayer.getCutout());
		
		//ADDON DEVELOEPRS: do this!
		provider.registerTemplateModels(
			//block
			Templates.SLOPE,
			//the blockstate you'd like the item model to show
			Templates.SLOPE.getDefaultState().with(SlopeBlock.FACING, Direction.EAST),
			//Function<BlockState, UnbakedModel> that creates your model
			SlopeUnbakedModel::new
		);
	}
}
