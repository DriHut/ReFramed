package io.github.cottonmc.templates;

import io.github.cottonmc.templates.api.TemplatesClientApi;
import io.github.cottonmc.templates.model.TemplateAppearanceManager;
import io.github.cottonmc.templates.model.UnbakedAutoRetexturedModel;
import io.github.cottonmc.templates.model.UnbakedJsonRetexturedModel;
import io.github.cottonmc.templates.model.UnbakedMeshRetexturedModel;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class TemplatesClientApiImpl implements TemplatesClientApi {
	public TemplatesClientApiImpl(TemplatesModelProvider prov) {
		this.prov = prov;
	}
	
	private final TemplatesModelProvider prov;
	
	@Override
	public TweakableUnbakedModel auto(Identifier parent) {
		return new UnbakedAutoRetexturedModel(parent);
	}
	
	@Override
	public TweakableUnbakedModel json(Identifier parent) {
		return new UnbakedJsonRetexturedModel(parent);
	}
	
	@Override
	public TweakableUnbakedModel mesh(Identifier parent, Function<Function<SpriteIdentifier, Sprite>, Mesh> baseMeshFactory) {
		return new UnbakedMeshRetexturedModel(parent, baseMeshFactory);
	}
	
	@Override
	public void addTemplateModel(Identifier id, UnbakedModel unbaked) {
		prov.addTemplateModel(id, unbaked);
	}
	
	@Override
	public void assignItemModel(Identifier templateModelId, ModelIdentifier... modelIds) {
		prov.assignItemModel(templateModelId, modelIds);
	}
	
	@Override
	public void assignItemModel(Identifier templateModelId, Identifier... itemIds) {
		prov.assignItemModel(templateModelId, itemIds);
	}
	
	@Override
	public void assignItemModel(Identifier templateModelId, ItemConvertible... itemConvs) {
		prov.assignItemModel(templateModelId, itemConvs);
	}
	
	@Override
	public TemplateAppearanceManager getOrCreateTemplateApperanceManager(Function<SpriteIdentifier, Sprite> spriteLookup) {
		return prov.getOrCreateTemplateApperanceManager(spriteLookup);
	}
	
	@Override
	public @NotNull Renderer getFabricRenderer() {
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
