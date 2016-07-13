package com.somethingyellow.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.somethingyellow.magnets.Main;

public class DesktopLauncher {
    public static final int WINDOW_WIDTH = 800;
    public static final int WINDOW_HEIGHT = 650;

    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = WINDOW_WIDTH;
        config.height = WINDOW_HEIGHT;
        new LwjglApplication(new Main(), config);
    }
}
