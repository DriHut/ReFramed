package fr.adrien1106.reframed.util.blocks;

import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Direction;

import java.util.Arrays;

public enum Edge implements StringIdentifiable {
    NORTH_DOWN("north_down", Direction.NORTH, Direction.DOWN, Direction.Axis.X, 0),
    DOWN_SOUTH("down_south", Direction.DOWN, Direction.SOUTH, Direction.Axis.X, 1),
    SOUTH_UP("south_up", Direction.SOUTH, Direction.UP, Direction.Axis.X, 2),
    UP_NORTH("up_north", Direction.UP, Direction.NORTH, Direction.Axis.X, 3),
    WEST_DOWN("west_down", Direction.WEST, Direction.DOWN, Direction.Axis.Z, 4),
    DOWN_EAST("down_east", Direction.DOWN, Direction.EAST, Direction.Axis.Z, 5),
    EAST_UP("east_up", Direction.EAST, Direction.UP, Direction.Axis.Z, 6),
    UP_WEST("up_west", Direction.UP, Direction.WEST, Direction.Axis.Z, 7),
    WEST_NORTH("west_north", Direction.WEST, Direction.NORTH, Direction.Axis.Y, 8),
    NORTH_EAST("north_east", Direction.NORTH, Direction.EAST, Direction.Axis.Y, 9),
    EAST_SOUTH("east_south", Direction.EAST, Direction.SOUTH, Direction.Axis.Y, 10),
    SOUTH_WEST("south_west", Direction.SOUTH, Direction.WEST, Direction.Axis.Y, 11);

    private final String name;
    private final Direction first_direction;
    private final Direction second_direction;
    private final Direction.Axis axis;
    private final int ID;

    Edge(String name, Direction first_direction, Direction second_direction, Direction.Axis axis, int id) {
        this.name = name;
        this.first_direction = first_direction;
        this.second_direction = second_direction;
        this.axis = axis;
        this.ID = id;
    }

    public String asString() {
        return this.name;
    }

    public String toString() {
        return asString();
    }

    public Direction getFirstDirection() {
        return first_direction;
    }

    public Direction getSecondDirection() {
        return second_direction;
    }

    public Direction getRightDirection() {
        return Direction.from(axis, Direction.AxisDirection.NEGATIVE);
    }

    public Direction getLeftDirection() {
        return Direction.from(axis, Direction.AxisDirection.POSITIVE);
    }

    public Direction getFace() {
        return first_direction == Direction.UP || first_direction == Direction.DOWN ? second_direction : first_direction;
    }

    public boolean hasDirection(Direction direction) {
        return this.first_direction.equals(direction)
            || this.second_direction.equals(direction);
    }

    public Direction.Axis getAxis() {
        return this.axis;
    }

    public int getID() {
        return this.ID;
    }

    public Edge opposite() {
        return getByDirections(first_direction.getOpposite(), second_direction.getOpposite());
    }

    public static Edge getByDirections(Direction direction_1, Direction direction_2) {
        return Arrays.stream(Edge.values())
            .filter(value -> value.hasDirection(direction_1) && value.hasDirection(direction_2))
            .findFirst().orElse(Edge.NORTH_DOWN);
    }

    public boolean isSide(Direction side) {
        return getRightDirection() == side || getLeftDirection() == side;
    }

    public Direction getOtherDirection(Direction direction) {
        return first_direction == direction ? second_direction : first_direction;
    }

    public static Edge fromId(int id) {
        return Arrays.stream(Edge.values())
            .filter(value -> value.getID() == id)
            .findFirst().orElse(Edge.NORTH_DOWN);
    }

    public static Edge fromName(String name) {
        return Arrays.stream(Edge.values())
            .filter(value -> value.name().equals(name))
            .findFirst().orElse(Edge.NORTH_DOWN);
    }

    public Direction getDirection(int side) {
        return side == 1 ? second_direction : first_direction;
    }

    public int getDirectionIndex(Direction direction) {
        return direction == first_direction ? 0 : 1;
    }

    public Edge getOpposite(int index) {
        return getOpposite(getDirection(index));
    }

    public Edge getOpposite(Direction direction) {
        return getByDirections(direction, getOtherDirection(direction).getOpposite());
    }

    public Edge rotate(BlockRotation rotation) {
        return getByDirections(
            rotation.rotate(first_direction),
            rotation.rotate(second_direction)
        );
    }

    public Edge mirror(BlockMirror mirror) {
        return getByDirections(
            mirror.apply(first_direction),
            mirror.apply(second_direction)
        );
    }
}
