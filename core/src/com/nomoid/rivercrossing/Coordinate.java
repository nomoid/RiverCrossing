package com.nomoid.rivercrossing;

import entities.Entity;

import java.util.Objects;

public class Coordinate implements Comparable<Coordinate> {

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

    @Override
    public int compareTo(Coordinate other) {
        int i = Integer.compare(x, other.x);
        if (i != 0) {
            return i;
        }
        return Integer.compare(y, other.y);
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }

    public static Coordinate fromEntity(Entity entity) {
        return new Coordinate(entity.getX(), entity.getY());
    }

}
