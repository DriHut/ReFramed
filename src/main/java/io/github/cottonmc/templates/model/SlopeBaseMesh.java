package io.github.cottonmc.templates.model;

import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;

public class SlopeBaseMesh {
	public static final int TAG_SLOPE = 0;
	public static final int TAG_LEFT = 1;
	public static final int TAG_RIGHT = 2;
	public static final int TAG_BACK = 3;
	public static final int TAG_BOTTOM = 4;
	
	public static Mesh make() {
		Renderer renderer = RendererAccess.INSTANCE.getRenderer();
		if(renderer == null) throw new IllegalStateException("RenderAccess.INSTANCE not populated - no Fabric Renderer API?");
		
		MeshBuilder builder = renderer.meshBuilder();
		QuadEmitter qu = builder.getEmitter();
		
		qu.tag(TAG_SLOPE)
			.pos(0, 0f, 0f, 0f).pos(1, 0f, 1f, 1f).pos(2, 1f, 1f, 1f).pos(3, 1f, 0f, 0f)
			.color(-1, -1, -1, -1)
			.emit()
			.tag(TAG_LEFT)
			.pos(0, 1f, 0f, 0f).pos(1, 1f, 1f, 1f).pos(2, 1f, 0f, 1f).pos(3, 1f, 0f, 1f)
			.color(-1, -1, -1, -1)
			.emit()
			.tag(TAG_RIGHT)
			.pos(0, 0f, 0f, 0f).pos(1, 0f, 0f, 0f).pos(2, 0f, 0f, 1f).pos(3, 0f, 1f, 1f)
			.color(-1, -1, -1, -1)
			.emit()
			.tag(TAG_BACK)
			.pos(0, 0f, 0f, 1f).pos(1, 1f, 0f, 1f).pos(2, 1f, 1f, 1f).pos(3, 0f, 1f, 1f)
			.color(-1, -1, -1, -1)
			.emit()
			.tag(TAG_BOTTOM)
			.pos(0, 0f, 0f, 0f).pos(1, 1f, 0f, 0f).pos(2, 1f, 0f, 1f).pos(3, 0f, 0f, 1f)
			.color(-1, -1, -1, -1)
			.emit();
		return builder.build();
	}
}
