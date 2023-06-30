package io.github.cottonmc.templates.model;

import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelProviderException;
import net.fabricmc.fabric.api.client.model.ModelResourceProvider;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class TemplateModelVariantProvider implements ModelResourceProvider {
	private final Map<Identifier, Supplier<UnbakedModel>> factories = new HashMap<>();
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
	
	public void addTemplateModel(Identifier id, Supplier<UnbakedModel> modelFactory) {
		factories.put(id, modelFactory);
	}
	
	public void dumpCache() {
		cache.clear();
	}
}
