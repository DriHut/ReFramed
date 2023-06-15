package io.github.cottonmc.templates.model;

import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;

public class SlopeBaseMesh {
	public static Mesh make(BlockState state) {
		Renderer renderer = RendererAccess.INSTANCE.getRenderer();
		if(renderer == null) throw new IllegalStateException("RenderAccess.INSTANCE not populated - no Fabric Renderer API?");
		
		final MeshBuilder builder = renderer.meshBuilder();
		final QuadEmitter quad = builder.getEmitter();
		final Direction dir = state.get(Properties.HORIZONTAL_FACING);
		drawSlope(quad.color(-1, -1, -1, -1), dir);
		drawLeftSide(quad.color(-1, -1, -1, -1), dir);
		drawRightSide(quad.color(-1, -1, -1, -1), dir);
		drawBack(quad.color(-1, -1, -1, -1), dir);
		drawBottom(quad.color(-1, -1, -1, -1));
		return builder.build();
	}
	
	public static final int TAG_SLOPE = 0;
	public static final int TAG_LEFT = 1;
	public static final int TAG_RIGHT = 2;
	public static final int TAG_BACK = 3;
	public static final int TAG_BOTTOM = 4;
	
	private static void drawSlope(QuadEmitter quad, Direction dir) {
		quad.tag(TAG_SLOPE);
		switch(dir) {
			case NORTH:
				quad.pos(0, 0f, 1f, 0f).pos(1, 0f, 0f, 1f).pos(2, 1f, 0f, 1f).pos(3, 1f, 1f, 0f).emit();
				break;
			case SOUTH:
				quad.pos(0, 0f, 0f, 0f).pos(1, 0f, 1f, 1f).pos(2, 1f, 1f, 1f).pos(3, 1f, 0f, 0f).emit();
				break;
			case EAST:
				quad.pos(0, 0f, 0f, 0f).pos(1, 0f, 0f, 1f).pos(2, 1f, 1f, 1f).pos(3, 1f, 1f, 0f).emit();
				break;
			case WEST:
				quad.pos(0, 0f, 1f, 0f).pos(1, 0f, 1f, 1f).pos(2, 1f, 0f, 1f).pos(3, 1f, 0f, 0f).emit();
			default:
				break;
		}
	}
	
	private static void drawLeftSide(QuadEmitter quad, Direction dir) {
		switch(dir) {
			case NORTH:
				quad.tag(TAG_LEFT).pos(0, 0f, 0f, 0f).pos(1, 0f, 0f, 1f).pos(2, 0f, 0f, 1f).pos(3, 0f, 1f, 0f).emit();
				break;
			case SOUTH:
				quad.tag(TAG_LEFT).pos(0, 1f, 0f, 0f).pos(1, 1f, 1f, 1f).pos(2, 1f, 0f, 1f).pos(3, 1f, 0f, 1f).emit();
				break;
			case EAST:
				quad.tag(TAG_LEFT).pos(0, 1f, 0f, 0f).pos(1, 0f, 0f, 0f).pos(2, 0f, 0f, 0f).pos(3, 1f, 1f, 0f).emit();
				break;
			case WEST:
				quad.tag(TAG_LEFT).pos(0, 0f, 0f, 1f).pos(1, 1f, 0f, 1f).pos(2, 1f, 0f, 1f).pos(3, 0f, 1f, 1f).emit();
			default:
				break;
		}
	}
	
	private static void drawRightSide(QuadEmitter quad, Direction dir) {
		switch(dir) {
			case NORTH:
				quad.tag(TAG_RIGHT).pos(0, 1f, 0f, 0f).pos(1, 1f, 1f, 0f).pos(2, 1f, 0f, 1f).pos(3, 1f, 0f, 1f).emit();
				break;
			case SOUTH:
				quad.tag(TAG_RIGHT).pos(0, 0f, 0f, 0f).pos(1, 0f, 0f, 0f).pos(2, 0f, 0f, 1f).pos(3, 0f, 1f, 1f).emit();
				break;
			case EAST:
				quad.tag(TAG_RIGHT).pos(0, 0f, 0f, 1f).pos(1, 0f, 0f, 1f).pos(2, 1f, 0f, 1f).pos(3, 1f, 1f, 1f).emit();
				break;
			case WEST:
				quad.tag(TAG_RIGHT).pos(0, 0f, 0f, 0f).pos(1, 0f, 1f, 0f).pos(2, 1f, 0f, 0f).pos(3, 1f, 0f, 0f).emit();
			default:
				break;
		}
	}
	
	private static void drawBack(QuadEmitter quad, Direction dir) {
		switch(dir) {
			case NORTH:
				quad.tag(TAG_BACK).pos(0, 0f, 0f, 0f).pos(1, 0f, 1f, 0f).pos(2, 1f, 1f, 0f).pos(3, 1f, 0f, 0f).emit();
				break;
			case SOUTH:
				quad.tag(TAG_BACK).pos(0, 0f, 0f, 1f).pos(1, 1f, 0f, 1f).pos(2, 1f, 1f, 1f).pos(3, 0f, 1f, 1f).emit();
				break;
			case EAST:
				quad.tag(TAG_BACK).pos(0, 1f, 0f, 0f).pos(1, 1f, 1f, 0f).pos(2, 1f, 1f, 1f).pos(3, 1f, 0f, 1f).emit();
				break;
			case WEST:
				quad.tag(TAG_BACK).pos(0, 0f, 0f, 0f).pos(1, 0f, 0f, 1f).pos(2, 0f, 1f, 1f).pos(3, 0f, 1f, 0f).emit();
			default:
				break;
		}
	}
	
	private static void drawBottom(QuadEmitter quad) {
		quad.tag(TAG_BOTTOM).pos(0, 0f, 0f, 0f).pos(1, 1f, 0f, 0f).pos(2, 1f, 0f, 1f).pos(3, 0f, 0f, 1f).emit();
	}
}
