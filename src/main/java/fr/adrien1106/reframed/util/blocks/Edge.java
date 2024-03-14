package fr.adrien1106.reframed.util.blocks;

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
        return switch (axis) {
            case X -> Direction.WEST;
            case Y -> Direction.DOWN;
            case Z -> Direction.SOUTH;
        };
    }
    public Direction getLeftDirection() {
        return switch (axis) {
            case X -> Direction.EAST;
            case Y -> Direction.UP;
            case Z -> Direction.NORTH;
        };
    }

    public boolean hasDirection(Direction direction) {
        return this.first_direction.equals(direction)
            || this.second_direction.equals(direction);
    }

    public int getID() {
        return this.ID;
    }

    public static Edge getByDirections(Direction direction_1, Direction direction_2) {
        return Arrays.stream(Edge.values())
            .filter(value -> value.hasDirection(direction_1) && value.hasDirection(direction_2))
            .findFirst().orElse(Edge.NORTH_DOWN);
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
}
