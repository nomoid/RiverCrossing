package entities;

import com.badlogic.gdx.graphics.Color;

import static entities.CollisionHandler.PUSH;

public class Goat extends Entity {

    public Goat(EntityContext context, int x, int y) {
        super(context);
        this.x = x;
        this.y = y;
    }

    @Override
    public Color getColor() {
        return new Color(0.8f, 0.9f, 0.6f, 1);
    }

    @Override
    public String getText() {
        return "G";
    }

    @Override
    public CollisionHandler getCollisionHandler() {
        return PUSH;
    }
}
