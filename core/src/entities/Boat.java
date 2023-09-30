package entities;

import com.badlogic.gdx.graphics.Color;

import static entities.CollisionHandler.BOAT;

public class Boat extends Entity {

    public Boat(EntityContext context, int x, int y) {
        super(context);
        this.x = x;
        this.y = y;
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
