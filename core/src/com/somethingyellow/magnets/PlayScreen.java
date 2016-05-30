package com.somethingyellow.magnets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

import com.somethingyellow.LogicMachine;
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
	public static final String TILE_REFERENCE = "~";
	public static final String TILE_STATE = "@";
	public static final String TILE_NAME = "#";
	public static final String TILE_ACTION = "+";
	public static final String TILE_EXPRESSION_AND = "AND";
	public static final String TILE_EXPRESSION_OR = "OR";
	public static final String TILE_EXPRESSION_NOT = "NOT";
	public static final String TILE_TYPE_PLAYER = "Player";
	public static final String TILE_TYPE_BLOCK = "Block";
	public static final String TILE_TYPE_MAGNETIC_SOURCE = "Magnetic Source";
	public static final String TILE_TYPE_MAGNETIC_FLOOR = "Magnetic Floor";
	public static final String TILE_TYPE_DOOR = "Door";
	public static final String TILE_TYPE_BUTTON = "Button";
	public static final String TILE_TYPE_WALL = "Wall";
	public static final String TILE_ISPUSHABLE = "IsPushable";
	public static final String TILE_ISMAGNETISABLE = "IsMagnetisable";
	public static final String TILE_ISOPEN = "IsOpen";
	public static final String TILE_BODY_WIDTH = "Body Width";
	public static final String TILE_BODY_AREA = "Body Area";
	public static final String TILE_THIS = "(this)";
	public static final String TILE_ACTOR_DEPTH = "Actor Depth";
	public static final String TILE_FRAME_DEPTH = "Frame Depth";
	public boolean DEBUG_MODE = false;
	// Paths/Textures
	private String _levelPath = "Levels/Easy Levels Pack/Buttons.tmx";
	private TiledStage _tiledStage;
	private PlayerActor _playerActor;
	private HashMap<String, TiledMapTile> _tilesByReference;
	private HashMap<TiledMapTile, ArrayList<TiledStageActor.Frame>> _tileFramesByTile;
	private LogicMachine _logicMachine;

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

	public static boolean ExtractIsOpen(TiledMapTile tile) {
		return TiledStage.ParseBooleanProp(tile.getProperties(), TILE_ISOPEN, false);
	}

	public HashMap<String, TiledStageActor.FrameSequence> getAnimations(TiledMapTile tile) {
		HashMap<String, TiledStageActor.FrameSequence> animationFrames = new HashMap<String, TiledStageActor.FrameSequence>();
		Iterator<String> props = tile.getProperties().getKeys();

		while (props.hasNext()) {
			String prop = props.next();

			if (prop.indexOf(TILE_STATE) == 0) {
				TiledMapTile animationTile = tile;

				String reference = TiledStage.ParseProp(tile.getProperties(), prop);
				if (!reference.equals(TILE_THIS)) {
					if (reference.indexOf(TILE_REFERENCE) == 0) {
						animationTile = _tilesByReference.get(reference.substring(TILE_REFERENCE.length()));
					} else {
						throw new IllegalArgumentException("Property '" + prop + "' should either be '" + TILE_THIS + "' or '" + TILE_REFERENCE + "tileReference'!");
					}
				}

				if (!_tileFramesByTile.containsKey(animationTile)) {
					_tileFramesByTile.put(animationTile, TiledStageActor.FrameSequence.TileToFrames(animationTile));
				}

				animationFrames.put(prop.substring(TILE_STATE.length()),
						new TiledStageActor.FrameSequence(_tileFramesByTile.get(animationTile), ExtractFrameDepth(tile)));
			}
		}

		return animationFrames;
	}

	public void processActions(final TiledStageActor actor, TiledStage.TiledObject object) {
		Iterator<String> props = object.properties().getKeys();
		while (props.hasNext()) {
			String prop = props.next();

			if (prop.indexOf(TILE_ACTION) == 0) {
				final String action = prop.substring(TILE_ACTION.length());

				String expressionString = TiledStage.ParseProp(object.properties(), prop);
				final String actionName = TILE_NAME + actor.getName() + TILE_ACTION + action;

				// Replace and/or/not and add statement to logicmachine
				expressionString = expressionString.replace(TILE_EXPRESSION_AND, LogicMachine.TERM_AND).
						replace(TILE_EXPRESSION_OR, LogicMachine.TERM_OR).
						replace(TILE_EXPRESSION_NOT, LogicMachine.TERM_NOT);

				// Hook action of actor to logicmachine expression
				LogicMachine.Expression expression = _logicMachine.addExpression(expressionString, new LogicMachine.Listener() {
					@Override
					public void changed(boolean isTrue) {
						if (isTrue) doAction(actor, action);
					}
				});

				// Hook premises of actor states to actor
				for (final String predicate : expression.premises()) {
					if (predicate.indexOf(TILE_NAME) != 0) {
						throw new IllegalArgumentException("Property '" + prop + "' should be a valid expression! Predicate " + predicate + " should be prefixed with an actor's name.");
					}

					String[] parts = predicate.substring(TILE_NAME.length()).split(TILE_STATE);
					if (parts.length != 2)
						throw new IllegalArgumentException("Property '" + prop + "' should be a valid expression! Predicate " + predicate + " should point to the actor's state.");

					TiledStageActor predActor = _tiledStage.getActor(parts[0]);
					if (predActor == null)
						throw new IllegalArgumentException("Property '" + prop + "' should be a valid expression! Predicate " + predicate + " should point a non-null actor.");

					final String predState = parts[1];
					predActor.addStateListener(new TiledStageActor.StateListener() {
						@Override
						public void added(String state) {
							if (state.equals(predState)) _logicMachine.set(predicate, true);
						}

						@Override
						public void removed(String state) {
							if (state.equals(predState)) _logicMachine.set(predicate, false);
						}
					});
				}
			}
		}
	}

	public void doAction(TiledStageActor actor, String action) {
		if (actor instanceof Door) {
			Door door = (Door) actor;
			if (action.equals(Door.ACTION_OPEN)) {
				door.open();
			} else if (action.equals(Door.ACTION_CLOSE)) {
				door.close();
			}
		}
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
		_tilesByReference = new HashMap<String, TiledMapTile>();
		_tileFramesByTile = new HashMap<TiledMapTile, ArrayList<TiledStageActor.Frame>>();
		_playerActor = null;
		_logicMachine = new LogicMachine();

		Gdx.input.setInputProcessor(_tiledStage);

		Iterator<TiledMapTile> tiles = _tiledStage.tilesIterator();
		while (tiles.hasNext()) {
			TiledMapTile tile = tiles.next();

			String reference = TiledStage.ParseProp(tile.getProperties(), TILE_REFERENCE);
			if (reference != null) {
				_tilesByReference.put(reference, tile);
			}
		}

		for (TiledStage.TiledObject object : _tiledStage.objects()) {
			TiledMapTile tile = object.tile();
			if (tile == null) continue;

			String type = TiledStage.ParseProp(tile.getProperties(), TILE_TYPE, "");
			TiledStageActor actor = null;
			if (type.equals(TILE_TYPE_BLOCK)) {
				actor = spawnBlock(object);
			} else if (type.equals(TILE_TYPE_MAGNETIC_SOURCE)) {
				actor = spawnMagneticSource(object);
			} else if (type.equals(TILE_TYPE_MAGNETIC_FLOOR)) {
				actor = spawnMagneticFloor(object);
			} else if (type.equals(TILE_TYPE_PLAYER)) {
				actor = spawnPlayer(object);
			} else if (type.equals(TILE_TYPE_BUTTON)) {
				actor = spawnButton(object);
			} else if (type.equals(TILE_TYPE_DOOR)) {
				actor = spawnDoor(object);
			}

			if (actor != null && object.name() != null) actor.setName(object.name());
		}

		for (TiledStage.TiledObject object : _tiledStage.objects()) {
			TiledStageActor actor = _tiledStage.getActor(object.name());
			if (actor == null) continue;

			processActions(actor, object);
		}
	}

	public TiledStageActor spawnPlayer(TiledStage.TiledObject object) {
		if (_playerActor != null) {
			throw new IllegalArgumentException("There should only be 1 player on the tiled map!");
		}

		// Add player to stage
		_playerActor = new Player(TiledStageActor.BodyArea1x1, 1,
				getAnimations(object.tile()),
				_tiledStage, object.origin(), ExtractActorDepth(object.tile()));

		_tiledStage.setCameraFocalActor(_playerActor);
		_tiledStage.setInputFocalActor(_playerActor);

		return _playerActor;
	}

	public TiledStageActor spawnBlock(TiledStage.TiledObject object) {
		return new Block(ExtractBodyArea(object.tile()), ExtractBodyWidth(object.tile()),
				getAnimations(object.tile()),
				_tiledStage, object.origin(),
				ExtractIsPushable(object.tile()), ExtractIsMagnetisable(object.tile()), ExtractActorDepth(object.tile()));
	}

	public TiledStageActor spawnMagneticSource(TiledStage.TiledObject object) {
		return new MagneticSource(getAnimations(object.tile()), _tiledStage,
				object.origin(), ExtractActorDepth(object.tile()));
	}

	public TiledStageActor spawnMagneticFloor(TiledStage.TiledObject object) {
		return new MagneticFloor(getAnimations(object.tile()), _tiledStage,
				object.origin(), ExtractActorDepth(object.tile()));
	}

	public TiledStageActor spawnDoor(TiledStage.TiledObject object) {
		return new Door(getAnimations(object.tile()), _tiledStage,
				object.origin(), ExtractActorDepth(object.tile()), ExtractIsOpen(object.tile()));
	}

	public TiledStageActor spawnButton(TiledStage.TiledObject object) {
		return new Button(getAnimations(object.tile()), _tiledStage,
				object.origin(), ExtractActorDepth(object.tile()));
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
		RESET, MAGNETISATION, FORCES, BLOCK_MOVEMENT, PLAYER_MOVEMENT, BUTTON_PRESSES, GRAPHICS
	}
}
