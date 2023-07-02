package io.github.cottonmc.templates.model;

import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelProviderException;
import net.fabricmc.fabric.api.client.model.ModelResourceProvider;
import net.fabricmc.fabric.api.client.model.ModelVariantProvider;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class TemplateModelVariantProvider implements ModelResourceProvider, ModelVariantProvider {
	private final Map<Identifier, Supplier<UnbakedModel>> factories = new HashMap<>();
	private final Map<ModelIdentifier, Identifier> itemAssignments = new HashMap<>();
	
	private final Map<Identifier, UnbakedModel> cache = new HashMap<>();
	
	@Override
	public @Nullable UnbakedModel loadModelResource(Identifier resourceId, ModelProviderContext context) throws ModelProviderException {
		UnbakedModel cacheResult = cache.get(resourceId);
		if(cacheResult != null) return cacheResult;
		
		//Either we have a factory for this model (just haven't cached its output yet),
		Supplier<UnbakedModel> factory = factories.get(resourceId);
		if(factory != null) {
			UnbakedModel freshModel = factory.get();
			cache.put(resourceId, freshModel);
			return freshModel;
		}
		
		//or we don't cover this model at all.
		return null;
	}
	
	//For blocks, you can point the game directly at the custom model in the blockstate json file.
	//Item models don't have that layer of indirection; it always wants to load the hardcoded "item:id#inventory" model.
	//You *would* be able to create a model json for it and set the "parent" field to the custom model,
	//but json models are never allowed to have non-json models as a parent, and template unbaked models are not json models. Ah well.
	//So, instead, we use a ModelVariantProvider to clunkily redirect the item:id#inventory model to the blockmodel.
	//Not my favorite solution (for one, it precludes setting custom rotations in the item model) but we'll live.
	@Override
	public @Nullable UnbakedModel loadModelVariant(ModelIdentifier modelId, ModelProviderContext context) throws ModelProviderException {
		Identifier customModelId = itemAssignments.get(modelId);
		return customModelId == null ? null : loadModelResource(customModelId, context);
	}
	
	// "public api"
	
	public void addTemplateModel(Identifier id, Supplier<UnbakedModel> modelFactory) {
		factories.put(id, modelFactory);
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
	
	public void dumpCache() {
		cache.clear();
	}
}
