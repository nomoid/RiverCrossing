package entities;

import com.badlogic.gdx.graphics.Color;

import java.util.ArrayList;

import static entities.CollisionHandler.BOAT;

public class Boat extends Entity {

    // Excluding player
    private final int capacity;
    private final ArrayList<Integer> carry;

    public Boat(EntityContext context, int x, int y, int capacity) {
        super(context);
        this.x = x;
        this.y = y;
        this.capacity = capacity;
        this.carry = new ArrayList<>();
    }

    public int getCapacity() {
        return capacity;
    }

    public ArrayList<Integer> getCarry() {
        return carry;
    }

    @Override
    public Color getColor() {
        return new Color(0.9f, 0.8f, 0.5f, 1);
    }

    @Override
    public String getText() {
        return "B";
    }

    @Override
    public CollisionHandler getCollisionHandler() {
        return BOAT;
    }
}
