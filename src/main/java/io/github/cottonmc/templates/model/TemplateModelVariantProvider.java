package io.github.cottonmc.templates.model;

import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelProviderException;
import net.fabricmc.fabric.api.client.model.ModelVariantProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.registry.Registries;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class TemplateModelVariantProvider implements ModelVariantProvider {
	private final Map<ModelIdentifier, Supplier<UnbakedModel>> factories = new HashMap<>();
	private final Map<ModelIdentifier, UnbakedModel> cache = new HashMap<>();
	
	@Override
	public UnbakedModel loadModelVariant(ModelIdentifier modelId, ModelProviderContext context) throws ModelProviderException {
		UnbakedModel cacheResult = cache.get(modelId);
		if(cacheResult != null) return cacheResult;
		
		//Either we have a factory for this model (just haven't cached its output yet),
		Supplier<UnbakedModel> factory = factories.get(modelId);
		if(factory != null) {
			UnbakedModel freshModel = factory.get();
			cache.put(modelId, freshModel);
			return freshModel;
		}
		
		//or we don't cover this model at all.
		return null;
	}
	
	public void registerTemplateModels(Block block, BlockState itemState, Function<BlockState, UnbakedModel> model) {
		for(BlockState state : block.getStateManager().getStates()) factories.put(BlockModels.getModelId(state), () -> model.apply(state));
		factories.put(new ModelIdentifier(Registries.ITEM.getId(block.asItem()), "inventory"), () -> model.apply(itemState));
	}
	
	public void dumpCache() {
		cache.clear();
	}
}
