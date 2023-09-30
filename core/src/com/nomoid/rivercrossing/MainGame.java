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

public class MainGame extends ApplicationAdapter {

    public static final int WIDTH = 1280;
    public static final int HEIGHT = 800;
    SpriteBatch batch;
    ShapeRenderer shapeRenderer;
    OrthographicCamera camera;
    FitViewport viewport;
    BitmapFont font;
    EntityContext entities;
    Entity player;

    private final int speed = 1;

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
        player = new Player(entities);
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

        ScreenUtils.clear(0.8f, 0.8f, 0.9f, 1);

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 1);
        shapeRenderer.rect(0, 0, 50, 50);
        for (Entity entity : entities) {
            shapeRenderer.setColor(entity.getColor());
            float screenX = translateX(entity.getX());
            float screenY = translateY(entity.getY());
            float screenWidth = translateX(1);
            float screenHeight = translateY(1);
            shapeRenderer.rect(screenX - screenWidth / 2.0f, screenY - screenHeight / 2.0f, screenWidth, screenHeight);
            shapeRenderer.setColor(Color.RED);
        }
        shapeRenderer.end();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.setColor(Color.WHITE);
        Label hello = new Label("Hello", style);
        hello.setPosition(0, 0);
        hello.draw(batch, 1);
        for (Entity entity : entities) {
            String text = entity.getText();
            if (text != null) {
                float screenX = translateX(entity.getX());
                float screenY = translateY(entity.getY());
                Label label = new Label(text, style);
                float fontScale = 0.5f;
                label.setFontScale(fontScale);
                label.invalidate();
                float screenWidth = label.getPrefWidth();
                float screenHeight = label.getPrefHeight() / fontScale;
                label.setPosition(screenX - screenWidth / 2.0f, screenY - screenHeight / 2.0f);
                label.draw(batch, 1);
            }
        }
        batch.end();

    }

    private void update() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            player.changePosition(0, speed);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            player.changePosition(-speed, 0);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            player.changePosition(0, -speed);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            player.changePosition(speed, 0);
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
