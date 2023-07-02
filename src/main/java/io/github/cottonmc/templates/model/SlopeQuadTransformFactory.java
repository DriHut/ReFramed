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
		
		return new Transformer(dir, appearance, material, globalTint);
	}
	
	@Override
	public @NotNull RenderContext.QuadTransform itemTransformer(ItemStack stack, Supplier<Random> randomSupplier) {
		return new Transformer(Direction.EAST, tam.getDefaultAppearance(), r.materialFinder().clear().find(), 0xFFFFFF);
	}
	
	private static record Transformer(Direction dir, TemplateAppearance appearance, RenderMaterial material, int color) implements RenderContext.QuadTransform {
		@Override
		public boolean transform(MutableQuadView quad) {
			quad.material(material);
			
			switch(quad.tag()) {
				case SlopeBaseMesh.TAG_SLOPE -> {
					if(appearance.hasColor(Direction.UP)) quad.color(color, color, color, color);
					paintSlope(quad, appearance.getSprite(Direction.UP));
				}
				case SlopeBaseMesh.TAG_LEFT -> {
					final Direction leftDir = this.dir.rotateYCounterclockwise();
					if(appearance.hasColor(leftDir)) quad.color(color, color, color, color);
					paintLeftSide(quad, appearance.getSprite(leftDir));
				}
				case SlopeBaseMesh.TAG_RIGHT -> {
					final Direction rightDir = this.dir.rotateYClockwise();
					if(appearance.hasColor(rightDir)) quad.color(color, color, color, color);
					paintRightSide(quad, appearance.getSprite(rightDir));
				}
				case SlopeBaseMesh.TAG_BACK -> {
					if(appearance.hasColor(dir)) quad.color(color, color, color, color);
					paintBack(quad, appearance.getSprite(dir));
				}
				case SlopeBaseMesh.TAG_BOTTOM -> {
					if(appearance.hasColor(Direction.DOWN)) quad.color(color, color, color, color);
					paintBottom(quad, appearance.getSprite(Direction.DOWN));
				}
			}
			
			return true;
		}
		
		private void paintSlope(MutableQuadView quad, Sprite sprite) {
			switch(dir) {
				case NORTH -> quad.uv(0, sprite.getMinU(), sprite.getMinV())
					.uv(1, sprite.getMinU(), sprite.getMaxV())
					.uv(2, sprite.getMaxU(), sprite.getMaxV())
					.uv(3, sprite.getMaxU(), sprite.getMinV());
				case SOUTH -> quad.uv(0, sprite.getMaxU(), sprite.getMaxV())
					.uv(1, sprite.getMaxU(), sprite.getMinV())
					.uv(2, sprite.getMinU(), sprite.getMinV())
					.uv(3, sprite.getMinU(), sprite.getMaxV());
				case EAST -> quad.uv(0, sprite.getMinU(), sprite.getMaxV())
					.uv(1, sprite.getMaxU(), sprite.getMaxV())
					.uv(2, sprite.getMaxU(), sprite.getMinV())
					.uv(3, sprite.getMinU(), sprite.getMinV());
				case WEST -> quad.uv(0, sprite.getMaxU(), sprite.getMinV())
					.uv(1, sprite.getMinU(), sprite.getMinV())
					.uv(2, sprite.getMinU(), sprite.getMaxV())
					.uv(3, sprite.getMaxU(), sprite.getMaxV());
			}
		}
		
		private void paintLeftSide(MutableQuadView quad, Sprite sprite) {
			switch(dir) {
				case NORTH, EAST, WEST -> quad.uv(0, sprite.getMinU(), sprite.getMaxV())
					.uv(1, sprite.getMaxU(), sprite.getMaxV())
					.uv(2, sprite.getMaxU(), sprite.getMinV())
					.uv(3, sprite.getMinU(), sprite.getMinV());
				case SOUTH -> quad.uv(0, sprite.getMaxU(), sprite.getMinV())
					.uv(1, sprite.getMinU(), sprite.getMinV())
					.uv(2, sprite.getMinU(), sprite.getMaxV())
					.uv(3, sprite.getMaxU(), sprite.getMaxV());
			}
		}
		
		private void paintRightSide(MutableQuadView quad, Sprite sprite) {
			switch(dir) {
				case NORTH, WEST -> quad.uv(0, sprite.getMaxU(), sprite.getMaxV())
					.uv(1, sprite.getMaxU(), sprite.getMinV())
					.uv(2, sprite.getMinU(), sprite.getMinV())
					.uv(3, sprite.getMinU(), sprite.getMaxV());
				case SOUTH, EAST -> quad.uv(0, sprite.getMinU(), sprite.getMinV())
					.uv(1, sprite.getMinU(), sprite.getMaxV())
					.uv(2, sprite.getMaxU(), sprite.getMaxV())
					.uv(3, sprite.getMaxU(), sprite.getMinV());
			}
		}
		
		private void paintBack(MutableQuadView quad, Sprite sprite) {
			switch(dir) {
				case NORTH, EAST -> quad.uv(0, sprite.getMaxU(), sprite.getMaxV())
					.uv(1, sprite.getMaxU(), sprite.getMinV())
					.uv(2, sprite.getMinU(), sprite.getMinV())
					.uv(3, sprite.getMinU(), sprite.getMaxV());
				case SOUTH, WEST -> quad.uv(0, sprite.getMinU(), sprite.getMaxV())
					.uv(1, sprite.getMaxU(), sprite.getMaxV())
					.uv(2, sprite.getMaxU(), sprite.getMinV())
					.uv(3, sprite.getMinU(), sprite.getMinV());
			}
		}
		
		private void paintBottom(MutableQuadView quad, Sprite sprite) {
			quad.uv(0, sprite.getMinU(), sprite.getMaxV())
				.uv(1, sprite.getMaxU(), sprite.getMaxV())
				.uv(2, sprite.getMaxU(), sprite.getMinV())
				.uv(3, sprite.getMinU(), sprite.getMinV());
		}
	}
}
