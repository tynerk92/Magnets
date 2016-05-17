package com.somethingyellow.magnets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Game;

public class Main extends Game {
	@Override
	public void create() {
		PlayScreen playScreen = new PlayScreen();
		setScreen(playScreen);
	}
}
