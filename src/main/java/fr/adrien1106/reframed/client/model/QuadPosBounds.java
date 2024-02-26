package fr.adrien1106.reframed.client.model;

import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadView;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector3f;

public record QuadPosBounds(float min_x, float max_x, float min_y, float max_y, float min_z, float max_z) {

    public static QuadPosBounds read(QuadView quad) {
        return read(quad, true);
    }

    public static QuadPosBounds read(QuadView quad, boolean check_full) {
        float x0 = quad.x(0), x1 = quad.x(1), x2 = quad.x(2), x3 = quad.x(3);
        float y0 = quad.y(0), y1 = quad.y(1), y2 = quad.y(2), y3 = quad.y(3);
        float z0 = quad.z(0), z1 = quad.z(1), z2 = quad.z(2), z3 = quad.z(3);

        // Checks if the Dimensions are either 0 or 1 except for the Axis dimension
        Direction.Axis axis = quad.nominalFace().getAxis();
        if (check_full && (axis == Direction.Axis.X || (
                (MathHelper.approximatelyEquals(x0, 0) || MathHelper.approximatelyEquals(x0, 1))
                && (MathHelper.approximatelyEquals(x1, 0) || MathHelper.approximatelyEquals(x1, 1))
                && (MathHelper.approximatelyEquals(x2, 0) || MathHelper.approximatelyEquals(x2, 1))
                && (MathHelper.approximatelyEquals(x3, 0) || MathHelper.approximatelyEquals(x3, 1))
            )) && (axis == Direction.Axis.Y || (
                (MathHelper.approximatelyEquals(y0, 0) || MathHelper.approximatelyEquals(y0, 1))
                && (MathHelper.approximatelyEquals(y1, 0) || MathHelper.approximatelyEquals(y1, 1))
                && (MathHelper.approximatelyEquals(y2, 0) || MathHelper.approximatelyEquals(y2, 1))
                && (MathHelper.approximatelyEquals(y3, 0) || MathHelper.approximatelyEquals(y3, 1))
            )) & (axis == Direction.Axis.Z || (
                (MathHelper.approximatelyEquals(z0, 0) || MathHelper.approximatelyEquals(z0, 1))
                && (MathHelper.approximatelyEquals(z1, 0) || MathHelper.approximatelyEquals(z1, 1))
                && (MathHelper.approximatelyEquals(z2, 0) || MathHelper.approximatelyEquals(z2, 1))
                && (MathHelper.approximatelyEquals(z3, 0) || MathHelper.approximatelyEquals(z3, 1))
            ))
        ) return null;

        return new QuadPosBounds(
            Math.min(Math.min(x0, x1), Math.min(x2, x3)),
            Math.max(Math.max(x0, x1), Math.max(x2, x3)),
            Math.min(Math.min(y0, y1), Math.min(y2, y3)),
            Math.max(Math.max(y0, y1), Math.max(y2, y3)),
            Math.min(Math.min(z0, z1), Math.min(z2, z3)),
            Math.max(Math.max(z0, z1), Math.max(z2, z3))
        );
    }

    public boolean matches(QuadPosBounds other_bounds) {
        return !(
               (min_x != max_x && (min_x >= other_bounds.max_x || max_x <= other_bounds.min_x))
            || (min_y != max_y && (min_y >= other_bounds.max_y || max_y <= other_bounds.min_y))
            || (min_z != max_z && (min_z >= other_bounds.max_z || max_z <= other_bounds.min_z))
        );
    }

    public QuadPosBounds intersection(QuadPosBounds other_bounds, Direction.Axis axis) {
        return new QuadPosBounds(
            axis.equals(Direction.Axis.X) ? other_bounds.min_x: Math.max(min_x, other_bounds.min_x),
            axis.equals(Direction.Axis.X) ? other_bounds.max_x: Math.min(max_x, other_bounds.max_x),
            axis.equals(Direction.Axis.Y) ? other_bounds.min_y: Math.max(min_y, other_bounds.min_y),
            axis.equals(Direction.Axis.Y) ? other_bounds.max_y: Math.min(max_y, other_bounds.max_y),
            axis.equals(Direction.Axis.Z) ? other_bounds.min_z: Math.max(min_z, other_bounds.min_z),
            axis.equals(Direction.Axis.Z) ? other_bounds.max_z: Math.min(max_z, other_bounds.max_z)
        );
    }

    public void apply(MutableQuadView quad, QuadPosBounds origin_bounds) {
        Vector3f pos = new Vector3f();
        for (int i = 0; i < 4; i++) {
            quad.copyPos(i, pos);
            pos.x = MathHelper.approximatelyEquals(pos.x, origin_bounds.min_x)? min_x: max_x;
            pos.y = MathHelper.approximatelyEquals(pos.y, origin_bounds.min_y)? min_y: max_y;
            pos.z = MathHelper.approximatelyEquals(pos.z, origin_bounds.min_z)? min_z: max_z;
            quad.pos(i, pos);
        }
    }
}
