package fr.adrien1106.reframed.util.blocks;

import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.Direction;

import java.util.Arrays;

public enum Corner implements StringIdentifiable {
    NORTH_EAST_DOWN("north_east_down", Direction.NORTH, Direction.EAST, Direction.DOWN, 0),
    EAST_SOUTH_DOWN("east_south_down", Direction.EAST, Direction.SOUTH, Direction.DOWN, 1),
    SOUTH_WEST_DOWN("south_west_down", Direction.SOUTH, Direction.WEST, Direction.DOWN, 2),
    WEST_NORTH_DOWN("west_north_down", Direction.WEST, Direction.NORTH, Direction.DOWN, 3),
    NORTH_EAST_UP("north_east_up", Direction.NORTH, Direction.EAST, Direction.UP, 4),
    EAST_SOUTH_UP("east_south_up", Direction.EAST, Direction.SOUTH, Direction.UP, 5),
    SOUTH_WEST_UP("south_west_up", Direction.SOUTH, Direction.WEST, Direction.UP, 6),
    WEST_NORTH_UP("west_north_up", Direction.WEST, Direction.NORTH, Direction.UP, 7);

    private final String name;
    private final Direction first_direction;
    private final Direction second_direction;
    private final Direction third_direction;
    private final int ID;

    Corner(String name, Direction first_direction, Direction second_direction, Direction third_direction, int id) {
        this.name = name;
        this.first_direction = first_direction;
        this.second_direction = second_direction;
        this.third_direction = third_direction;
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

    public Direction getThirdDirection() {
        return third_direction;
    }

    public boolean hasDirection(Direction direction) {
        return this.first_direction.equals(direction)
            || this.second_direction.equals(direction)
            || this.third_direction.equals(direction);
    }

    public int getID() {
        return this.ID;
    }

    public static Corner getByDirections(Direction direction_1, Direction direction_2, Direction direction_3) {
        return Arrays.stream(Corner.values())
            .filter(value -> value.hasDirection(direction_1) && value.hasDirection(direction_2) && value.hasDirection(direction_3))
            .findFirst().orElse(Corner.NORTH_EAST_DOWN);
    }

    public static Corner fromId(int id) {
        return Arrays.stream(Corner.values())
            .filter(value -> value.getID() == id)
            .findFirst().orElse(Corner.NORTH_EAST_DOWN);
    }

    public static Corner fromName(String name) {
        return Arrays.stream(Corner.values())
            .filter(value -> value.name().equals(name))
            .findFirst().orElse(Corner.NORTH_EAST_DOWN);
    }

    public int getDirectionIndex(Direction side) {
        return side == second_direction ? 1 : side == third_direction ? 2 : 0;
    }

    public Direction getDirection(int index) {
        return index == 1 ? second_direction : index == 2 ? third_direction : first_direction;
    }

    public Corner getOpposite() {
        return getByDirections(first_direction.getOpposite(), second_direction.getOpposite(), third_direction.getOpposite());
    }

    /**
     * @param index - an index of the direction to keep and use as reference
     * @return the opposite corner on same direction plane
     */
    public Corner getOpposite(int index) {
        return getOpposite(getDirection(index));
    }

    /**
     * @param direction - a direction to keep and use as reference
     * @return the opposite corner on same direction plane
     */
    public Corner getOpposite(Direction direction) {
        Direction other_1 = first_direction == direction ? second_direction : first_direction;
        Direction other_2 = second_direction == direction || first_direction == direction ? third_direction : second_direction;
        return getByDirections(direction, other_1.getOpposite(), other_2.getOpposite());
    }

    public Edge getEdge(Direction direction) {
        return Edge.getByDirections(
            first_direction == direction ? second_direction : first_direction,
            second_direction == direction || first_direction == direction ? third_direction : second_direction
        );
    }

    public Edge getEdgeWith(Direction direction) {
        return Edge.getByDirections(
            first_direction == direction ? first_direction : second_direction,
            first_direction == direction ? second_direction : third_direction
        );

    }

    public Direction getOtherDirection(Edge edge) {
        if (edge.getFirstDirection() != second_direction && edge.getSecondDirection() != second_direction) return second_direction;
        if (edge.getFirstDirection() != third_direction && edge.getSecondDirection() != third_direction) return third_direction;
        return first_direction;
    }

    public Corner rotate(BlockRotation rotation) {
        return getByDirections(
            rotation.rotate(first_direction),
            rotation.rotate(second_direction),
            rotation.rotate(third_direction)
        );
    }

    public Corner mirror(BlockMirror mirror) {
        return getByDirections(
            mirror.apply(first_direction),
            mirror.apply(second_direction),
            mirror.apply(third_direction)
        );
    }

    public Corner change(Direction face) {
        Direction opposite = face.getOpposite();
        return getByDirections(
            first_direction == face ? opposite : first_direction,
            second_direction == face ? opposite : second_direction,
            third_direction == face ? opposite : third_direction
        );
    }

    public Direction getMatchingDirection(Edge edge) {
        return hasDirection(edge.getSecondDirection()) ? edge.getSecondDirection() : edge.getFirstDirection();
    }
}
