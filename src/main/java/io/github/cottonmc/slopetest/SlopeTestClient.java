package io.github.cottonmc.slopetest;

import io.github.cottonmc.slopetest.block.entity.SlopeTestEntity;
import io.github.cottonmc.slopetest.block.entity.render.SlopeTestRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.render.BlockEntityRendererRegistry;

public class SlopeTestClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		BlockEntityRendererRegistry.INSTANCE.register(SlopeTestEntity.class, new SlopeTestRenderer());
	}
}
