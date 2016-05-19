package com.somethingyellow.magnets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
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
	public static final String LAYER_OBJECTS = "Objects";
	public static final String TILE_PROP_PLAYER = "Player";
	public static final String TILE_PROP_STONE = "Stone";
	public static final String TILE_PROP_WALL = "Wall";
	public boolean DEBUG_MODE = false;
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

		MapLayer layer = map.getLayers().get(LAYER_OBJECTS);
		if (!(layer instanceof TiledMapTileLayer))
			throw new IllegalArgumentException("LAYER_OBJECTS should point to a tiled layer!");
		_tiledStage = new TiledStage(map, (TiledMapTileLayer) layer, WORLD_WIDTH, WORLD_WIDTH / width * height);

		Gdx.input.setInputProcessor(_tiledStage);

		// Spawn player
		LinkedList<TiledStage.Coordinate> coordinates = _tiledStage.findCoordinates(LAYER_OBJECTS, TILE_PROP_PLAYER, true);
		if (coordinates.size() != 1)
			throw new IllegalArgumentException("Player count should be exactly 1!");

		_playerActor = new Player();
		_tiledStage.addActor(_playerActor, coordinates.get(0), OBJECT_TYPES.PLAYER.ordinal());
		coordinates.get(0).removeTile(LAYER_OBJECTS);
		_tiledStage.setCameraFocalActor(_playerActor);
		_tiledStage.setInputFocalActor(_playerActor);

		// Spawn stones
		coordinates = _tiledStage.findCoordinates(LAYER_OBJECTS, TILE_PROP_STONE, true);

		Block block;
		for (TiledStage.Coordinate coordinate : coordinates) {
			block = new Block();
			_tiledStage.addActor(block, coordinate, OBJECT_TYPES.STONE.ordinal());
			coordinate.removeTile(LAYER_OBJECTS);
		}

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

	public enum OBJECT_TYPES {
		PLAYER, STONE
	}
}
