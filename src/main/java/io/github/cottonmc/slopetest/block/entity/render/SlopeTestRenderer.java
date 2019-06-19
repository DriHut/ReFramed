package io.github.cottonmc.slopetest.block.entity.render;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.cottonmc.slopetest.SpriteSet;
import io.github.cottonmc.slopetest.block.entity.SlopeTestEntity;
import net.fabricmc.fabric.api.client.render.ColorProviderRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.block.BlockColorProvider;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import org.lwjgl.opengl.GL11;

public class SlopeTestRenderer extends BlockEntityRenderer<SlopeTestEntity> {

	@Override
	public void render(SlopeTestEntity be, double x, double y, double z, float partialTicks, int destroyStage) {
		final Tessellator tessellator = Tessellator.getInstance();
		final BufferBuilder buffer = tessellator.getBufferBuilder();
		MinecraftClient minecraft = MinecraftClient.getInstance();
		buffer.setOffset(x, y, z);
		GlStateManager.enableBlend();
		GlStateManager.disableAlphaTest();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		renderManager.textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		BlockState state = getWorld().getBlockState(be.getPos());
		Direction dir = state.get(Properties.HORIZONTAL_FACING);
		SpriteSet sprites;
		int color = 0xffffff;
		if (be.getRenderedBlock() != Blocks.AIR) {
			BlockState blockDefaultState = be.getRenderedBlock().getDefaultState();
			BakedModel model = minecraft.getBlockRenderManager().getModel(blockDefaultState);
			sprites = new SpriteSet(model);
			BlockColorProvider blockColor = ColorProviderRegistry.BLOCK.get(be.getRenderedBlock());
			if (blockColor != null) {
				color = blockColor.getColor(blockDefaultState, be.getWorld(), be.getPos(), 1);
			}
		} else {
			sprites = new SpriteSet(minecraft.getSpriteAtlas().getSprite("minecraft:block/scaffolding_top"), false);
		}
		buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_UV_COLOR);
		drawSlope(dir, sprites.getSprite(Direction.UP), buffer, sprites.hasColor(Direction.UP)? color : 0xffffff);
		drawLeftSide(dir, sprites.getSprite(dir.rotateYCounterclockwise()), buffer, sprites.hasColor(dir.rotateYCounterclockwise())? color : 0xffffff);
		drawRightSide(dir, sprites.getSprite(dir.rotateYClockwise()), buffer, sprites.hasColor(dir.rotateYClockwise())? color: 0xffffff);
		drawBack(dir, sprites.getSprite(dir), buffer, sprites.hasColor(dir)? color : 0xffffff);
		drawBottom(sprites.getSprite(Direction.DOWN), buffer, sprites.hasColor(Direction.DOWN)? color : 0xffffff);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		tessellator.draw();
		GlStateManager.disableBlend();
		GlStateManager.enableAlphaTest();
		buffer.setOffset(0.0d, 0.0d, 0.0d);
		super.render(be, x, y, z, partialTicks, destroyStage);
	}

	public static void drawSlope(Direction dir, Sprite sprite, BufferBuilder buffer, int color) {
		float r = (float)(color >> 16 & 255) / 255.0F;
		float g = (float)(color >> 8 & 255) / 255.0F;
		float b = (float)(color & 255) / 255.0F;
		switch (dir) {
			case NORTH:
				buffer.vertex(0f, 1f, 0f).texture(sprite.getMinU(), sprite.getMinV()).color(r, g, b, 1f).next();
				buffer.vertex(0f, 0f, 1f).texture(sprite.getMinU(), sprite.getMaxV()).color(r, g, b, 1f).next();
				buffer.vertex(1f, 0f, 1f).texture(sprite.getMaxU(), sprite.getMaxV()).color(r, g, b, 1f).next();
				buffer.vertex(1f, 1f, 0f).texture(sprite.getMaxU(), sprite.getMinV()).color(r, g, b, 1f).next();
				break;
			case SOUTH:
				buffer.vertex(0f, 0f, 0f).texture(sprite.getMaxU(), sprite.getMaxV()).color(r, g, b, 1f).next();
				buffer.vertex(0f, 1f, 1f).texture(sprite.getMaxU(), sprite.getMinV()).color(r, g, b, 1f).next();
				buffer.vertex(1f, 1f, 1f).texture(sprite.getMinU(), sprite.getMinV()).color(r, g, b, 1f).next();
				buffer.vertex(1f, 0f, 0f).texture(sprite.getMinU(), sprite.getMaxV()).color(r, g, b, 1f).next();
				break;
			case EAST:
				buffer.vertex(0f, 0f, 0f).texture(sprite.getMinU(), sprite.getMaxV()).color(r, g, b, 1f).next();
				buffer.vertex(0f, 0f, 1f).texture(sprite.getMaxU(), sprite.getMaxV()).color(r, g, b, 1f).next();
				buffer.vertex(1f, 1f, 1f).texture(sprite.getMaxU(), sprite.getMinV()).color(r, g, b, 1f).next();
				buffer.vertex(1f, 1f, 0f).texture(sprite.getMinU(), sprite.getMinV()).color(r, g, b, 1f).next();
				break;
			case WEST:
				buffer.vertex(0f, 1f, 0f).texture(sprite.getMaxU(), sprite.getMinV()).color(r, g, b, 1f).next();
				buffer.vertex(0f, 1f, 1f).texture(sprite.getMinU(), sprite.getMinV()).color(r, g, b, 1f).next();
				buffer.vertex(1f, 0f, 1f).texture(sprite.getMinU(), sprite.getMaxV()).color(r, g, b, 1f).next();
				buffer.vertex(1f, 0f, 0f).texture(sprite.getMaxU(), sprite.getMaxV()).color(r, g, b, 1f).next();
			default:
				break;
		}
	}

	public static void drawLeftSide(Direction dir, Sprite sprite, BufferBuilder buffer, int color) {
		float r = (float)(color >> 16 & 255) / 255.0F;
		float g = (float)(color >> 8 & 255) / 255.0F;
		float b = (float)(color & 255) / 255.0F;
		switch(dir) {
			case NORTH:
				buffer.vertex(0f, 0f, 0f).texture(sprite.getMinU(), sprite.getMaxV()).color(r, g, b, 1f).next();
				buffer.vertex(0f, 0f, 1f).texture(sprite.getMaxU(), sprite.getMaxV()).color(r, g, b, 1f).next();
				buffer.vertex(0f, 0f, 1f).texture(sprite.getMaxU(), sprite.getMinV()).color(r, g, b, 1f).next();
				buffer.vertex(0f, 1f, 0f).texture(sprite.getMinU(), sprite.getMinV()).color(r, g, b, 1f).next();
				break;
			case SOUTH:
				buffer.vertex(1f, 0f, 0f).texture(sprite.getMaxU(), sprite.getMinV()).color(r, g, b, 1f).next();
				buffer.vertex(1f, 1f, 1f).texture(sprite.getMinU(), sprite.getMinV()).color(r, g, b, 1f).next();
				buffer.vertex(1f, 0f, 1f).texture(sprite.getMinU(), sprite.getMaxV()).color(r, g, b, 1f).next();
				buffer.vertex(1f, 0f, 0f).texture(sprite.getMaxU(), sprite.getMaxV()).color(r, g, b, 1f).next();
				break;
			case EAST:
				buffer.vertex(1f, 0f, 0f).texture(sprite.getMinU(), sprite.getMaxV()).color(r, g, b, 1f).next();
				buffer.vertex(0f, 0f, 0f).texture(sprite.getMaxU(), sprite.getMaxV()).color(r, g, b, 1f).next();
				buffer.vertex(0f, 0f, 0f).texture(sprite.getMaxU(), sprite.getMinV()).color(r, g, b, 1f).next();
				buffer.vertex(1f, 1f, 0f).texture(sprite.getMinU(), sprite.getMinV()).color(r, g, b, 1f).next();
				break;
			case WEST:
				buffer.vertex(0f, 0f, 1f).texture(sprite.getMinU(), sprite.getMaxV()).color(r, g, b, 1f).next();
				buffer.vertex(1f, 0f, 1f).texture(sprite.getMaxU(), sprite.getMaxV()).color(r, g, b, 1f).next();
				buffer.vertex(1f, 0f, 1f).texture(sprite.getMaxU(), sprite.getMinV()).color(r, g, b, 1f).next();
				buffer.vertex(0f, 1f, 1f).texture(sprite.getMinU(), sprite.getMinV()).color(r, g, b, 1f).next();
			default:
				break;
		}
	}

	public static void drawRightSide(Direction dir, Sprite sprite, BufferBuilder buffer, int color) {
		float r = (float)(color >> 16 & 255) / 255.0F;
		float g = (float)(color >> 8 & 255) / 255.0F;
		float b = (float)(color & 255) / 255.0F;
		switch(dir) {
			case NORTH:
				buffer.vertex(1f, 0f, 0f).texture(sprite.getMaxU(), sprite.getMaxV()).color(r, g, b, 1f).next();
				buffer.vertex(1f, 1f, 0f).texture(sprite.getMaxU(), sprite.getMinV()).color(r, g, b, 1f).next();
				buffer.vertex(1f, 0f, 1f).texture(sprite.getMinU(), sprite.getMinV()).color(r, g, b, 1f).next();
				buffer.vertex(1f, 0f, 1f).texture(sprite.getMinU(), sprite.getMaxV()).color(r, g, b, 1f).next();
				break;
			case SOUTH:
				buffer.vertex(0f, 0f, 0f).texture(sprite.getMinU(), sprite.getMinV()).color(r, g, b, 1f).next();
				buffer.vertex(0f, 0f, 0f).texture(sprite.getMinU(), sprite.getMaxV()).color(r, g, b, 1f).next();
				buffer.vertex(0f, 0f, 1f).texture(sprite.getMaxU(), sprite.getMaxV()).color(r, g, b, 1f).next();
				buffer.vertex(0f, 1f, 1f).texture(sprite.getMaxU(), sprite.getMinV()).color(r, g, b, 1f).next();
				break;
			case EAST:
				buffer.vertex(0f, 0f, 1f).texture(sprite.getMinU(), sprite.getMinV()).color(r, g, b, 1f).next();
				buffer.vertex(0f, 0f, 1f).texture(sprite.getMinU(), sprite.getMaxV()).color(r, g, b, 1f).next();
				buffer.vertex(1f, 0f, 1f).texture(sprite.getMaxU(), sprite.getMaxV()).color(r, g, b, 1f).next();
				buffer.vertex(1f, 1f, 1f).texture(sprite.getMaxU(), sprite.getMinV()).color(r, g, b, 1f).next();
				break;
			case WEST:
				buffer.vertex(0f, 0f, 0f).texture(sprite.getMaxU(), sprite.getMaxV()).color(r, g, b, 1f).next();
				buffer.vertex(0f, 1f, 0f).texture(sprite.getMaxU(), sprite.getMinV()).color(r, g, b, 1f).next();
				buffer.vertex(1f, 0f, 0f).texture(sprite.getMinU(), sprite.getMinV()).color(r, g, b, 1f).next();
				buffer.vertex(1f, 0f, 0f).texture(sprite.getMinU(), sprite.getMaxV()).color(r, g, b, 1f).next();
			default:
				break;
		}
	}

	public static void drawBack(Direction dir, Sprite sprite, BufferBuilder buffer, int color) {
		float r = (float)(color >> 16 & 255) / 255.0F;
		float g = (float)(color >> 8 & 255) / 255.0F;
		float b = (float)(color & 255) / 255.0F;
		switch(dir) {
			case NORTH:
				buffer.vertex(0f, 0f, 0f).texture(sprite.getMaxU(), sprite.getMaxV()).color(r, g, b, 1f).next();
				buffer.vertex(0f, 1f, 0f).texture(sprite.getMaxU(), sprite.getMinV()).color(r, g, b, 1f).next();
				buffer.vertex(1f, 1f, 0f).texture(sprite.getMinU(), sprite.getMinV()).color(r, g, b, 1f).next();
				buffer.vertex(1f, 0f, 0f).texture(sprite.getMinU(), sprite.getMaxV()).color(r, g, b, 1f).next();
				break;
			case SOUTH:
				buffer.vertex(0f, 0f, 1f).texture(sprite.getMinU(), sprite.getMaxV()).color(r, g, b, 1f).next();
				buffer.vertex(1f, 0f, 1f).texture(sprite.getMaxU(), sprite.getMaxV()).color(r, g, b, 1f).next();
				buffer.vertex(1f, 1f, 1f).texture(sprite.getMaxU(), sprite.getMinV()).color(r, g, b, 1f).next();
				buffer.vertex(0f, 1f, 1f).texture(sprite.getMinU(), sprite.getMinV()).color(r, g, b, 1f).next();
				break;
			case EAST:
				buffer.vertex(1f, 0f, 0f).texture(sprite.getMaxU(), sprite.getMaxV()).color(r, g, b, 1f).next();
				buffer.vertex(1f, 1f, 0f).texture(sprite.getMaxU(), sprite.getMinV()).color(r, g, b, 1f).next();
				buffer.vertex(1f, 1f, 1f).texture(sprite.getMinU(), sprite.getMinV()).color(r, g, b, 1f).next();
				buffer.vertex(1f, 0f, 1f).texture(sprite.getMinU(), sprite.getMaxV()).color(r, g, b, 1f).next();
				break;
			case WEST:
				buffer.vertex(0f, 0f, 0f).texture(sprite.getMinU(), sprite.getMaxV()).color(r, g, b, 1f).next();
				buffer.vertex(0f, 0f, 1f).texture(sprite.getMaxU(), sprite.getMaxV()).color(r, g, b, 1f).next();
				buffer.vertex(0f, 1f, 1f).texture(sprite.getMaxU(), sprite.getMinV()).color(r, g, b, 1f).next();
				buffer.vertex(0f, 1f, 0f).texture(sprite.getMinU(), sprite.getMinV()).color(r, g, b, 1f).next();
			default:
				break;
		}
	}

	public static void drawBottom(Sprite sprite, BufferBuilder buffer, int color) {
		float r = (float)(color >> 16 & 255) / 255.0F;
		float g = (float)(color >> 8 & 255) / 255.0F;
		float b = (float)(color & 255) / 255.0F;
		buffer.vertex(0f, 0f, 0f).texture(sprite.getMinU(), sprite.getMaxV()).color(r, g, b, 1f).next();
		buffer.vertex(1f, 0f, 0f).texture(sprite.getMaxU(), sprite.getMaxV()).color(r, g, b, 1f).next();
		buffer.vertex(1f, 0f, 1f).texture(sprite.getMaxU(), sprite.getMinV()).color(r, g, b, 1f).next();
		buffer.vertex(0f, 0f, 1f).texture(sprite.getMinU(), sprite.getMinV()).color(r, g, b, 1f).next();
	}
}
