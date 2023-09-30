package com.nomoid.rivercrossing;

import com.badlogic.gdx.graphics.Color;

public class Player extends Entity {
    public Player(EntityContext context) {
        super(context);
    }

    @Override
    public Color getColor() {
        return new Color(1, 1, 0.4f, 1);
    }

    @Override
    public String getText() {
        return "P";
    }
}
