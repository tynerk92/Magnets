package com.somethingyellow.magnets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.scenes.scene2d.Stage;

import com.somethingyellow.tiled.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

public class PlayScreen implements Screen {
	public static final float WORLD_WIDTH = 500f;
	public static final float TILE_ANIMATION_FRAME_DURATION = 0.1f;
	public static final String LAYER_OBJECTS = "Walls and Objects";
	public static final String TILE_TYPE = "Type";
	public static final String TILE_NAME = "Name";
	public static final String TILE_TYPE_PLAYER = "Player";
	public static final String TILE_TYPE_BLOCK = "Block";
	public static final String TILE_TYPE_MAGNETIC_SOURCE = "Magnetic Source";
	public static final String TILE_TYPE_WALL = "Wall";
	public static final String TILE_ISPUSHABLE = "IsPushable";
	public static final String TILE_ISMAGNETISABLE = "IsMagnetisable";
	public static final String TILE_BODY_WIDTH = "Body Width";
	public static final String TILE_BODY_AREA = "Body Area";
	public static final String TILE_THIS = "(this)";
	public static final String TILE_ACTOR_DEPTH = "Actor Depth";
	public static final String TILE_FRAME_DEPTH = "Frame Depth";
	// Layer/Property names
	public static int TILE_SIZE = 32;
	public boolean DEBUG_MODE = false;
	// Paths/Textures
	private String _levelPath = "Tutorial.tmx";
	private TiledStage _tiledStage;
	private PlayerActor _playerActor;
	private Stage _uiStage;
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

	public static HashMap<String, TiledStageActor.Frames> ExtractAnimations(TiledStage stage, TiledMapTile tile) {
		HashMap<String, TiledStageActor.Frames> animationFrames = new HashMap<String, TiledStageActor.Frames>();
		Iterator<String> props = tile.getProperties().getKeys();

		while (props.hasNext()) {
			String prop = props.next();
			if (prop.charAt(0) == '#') {
				String name = TiledStage.ParseProp(tile.getProperties(), prop);
				TiledMapTile animationTile = (name.equals(TILE_THIS)) ? tile : stage.findTile(TILE_NAME, name);
				animationFrames.put(prop.substring(1), TileToAnimationFrames(animationTile));
			}
		}

		return animationFrames;
	}

	public static TiledStageActor.Frames TileToAnimationFrames(TiledMapTile tile) {
		ArrayList<TextureRegion> textureRegions;
		StaticTiledMapTile[] frames;

		if (tile instanceof AnimatedTiledMapTile) {
			frames = ((AnimatedTiledMapTile) tile).getFrameTiles();
		} else if (tile instanceof StaticTiledMapTile) {
			frames = new StaticTiledMapTile[]{(StaticTiledMapTile) tile};
		} else {
			return null;
		}

		textureRegions = new ArrayList<TextureRegion>();
		for (StaticTiledMapTile frameTile : frames) {
			textureRegions.add(frameTile.getTextureRegion());
		}

		// TODO: Memoise animations from same tile
		return new TiledStageActor.Frames(textureRegions, TILE_ANIMATION_FRAME_DURATION, ExtractFrameDepth(tile));
	}

	public static int ExtractFrameDepth(TiledMapTile tile) {
		return TiledStage.ParseIntegerProp(tile.getProperties(), TILE_FRAME_DEPTH, 0);
	}

	public static int ExtractActorDepth(TiledMapTile tile) {
		return TiledStage.ParseIntegerProp(tile.getProperties(), TILE_ACTOR_DEPTH, 0);
	}

	@Override
	public void show() {
		loadLevel(1);
	}

	public void loadLevel(int level) {
		float width = Gdx.graphics.getWidth();
		float height = Gdx.graphics.getHeight();

		TiledMap map = new TmxMapLoader().load(_levelPath);

		_tiledStage = new TiledStage(map, WORLD_WIDTH, WORLD_WIDTH / width * height, TICKS.values().length);

		Gdx.input.setInputProcessor(_tiledStage);
		spawnPlayer();
		spawnBlocks();
		spawnMagneticSources();
	}

	public void spawnPlayer() {
		TreeSet<TiledStage.Coordinate> coordinates = _tiledStage.findCoordinates(LAYER_OBJECTS, TILE_TYPE, TILE_TYPE_PLAYER);
		if (coordinates.size() != 1)
			throw new IllegalArgumentException("There should only be 1 player on the tiled map!");

		// Add player to stage
		TiledMapTile tile = coordinates.first().getTile(LAYER_OBJECTS);
		_playerActor = new Player(OBJECT_TYPES.PLAYER.ordinal(),
				TiledStageActor.BodyArea1x1, 1,
				ExtractAnimations(_tiledStage, tile),
				_tiledStage, LAYER_OBJECTS, coordinates.first(), ExtractActorDepth(tile));

		coordinates.first().removeTile(LAYER_OBJECTS);
		_tiledStage.setCameraFocalActor(_playerActor);
		_tiledStage.setInputFocalActor(_playerActor);
	}

	public void spawnBlocks() {
		TreeSet<TiledStage.Coordinate> coordinates = _tiledStage.findCoordinates(LAYER_OBJECTS, TILE_TYPE, TILE_TYPE_BLOCK);
		Block block;
		TiledMapTile tile;

		for (TiledStage.Coordinate coordinate : coordinates) {
			tile = coordinate.getTile(LAYER_OBJECTS);
			block = new Block(OBJECT_TYPES.BLOCK.ordinal(),
					ExtractBodyArea(tile), ExtractBodyWidth(tile),
					ExtractAnimations(_tiledStage, tile),
					_tiledStage, LAYER_OBJECTS, coordinate,
					ExtractIsPushable(tile), ExtractIsMagnetisable(tile), ExtractActorDepth(tile));

			coordinate.removeTile(LAYER_OBJECTS);
		}
	}

	public void spawnMagneticSources() {
		TreeSet<TiledStage.Coordinate> coordinates = _tiledStage.findCoordinates(LAYER_OBJECTS, TILE_TYPE, TILE_TYPE_MAGNETIC_SOURCE);
		MagneticSource source;
		TiledMapTile tile;


		for (TiledStage.Coordinate coordinate : coordinates) {
			tile = coordinate.getTile(LAYER_OBJECTS);
			source = new MagneticSource(OBJECT_TYPES.MAGNETIC_SOURCE.ordinal(),
					ExtractBodyArea(tile), ExtractBodyWidth(tile),
					ExtractAnimations(_tiledStage, tile),
					_tiledStage, LAYER_OBJECTS, coordinate, ExtractActorDepth(tile));

			coordinate.removeTile(LAYER_OBJECTS);
		}
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
		RESET, MAGNETISATION, FORCES, MOVEMENT
	}

	public enum OBJECT_TYPES {
		PLAYER, BLOCK, MAGNETIC_SOURCE
	}
}
