package io.github.cottonmc.templates.util;

import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.joml.Vector3d;

public class StairShapeMaker {
	//TODO: clean this the fuck up, maybe keep in mind that VoxelShapes can be rotated multiples of 90 degrees by just rotating their corners
	public static VoxelShape makeStair(Edge innerEdge, double stepIn, double initialStepRise, double stepRise, double stepRun, int stepCount) {
		Edge.CoordinateFrame frame = innerEdge.makeCoordinateFrame();
		Vector3d origin = frame.origin();
		
		Vector3d in = new Vector3d(frame.a()).mul(stepIn);
		Vector3d fstRise = new Vector3d(frame.b()).mul(initialStepRise);
		
		Vector3d cursor = new Vector3d(origin).add(frame.along()).add(in).add(fstRise);
		Vector3d step = new Vector3d(frame.b()).mul(stepRise).add(new Vector3d(frame.a()).mul(-stepRun));
		
		VoxelShape shape = VoxelShapes.empty();
		for(int i = 0; i < stepCount; i++) {
			shape = VoxelShapes.union(shape, box(origin.x, origin.y, origin.z, cursor.x, cursor.y, cursor.z));
			cursor.add(step);
		}
		
		return shape.simplify();
	}
	
	//VoxelShape's constructor is picky about specifying your mins before your maxs
	private static VoxelShape box(double x1, double y1, double z1, double x2, double y2, double z2) {
		return VoxelShapes.cuboid(Math.min(x1, x2), Math.min(y1, y2), Math.min(z1, z2), Math.max(x1, x2), Math.max(y1, y2), Math.max(z1, z2));
	}
}
