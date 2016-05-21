package com.somethingyellow.magnets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSets;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.graphics.Color;

import com.somethingyellow.tiled.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class PlayScreen implements Screen {
	public static final float WORLD_WIDTH = 500f;
	public static final float TILE_ANIMATION_FRAME_DURATION = 0.5f;
	public static final String LAYER_OBJECTS = "Objects";
	public static final String TILE_TYPE = "Type";
	public static final String TILE_TYPE_PLAYER = "Player";
	public static final String TILE_TYPE_BLOCK = "Block";
	public static final String TILE_TYPE_WALL = "Wall";
	public static final String TILE_ISPUSHABLE = "IsPushable";
	public static final String TILE_BODY_WIDTH = "Body Width";
	public static final String TILE_BODY_AREA = "Body Area";

	// Layer/Property names
	public static int TILE_SIZE = 32;
	public boolean DEBUG_MODE = false;
	// Paths/Textures
	private String _levelPath = "Level 1.tmx";
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
		LinkedList<TiledStage.Coordinate> coordinates = _tiledStage.findCoordinates(LAYER_OBJECTS, TILE_TYPE, TILE_TYPE_PLAYER);
		if (coordinates.size() != 1)
			throw new IllegalArgumentException("There should only be 1 player on the tiled map!");

		// Add player to stage
		_playerActor = new Player(OBJECT_TYPES.PLAYER.ordinal(),
				TiledStageActor.BodyArea1x1, 1,
				extractAnimations(_tiledStage.map().getTileSets(), coordinates.get(0).getTile(LAYER_OBJECTS)),
				_tiledStage, coordinates.get(0));

		coordinates.get(0).removeTile(LAYER_OBJECTS);
		_tiledStage.setCameraFocalActor(_playerActor);
		_tiledStage.setInputFocalActor(_playerActor);
	}

	public void spawnBlocks() {
		LinkedList<TiledStage.Coordinate> coordinates = _tiledStage.findCoordinates(LAYER_OBJECTS, TILE_TYPE, TILE_TYPE_BLOCK);
		Block block;
		TiledMapTile tile;

		for (TiledStage.Coordinate coordinate : coordinates) {
			tile = coordinate.getTile(LAYER_OBJECTS);
			block = new Block(OBJECT_TYPES.BLOCK.ordinal(),
					extractBodyArea(tile), extractBodyWidth(tile),
					extractAnimations(_tiledStage.map().getTileSets(), tile),
					_tiledStage, coordinate, extractIsPushable(tile));

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

	public boolean[] extractBodyArea(TiledMapTile tile) {
		String bodyArea = TiledStage.ParseProp(tile.getProperties(), TILE_BODY_AREA);
		if (bodyArea.length() == 0) throw new IllegalArgumentException("'Body Area' must be defined!");
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

		return area;
	}

	public int extractBodyWidth(TiledMapTile tile) {
		return TiledStage.ParseIntegerProp(tile.getProperties(), TILE_BODY_WIDTH);
	}

	public boolean extractIsPushable(TiledMapTile tile) {
		return TiledStage.ParseBooleanProp(tile.getProperties(), TILE_ISPUSHABLE);
	}

	public HashMap<String, Animation> extractAnimations(TiledMapTileSets tilesets, TiledMapTile tile) {
		TiledMapTile animationTile;
		StaticTiledMapTile[] frames;
		Array<TextureRegion> animationTextureRegions;

		HashMap<String, Animation> animations = new HashMap<String, Animation>();
		Iterator<String> props = tile.getProperties().getKeys();

		animations.put("", new Animation(TILE_ANIMATION_FRAME_DURATION, tile.getTextureRegion()));

		while (props.hasNext()) {
			String prop = props.next();
			if (prop.charAt(0) == '#') {
				int id = Integer.parseInt((String) tile.getProperties().get(prop));
				animationTile = tilesets.getTile(id);

				if (animationTile instanceof AnimatedTiledMapTile) {
					frames = ((AnimatedTiledMapTile) animationTile).getFrameTiles();
				} else if (animationTile instanceof StaticTiledMapTile) {
					frames = new StaticTiledMapTile[]{(StaticTiledMapTile) animationTile};
				} else {
					continue;
				}

				animationTextureRegions = new Array<TextureRegion>(frames.length);
				for (StaticTiledMapTile frameTile : frames) {
					animationTextureRegions.add(frameTile.getTextureRegion());
				}

				animations.put(prop.substring(1), new Animation(TILE_ANIMATION_FRAME_DURATION, animationTextureRegions));
			}
		}

		return animations;
	}

	@Override
	public void dispose() {
		_tiledStage.dispose();
	}

	public enum OBJECT_TYPES {
		PLAYER, BLOCK
	}
}
