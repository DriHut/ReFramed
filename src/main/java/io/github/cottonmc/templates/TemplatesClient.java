package io.github.cottonmc.templates;

import io.github.cottonmc.templates.block.SlopeBlock;
import io.github.cottonmc.templates.model.SlopeModel;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.minecraft.util.math.Direction;

public class TemplatesClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
	    ModelLoadingRegistry.INSTANCE.registerVariantProvider(rm -> Templates.provider);
	    Templates.provider.registerTemplateModels(Templates.SLOPE, Templates.SLOPE.getDefaultState().with(SlopeBlock.FACING, Direction.SOUTH), SlopeModel::new);
	}
}
