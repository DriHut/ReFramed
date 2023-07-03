package io.github.cottonmc.templates;

import io.github.cottonmc.templates.model.SlopeUnbakedModel;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkSectionPos;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class TemplatesClient implements ClientModInitializer {
	public static TemplatesModelProvider provider = new TemplatesModelProvider();
	
	public static @NotNull Renderer getFabricRenderer() {
		return Objects.requireNonNull(RendererAccess.INSTANCE.getRenderer(), "A Fabric Rendering API implementation is required to use Templates!");
	}
	
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
		
		ModelLoadingRegistry.INSTANCE.registerResourceProvider(rm -> provider); //block models
		ModelLoadingRegistry.INSTANCE.registerVariantProvider(rm -> provider); //item models
		
		BlockRenderLayerMap.INSTANCE.putBlock(Templates.SLOPE, RenderLayer.getCutout());
		
		provider.addTemplateModel(Templates.id("slope_special"), SlopeUnbakedModel::new);
		provider.assignItemModel(Templates.id("slope_special"), Templates.SLOPE);
	}
}
