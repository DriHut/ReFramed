package fr.adrien1106.reframed.util;

import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

public class VoxelHelper {
    public static VoxelShape rotateClockwise(VoxelShape shape, Direction.Axis axis) {
        VoxelShape[] buffer = new VoxelShape[]{ shape, VoxelShapes.empty() };
        switch (axis) {
            case Y:
                buffer[0].forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = VoxelShapes.combineAndSimplify( buffer[1], VoxelShapes.cuboid( 1 - maxZ, minY, minX, 1 - minZ, maxY, maxX), BooleanBiFunction.OR));
                break;
            case X:
                buffer[0].forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = VoxelShapes.combineAndSimplify( buffer[1], VoxelShapes.cuboid( minX, 1 - maxZ, minY, maxX, 1 - minZ, maxY), BooleanBiFunction.OR));
                break;
            case Z:
                buffer[0].forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = VoxelShapes.combineAndSimplify( buffer[1], VoxelShapes.cuboid( 1 - maxY, minX, minZ, 1 - minY, maxX, maxZ), BooleanBiFunction.OR));
                break;
        }
        return buffer[1];
    }

    public static VoxelShape rotateCounterClockwise(VoxelShape shape, Direction.Axis axis) {
        VoxelShape[] buffer = new VoxelShape[]{ shape, VoxelShapes.empty() };
        switch (axis) {
            case Y:
                buffer[0].forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = VoxelShapes.combineAndSimplify( buffer[1], VoxelShapes.cuboid( minZ, minY, 1 - maxX, maxZ, maxY, 1 - minX), BooleanBiFunction.OR));
                break;
            case X:
                buffer[0].forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = VoxelShapes.combineAndSimplify( buffer[1], VoxelShapes.cuboid( minX, minZ, 1 - maxY, maxX, maxZ, 1 - minY), BooleanBiFunction.OR));
                break;
            case Z:
                buffer[0].forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = VoxelShapes.combineAndSimplify( buffer[1], VoxelShapes.cuboid( minY, 1 - maxX, minZ, maxY, 1 - minX, maxZ), BooleanBiFunction.OR));
                break;
        }
        return buffer[1];
    }

    public static VoxelShape mirror(VoxelShape shape, Direction.Axis axis) {
        VoxelShape[] buffer = new VoxelShape[]{ shape, VoxelShapes.empty() };
        switch (axis) {
            case Y:
                buffer[0].forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = VoxelShapes.combineAndSimplify( buffer[1], VoxelShapes.cuboid( minX, 1 - maxY, minZ, maxX, 1 - minY, maxZ), BooleanBiFunction.OR));
                break;
            case X:
                buffer[0].forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = VoxelShapes.combineAndSimplify( buffer[1], VoxelShapes.cuboid( 1 - maxX, minY, minZ, 1 - minX, maxY, maxZ), BooleanBiFunction.OR));
                break;
            case Z:
                buffer[0].forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = VoxelShapes.combineAndSimplify( buffer[1], VoxelShapes.cuboid( minX, minY, 1 - maxZ, maxX, maxY, 1 - minZ), BooleanBiFunction.OR));
                break;
        }
        return buffer[1];
    }

    public static VoxelShape offset(VoxelShape shape, Direction.Axis axis, float offset) {
        VoxelShape[] buffer = new VoxelShape[]{ shape, VoxelShapes.empty() };
        switch (axis) {
            case Y:
                buffer[0].forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = VoxelShapes.combineAndSimplify( buffer[1], VoxelShapes.cuboid( minX, offset + minY, minZ, maxX, offset + maxY, maxZ), BooleanBiFunction.OR));
                break;
            case X:
                buffer[0].forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = VoxelShapes.combineAndSimplify( buffer[1], VoxelShapes.cuboid( offset + minX, minY, minZ, offset + maxX, maxY, maxZ), BooleanBiFunction.OR));
                break;
            case Z:
                buffer[0].forEachBox((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = VoxelShapes.combineAndSimplify( buffer[1], VoxelShapes.cuboid( minX, minY, offset + minZ, maxX, maxY, offset + maxZ), BooleanBiFunction.OR));
                break;
        }
        return buffer[1];
    }
}
