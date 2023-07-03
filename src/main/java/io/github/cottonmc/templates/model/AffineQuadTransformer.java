package io.github.cottonmc.templates.model;

import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.util.math.AffineTransformation;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public record AffineQuadTransformer(Matrix4f affineMatrix) implements RenderContext.QuadTransform {
	public AffineQuadTransformer(AffineTransformation aff) {
		this(aff.getMatrix());
	}
	
	@Override
	public boolean transform(MutableQuadView quad) {
		Vector3f pos3 = new Vector3f();
		Vector4f pos4 = new Vector4f(); //ugh
		
		for(int i = 0; i < 4; i++) {
			//Copy pos into a vec3, then a vec4. the w component is set to 0 since this is a point, not a normal
			quad.copyPos(i, pos3);
			//kinda a hack to center the affine transformation not at 0,0,0
			//it's an *affine* transformation, they can rotate around the not-origin... should modify the transformation instead?
			pos3.add(-0.5f, -0.5f, -0.5f);
			pos4.set(pos3, 0);
			
			//Compute the matrix-vector product. This function mutates the vec4 in-place.
			//Note that `transformAffine` has the same purpose as `transform`; the difference is it
			//assumes (without checking) that the last row of the matrix is 0,0,0,1, as an optimization
			affineMatrix.transformAffine(pos4);
			
			//Manually copy the data back onto the vertex
			quad.pos(i, pos4.x + 0.5f, pos4.y + 0.5f, pos4.z + 0.5f);
		}
		
		return true;
	}
}
