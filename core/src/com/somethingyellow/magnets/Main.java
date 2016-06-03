package com.somethingyellow.magnets;

import com.badlogic.gdx.Game;

public class Main extends Game implements LevelSelectScreen.Listener, PlayScreen.Listener {
	public static PlayScreen playScreen;
	public static LevelSelectScreen levelSelectScreen;

	public Main() {
		playScreen = new PlayScreen(this);
		levelSelectScreen = new LevelSelectScreen(this);
	}

	@Override
	public void create() {
		setScreen(levelSelectScreen);
	}

	@Override
	public void startLevel(String levelPath) {
		setScreen(playScreen);
		playScreen.loadLevel(levelPath);
	}

	@Override
	public void exitLevel() {
		setScreen(levelSelectScreen);
	}
}
