package entities;

import com.badlogic.gdx.graphics.Color;

import static entities.CollisionHandler.PUSH;

public class Cabbage extends Entity {

    public Cabbage(EntityContext context, int x, int y) {
        super(context);
        this.x = x;
        this.y = y;
    }

    @Override
    public Color getColor() {
        return new Color(0.5f, 0.9f, 0.4f, 1);
    }

    @Override
    public String getText() {
        return "C";
    }

    @Override
    public CollisionHandler getCollisionHandler() {
        return PUSH;
    }
}
