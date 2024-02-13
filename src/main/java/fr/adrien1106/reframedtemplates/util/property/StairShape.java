package fr.adrien1106.reframedtemplates.util.property;

import net.minecraft.util.StringIdentifiable;

import java.util.Arrays;

public enum StairShape implements StringIdentifiable {
    STRAIGHT("straight", 0),
    INNER_RIGHT("inner_right", 1),
    INNER_LEFT("inner_left", 2),
    OUTER_RIGHT("outer_right", 3),
    OUTER_LEFT("outer_left", 4),
    FIRST_OUTER_RIGHT("first_outer_right", 5),
    FIRST_OUTER_LEFT("first_outer_left", 6),
    SECOND_OUTER_RIGHT("second_outer_right", 7),
    SECOND_OUTER_LEFT("second_outer_left", 8);


    private final String name;
    private final int ID;

    StairShape(String name, int id) {
        this.name = name;
        this.ID = id;
    }

    public String asString() {
        return this.name;
    }

    public String toString() {
        return asString();
    }

    public int getID() {
        return this.ID;
    }

    public static StairShape fromId(int id) {
        return Arrays.stream(StairShape.values())
            .filter(value -> value.getID() == id)
            .findFirst().orElse(StairShape.STRAIGHT);
    }

    public static StairShape fromName(String name) {
        return Arrays.stream(StairShape.values())
            .filter(value -> value.name().equals(name))
            .findFirst().orElse(StairShape.STRAIGHT);
    }

}
