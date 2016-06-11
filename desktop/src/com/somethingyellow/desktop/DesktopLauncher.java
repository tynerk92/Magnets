package com.somethingyellow.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
// import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.somethingyellow.magnets.Config;
import com.somethingyellow.magnets.Main;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        Config.Configure();
        new LwjglApplication(new Main(), config);
    }
}
