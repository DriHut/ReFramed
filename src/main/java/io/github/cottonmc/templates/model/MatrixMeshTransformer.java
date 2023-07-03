package io.github.cottonmc.templates.model;

import io.github.cottonmc.templates.TemplatesClient;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.util.math.AffineTransformation;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * Transforms the position of each vertex in a `Mesh`.
 * The transformation's origin is bumped to (0.5, 0.5, 0.5) just because it's more convenient for me lol.
 */
public class MatrixMeshTransformer {
	public static Mesh transformAroundCenter(AffineTransformation aff, Mesh oldMesh) {
		return transformAroundCenter(aff.getMatrix(), oldMesh);
	}
	
	public static Mesh transformAroundCenter(Matrix4f mat, Mesh oldMesh) {
		Renderer r = TemplatesClient.getFabricRenderer();
		MeshBuilder newMesh = r.meshBuilder();
		QuadEmitter emitter = newMesh.getEmitter();
		
		//re-used buffers
		Vector3f pos3 = new Vector3f();
		Vector4f pos4 = new Vector4f();
		
		oldMesh.forEach(oldQuad -> {
			//Initialize the new quad
			emitter.copyFrom(oldQuad);
			
			//For each vertex:
			for(int i = 0; i < 4; i++) {
				//Copy pos into a vec3, then a vec4. the w component is set to 0 since this is a point, not a normal
				emitter.copyPos(i, pos3);
				pos3.add(-0.5f, -0.5f, -0.5f);
				pos4.set(pos3, 0);
				
				//Compute the matrix-vector product. This function mutates the vec4 in-place.
				//Note that `transformAffine` has the same purpose as `transform`; the difference is it
				//assumes (without checking) that the last row of the matrix is 0,0,0,1, as an optimization
				mat.transform(pos4);
				
				//Manually copy the data back onto the vertex
				emitter.pos(i, pos4.x + 0.5f, pos4.y + 0.5f, pos4.z + 0.5f);
			}
			
			//Output the quad
			emitter.emit();
		});
		
		return newMesh.build();
	}
}
