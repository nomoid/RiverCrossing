package entities;

import com.badlogic.gdx.graphics.Color;

import static entities.CollisionHandler.RIVER;

public class River extends Entity {

    public River(EntityContext context, int x, int y) {
        super(context);
        this.x = x;
        this.y = y;
    }

    @Override
    public Color getColor() {
        return new Color(0.5f, 0.5f, 0.9f, 1);
    }

    @Override
    public String getText() {
        return null;
    }

    @Override
    public CollisionHandler getCollisionHandler() {
        return RIVER;
    }
}
