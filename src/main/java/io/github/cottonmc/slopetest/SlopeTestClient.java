package io.github.cottonmc.slopetest;

import io.github.cottonmc.slopetest.model.SlopeModelVariantProvider;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;

public class SlopeTestClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		//BlockEntityRendererRegistry.INSTANCE.register(SlopeTestEntity.class, new SlopeTestRenderer());
	    ModelLoadingRegistry.INSTANCE.registerVariantProvider(rm -> new SlopeModelVariantProvider());
	}
}
