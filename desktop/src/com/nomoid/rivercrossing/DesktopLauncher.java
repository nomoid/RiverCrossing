package com.nomoid.rivercrossing;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setResizable(false);
        config.setWindowedMode(MainGame.WIDTH, MainGame.HEIGHT);
        config.setForegroundFPS(60);
        config.setTitle("RiverCrossing");
        new Lwjgl3Application(new MainGame(), config);
    }
}
