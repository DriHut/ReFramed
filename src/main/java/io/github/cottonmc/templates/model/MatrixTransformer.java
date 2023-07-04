package io.github.cottonmc.templates.model;

import io.github.cottonmc.templates.TemplatesClient;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.minecraft.util.math.AffineTransformation;
import net.minecraft.util.math.Direction;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.EnumMap;
import java.util.Map;

/**
 * Transforms the position of each vertex in a `Mesh`.
 * The transformation's origin is bumped to (0.5, 0.5, 0.5) just because it's more convenient for me lol.
 */
public class MatrixTransformer {
	public static Mesh meshAroundCenter(AffineTransformation aff, Mesh oldMesh) {
		return meshAroundCenter(aff.getMatrix(), oldMesh);
	}
	
	public static Mesh meshAroundCenter(Matrix4f mat, Mesh oldMesh) {
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
	
	//Hard to explain what this is for...
	//Basically, the previous incarnation of this mod assembled the north/south/east/west faces all individually.
	//This means it was easy to get the orientation of the block correct - to popular the north face of the slope, look at
	//the north texture of the theme block. In this version, there is only *one* slope model that is dynamically rotated
	//to form the other possible orientations. If I populate the north face of the model using the north face of the theme,
	//that model will then be rotated so it's no longer facing the right way.
	//
	//This seems to work, but I'm kinda surprised I don't need to invert the transformation here, which is a clue that
	//I don't really understand all the math, loool
	public static Map<Direction, Direction> facePermutation(AffineTransformation aff) {
		Map<Direction, Direction> facePermutation = new EnumMap<>(Direction.class);
		for(Direction input : Direction.values()) {
			Direction output = Direction.transform(aff.getMatrix(), input);
			facePermutation.put(input, output);
		}
		return facePermutation;
	}
}
