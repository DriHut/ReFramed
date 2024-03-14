package fr.adrien1106.reframed.util;

import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import static net.minecraft.util.shape.VoxelShapes.*;

public class VoxelHelper {

    /* ---------------------------------------- Methods for VoxelListBuilder ---------------------------------------- */
    public static VoxelShape rotateX(VoxelShape shape) {
        return rotateClockwise(shape, Direction.Axis.X);
    }

    public static VoxelShape rotateY(VoxelShape shape) {
        return rotateClockwise(shape, Direction.Axis.Y);
    }
    public static VoxelShape rotateZ(VoxelShape shape) {
        return rotateClockwise(shape, Direction.Axis.Z);
    }

    public static VoxelShape rotateCX(VoxelShape shape) {
        return rotateCounterClockwise(shape, Direction.Axis.X);
    }

    public static VoxelShape rotateCY(VoxelShape shape) {
        return rotateCounterClockwise(shape, Direction.Axis.Y);
    }
    public static VoxelShape rotateCZ(VoxelShape shape) {
        return rotateCounterClockwise(shape, Direction.Axis.Z);
    }

    public static VoxelShape mirrorX(VoxelShape shape) {
        return mirror(shape, Direction.Axis.X);
    }

    public static VoxelShape mirrorY(VoxelShape shape) {
        return mirror(shape, Direction.Axis.Y);
    }
    public static VoxelShape mirrorZ(VoxelShape shape) {
        return mirror(shape, Direction.Axis.Z);
    }

    public static VoxelShape rotateClockwise(VoxelShape shape, Direction.Axis axis) {
        AtomicReference<VoxelShape> new_shape = new AtomicReference<>(empty());
        shape.forEachBox((minX, minY, minZ, maxX, maxY, maxZ) ->
            new_shape.getAndUpdate(s ->
                combineAndSimplify(
                    s,
                    switch (axis) {
                        case Y -> cuboid(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX);
                        case X -> cuboid(minX, 1 - maxZ, minY, maxX, 1 - minZ, maxY);
                        case Z -> cuboid(1 - maxY, minX, minZ, 1 - minY, maxX, maxZ);
                    },
                    BooleanBiFunction.OR
                )
            )
        );
        return new_shape.get();
    }

    public static VoxelShape rotateCounterClockwise(VoxelShape shape, Direction.Axis axis) {
        AtomicReference<VoxelShape> new_shape = new AtomicReference<>(empty());
        shape.forEachBox((minX, minY, minZ, maxX, maxY, maxZ) ->
            new_shape.getAndUpdate(s ->
                combineAndSimplify(
                    s,
                    switch (axis) {
                        case Y -> cuboid(minZ, minY, 1 - maxX, maxZ, maxY, 1 - minX);
                        case X -> cuboid(minX, minZ, 1 - maxY, maxX, maxZ, 1 - minY);
                        case Z -> cuboid(minY, 1 - maxX, minZ, maxY, 1 - minX, maxZ);
                    },
                    BooleanBiFunction.OR
                )
            )
        );
        return new_shape.get();
    }

    public static VoxelShape mirror(VoxelShape shape, Direction.Axis axis) {
        AtomicReference<VoxelShape> new_shape = new AtomicReference<>(empty());
        shape.forEachBox((minX, minY, minZ, maxX, maxY, maxZ) ->
            new_shape.getAndUpdate(s ->
                combineAndSimplify(
                    s,
                    switch (axis) {
                        case Y -> cuboid(minX, 1 - maxY, minZ, maxX, 1 - minY, maxZ);
                        case X -> cuboid(1 - maxX, minY, minZ, 1 - minX, maxY, maxZ);
                        case Z -> cuboid(minX, minY, 1 - maxZ, maxX, maxY, 1 - minZ);
                    },
                    BooleanBiFunction.OR
                )
            )
        );
        return new_shape.get();
    }

    public static VoxelShape offset(VoxelShape shape, Direction.Axis axis, float offset) {
        AtomicReference<VoxelShape> new_shape = new AtomicReference<>(empty());
        shape.forEachBox((minX, minY, minZ, maxX, maxY, maxZ) ->
            new_shape.getAndUpdate(s ->
                combineAndSimplify(
                    s,
                    switch (axis) {
                        case Y -> cuboid(minX, offset + minY, minZ, maxX, offset + maxY, maxZ);
                        case X -> cuboid(offset + minX, minY, minZ, offset + maxX, maxY, maxZ);
                        case Z -> cuboid(minX, minY, offset + minZ, maxX, maxY, offset + maxZ);
                    },
                    BooleanBiFunction.OR
                )
            )
        );
        return new_shape.get();
    }

    public static class VoxelListBuilder {
        private final List<VoxelShape> voxels;

        /**
         * A simple class that helps to get cleaner shape list generation (Hopefully)
         * @param base_shape - the shape to start with
         * @param size - the amount of shapes expected
         */
        private VoxelListBuilder(VoxelShape base_shape, int size) {
            voxels = new ArrayList<>(size);
            voxels.add(base_shape);
        }

        public static VoxelListBuilder create(VoxelShape base_shape, int size) {
            return new VoxelListBuilder(base_shape, size);
        }

        /**
         * Add shape by applying modification to previous shape
         * @param modifications - the modifications to apply @See {@link VoxelHelper} methods
         * @return this instance
         */
        @SafeVarargs
        public final VoxelListBuilder add(Function<VoxelShape, VoxelShape>... modifications) {
            return add(voxels.size() - 1, modifications);
        }

        /**
         * Add shape by applying modifications to given shape
         * @param ref - the index of the reference shape
         * @param modifications - the modifications to apply @See {@link VoxelHelper} methods
         * @return this instance
         */
        @SafeVarargs
        public final VoxelListBuilder add(int ref, Function<VoxelShape, VoxelShape>... modifications) {
            return add(voxels.get(ref), modifications);
        }

        /**
         * Add shape by applying modifications to given shape
         * @param ref - the shape you want to add and apply modifications if present
         * @param modifications - the modifications to apply @See {@link VoxelHelper} methods
         * @return this instance
         */
        @SafeVarargs
        public final VoxelListBuilder add(VoxelShape ref, Function<VoxelShape, VoxelShape>... modifications) {
            for(Function<VoxelShape, VoxelShape> modif: modifications) {
                ref = modif.apply(ref);
            }
            voxels.add(ref);
            return this;
        }

        /**
         * @return the final array of voxel shapes
         */
        public VoxelShape[] build() {
            return voxels.toArray(new VoxelShape[0]);
        }

        /**
         * build a new set of voxels based on th negation of the references and a full cube
         * @param ref_voxels - the reference to subtract from the wanted shape
         * @return the array of complementary voxels
         */
        public static VoxelShape[] buildFrom(VoxelShape[] ref_voxels) {
            return buildFrom(VoxelShapes.fullCube(), ref_voxels);
        }

        /**
         * build a new set of voxels based on th negation of the references and a wanted_shape
         * @param ref_voxels - the reference to subtract from the wanted shape
         * @return the array of complementary voxels
         */
        public static VoxelShape[] buildFrom(VoxelShape wanted_shape, VoxelShape[] ref_voxels) {
            VoxelShape[] shapes = new VoxelShape[ref_voxels.length];
            for (int i = 0; i < shapes.length; i++) {
                shapes[i] = VoxelShapes.combineAndSimplify(wanted_shape, ref_voxels[i], BooleanBiFunction.ONLY_FIRST);
            }
            return shapes;
        }
    }
}
