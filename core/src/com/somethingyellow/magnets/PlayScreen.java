package com.somethingyellow.magnets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

import com.somethingyellow.tiled.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class PlayScreen implements Screen {
	public static final float WORLD_WIDTH = 500f;
	public static final float TILE_ANIMATION_FRAME_DURATION = 0.1f;
	public static final String LAYER_ACTORS = "Walls and Objects";
	// Tile properties
	public static final String TILE_TYPE = "Type";
	public static final String TILE_NAME = "Name";
	public static final String TILE_TYPE_PLAYER = "Player";
	public static final String TILE_TYPE_BLOCK = "Block";
	public static final String TILE_TYPE_MAGNETIC_AREA = "Magnetic Source";
	public static final String TILE_TYPE_WALL = "Wall";
	public static final String TILE_ISPUSHABLE = "IsPushable";
	public static final String TILE_ISMAGNETISABLE = "IsMagnetisable";
	public static final String TILE_BODY_WIDTH = "Body Width";
	public static final String TILE_BODY_AREA = "Body Area";
	public static final String TILE_THIS = "(this)";
	public static final String TILE_ACTOR_DEPTH = "Actor Depth";
	public static final String TILE_FRAME_DEPTH = "Frame Depth";
	public boolean DEBUG_MODE = false;
	// Paths/Textures
	private String _levelPath = "Levels/Test Cases.tmx";
	private TiledStage _tiledStage;
	private PlayerActor _playerActor;
	private HashMap<String, TiledMapTile> _tilesByName;
	private HashMap<TiledMapTile, ArrayList<TiledStageActor.Frame>> _tileFramesByName;
	// Debugging tools
	private FPSLogger _fpsLogger = new FPSLogger();

	public static boolean[] ExtractBodyArea(TiledMapTile tile) {
		String bodyArea = TiledStage.ParseProp(tile.getProperties(), TILE_BODY_AREA);
		if (bodyArea == null) return TiledStageActor.BodyArea1x1;

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

	public static int ExtractBodyWidth(TiledMapTile tile) {
		return TiledStage.ParseIntegerProp(tile.getProperties(), TILE_BODY_WIDTH, 1);
	}

	public static boolean ExtractIsPushable(TiledMapTile tile) {
		return TiledStage.ParseBooleanProp(tile.getProperties(), TILE_ISPUSHABLE, false);
	}

	public static boolean ExtractIsMagnetisable(TiledMapTile tile) {
		return TiledStage.ParseBooleanProp(tile.getProperties(), TILE_ISMAGNETISABLE, false);
	}

	public static int ExtractFrameDepth(TiledMapTile tile) {
		return TiledStage.ParseIntegerProp(tile.getProperties(), TILE_FRAME_DEPTH, 0);
	}

	public static int ExtractActorDepth(TiledMapTile tile) {
		return TiledStage.ParseIntegerProp(tile.getProperties(), TILE_ACTOR_DEPTH, 0);
	}

	public HashMap<String, TiledStageActor.FrameSequence> getAnimations(TiledMapTile tile) {
		HashMap<String, TiledStageActor.FrameSequence> animationFrames = new HashMap<String, TiledStageActor.FrameSequence>();
		Iterator<String> props = tile.getProperties().getKeys();

		while (props.hasNext()) {
			String prop = props.next();

			if (prop.charAt(0) == '#') {
				String name = TiledStage.ParseProp(tile.getProperties(), prop);

				TiledMapTile animationTile = tile;
				if (!name.equals(TILE_THIS)) {
					animationTile = _tilesByName.get(name);
				}

				if (!_tileFramesByName.containsKey(animationTile)) {
					_tileFramesByName.put(animationTile, TiledStageActor.FrameSequence.TileToFrames(animationTile));
				}

				animationFrames.put(prop.substring(1), new TiledStageActor.FrameSequence(_tileFramesByName.get(animationTile), ExtractFrameDepth(tile)));
			}
		}

		return animationFrames;
	}

	@Override
	public void show() {
		loadLevel(1);
	}

	public void loadLevel(int level) {
		float width = Gdx.graphics.getWidth();
		float height = Gdx.graphics.getHeight();

		TiledMap map = new TmxMapLoader().load(_levelPath);

		_tiledStage = new TiledStage(map, LAYER_ACTORS, WORLD_WIDTH, WORLD_WIDTH / width * height, TICKS.values().length);
		_tilesByName = new HashMap<String, TiledMapTile>();
		_tileFramesByName = new HashMap<TiledMapTile, ArrayList<TiledStageActor.Frame>>();

		Gdx.input.setInputProcessor(_tiledStage);

		Iterator<TiledMapTile> tiles = _tiledStage.tiles();
		while (tiles.hasNext()) {
			TiledMapTile tile = tiles.next();

			String name = TiledStage.ParseProp(tile.getProperties(), TILE_NAME);
			if (name != null) {
				_tilesByName.put(name, tile);
			}
		}

		Iterator<TiledStage.Cell> cells = _tiledStage.cells();
		while (cells.hasNext()) {
			TiledStage.Cell cell = cells.next();
			TiledMapTile tile = cell.tile();
			if (tile == null) continue;

			String type = TiledStage.ParseProp(tile.getProperties(), TILE_TYPE, "");
			if (type.equals(TILE_TYPE_BLOCK)) {
				spawnBlock(cell);
			} else if (type.equals(TILE_TYPE_MAGNETIC_AREA)) {
				spawnMagneticArea(cell);
			} else if (type.equals(TILE_TYPE_PLAYER)) {
				spawnPlayer(cell);
			}
		}
	}

	public void spawnPlayer(TiledStage.Cell cell) {
		if (_playerActor != null)
			throw new IllegalArgumentException("There should only be 1 player on the tiled map!");

		// Add player to stage
		_playerActor = new Player(OBJECT_TYPES.PLAYER.ordinal(),
				TiledStageActor.BodyArea1x1, 1,
				getAnimations(cell.tile()),
				_tiledStage, cell.coordinate(), ExtractActorDepth(cell.tile()));

		cell.removeTile();
		_tiledStage.setCameraFocalActor(_playerActor);
		_tiledStage.setInputFocalActor(_playerActor);
	}

	public void spawnBlock(TiledStage.Cell cell) {

		new Block(OBJECT_TYPES.BLOCK.ordinal(),
				ExtractBodyArea(cell.tile()), ExtractBodyWidth(cell.tile()),
				getAnimations(cell.tile()),
				_tiledStage, cell.coordinate(),
				ExtractIsPushable(cell.tile()), ExtractIsMagnetisable(cell.tile()), ExtractActorDepth(cell.tile()));

		cell.removeTile();
	}

	public void spawnMagneticArea(TiledStage.Cell cell) {

		new MagneticSource(OBJECT_TYPES.MAGNETIC_SOURCE.ordinal(),
				ExtractBodyArea(cell.tile()), ExtractBodyWidth(cell.tile()),
				getAnimations(cell.tile()),
				_tiledStage, cell.coordinate(), ExtractActorDepth(cell.tile()));

		cell.removeTile();
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0f, 0f, 0f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		_tiledStage.draw();

		if (DEBUG_MODE) _fpsLogger.log();
	}

	@Override
	public void resize(int width, int height) {
		_tiledStage.getViewport().update(width, height);
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
		_tiledStage.dispose();
	}

	public enum TICKS {
		RESET, MAGNETISATION, FORCES, BLOCK_MOVEMENT, PLAYER_MOVEMENT
	}

	public enum OBJECT_TYPES {
		PLAYER, BLOCK, MAGNETIC_SOURCE
	}
}
