package fr.adrien1106.reframedtemplates.util.property;

import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Direction;

import java.util.Arrays;
import java.util.Map;

public enum StairDirection implements StringIdentifiable {
    NORTH_DOWN("north_down", Direction.NORTH, Direction.DOWN, Direction.EAST, 0),
    DOWN_SOUTH("down_south", Direction.DOWN, Direction.SOUTH, Direction.EAST, 1),
    SOUTH_UP("south_up", Direction.SOUTH, Direction.UP, Direction.EAST, 2),
    UP_NORTH("up_north", Direction.UP, Direction.NORTH, Direction.EAST, 3),
    WEST_DOWN("west_down", Direction.WEST, Direction.DOWN, Direction.SOUTH, 4),
    DOWN_EAST("down_east", Direction.DOWN, Direction.EAST, Direction.SOUTH, 5),
    EAST_UP("east_up", Direction.EAST, Direction.UP, Direction.SOUTH, 6),
    UP_WEST("up_west", Direction.UP, Direction.WEST, Direction.SOUTH, 7),
    WEST_NORTH("west_north", Direction.WEST, Direction.NORTH, Direction.DOWN, 8),
    NORTH_EAST("north_east", Direction.NORTH, Direction.EAST, Direction.DOWN, 9),
    EAST_SOUTH("east_south", Direction.EAST, Direction.SOUTH, Direction.DOWN, 10),
    SOUTH_WEST("south_west", Direction.SOUTH, Direction.WEST, Direction.DOWN, 11);

    private final String name;
    private final Direction first_direction;
    private final Direction second_direction;
    private final Direction right_direction;
    private final Direction left_direction;
    private final int ID;

    StairDirection(String name, Direction first_direction, Direction second_direction, Direction right_direction, int id) {
        this.name = name;
        this.first_direction = first_direction;
        this.second_direction = second_direction;
        this.right_direction = right_direction;
        this.left_direction = right_direction.getOpposite();
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
        return right_direction;
    }
    public Direction getLeftDirection() {
        return left_direction;
    }

    public boolean hasDirection(Direction direction) {
        return this.first_direction.equals(direction)
            || this.second_direction.equals(direction);
    }

    public int getID() {
        return this.ID;
    }

    public static StairDirection getByDirections(Direction direction_1, Direction direction_2) {
        return Arrays.stream(StairDirection.values())
            .filter(value -> value.hasDirection(direction_1) && value.hasDirection(direction_2))
            .findFirst().orElse(StairDirection.NORTH_DOWN);
    }

    public static StairDirection fromId(int id) {
        return Arrays.stream(StairDirection.values())
            .filter(value -> value.getID() == id)
            .findFirst().orElse(StairDirection.NORTH_DOWN);
    }

    public static StairDirection fromName(String name) {
        return Arrays.stream(StairDirection.values())
            .filter(value -> value.name().equals(name))
            .findFirst().orElse(StairDirection.NORTH_DOWN);
    }
}
