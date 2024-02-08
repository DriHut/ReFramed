package fr.adrien1106.reframedtemplates.model;

import fr.adrien1106.reframedtemplates.api.TemplatesClientApi;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.render.model.ModelBakeSettings;
import net.minecraft.util.math.Direction;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.EnumMap;
import java.util.Map;

public class MeshTransformUtil {
	public static Mesh pretransformMesh(Mesh mesh, RenderContext.QuadTransform transform) {
		MeshBuilder builder = TemplatesClientApi.getInstance().getFabricRenderer().meshBuilder();
		QuadEmitter emitter = builder.getEmitter();
		
		mesh.forEach(quad -> {
			emitter.copyFrom(quad);
			if(transform.transform(emitter)) emitter.emit();
		});
		
		return builder.build();
	}
	
	//Hard to explain what this is for...
	//Basically, Templates 1.x manually emitted the north/south/east/west faces all individually.
	//This means it was easy to get the orientation of the block correct - to popular the north face of the slope, look at
	//the north texture of the theme block. In this version, there is only *one* slope model that is dynamically rotated
	//to form the other possible orientations. If I populate the north face of the model using the north face of the theme,
	//then rotate the model, it's no longer facing the right way. So I need to "un-rotate" the face assignments first.
	//
	//This seems to work, but I'm kinda surprised I don't need to invert the transformation here, which is a clue that
	//I don't really understand all the math, loool
	public static Map<Direction, Direction> facePermutation(ModelBakeSettings aff) {
		return facePermutation(aff.getRotation().getMatrix());
	}
	
	public static Map<Direction, Direction> facePermutation(Matrix4f mat) {
		Map<Direction, Direction> facePermutation = new EnumMap<>(Direction.class);
		for(Direction input : Direction.values()) {
			Direction output = Direction.transform(mat, input);
			facePermutation.put(input, output);
		}
		return facePermutation;
	}
	
	public static RenderContext.QuadTransform applyAffine(ModelBakeSettings settings) {
		return applyMatrix(settings.getRotation().getMatrix());
	}
	
	public static RenderContext.QuadTransform applyMatrix(Matrix4f mat) {
		Map<Direction, Direction> facePermutation = facePermutation(mat);
		Vector3f pos3 = new Vector3f();
		Vector4f pos4 = new Vector4f();
		
		return quad -> {
			//For each vertex:
			for(int i = 0; i < 4; i++) {
				//Copy pos into a vec3, then a vec4. the w component is set to 0 since this is a point, not a normal
				quad.copyPos(i, pos3);
				pos3.add(-0.5f, -0.5f, -0.5f);
				pos4.set(pos3, 0);
				
				//Compute the matrix-vector product. This function mutates the vec4 in-place.
				//Note that `transformAffine` has the same purpose as `transform`; the difference is it
				//assumes (without checking) that the last row of the matrix is 0,0,0,1, as an optimization
				mat.transform(pos4);
				
				//Manually copy the data back onto the vertex
				quad.pos(i, pos4.x + 0.5f, pos4.y + 0.5f, pos4.z + 0.5f);
			}
			
			//permute tags
			int tag = quad.tag();
			if(tag != 0) quad.tag(facePermutation.get(RetexturingBakedModel.DIRECTIONS[tag - 1]).ordinal() + 1);
			
			//permute lighting face (?)
			quad.nominalFace(facePermutation.get(quad.lightFace()));
			
			//permute cullface
			Direction cull = quad.cullFace();
			if(cull != null) quad.cullFace(facePermutation.get(cull));
			
			//Output the quad
			return true;
		};
	}
}
