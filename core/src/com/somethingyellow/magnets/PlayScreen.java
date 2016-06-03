package com.somethingyellow.magnets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

import com.badlogic.gdx.utils.Pools;
import com.somethingyellow.LogicMachine;
import com.somethingyellow.tiled.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class PlayScreen implements Screen, Player.Listener {
	public static final float TICK_DURATION = 0.06f;
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
	public static final String TILE_TYPE_OBSTRUCTED_FLOOR = "Obstructed Floor";
	public static final String TILE_TYPE_DOOR = "Door";
	public static final String TILE_TYPE_BUTTON = "Button";
	public static final String TILE_TYPE_WALL = "Wall";
	public static final String TILE_TYPE_EXIT = "Exit";
	public static final String TILE_ISPUSHABLE = "IsPushable";
	public static final String TILE_ISMAGNETISABLE = "IsMagnetisable";
	public static final String TILE_ISOPEN = "IsOpen";
	public static final String TILE_ELEVATION = "Elevation";
	public static final String TILE_BODY_WIDTH = "Body Width";
	public static final String TILE_BODY_AREA = "Body Area";
	public static final String TILE_THIS = "(this)";
	public static final String TILE_ACTOR_DEPTH = "Actor Depth";
	public static final String TILE_FRAME_DEPTH = "Frame Depth";
	public boolean DEBUG_MODE = false;

	private TiledStage _tiledStage;
	private TiledMap _map;
	private Player _playerActor;
	private HashMap<String, TiledMapTile> _tilesByReference = new HashMap<String, TiledMapTile>();
	private HashMap<TiledMapTile, ArrayList<TiledStageActor.Frame>> _tileFramesByTile = new HashMap<TiledMapTile, ArrayList<TiledStageActor.Frame>>();
	private LogicMachine _logicMachine = new LogicMachine();
	private TmxMapLoader _tmxMapLoader = new TmxMapLoader();
	private LinkedList<TiledStageActor> _actors = new LinkedList<TiledStageActor>();
	private String _levelPath;
	private Listener _listener;

	// Debugging tools
	private FPSLogger _fpsLogger = new FPSLogger();

	public PlayScreen(Listener listener) {
		_listener = listener;
	}

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

	public static int ExtractElevation(TiledMapTile tile) {
		return TiledStage.ParseIntegerProp(tile.getProperties(), TILE_ELEVATION, 0);
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
					_tileFramesByTile.put(animationTile, TiledStageActor.FrameSequence.TileToFrames(animationTile, _tiledStage.tickDuration()));
				}

				animationFrames.put(prop.substring(TILE_STATE.length()),
						new TiledStageActor.FrameSequence(_tileFramesByTile.get(animationTile), ExtractFrameDepth(tile)));
			}
		}

		return animationFrames;
	}

	public void processProperties(TiledStageActor actor, TiledMapTile tile) {
		actor.setActorDepth(ExtractActorDepth(tile));
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
					public void expressionChanged(boolean isTrue) {
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
		if (_tiledStage == null) {
			_tiledStage = new TiledStage(LAYER_ACTORS, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), SUBTICKS.values().length, TICK_DURATION);
		}

		Gdx.input.setInputProcessor(_tiledStage);
	}

	public void loadLevel(String levelPath) {
		// Unload previous level's data
		_tilesByReference.clear();
		_tileFramesByTile.clear();
		_logicMachine.clear();
		_playerActor = null;
		for (TiledStageActor actor : _actors) {
			Pools.free(actor);
		}
		_actors.clear();

		if (_map != null) _map.dispose();

		_levelPath = levelPath;

		_map = _tmxMapLoader.load(_levelPath);
		_tiledStage.load(_map);

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

			String type = TiledStage.ParseProp(tile.getProperties(), TILE_TYPE);
			TiledStageActor actor = null;
			if (type != null) {
				if (type.equals(TILE_TYPE_BLOCK)) {
					actor = spawnBlock(object);
				} else if (type.equals(TILE_TYPE_MAGNETIC_SOURCE)) {
					actor = spawnMagneticSource(object);
				} else if (type.equals(TILE_TYPE_MAGNETIC_FLOOR)) {
					actor = spawnMagneticFloor(object);
				} else if (type.equals(TILE_TYPE_OBSTRUCTED_FLOOR)) {
					actor = spawnObstructedFloor(object);
				} else if (type.equals(TILE_TYPE_PLAYER)) {
					actor = spawnPlayer(object);
				} else if (type.equals(TILE_TYPE_BUTTON)) {
					actor = spawnButton(object);
				} else if (type.equals(TILE_TYPE_DOOR)) {
					actor = spawnDoor(object);
				}
			}

			processProperties(actor, tile);

			if (actor != null) {
				_actors.add(actor);
				if (object.name() != null) actor.setName(object.name());
			}
		}

		for (TiledStage.TiledObject object : _tiledStage.objects()) {
			TiledStageActor actor = _tiledStage.getActor(object.name());
			if (actor == null) continue;

			processActions(actor, object);
		}
	}

	public void unload() {

	}

	public TiledStageActor spawnPlayer(TiledStage.TiledObject object) {
		if (_playerActor != null) {
			throw new IllegalArgumentException("There should only be 1 player on the tiled map!");
		}

		// Add player to stage
		_playerActor = Pools.get(Player.class).obtain();
		_playerActor.initialize(_tiledStage, TiledStageActor.BodyArea1x1, 1, getAnimations(object.tile()),
				object.origin(), this);

		_tiledStage.setCameraFocalActor(_playerActor);
		_tiledStage.setInputFocalActor(_playerActor);

		return _playerActor;
	}

	public TiledStageActor spawnBlock(TiledStage.TiledObject object) {
		Block block = Pools.get(Block.class).obtain();
		block.initialize(_tiledStage, ExtractBodyArea(object.tile()), ExtractBodyWidth(object.tile()),
				getAnimations(object.tile()), object.origin(), ExtractIsPushable(object.tile()), ExtractIsMagnetisable(object.tile()));
		return block;
	}

	public TiledStageActor spawnMagneticSource(TiledStage.TiledObject object) {
		MagneticSource magneticSource = Pools.get(MagneticSource.class).obtain();
		magneticSource.initialize(_tiledStage, getAnimations(object.tile()), object.origin());
		return magneticSource;
	}

	public TiledStageActor spawnMagneticFloor(TiledStage.TiledObject object) {
		MagneticFloor magneticFloor = Pools.get(MagneticFloor.class).obtain();
		magneticFloor.initialize(_tiledStage, getAnimations(object.tile()), object.origin());
		return magneticFloor;
	}

	public TiledStageActor spawnObstructedFloor(TiledStage.TiledObject object) {
		ObstructedFloor obstructedFloor = Pools.get(ObstructedFloor.class).obtain();
		obstructedFloor.initialize(_tiledStage, getAnimations(object.tile()), object.origin(), ExtractElevation(object.tile()));
		return obstructedFloor;
	}

	public TiledStageActor spawnDoor(TiledStage.TiledObject object) {
		Door door = Pools.get(Door.class).obtain();
		door.initialize(_tiledStage, ExtractBodyArea(object.tile()), ExtractBodyWidth(object.tile()),
				getAnimations(object.tile()), object.origin(), ExtractIsOpen(object.tile()));
		return door;
	}

	public TiledStageActor spawnButton(TiledStage.TiledObject object) {
		Button button = Pools.get(Button.class).obtain();
		button.initialize(_tiledStage, ExtractBodyArea(object.tile()), ExtractBodyWidth(object.tile()),
				getAnimations(object.tile()), object.origin());
		return button;
	}


	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0f, 0f, 0f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if (_tiledStage != null) _tiledStage.draw();
		if (DEBUG_MODE) _fpsLogger.log();
	}

	@Override
	public void resize(int width, int height) {
		_tiledStage.setScreenSize(width, height);
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
		if (_map != null) _map.dispose();
		_tiledStage.dispose();
	}

	@Override
	public void resetLevel() {
		loadLevel(_levelPath);
	}

	@Override
	public void exitLevel() {
		_listener.exitLevel();
	}

	@Override
	public void setZoom(float zoom) {
		_tiledStage.setZoom(zoom);
	}

	public enum SUBTICKS {
		RESET, BUTTON_PRESSES, MAGNETISATION, FORCES, BLOCK_MOVEMENT, PLAYER_MOVEMENT, GRAPHICS
	}

	public interface Listener {
		void exitLevel();
	}
}
