package fr.adrien1106.reframed.client;

import fr.adrien1106.reframed.client.model.UnbakedAutoRetexturedModel;
import fr.adrien1106.reframed.client.model.UnbakedDoubleRetexturedModel;
import fr.adrien1106.reframed.client.model.UnbakedJsonRetexturedModel;
import fr.adrien1106.reframed.client.model.UnbakedRetexturedModel;
import fr.adrien1106.reframed.client.model.apperance.CamoAppearanceManager;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class ReFramedClientHelper {

	public ReFramedClientHelper(ReFramedModelProvider prov) {
		this.prov = prov;
	}

	private final ReFramedModelProvider prov;


	public UnbakedRetexturedModel auto(Identifier parent) {
		return new UnbakedAutoRetexturedModel(parent);
	}

	public UnbakedRetexturedModel json(Identifier parent) {
		return new UnbakedJsonRetexturedModel(parent);
	}

	public UnbakedModel autoDouble(Identifier first, Identifier second) {
		return new UnbakedDoubleRetexturedModel(auto(first), auto(second));
	}

	public void addReFramedModel(Identifier id, UnbakedModel unbaked) {
		prov.addReFramedModel(id, unbaked);
	}

	public void assignItemModel(Identifier model_id, ItemConvertible... item_convertibles) {
		prov.assignItemModel(model_id, item_convertibles);
	}

	public CamoAppearanceManager getCamoApperanceManager(Function<SpriteIdentifier, Sprite> spriteLookup) {
		return prov.getCamoApperanceManager(spriteLookup);
	}

	public @NotNull Renderer getFabricRenderer() {
		Renderer obj = RendererAccess.INSTANCE.getRenderer();
		if(obj != null) return obj;
		
		//Welp, not much more we can do, this mod heavily relies on frapi
		String msg = "A Fabric Rendering API implementation is required to use ReFramed!";
		
		if(!FabricLoader.getInstance().isModLoaded("fabric-renderer-indigo"))
			msg += "\nI noticed you don't have Indigo installed, which is a part of the complete Fabric API package.";
		if(FabricLoader.getInstance().isModLoaded("sodium"))
			msg += "\nI noticed you have Sodium installed - consider also installing Indium to provide a compatible renderer implementation.";
		
		throw new NullPointerException(msg);
	}
}
