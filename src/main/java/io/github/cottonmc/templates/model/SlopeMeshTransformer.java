package io.github.cottonmc.templates.model;

import io.github.cottonmc.templates.util.SpriteSet;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.BlendMode;
import net.fabricmc.fabric.api.renderer.v1.material.MaterialFinder;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachedBlockView;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;

import java.util.function.Function;
import java.util.function.Supplier;

public class SlopeMeshTransformer implements MeshTransformer {
	public SlopeMeshTransformer(Function<SpriteIdentifier, Sprite> spriteLookup) {
		this.sprites = new SpriteSet(spriteLookup);
	}
	
	private final MinecraftClient minecraft = MinecraftClient.getInstance();
	private final SpriteSet sprites;
	private final MaterialFinder finder = RendererAccess.INSTANCE.getRenderer().materialFinder();
	
	private int color;
	private Direction dir;
	private RenderMaterial material;
	
	@Override
	public MeshTransformer prepare(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier) {
		dir = state.get(Properties.HORIZONTAL_FACING);
		color = 0xffffff;
		
		Object renderAttach = ((RenderAttachedBlockView) blockView).getBlockEntityRenderAttachment(pos);
		BlockState template = (renderAttach instanceof BlockState s) ? s : Blocks.AIR.getDefaultState();
		final Block block = template.getBlock();
		
		if(block == Blocks.AIR) {
			sprites.clear();
			material = finder.clear().blendMode(BlendMode.CUTOUT).find();
		} else {
			material = finder.clear()
				.disableDiffuse(false)
				.ambientOcclusion(TriState.FALSE)
				.blendMode(BlendMode.fromRenderLayer(RenderLayers.getBlockLayer(state)))
				.find();
			
			BakedModel model = minecraft.getBlockRenderManager().getModel(template);
			sprites.prepare(model, randomSupplier.get());
			BlockColorProvider blockColor = ColorProviderRegistry.BLOCK.get(block);
			if(blockColor != null) color = 0xff000000 | blockColor.getColor(template, blockView, pos, 1);
		}
		return this;
	}
	
	@Override
	public MeshTransformer prepare(ItemStack stack, Supplier<Random> randomSupplier) {
		dir = Direction.NORTH;
		color = 0xffffff;
		sprites.clear();
		material = finder.clear().find();
		return this;
	}
	
	@Override
	public boolean transform(MutableQuadView quad) {
		quad.material(material);
		
		final SpriteSet sprites = this.sprites;
		switch(quad.tag()) {
			case SlopeBaseMesh.TAG_SLOPE -> {
				if(sprites.hasColor(Direction.UP)) quad.color(color, color, color, color);
				paintSlope(quad, dir, sprites.getSprite(Direction.UP));
			}
			case SlopeBaseMesh.TAG_LEFT -> {
				final Direction leftDir = this.dir.rotateYCounterclockwise();
				if(sprites.hasColor(leftDir)) quad.color(color, color, color, color);
				paintLeftSide(quad, dir, sprites.getSprite(leftDir));
			}
			case SlopeBaseMesh.TAG_RIGHT -> {
				final Direction rightDir = this.dir.rotateYClockwise();
				if(sprites.hasColor(rightDir)) quad.color(color, color, color, color);
				paintRightSide(quad, dir, sprites.getSprite(rightDir));
			}
			case SlopeBaseMesh.TAG_BACK -> {
				if(sprites.hasColor(dir)) quad.color(color, color, color, color);
				paintBack(quad, dir, sprites.getSprite(dir));
			}
			case SlopeBaseMesh.TAG_BOTTOM -> {
				if(sprites.hasColor(Direction.DOWN)) quad.color(color, color, color, color);
				paintBottom(quad, sprites.getSprite(Direction.DOWN));
			}
		}
		return true;
	}
	
	private static void paintSlope(MutableQuadView quad, Direction dir, Sprite sprite) {
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
	
	private static void paintLeftSide(MutableQuadView quad, Direction dir, Sprite sprite) {
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
	
	private static void paintRightSide(MutableQuadView quad, Direction dir, Sprite sprite) {
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
	
	private static void paintBack(MutableQuadView quad, Direction dir, Sprite sprite) {
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
	
	private static void paintBottom(MutableQuadView quad, Sprite sprite) {
		quad.uv(0, sprite.getMinU(), sprite.getMaxV())
			.uv(1, sprite.getMaxU(), sprite.getMaxV())
			.uv(2, sprite.getMaxU(), sprite.getMinV())
			.uv(3, sprite.getMinU(), sprite.getMinV());
	}
}
