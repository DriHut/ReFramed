package io.github.cottonmc.slopetest.model;

import java.util.HashMap;

import io.github.cottonmc.slopetest.SlopeTest;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelProviderException;
import net.fabricmc.fabric.api.client.model.ModelVariantProvider;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.util.registry.Registry;

public class SlopeModelVariantProvider implements ModelVariantProvider {

    private final HashMap<ModelIdentifier, UnbakedModel> variants = new HashMap<>();
    
    public SlopeModelVariantProvider() {
        // A bit ugly to hard-code this in the constructor, but however it is done, 
        // best to have variants for all the mod's blocks in a single provider/map 
        // instance so that model loader doesn't have to query a large number of providers.
        
        for(BlockState state : SlopeTest.SLOPE.getStateFactory().getStates()) {
            variants.put(BlockModels.getModelId(state), (SimpleUnbakedModel)() -> new SlopeTestModel(state));
        }
        
        variants.put(new ModelIdentifier(Registry.ITEM.getId(SlopeTest.SLOPE.asItem()), "inventory"), (SimpleUnbakedModel)() -> new SlopeTestModel(SlopeTest.SLOPE.getDefaultState()));
    }
    
    @Override
    public UnbakedModel loadModelVariant(ModelIdentifier modelId, ModelProviderContext context) throws ModelProviderException {
        return variants.get(modelId);
    }
}
