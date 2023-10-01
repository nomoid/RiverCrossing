package entities;

import com.badlogic.gdx.graphics.Color;

import java.util.List;

public class Victory extends Entity {

    public final List<Integer> required;

    public Victory(EntityContext context, int x, int y, List<Integer> required) {
        super(context);
        this.x = x;
        this.y = y;
        this.required = required;
    }

    @Override
    public Color getColor() {
        return null;
    }

    @Override
    public String getText() {
        return null;
    }

    @Override
    public CollisionHandler getCollisionHandler() {
        return CollisionHandler.VICTORY;
    }
}
