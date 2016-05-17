package com.somethingyellow.magnets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.graphics.Color;

import com.somethingyellow.tiled.*;

import java.util.LinkedList;

public class PlayScreen implements Screen {
	public static final float WORLD_WIDTH = 500f;
	public static final String LAYER_ACTORS = "Actors";
	public static final String LAYER_PROP_ISTOP = "isTop";
	public static final String TILE_PROP_PLAYER = "Player";
	public boolean DEBUG_MODE = true;

	private Viewport _uiViewport;
	private TiledStage _tiledStage;
	private PlayerActor _playerActor;
	private Stage _uiStage;

	// Debugging tools
	private FPSLogger _fpsLogger = new FPSLogger();

	@Override
	public void show() {
		loadLevel(1);
	}

	public void loadLevel(int level) {
		float width = Gdx.graphics.getWidth();
		float height = Gdx.graphics.getHeight();

		TiledMap map = new TmxMapLoader().load("Level 1.tmx");

		_tiledStage = new TiledStage(map, WORLD_WIDTH, WORLD_WIDTH / width * height);
		Gdx.input.setInputProcessor(_tiledStage);

		// Hide actors layer
		_tiledStage.addHiddenLayers(new LinkedList<MapLayer>() {{
			add(_tiledStage.getLayer(LAYER_ACTORS));
		}});

		// Define layers rendered on top
		_tiledStage.addTopLayers(_tiledStage.findLayers(LAYER_PROP_ISTOP, true));

		LinkedList<TiledStage.Coordinate> players = _tiledStage.findCoordinates(LAYER_ACTORS, TILE_PROP_PLAYER, true);
		if (players.size() != 1)
			throw new IllegalArgumentException("Player count should be exactly 1!");

		_playerActor = new Player();
		_tiledStage.addActor(_playerActor, players.get(0));
		_tiledStage.setCameraFocalActor(_playerActor);
		_tiledStage.setInputFocalActor(_playerActor);

		// UI
		_uiViewport = new FitViewport(width, height);
		_uiStage = new Stage(_uiViewport);
		Label label = new Label("SCORE", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
		label.setPosition(0, 0);
		_uiStage.addActor(label);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0f, 0f, 0f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		_tiledStage.draw();
		_uiStage.draw();

		if (DEBUG_MODE) _fpsLogger.log();
	}

	@Override
	public void resize(int width, int height) {
		_tiledStage.getViewport().update(width, height);
		_uiViewport.update(width, height);
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose() {
		_playerActor.dispose();
		_tiledStage.dispose();
	}
}
