package io.github.cottonmc.templates.model;

import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.AffineTransformations;
import net.minecraft.util.math.Direction;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public record AffineQuadTransformer(Matrix4f affineMatrix) implements RenderContext.QuadTransform {
	public AffineQuadTransformer(AffineTransformation aff) {
		this(aff.getMatrix());
	}
	
	@Override
	public boolean transform(MutableQuadView quad) {
		Matrix4f mat = affineMatrix;
		//TODO: Hard Coded for DEBUGGING
		mat = (
			AffineTransformations.DIRECTION_ROTATIONS.get(Direction.NORTH)
		).getMatrix();
		
		Vector3f pos3 = new Vector3f();
		//Vector3f norm3 = new Vector3f();
		
		//ugh
		Vector4f pos4 = new Vector4f();
		//Vector4f norm4 = new Vector4f();
		
		for(int i = 0; i < 4; i++) {
			//Copy quad data into vec3's 
			quad.copyPos(i, pos3);
			//quad.copyNormal(i, norm3);
			
			pos3.add(-0.5f, -0.5f, -0.5f); //TODO HACK
			
			//Initialize the x/y/z components of vec4s using that data
			//W component of normal vector is set to 1, normal vectors transform differently from points :)
			pos4.set(pos3, 0);
			//norm4.set(norm3, 1);
			
			//Compute the matrix-vector product. This function mutates the vec4 in-place.
			//Note that `transformAffine` has the same purpose as `transform`; the difference is it
			//assumes (without checking) that the last row of the matrix is 0,0,0,1, as an optimization
			mat.transformAffine(pos4);
			//mat.transformAffine(norm4);
			
			//Manually copy the data back onto the vertex
			quad.pos(i, pos4.x + 0.5f, pos4.y + 0.5f, pos4.z + 0.5f);
			//quad.normal(i, norm4.x, norm4.y + 1, norm4.z);
		}
		
		return true;
	}
}
