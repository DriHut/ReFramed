package io.github.cottonmc.templates;

import io.github.cottonmc.templates.block.SlopeBlock;
import io.github.cottonmc.templates.model.SlopeUnbakedModel;
import io.github.cottonmc.templates.model.TemplateModelVariantProvider;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
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
		
		ModelLoadingRegistry.INSTANCE.registerVariantProvider(rm -> provider);
		provider.registerTemplateModels2(Templates.SLOPE, Templates.SLOPE.getDefaultState().with(SlopeBlock.FACING, Direction.SOUTH), SlopeUnbakedModel::new);
		
		BlockRenderLayerMap.INSTANCE.putBlock(Templates.SLOPE, RenderLayer.getCutout());
	}
}
