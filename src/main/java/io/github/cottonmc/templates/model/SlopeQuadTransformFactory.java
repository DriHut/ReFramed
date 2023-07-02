package io.github.cottonmc.templates.model;

import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachedBlockView;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.texture.Sprite;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Supplier;

public class SlopeQuadTransformFactory implements TemplateQuadTransformFactory {
	public SlopeQuadTransformFactory(TemplateAppearanceManager tam) {
		this.tam = tam;
		this.r = Objects.requireNonNull(RendererAccess.INSTANCE.getRenderer(), "A Fabric Rendering API implementation is required");
	}
	
	private final TemplateAppearanceManager tam;
	private final Renderer r;
	
	@Override
	public @NotNull RenderContext.QuadTransform blockTransformer(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier) {
		Direction dir = state.get(Properties.HORIZONTAL_FACING);
		
		Object renderAttach = ((RenderAttachedBlockView) blockView).getBlockEntityRenderAttachment(pos);
		BlockState template = (renderAttach instanceof BlockState s) ? s : Blocks.AIR.getDefaultState();
		Block block = template.getBlock();
		
		TemplateAppearance appearance;
		RenderMaterial material;
		int globalTint;
		
		if(block == null || block == Blocks.AIR) {
			appearance = tam.getDefaultAppearance();
			material = r.materialFinder().clear().blendMode(BlendMode.CUTOUT).find();
			globalTint = 0xFFFFFF;
		} else {
			appearance = tam.getAppearance(template);
			material = r.materialFinder().clear()
				.disableDiffuse(false)
				.ambientOcclusion(TriState.FALSE)
				.blendMode(BlendMode.fromRenderLayer(RenderLayers.getBlockLayer(template)))
				.find();
			
			BlockColorProvider tint = ColorProviderRegistry.BLOCK.get(block);
			if(tint != null) globalTint = 0xFF000000 | tint.getColor(template, blockView, pos, 1);
			else globalTint = 0xFFFFFF;
		}
		
		return new Transformer(appearance, material, globalTint);
	}
	
	@Override
	public @NotNull RenderContext.QuadTransform itemTransformer(ItemStack stack, Supplier<Random> randomSupplier) {
		return new Transformer(tam.getDefaultAppearance(), r.materialFinder().clear().find(), 0xFFFFFF);
	}
	
	private static record Transformer(TemplateAppearance appearance, RenderMaterial material, int color) implements RenderContext.QuadTransform {
		@Override
		public boolean transform(MutableQuadView quad) {
			quad.material(material);
			
			Sprite sprite = null;
			
			//TODO: this newly-simplified direction passing in hasColor is almost certainly incorrect
			
			switch(quad.tag()) {
				case SlopeBaseMesh.TAG_SLOPE -> {
					if(appearance.hasColor(Direction.UP)) quad.color(color, color, color, color);
					sprite = appearance.getSprite(Direction.UP);
				}
				case SlopeBaseMesh.TAG_LEFT -> {
					if(appearance.hasColor(Direction.EAST)) quad.color(color, color, color, color);
					sprite = appearance.getSprite(Direction.EAST);
				}
				case SlopeBaseMesh.TAG_RIGHT -> {
					if(appearance.hasColor(Direction.WEST)) quad.color(color, color, color, color);
					sprite = appearance.getSprite(Direction.WEST);
				}
				case SlopeBaseMesh.TAG_BACK -> {
					if(appearance.hasColor(Direction.SOUTH)) quad.color(color, color, color, color);
					sprite = appearance.getSprite(Direction.SOUTH);
				}
				case SlopeBaseMesh.TAG_BOTTOM -> {
					if(appearance.hasColor(Direction.DOWN)) quad.color(color, color, color, color);
					sprite = appearance.getSprite(Direction.DOWN);
				}
			}
			
			if(sprite == null) return false; //remove this quad
			
			quad.spriteBake(sprite, MutableQuadView.BAKE_NORMALIZED);
			return true;
		}
	}
}
