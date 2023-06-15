package io.github.cottonmc.templates.model;

import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;

public abstract class AbstractModel implements BakedModel, FabricBakedModel {
	protected static final Renderer RENDERER = RendererAccess.INSTANCE.getRenderer();
	
	static {
		if(RENDERER == null) {
			throw new ExceptionInInitializerError("RenderAccess.INSTANCE must be populated");
		}
	}
	
	protected final Sprite modelSprite;
	protected final ModelTransformation transformation;
	
	protected AbstractModel(Sprite sprite, ModelTransformation transformation) {
		this.modelSprite = sprite;
		this.transformation = transformation;
	}
	
	@Override
	public boolean useAmbientOcclusion() {
		return true;
	}
	
	@Override
	public boolean hasDepth() {
		return true;
	}
	
	@Override
	public boolean isBuiltin() {
		return false;
	}
	
	@Override
	public Sprite getParticleSprite() {
		//TODO
		return MinecraftClient.getInstance().getBakedModelManager().getMissingModel().getParticleSprite();
	}
	
	@Override
	public boolean isSideLit() {
		return false; //?
	}
	
	@Override
	public ModelTransformation getTransformation() {
		return transformation;
	}
}
