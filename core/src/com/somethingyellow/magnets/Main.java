package com.somethingyellow.magnets;

import com.badlogic.gdx.Game;

public class Main extends Game {
	public static PlayScreen PlayScreen = new PlayScreen();

	@Override
	public void create() {
		setScreen(PlayScreen);
	}
}
