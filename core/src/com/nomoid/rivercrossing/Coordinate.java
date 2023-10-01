package com.nomoid.rivercrossing;

import entities.Entity;

import java.util.Objects;

public class Coordinate {

    public final int x;
    public final int y;

    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Coordinate) {
            Coordinate c = (Coordinate) o;
            return x == c.x && y == c.y;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, ~y);
    }

    public static Coordinate fromEntity(Entity entity) {
        return new Coordinate(entity.getX(), entity.getY());
    }
}
