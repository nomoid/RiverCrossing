package com.nomoid.rivercrossing;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import entities.*;

import java.util.ArrayList;

public class MainGame extends ApplicationAdapter {

    public static final int WIDTH = 1280;
    public static final int HEIGHT = 800;
    EntityContext entities;
    Renderer renderer;
    Player player;
    Boat boat;

    int currentLevel = 1;

    private final int speed = 1;
    private final int minX = -5;
    private final int maxX = 10;
    private final int minY = -5;
    private final int maxY = 5;

    private void generateLevel() {
        entities.reset();
        switch (currentLevel) {
            case 1:
                new Wolf(entities, -2, -2);
                new Goat(entities, -2, 0);
                new Cabbage(entities, -2, 2);
                for (int i = minY + 1; i <= maxY - 1; i++) {
                    new River(entities, 2, i);
                    new River(entities, 3, i);
                }
                for (int i = minX; i <= maxX; i++) {
                    new Wall(entities, i, minY);
                    new Wall(entities, i, maxY);
                }
                for (int i = minY + 1; i <= maxY - 1; i++) {
                    new Wall(entities, minX, i);
                    new Wall(entities, maxX, i);
                }
                boat = new Boat(entities, 2, 0);
                player = new Player(entities, 0, 0);
                break;
            default:
                break;
        }
    }

    @Override
    public void create() {
        entities = new EntityContext();
        renderer = new Renderer();
        generateLevel();
    }

    @Override
    public void resize(int width, int height) {
        renderer.update(width, height);
    }

    private void draw() {
        renderer.begin();

        renderer.renderEntities(entities);

        renderer.end();
    }


    private boolean tryMove(int originalX, int originalY, int dx, int dy) {
        int newX = originalX + dx;
        int newY = originalY + dy;
        ArrayList<Entity> originalEntities = new ArrayList<>();
        ArrayList<Entity> collidedEntities = new ArrayList<>();
        // TODO: Add in boat check
        for (Entity entity : entities) {
            if (entity.getX() == originalX && entity.getY() == originalY) {
                originalEntities.add(entity);
            }
            if (entity.getX() == newX && entity.getY() == newY) {
                collidedEntities.add(entity);
            }
        }
        if (collidedEntities.isEmpty()) {
            for (Entity entity : originalEntities) {
                entity.setPosition(newX, newY);
            }
            return true;
        }
        CollisionHandler handler = null;
        for (Entity entity : collidedEntities) {
            CollisionHandler newHandler = entity.getCollisionHandler();
            if (handler == null) {
                handler = newHandler;
            } else {
                if (newHandler.priority > handler.priority) {
                    handler = newHandler;
                }
            }
        }
        switch (handler) {
            case BOAT:
                // TODO: Handle boat
                break;
            case RIVER:
                // TODO: Handle river
                break;
            case STOP:
                break;
            case PUSH:
                int newDX = Integer.signum(dx);
                int newDY = Integer.signum(dy);
                if (tryMove(newX, newY, newDX, newDY)) {
                    for (Entity entity : originalEntities) {
                        entity.setPosition(newX, newY);
                    }
                    return true;
                }
                break;
        }
        return false;
    }

    private void update() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            generateLevel();
            return;
        }
        int playerX = player.getX();
        int playerY = player.getY();
        // Player movement
        if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            tryMove(playerX, playerY, 0, speed);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            tryMove(playerX, playerY, -speed, 0);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            tryMove(playerX, playerY, 0, -speed);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            tryMove(playerX, playerY, speed, 0);
        }
    }

    @Override
    public void render() {
        draw();
        update();
    }

    @Override
    public void dispose() {
        renderer.dispose();
    }

}
