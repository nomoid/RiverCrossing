package com.nomoid.rivercrossing;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import entities.*;

import java.util.*;

public class MainGame extends ApplicationAdapter {

    public static final int WIDTH = 1280;
    public static final int HEIGHT = 800;
    EntityContext entities;
    Renderer renderer;
    Player player;

    int currentLevel = 1;

    private final int speed = 1;


    private int minX;
    private int minY;
    private int maxX;
    private int maxY;
    private boolean canMove = true;
    private boolean shouldUpdateAuto = false;
    private float sinceLastTick = 0;
    private int tickCount = 0;
    private float tickLength = 0.5f;

    private void generateWalls() {
        for (int i = minX; i <= maxX; i++) {
            new Wall(entities, i, minY);
            new Wall(entities, i, maxY);
        }
        for (int i = minY + 1; i <= maxY - 1; i++) {
            new Wall(entities, minX, i);
            new Wall(entities, maxX, i);
        }
    }

    private void generateLevel() {
        switch (currentLevel) {
            case 1:
                minX = -5;
                maxX = 10;
                minY = -5;
                maxY = 5;
                entities.reset(minX, maxX, minY, maxY);
                generateWalls();
                new Wolf(entities, -2, -2);
                new Goat(entities, -2, 0);
                new Cabbage(entities, -2, 2);
                for (int i = minY + 1; i <= maxY - 1; i++) {
                    new River(entities, 2, i);
                    new River(entities, 3, i);
                }
                new Boat(entities, 2, 0, 1);
                new Boat(entities, 2, 2, 1);
                player = new Player(entities, 0, 0);
                break;
            default:
                break;
        }
        tickCount = 0;
        canMove = true;
    }

    @Override
    public void create() {
        entities = new EntityContext(0, 0, 0, 0);
        renderer = new Renderer();
        generateLevel();
    }

    @Override
    public void resize(int width, int height) {
        renderer.update(width, height);
    }

    private void draw() {
        renderer.begin(canMove);

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
        if (canMove) {
            boolean triedMove = false;
            if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
                tryMove(playerX, playerY, 0, speed);
                triedMove = true;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
                tryMove(playerX, playerY, -speed, 0);
                triedMove = true;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
                tryMove(playerX, playerY, 0, -speed);
                triedMove = true;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
                tryMove(playerX, playerY, speed, 0);
                triedMove = true;
            }
            if (triedMove) {
                shouldUpdateAuto = true;
                canMove = false;
                sinceLastTick = tickLength;
            }
        }
        entities.processRemoval();
    }

    private void updateAuto() {
        if (shouldUpdateAuto) {
            sinceLastTick += Gdx.graphics.getDeltaTime();
            if (sinceLastTick >= tickLength) {
                tickCount++;
                boolean anyChange = false;
                for (Entity entity : entities) {
                    if (entity.hasIndependentBehavior()) {
                        HashSet<CollisionHandler> blockers =
                                new HashSet<>(Arrays.asList(
                                        CollisionHandler.STOP,
                                        CollisionHandler.RIVER,
                                        CollisionHandler.BOAT));
                        if (entities.canReach(player, entity, blockers, new HashSet<>()) != null) {
                            System.out.println("Can reach " + entity.getClass().getName());
                        } else {
                            boolean changed = entity.independentBehavior(entities, tickCount);
                            if (changed) {
                                anyChange = true;
                            }
                        }
                    }
                }
                if (anyChange) {
                    canMove = false;
                    sinceLastTick = 0.0f;
                } else {
                    canMove = true;
                }
            }
        }
    }

    @Override
    public void render() {
        shouldUpdateAuto = !canMove;
        draw();
        update();
        updateAuto();
    }

    @Override
    public void dispose() {
        renderer.dispose();
    }

}
