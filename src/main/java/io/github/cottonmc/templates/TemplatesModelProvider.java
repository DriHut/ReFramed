package io.github.cottonmc.templates;

import io.github.cottonmc.templates.model.TemplateAppearanceManager;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelProviderException;
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

public class TemplatesModelProvider implements ModelResourceProvider, ModelVariantProvider {
	private final Map<Identifier, UnbakedModel> models = new HashMap<>();
	private final Map<ModelIdentifier, Identifier> itemAssignments = new HashMap<>();
	
	private volatile TemplateAppearanceManager appearanceManager;
	
	/// fabric model provider api
	
	@Override
	public @Nullable UnbakedModel loadModelResource(Identifier resourceId, ModelProviderContext context) {
		return models.get(resourceId);
	}
	
	//For blocks, you can point the game directly at the custom model in the blockstate json file.
	//Item models don't have that layer of indirection; it always wants to load the hardcoded "item:id#inventory" model.
	//You *would* be able to create a model json for it and set the "parent" field to the custom model,
	//but json models are never allowed to have non-json models as a parent, and template unbaked models are not json models. Ah well.
	//So, instead, we use a ModelVariantProvider to clunkily redirect the item:id#inventory model to the blockmodel.
	//Not my favorite solution but we'll live.
	@Override
	public @Nullable UnbakedModel loadModelVariant(ModelIdentifier modelId, ModelProviderContext context) {
		Identifier customModelId = itemAssignments.get(modelId);
		return customModelId == null ? null : loadModelResource(customModelId, context);
	}
	
	/// template appearance manager cache
	
	public TemplateAppearanceManager getOrCreateTemplateApperanceManager(Function<SpriteIdentifier, Sprite> spriteLookup) {
		//This is kind of needlessly fancy using the "volatile double checked locking" pattern.
		//I'd like all template models to use the same TemplateApperanceManager, despite the model
		//baking process happening concurrently on several threads.
		
		//Volatile field read:
		TemplateAppearanceManager read = appearanceManager;
		
		if(read == null) {
			//Acquire a lock:
			synchronized(this) {
				//There's a chance another thread just initialized the object and released the lock
				//while we were waiting for it, so we do another volatile field read (the "double check"):
				read = appearanceManager;
				if(read == null) {
					//If no-one has initialized it still, I guess we should do it
					read = appearanceManager = new TemplateAppearanceManager(spriteLookup);
				}
			}
		}
		
		return Objects.requireNonNull(read);
	}
	
	public void dumpCache() {
		appearanceManager = null;
	}
	
	/// "public api"
	
	public void addTemplateModel(Identifier id, UnbakedModel modelFactory) {
		models.put(id, modelFactory);
	}
	
	public void assignItemModel(Identifier templateModelId, ModelIdentifier... modelIds) {
		for(ModelIdentifier modelId : modelIds) itemAssignments.put(modelId, templateModelId);
	}
	
	public void assignItemModel(Identifier templateModelId, Identifier... itemIds) {
		for(Identifier itemId : itemIds) itemAssignments.put(new ModelIdentifier(itemId, "inventory"), templateModelId);
	}
	
	public void assignItemModel(Identifier templateModelId, ItemConvertible... itemConvs) {
		for(ItemConvertible itemConv : itemConvs) assignItemModel(templateModelId, Registries.ITEM.getId(itemConv.asItem()));
	}
}
