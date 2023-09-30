package com.nomoid.rivercrossing;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import entities.*;

import java.util.ArrayList;

public class MainGame extends ApplicationAdapter {

    public static final int WIDTH = 1280;
    public static final int HEIGHT = 800;
    public static final Color BACKGROUND = new Color(0.7f, 0.5f, 0.4f, 1);
    SpriteBatch batch;
    ShapeRenderer shapeRenderer;
    OrthographicCamera camera;
    FitViewport viewport;
    BitmapFont font;
    EntityContext entities;
    Player player;
    Boat boat;

    private final int speed = 1;
    private final int minX = -5;
    private final int maxX = 10;
    private final int minY = -5;
    private final int maxY = 5;

    private void generateLevel() {
        new Wolf(entities, -2, -2);
        new Goat(entities, -2, 0);
        new Cabbage(entities, -2, 2);
        for (int i = minY; i <= maxY; i++) {
            new River(entities, 2, i);
            new River(entities, 3, i);
        }
        for (int i = minX; i <= maxX; i++) {
            new Wall(entities, i, minY);
            new Wall(entities, i, maxY);
        }
        for (int i = minY; i <= maxY; i++) {
            new Wall(entities, minX, i);
            new Wall(entities, maxX, i);
        }
        boat = new Boat(entities, 2, 0);
        player = new Player(entities, 0, 0);
    }

    @Override
    public void create() {
        entities = new EntityContext();
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        camera = new OrthographicCamera();
        viewport = new FitViewport(WIDTH, HEIGHT, camera);
//        font = new BitmapFont();
        font = new BitmapFont(Gdx.files.internal("fonts/Roboto-Medium-72.fnt"));
        font.setUseIntegerPositions(false);
        generateLevel();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    public int tileWidth = 50;
    public int tileHeight = 50;

    public float translateX(int x) {
        return x * tileWidth;
    }

    public float translateY(int y) {
        return y * tileHeight;
    }

    private void draw() {

        Label.LabelStyle style = new Label.LabelStyle();
        style.font = font;
        style.fontColor = Color.BLACK;

        ScreenUtils.clear(BACKGROUND);

        for (Entity entity : entities) {
            // Render box
            {
                shapeRenderer.setProjectionMatrix(camera.combined);
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.setColor(entity.getColor());
                float screenX = translateX(entity.getX());
                float screenY = translateY(entity.getY());
                float screenWidth = translateX(1);
                float screenHeight = translateY(1);
                shapeRenderer.rect(screenX - screenWidth / 2.0f, screenY - screenHeight / 2.0f, screenWidth, screenHeight);
                shapeRenderer.end();
            }

            // Render text
            {
                batch.setProjectionMatrix(camera.combined);
                batch.begin();
                batch.setColor(Color.WHITE);

                String text = entity.getText();
                if (text != null) {
                    float screenX = translateX(entity.getX());
                    float screenY = translateY(entity.getY());
                    Label label = new Label(text, style);
                    float fontScale = 0.5f;
                    label.setFontScale(fontScale);
                    label.invalidate();
                    float screenWidth = label.getPrefWidth();
                    // Not sure why I need to divide by fontscale here but it works
                    float screenHeight = label.getPrefHeight() / fontScale;
                    label.setPosition(screenX - screenWidth / 2.0f, screenY - screenHeight / 2.0f);
                    label.draw(batch, 1);
                }
                batch.end();
            }
        }
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
                break;
            case RIVER:
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
        batch.dispose();
        shapeRenderer.dispose();
    }

}
