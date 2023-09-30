package com.nomoid.rivercrossing;


import com.badlogic.gdx.graphics.Color;

public abstract class Entity {
    private static int nextId = 0;
    private int x;
    private int y;
    private final int id;

    public Entity(EntityContext context) {
        this.id = nextId;
        nextId++;
        context.addEntity(this);
    }


    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void changePosition(int dx, int dy) {
        this.x += dx;
        this.y += dy;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getId() {
        return id;
    }

    public abstract Color getColor();

    // Nullable
    public abstract String getText();
}
