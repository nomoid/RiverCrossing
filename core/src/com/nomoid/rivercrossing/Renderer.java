package com.nomoid.rivercrossing;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import entities.Boat;
import entities.Entity;
import entities.EntityContext;
import entities.River;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class Renderer {

    public static final Color BACKGROUND = new Color(0.7f, 0.5f, 0.4f, 1);

    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    private final OrthographicCamera camera;
    private final FitViewport viewport;
    private final BitmapFont font;
    private final Label.LabelStyle labelStyle;

    public Renderer() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        camera = new OrthographicCamera();
        viewport = new FitViewport(MainGame.WIDTH, MainGame.HEIGHT, camera);
        font = new BitmapFont(Gdx.files.internal("fonts/Roboto-Medium-72.fnt"));
        font.setUseIntegerPositions(false);
        labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = Color.BLACK;
    }

    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        font.dispose();
    }

    public void update(int width, int height) {
        viewport.update(width, height);
    }

    public void begin() {

        ScreenUtils.clear(BACKGROUND);
    }

    private final int tileWidth = 50;
    private final int tileHeight = 50;

    private float translateX(int x, int miniOffsetX) {
        return (x + miniOffsetX / 3.0f) * tileWidth;
    }

    private float translateY(int y, int miniOffsetY) {
        return (y + miniOffsetY / 3.0f) * tileHeight;
    }

    private int miniOffsetX(int miniOffset) {
        if (miniOffset == 1 || miniOffset == 4 || miniOffset == 7) {
            return -1;
        }
        if (miniOffset == 3 || miniOffset == 6 || miniOffset == 9) {
            return 1;
        }
        return 0;
    }

    private int miniOffsetY(int miniOffset) {
        if (miniOffset == 1 || miniOffset == 2 || miniOffset == 3) {
            return 1;
        }
        if (miniOffset == 7 || miniOffset == 8 || miniOffset == 9) {
            return -1;
        }
        return 0;
    }

    // miniOffset = 0: Not mini
    // miniOffset = 1-9: Mini
    private void renderEntity(Entity entity, int miniOffset) {
        int miniOffsetX = miniOffsetX(miniOffset);
        int miniOffsetY = miniOffsetY(miniOffset);
        boolean isMini = miniOffset != 0;
        // Render box
        {
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(entity.getColor());
            float screenX = translateX(entity.getX(), miniOffsetX);
            float screenY = translateY(entity.getY(), miniOffsetY);
            float screenWidth = translateX(1, 0);
            float screenHeight = translateY(1, 0);
            if (isMini) {
                screenWidth /= 3.0f;
                screenHeight /= 3.0f;
            }
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
                float screenX = translateX(entity.getX(), miniOffsetX);
                float screenY = translateY(entity.getY(), miniOffsetY);
                Label label = new Label(text, labelStyle);
                float fontScale = 0.5f;
                if (isMini) {
                    fontScale /= 3.0f;
                }
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

    public void renderEntities(EntityContext entities) {
        LinkedHashMap<Coordinate, ArrayList<Entity>> entitiesByCoordinate = new LinkedHashMap<>();
        for (Entity entity : entities) {
            Coordinate coord = Coordinate.fromEntity(entity);
            ArrayList<Entity> atCoord =
                    entitiesByCoordinate.get(coord);
            if (atCoord == null) {
                atCoord = new ArrayList<>();
                entitiesByCoordinate.put(coord, atCoord);
            }
            atCoord.add(entity);
        }
        for (Map.Entry<Coordinate, ArrayList<Entity>> entry : entitiesByCoordinate.entrySet()) {
            ArrayList<Entity> atCoord = entry.getValue();
            if (atCoord.size() == 1) {
                renderEntity(atCoord.get(0), 0);
            } else {
                int miniOffset = 1;
                Boat boat = null;
                for (Entity entity : atCoord) {
                    // In the future, maybe generalize this to all vehicles
                    if (entity instanceof Boat) {
                        boat = (Boat) entity;
                        break;
                    }
                }
                if (boat != null) {
                    renderEntity(boat, 0);
                }
                for (Entity entity : atCoord) {
                    boolean isBoat =
                            entity instanceof Boat;
                    boolean isRiver = entity instanceof River;
                    boolean hasBoat = (boat != null);

                    if (!hasBoat || (!isBoat && !isRiver)) {
                        renderEntity(entity, miniOffset);
                        miniOffset++;
                    }
                }
            }
        }
    }

    public void end() {
        // Do nothing
    }
}

