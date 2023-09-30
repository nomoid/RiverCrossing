package entities;

import com.badlogic.gdx.graphics.Color;

public class Wolf extends Entity {

    public Wolf(EntityContext context, int x, int y) {
        super(context);
        this.x = x;
        this.y = y;
    }

    @Override
    public Color getColor() {
        return new Color(0.7f, 0.7f, 0.7f, 1);
    }

    @Override
    public String getText() {
        return "W";
    }
}
