package entities;

import com.badlogic.gdx.graphics.Color;

import static entities.CollisionHandler.STOP;

public class Wall extends Entity {

    public Wall(EntityContext context, int x, int y) {
        super(context);
        this.x = x;
        this.y = y;
    }

    @Override
    public Color getColor() {
        return new Color(0.3f, 0.3f, 0.3f, 1);
    }

    @Override
    public String getText() {
        return null;
    }

    @Override
    public CollisionHandler getCollisionHandler() {
        return STOP;
    }
}
