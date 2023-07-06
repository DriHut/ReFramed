package io.github.cottonmc.templates;

import io.github.cottonmc.templates.model.RetexturedJsonModelUnbakedModel;
import io.github.cottonmc.templates.model.RetexturedMeshUnbakedModel;
import io.github.cottonmc.templates.model.SlopeBaseMesh;
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
		
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), Templates.CUBE, Templates.FENCE, Templates.SLAB, Templates.SLOPE, Templates.WALL);
		
		provider.addTemplateModel(Templates.id("cube_special"), new RetexturedJsonModelUnbakedModel(Templates.id("block/cube")));
		provider.addTemplateModel(Templates.id("fence_post_special"), new RetexturedJsonModelUnbakedModel(Templates.id("block/fence_post")));
		provider.addTemplateModel(Templates.id("fence_side_special"), new RetexturedJsonModelUnbakedModel(Templates.id("block/fence_side")));
		provider.addTemplateModel(Templates.id("fence_inventory_special"), new RetexturedJsonModelUnbakedModel(Templates.id("block/fence_inventory")));
		provider.addTemplateModel(Templates.id("slab_bottom_special"), new RetexturedJsonModelUnbakedModel(Templates.id("block/slab_bottom")));
		provider.addTemplateModel(Templates.id("slab_top_special"), new RetexturedJsonModelUnbakedModel(Templates.id("block/slab_top")));
		provider.addTemplateModel(Templates.id("slope_special"), new RetexturedMeshUnbakedModel(Templates.id("block/slope_base"), SlopeBaseMesh::make));
		provider.addTemplateModel(Templates.id("wall_inventory_special"), new RetexturedJsonModelUnbakedModel(Templates.id("block/wall_inventory")));
		provider.addTemplateModel(Templates.id("wall_post_special"), new RetexturedJsonModelUnbakedModel(Templates.id("block/wall_post")));
		provider.addTemplateModel(Templates.id("wall_side_special"), new RetexturedJsonModelUnbakedModel(Templates.id("block/wall_side")));
		provider.addTemplateModel(Templates.id("wall_side_tall_special"), new RetexturedJsonModelUnbakedModel(Templates.id("block/wall_side_tall")));
		
		provider.assignItemModel(Templates.id("cube_special"), Templates.CUBE);
		provider.assignItemModel(Templates.id("fence_inventory_special"), Templates.FENCE);
		provider.assignItemModel(Templates.id("slope_special"), Templates.SLOPE);
		provider.assignItemModel(Templates.id("slab_bottom_special"), Templates.SLAB);
		provider.assignItemModel(Templates.id("wall_inventory_special"), Templates.WALL);
	}
}
