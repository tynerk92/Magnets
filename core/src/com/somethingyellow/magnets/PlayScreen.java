package com.somethingyellow.magnets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.graphics.Color;

import com.somethingyellow.tiled.*;

import java.util.HashMap;
import java.util.LinkedList;

public class PlayScreen implements Screen {
	public static final float WORLD_WIDTH = 500f;
	public static final String LAYER_OBJECTS = "Objects";
	public static final String TILE_PROP_PLAYER = "Player";
	public static final String TILE_PROP_BLOCK = "Block";
	public static final String TILE_PROP_WALL = "Wall";
	public static final String TILE_PROP_ISPUSHABLE = "IsPushable";
	// Layer/Property names
	public static int TILE_SIZE = 32;
	public boolean DEBUG_MODE = false;
	// Paths/Textures
	private String _levelPath = "Level 1.tmx";
	private Viewport _uiViewport;
	private TiledStage _tiledStage;
	private PlayerActor _playerActor;
	private Stage _uiStage;
	private HashMap<String, Texture> _textures;

	// Debugging tools
	private FPSLogger _fpsLogger = new FPSLogger();

	@Override
	public void show() {
		_textures = new HashMap<String, Texture>();
		loadLevel(1);
	}

	public void loadLevel(int level) {
		float width = Gdx.graphics.getWidth();
		float height = Gdx.graphics.getHeight();

		TiledMap map = new TmxMapLoader().load(_levelPath);

		MapLayer layer = map.getLayers().get(LAYER_OBJECTS);
		if (!(layer instanceof TiledMapTileLayer))
			throw new IllegalArgumentException("LAYER_OBJECTS should point to a tiled layer!");
		_tiledStage = new TiledStage(map, (TiledMapTileLayer) layer, WORLD_WIDTH, WORLD_WIDTH / width * height);

		Gdx.input.setInputProcessor(_tiledStage);
		spawnPlayer();
		spawnBlocks();

		// UI
		_uiViewport = new FitViewport(width, height);
		_uiStage = new Stage(_uiViewport);
		Label label = new Label("SCORE", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
		label.setPosition(0, 0);
		_uiStage.addActor(label);
	}

	public void spawnPlayer() {
		LinkedList<TiledStage.Coordinate> coordinates = _tiledStage.findCoordinates(LAYER_OBJECTS, TILE_PROP_PLAYER, true);
		if (coordinates.size() != 1)
			throw new IllegalArgumentException("Player count should be exactly 1!");

		// Add player to stage
		_playerActor = new Player();
		_tiledStage.addActor(
				_playerActor,
				coordinates.get(0),
				TiledStageActor.Body1x1,
				createSprite(coordinates.get(0).getTile(LAYER_OBJECTS)),
				OBJECT_TYPES.PLAYER.ordinal());

		coordinates.get(0).removeTile(LAYER_OBJECTS);
		_tiledStage.setCameraFocalActor(_playerActor);
		_tiledStage.setInputFocalActor(_playerActor);
	}

	public void spawnBlocks() {
		// Create texture region for block1x1
		LinkedList<TiledStage.Coordinate> coordinates = _tiledStage.findCoordinates(LAYER_OBJECTS, TILE_PROP_BLOCK, true);
		Sprite sprite;
		Block block;
		TiledMapTile tile;

		for (TiledStage.Coordinate coordinate : coordinates) {
			tile = coordinate.getTile(LAYER_OBJECTS);
			sprite = createSprite(tile);
			block = new Block(TiledStage.ParseBooleanProp(tile.getProperties(), TILE_PROP_ISPUSHABLE));

			_tiledStage.addActor(
					block,
					coordinate,
					createBodyArea(coordinate.getTile(LAYER_OBJECTS)),
					createSprite(coordinate.getTile(LAYER_OBJECTS)),
					OBJECT_TYPES.BLOCK.ordinal());

			coordinate.removeTile(LAYER_OBJECTS);
		}
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

	public Sprite createSprite(TiledMapTile tile) {
		String path = TiledStage.ParseProp(tile.getProperties(), "Image Path");

		// Memoize textures
		if (!_textures.containsKey(path)) {
			_textures.put(path, new Texture(Gdx.files.internal(path)));
		}

		Texture texture = _textures.get(path);
		return new Sprite(texture);
	}

	public TiledStageActor.BodyArea createBodyArea(TiledMapTile tile) {
		String bodyArea = TiledStage.ParseProp(tile.getProperties(), "Body Area");
		int bodyWidth = TiledStage.ParseIntegerProp(tile.getProperties(), "Body Width");
		if (bodyArea.length() % bodyWidth != 0)
			throw new IllegalArgumentException("Length of 'Body Area' should be a multiple of 'Body Width'!");

		boolean[] area = new boolean[bodyArea.length()];
		for (int i = 0; i < bodyArea.length(); i++) {
			switch (bodyArea.charAt(i)) {
				case '0':
					area[i] = false;
					break;
				case '1':
					area[i] = true;
					break;
				default:
					throw new IllegalArgumentException("'Body Area' should only contain '0's and '1's!");
			}
		}

		return new TiledStageActor.BodyArea(area, bodyWidth);
	}

	@Override
	public void dispose() {
		_tiledStage.dispose();
		for (Texture texture : _textures.values()) {
			texture.dispose();
		}
	}

	public enum OBJECT_TYPES {
		PLAYER, BLOCK
	}
}
