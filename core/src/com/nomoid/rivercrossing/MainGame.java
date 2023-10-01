package com.nomoid.rivercrossing;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import entities.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainGame extends ApplicationAdapter {

    public static final int WIDTH = 1280;
    public static final int HEIGHT = 800;
    EntityContext entities;
    Renderer renderer;
    Player player;

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
                new Boat(entities, 2, 0, 1);
                new Boat(entities, 2, 2, 1);
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

    private void doMove(List<Entity> entities, int newX, int newY) {
        for (Entity entity : entities) {
            if (entity.getCollisionHandler() == CollisionHandler.BOAT ||
                    entity.getCollisionHandler() == CollisionHandler.PLAYER ||
                    entity.getCollisionHandler() == CollisionHandler.PUSH) {
                entity.setPosition(newX, newY);
            }
        }
    }

    private boolean handleMove(List<Entity> originalEntities, boolean disembark, int newX, int newY) {
        if (disembark) {
            Boat boat = null;
            for (Entity entity : originalEntities) {
                if (entity instanceof Boat) {
                    Boat boatCandidate = (Boat) entity;
                    if (!boatCandidate.getCarry().isEmpty()) {
                        boat = boatCandidate;
                        break;
                    }
                }
            }
            if (boat == null) {
                return false;
            }
            Entity entity = entities.getEntity(boat.getCarry().remove(0));
            doMove(Collections.singletonList(entity), newX, newY);
            return false;
        } else {
            doMove(originalEntities, newX, newY);
            return true;
        }
    }

    private boolean handlePush(List<Entity> originalEntities, boolean disembark, int newX, int newY, int dx, int dy) {
        int newDX = Integer.signum(dx);
        int newDY = Integer.signum(dy);
        if (tryMove(newX, newY, newDX, newDY)) {
            return handleMove(originalEntities, disembark, newX, newY);
        }
        return false;
    }


    private boolean tryMove(int originalX, int originalY, int dx, int dy) {
        int newX = originalX + dx;
        int newY = originalY + dy;
        ArrayList<Entity> originalEntities = new ArrayList<>();
        ArrayList<Entity> collidedEntities = new ArrayList<>();
        for (Entity entity : entities) {
            if (entity.getX() == originalX && entity.getY() == originalY) {
                originalEntities.add(entity);
            }
            if (entity.getX() == newX && entity.getY() == newY) {
                collidedEntities.add(entity);
            }
        }
        Boat onBoat = (Boat) originalEntities.stream().filter((Entity entity) -> entity.getCollisionHandler() == CollisionHandler.BOAT).findFirst().orElse(null);
        CollisionHandler handler = null;
        Entity handlerEntity = null;
        for (Entity entity : collidedEntities) {
            CollisionHandler newHandler = entity.getCollisionHandler();
            if (handler == null) {
                handler = newHandler;
                handlerEntity = entity;
            } else {
                if (newHandler.priority > handler.priority) {
                    handler = newHandler;
                    handlerEntity = entity;
                }
            }
        }
        if (collidedEntities.isEmpty()) {
            return handleMove(originalEntities, onBoat != null, newX, newY);
        }
        switch (handler) {
            case BOAT:
                if (onBoat != null) {
                    Boat boat = (Boat) handlerEntity;
                    ArrayList<Integer> carry = boat.getCarry();
                    ArrayList<Integer> onBoatCarry = onBoat.getCarry();
                    for (Entity entity : originalEntities) {
                        if (entity.getCollisionHandler() == CollisionHandler.PLAYER) {
                            doMove(Collections.singletonList(entity), newX, newY);
                            carry.add(entity.getId());
                            onBoatCarry.remove((Integer) entity.getId());
                        }
                    }
                    return true;
                } else {
                    Boat boat = (Boat) handlerEntity;
                    ArrayList<Integer> carry = boat.getCarry();
                    boolean allMoved = true;
                    for (Entity entity : originalEntities) {
                        if (boat.getCapacity() > carry.size() || entity.getCollisionHandler() == CollisionHandler.PLAYER) {
                            doMove(Collections.singletonList(entity), newX, newY);
                            carry.add(entity.getId());
                        } else {
                            allMoved = false;
                        }
                    }
                    return allMoved;
                }
            case RIVER:
                if (onBoat != null) {
                    doMove(originalEntities, newX, newY);
                    return true;
                }
                break;
            case STOP:
                break;
            case PUSH:
                return handlePush(originalEntities, onBoat != null, newX, newY, dx, dy);
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
