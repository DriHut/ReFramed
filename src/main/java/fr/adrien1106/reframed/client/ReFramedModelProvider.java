package fr.adrien1106.reframed.client;

import fr.adrien1106.reframed.client.model.apperance.CamoAppearanceManager;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelResourceProvider;
import net.fabricmc.fabric.api.client.model.ModelVariantProvider;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class ReFramedModelProvider implements ModelResourceProvider, ModelVariantProvider {
	private final Map<Identifier, UnbakedModel> models = new HashMap<>();
	private final Map<ModelIdentifier, Identifier> itemAssignments = new HashMap<>();
	
	private volatile CamoAppearanceManager appearanceManager;
	
	/// fabric model provider api
	
	@Override
	public @Nullable UnbakedModel loadModelResource(Identifier resourceId, ModelProviderContext context) {
		return models.get(resourceId);
	}
	
	//For blocks, you can point the game directly at the custom model in the blockstate json file.
	//Item models don't have that layer of indirection; it always wants to load the hardcoded "item:id#inventory" model.
	//You *would* be able to create a model json for it and set the "parent" field to the custom model,
	//but json models are never allowed to have non-json models as a parent, and frame unbaked models are not json models. Ah well.
	//So, instead, we use a ModelVariantProvider to redirect attempts to load the item:id#inventory model.
	@Override
	public @Nullable UnbakedModel loadModelVariant(ModelIdentifier model, ModelProviderContext context) {
		Identifier custom_model = itemAssignments.get(model);
		return custom_model == null ? null : loadModelResource(custom_model, context);
	}
	
	/// camo appearance manager cache
	
	public CamoAppearanceManager getCamoApperanceManager(Function<SpriteIdentifier, Sprite> spriteLookup) {
		//This is kind of needlessly sketchy using the "volatile double checked locking" pattern.
		//I'd like all frame models to use the same CamoApperanceManager, despite the model
		//baking process happening concurrently on several threads, but I also don't want to
		//hold up the model baking process too long.
		
		//Volatile field read:
		CamoAppearanceManager read = appearanceManager;
		
		if(read == null) {
			//Acquire a lock:
			synchronized(this) {
				//There's a chance another thread just initialized the object and released the lock
				//while we were waiting for it, so we do another volatile field read (the "double check"):
				read = appearanceManager;
				if(read == null) {
					//If no-one has initialized it still, I guess it falls to us
					read = appearanceManager = new CamoAppearanceManager(spriteLookup);
				}
			}
		}
		
		return Objects.requireNonNull(read);
	}
	
	public void dumpCache() {
		CamoAppearanceManager.dumpCahe();
		appearanceManager = null; //volatile write
	}
	
	public void addReFramedModel(Identifier id, UnbakedModel unbaked) {
		models.put(id, unbaked);
	}
	
	public void assignItemModel(Identifier model_id, Identifier... itemIds) {
		for(Identifier itemId : itemIds) itemAssignments.put(new ModelIdentifier(itemId, "inventory"), model_id);
	}
	
	public void assignItemModel(Identifier model_id, ItemConvertible... itemConvs) {
		for(ItemConvertible itemConv : itemConvs) assignItemModel(model_id, Registries.ITEM.getId(itemConv.asItem()));
	}
}
