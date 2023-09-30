package com.nomoid.rivercrossing;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class MainGame extends ApplicationAdapter {

    public static final int WIDTH = 1280;
    public static final int HEIGHT = 800;
    SpriteBatch batch;
    ShapeRenderer shapeRenderer;
    OrthographicCamera camera;
    FitViewport viewport;
    Texture img;
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
        img = new Texture("badlogic.jpg");
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
        ScreenUtils.clear(0.8f, 0.8f, 0.9f, 1);
        batch.begin();
//        batch.draw(img, 0, 0);
        batch.end();
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 1);
        shapeRenderer.rect(0, 0, 50, 50);
        for (Entity entity : entities) {
            shapeRenderer.setColor(entity.getColor());
            shapeRenderer.rect(translateX(entity.getX()), translateY(entity.getY()), translateX(1), translateY(1));
        }
        shapeRenderer.end();

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
        img.dispose();
        shapeRenderer.dispose();
    }

}
