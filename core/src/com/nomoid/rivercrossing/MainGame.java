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

    private int x = 100;
    private int y = 100;
    private final int speed = 50;

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        camera = new OrthographicCamera();
        viewport = new FitViewport(WIDTH, HEIGHT, camera);
        img = new Texture("badlogic.jpg");
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
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
        shapeRenderer.setColor(1, 1, 0.4f, 1);
        shapeRenderer.rect(x, y, 50, 50);
        shapeRenderer.end();

    }

    private void update() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.W)) {
            y += speed;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.A)) {
            x -= speed;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            y -= speed;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.D)) {
            x += speed;
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
