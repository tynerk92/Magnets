package com.somethingyellow.magnets;

import com.badlogic.gdx.Game;

public class Main extends Game {
	public static PlayScreen playScreen;
	public static LevelSelectScreen levelSelectScreen;
	public static String levelPath = "Levels/Weird Levels Pack/What would happen 3.tmx";

	public Main() {
		playScreen = new PlayScreen();
		levelSelectScreen = new LevelSelectScreen();
	}

	@Override
	public void create() {
		setScreen(playScreen);
		playScreen.loadLevel(levelPath);
	}
}
