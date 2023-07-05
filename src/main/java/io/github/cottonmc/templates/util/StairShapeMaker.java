package io.github.cottonmc.templates.util;

import net.minecraft.block.enums.BlockHalf;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

public class StairShapeMaker {
	public static VoxelShape createHorizontal(Direction shortSide, BlockHalf half, int steps, double rise, double run) {
		VoxelShape shape = VoxelShapes.empty();
		
		Vec3d a, b, march;
		switch(shortSide) {
			case SOUTH -> {
				a = new Vec3d(0, 0, 1);
				b = new Vec3d(1, rise, 0);
				march = new Vec3d(0, rise, run);
			}
			case NORTH -> {
				a = new Vec3d(0, 0, 0);
				b = new Vec3d(1, rise, 1);
				march = new Vec3d(0, rise, -run);
			}
			case EAST -> {
				a = new Vec3d(1, 0, 0);
				b = new Vec3d(0, rise, 1);
				march = new Vec3d(run, rise, 0);
			}
			case WEST -> {
				a = new Vec3d(0, 0, 0);
				b = new Vec3d(1, rise, 1);
				march = new Vec3d(-run, rise, 0);
			}
			default -> {
				return VoxelShapes.fullCube(); //TODO
			}
		}
		
		if(half == BlockHalf.TOP) {
			a = new Vec3d(a.x, 1 - a.y, a.z);
			b = new Vec3d(b.x, 1 - b.y, b.z);
			march = march.multiply(1, -1, 1);
		}
		
		for(int i = 0; i < steps; i++) {
			VoxelShape newShape = agh(a.x, a.y, a.z, b.x, b.y, b.z);
			shape = VoxelShapes.union(shape, newShape);
			b = b.add(march);
		}
		
		return shape.simplify();
	}
	
	private static VoxelShape agh(double x1, double y1, double z1, double x2, double y2, double z2) {
		double minX = Math.min(x1, x2);
		double maxX = Math.max(x1, x2);
		double minY = Math.min(y1, y2);
		double maxY = Math.max(y1, y2);
		double minZ = Math.min(z1, z2);
		double maxZ = Math.max(z1, z2);
		return VoxelShapes.cuboid(minX, minY, minZ, maxX, maxY, maxZ);
	}
}
