package io.github.cottonmc.slopetest;

import io.github.cottonmc.slopetest.block.SlopeTestBlock;
import io.github.cottonmc.slopetest.model.SlopeTestModel;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.minecraft.util.math.Direction;

public class SlopeTestClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
	    ModelLoadingRegistry.INSTANCE.registerVariantProvider(rm -> SlopeTest.provider);
	    SlopeTest.provider.registerTemplateBlock(SlopeTest.SLOPE, SlopeTest.SLOPE.getDefaultState().with(SlopeTestBlock.FACING, Direction.SOUTH), SlopeTestModel::new);
	}
}
