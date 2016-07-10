package com.somethingyellow.magnets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Pools;
import com.somethingyellow.Controller;
import com.somethingyellow.LogicMachine;
import com.somethingyellow.graphics.AnimatedActor;
import com.somethingyellow.graphics.Animation;
import com.somethingyellow.graphics.AnimationDef;
import com.somethingyellow.tiled.TiledStage;
import com.somethingyellow.tiled.TiledStageActor;
import com.somethingyellow.tiled.TiledStageLightSource;
import com.somethingyellow.utility.TiledMapHelper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

public class PlayScreen implements Screen, Player.Commands, Lodestone.Commands {
	private TiledStage _tiledStage;
	private TiledMap _animationsMap;
	private Controller _controller = new Controller();
	private HashMap<String, TiledMapTile> _tilesByType = new HashMap<String, TiledMapTile>();
	private Player _player;
	private LogicMachine _logicMachine = new LogicMachine();
	private HashMap<String, AnimationDef> _animationDefs;
	private HashMap<String, AnimationDef> _tempAnimationDefs = new HashMap<String, AnimationDef>();
	private LinkedList<TiledStageActor> _bodies = new LinkedList<TiledStageActor>();
	private LinkedList<TiledStageLightSource> _lightSources = new LinkedList<TiledStageLightSource>();
	private String _levelPath;
	private LinkedList<TiledStage.DIRECTION> _moveQueue = new LinkedList<TiledStage.DIRECTION>();
	private TiledStageListener _tiledStageListener = new TiledStageListener();
	private TiledStageCommands _tiledStageCommands = new TiledStageCommands();
	private TiledStageActorListener _tiledStageActorListener = new TiledStageActorListener();
	private ControllerListener _controllerListener = new ControllerListener();
	private LinkedList<HashMap<TiledStageActor, TiledStageActor.State>> _statesHistory = new LinkedList<HashMap<TiledStageActor, TiledStageActor.State>>();
	private HashSet<TiledStageActor> _stateChangedInTick = new HashSet<TiledStageActor>();
	private boolean _toUndo = false;
	private boolean _toEnd = false;
	private int _playerMovesCount = 0;
	private SpriteBatch _UISpriteBatch;
	private Animation _pauseOverlayAnimation;
	private Commands _commands;
	private Skin _skin;

	private Stack<String> solution = new Stack<String>();

	public PlayScreen(Skin skin, Commands commands) {
		_skin = skin;
		_commands = commands;
		_tiledStage = new TiledStage(SUBTICKS.values().length, Config.GameTickDuration, _tiledStageCommands);
		_tiledStage.listeners().add(_tiledStageListener);
		_UISpriteBatch = new SpriteBatch();
		_animationsMap = _commands.loadMap(Config.GameAnimationsTMXPath);
		_animationDefs = _commands.loadAnimations(_animationsMap, Config.GameTickDuration);
		_pauseOverlayAnimation = new Animation(_animationDefs.get(Config.AnimationPauseOverlay));
	}

	public static boolean[] StringToBodyArea(String bodyArea, boolean[] defaultBodyArea) {
		if (bodyArea == null) return defaultBodyArea;
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

	public TiledStageLightSource getLightSource(TiledMapTile tile) {
		String lightingImagePath = TiledMapHelper.ParseProp(tile.getProperties(), Config.TMX.Tiles.LightingImagePathProp);
		if (lightingImagePath == null) return null; // no light source

		Texture lightingTexture = new Texture(Gdx.files.internal(lightingImagePath));

		float sizeX = TiledMapHelper.ParseFloatProp(tile.getProperties(), Config.TMX.Tiles.LightingWidthProp, 0f);
		float sizeY = TiledMapHelper.ParseFloatProp(tile.getProperties(), Config.TMX.Tiles.LightingHeightProp, 0f);

		if (sizeX == 0f || sizeY == 0f)
			throw new IllegalArgumentException("`Width` and `Height` of lighting must be defined!");

		float intensity = TiledMapHelper.ParseFloatProp(tile.getProperties(), Config.TMX.Tiles.LightingIntensityProp, 1f);
		float displacementX = TiledMapHelper.ParseIntegerProp(tile.getProperties(), Config.TMX.Tiles.LightingDisplacementXProp, 0);
		float displacementY = TiledMapHelper.ParseIntegerProp(tile.getProperties(), Config.TMX.Tiles.LightingDisplacementYProp, 0);

		TiledStageLightSource lightSource = Pools.obtain(TiledStageLightSource.class);
		lightSource.initialize(lightingTexture);
		lightSource.setIntensity(intensity);
		lightSource.setSize(sizeX, sizeY);
		lightSource.setRenderDisplacement(sizeX / 2 - displacementX, sizeY / 2 - displacementY);
		return lightSource;
	}

	public HashMap<String, AnimationDef> getAnimationDefs(TiledMapTile tile) {
		_tempAnimationDefs.clear();
		Iterator<String> props = tile.getProperties().getKeys();

		while (props.hasNext()) {
			String prop = props.next();

			if (prop.indexOf(Config.TMX.Tiles.AnimationPropPrefix) == 0) {
				String animationName = TiledMapHelper.ParseProp(tile.getProperties(), prop);
				if (!_animationDefs.containsKey(animationName))
					throw new IllegalArgumentException("Property '" + prop + "' pointing to missing animation!");

				AnimationDef animationDef = _animationDefs.get(animationName);
				_tempAnimationDefs.put(prop.substring(Config.TMX.Tiles.AnimationPropPrefix.length()), animationDef);
			}
		}

		return _tempAnimationDefs;
	}

	public void processProperties(TiledStageActor body, TiledMapTile tile) {
		// Light source
		final TiledStageLightSource lightSource = getLightSource(tile);
		if (lightSource != null) {
			addLightSource(lightSource);
			lightSource.setPosition(body.getX(), body.getY());
			body.listeners().add(new TiledStageActor.Listener() {
				@Override
				public void positionChanged(TiledStageActor body) {
					lightSource.setPosition(body.getX(), body.getY());
				}

				@Override
				public void removed(AnimatedActor actor) {
					actor.remove();
				}
			});
		}
	}

	public void doAction(TiledStageActor body, String action) {
		if (body instanceof Door) {
			Door door = (Door) body;
			if (action.equals(Config.TMX.Tiles.Door.Actions.Open)) {
				door.open();
			} else if (action.equals(Config.TMX.Tiles.Door.Actions.Close)) {
				door.close();
			}
		}
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(_controller);
	}

	public void loadLevel(String levelPath) {
		if (_tiledStage.isLoaded()) _tiledStage.unload();

		_levelPath = levelPath;

		TiledMap map = _commands.loadMap(_levelPath);
		_tiledStage.load(map, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), _controller.zoom(), Config.GameWallLayer);

		if (_player == null) throw new IllegalArgumentException("No player on the map!");

		// Saving first instance of each tile type in tileset
		_tilesByType.clear();
		Iterator<TiledMapTile> iterator = _tiledStage.tilesIterator();
		while (iterator.hasNext()) {
			TiledMapTile tile = iterator.next();
			String type = TiledMapHelper.ParseProp(tile.getProperties(), Config.TMX.Tiles.TypeProp);
			if (type != null && !_tilesByType.containsKey(type)) {
				_tilesByType.put(type, tile);
			}
		}

		// Controls
		_controller.listeners().add(_controllerListener);
		_moveQueue.clear();

		// Remember first state
		_statesHistory.clear();
		HashMap<TiledStageActor, TiledStageActor.State> states = new HashMap<TiledStageActor, TiledStageActor.State>();
		for (TiledStageActor body : _bodies) {
			states.put(body, body.getState());
		}
		_statesHistory.add(states);

		_playerMovesCount = 0;
	}

	public void unloadLevel() {
		_controller.listeners().clear();
		_controller.reset();
		_logicMachine.clear();
		for (TiledStageActor body : _bodies.toArray(new TiledStageActor[_bodies.size()])) {
			body.remove();
		}
		_bodies.clear();
		_player = null;
		for (TiledStageLightSource lightSource : _lightSources.toArray(new TiledStageLightSource[_lightSources.size()])) {
			lightSource.remove();
		}
		_lightSources.clear();
		_tiledStage.unload();
	}

	public TiledStageActor spawnActor(TiledMapTile tile, TiledStage.Coordinate origin) {
		String type = TiledMapHelper.ParseProp(tile.getProperties(), Config.TMX.Tiles.TypeProp);
		if (type == null) {
			throw new IllegalArgumentException("Property '" + Config.TMX.Tiles.TypeProp + "' not found!");
		}

		TiledStageActor body = null;

		if (type.equals(Config.TMX.Tiles.Types.MagneticField)) {

			MagneticField magneticField = Pools.get(MagneticField.class).obtain();
			magneticField.initialize(getAnimationDefs(tile), origin);
			body = magneticField;

		} else if (type.equals(Config.TMX.Tiles.Types.Lodestone)) {

			Lodestone lodestone = Pools.get(Lodestone.class).obtain();
			lodestone.initialize(
					getAnimationDefs(tile),
					StringToBodyArea(TiledMapHelper.ParseProp(tile.getProperties(), Config.TMX.Tiles.Lodestone.BodyAreaProp), TiledStageActor.BodyArea1x1),
					TiledMapHelper.ParseIntegerProp(tile.getProperties(), Config.TMX.Tiles.Lodestone.BodyWidthProp, 1),
					origin,
					TiledMapHelper.ParseBooleanProp(tile.getProperties(), Config.TMX.Tiles.Lodestone.IsPushableProp, false),
					TiledMapHelper.ParseBooleanProp(tile.getProperties(), Config.TMX.Tiles.Lodestone.IsMagnetisableProp, false), this);
			body = lodestone;

		} else if (type.equals(Config.TMX.Tiles.Types.MagneticSource)) {

			MagneticSource magneticSource = Pools.get(MagneticSource.class).obtain();
			magneticSource.initialize(getAnimationDefs(tile), origin);
			body = magneticSource;

		} else if (type.equals(Config.TMX.Tiles.Types.MagneticFloor)) {

			MagneticFloor magneticFloor = Pools.get(MagneticFloor.class).obtain();
			magneticFloor.initialize(getAnimationDefs(tile), origin);
			body = magneticFloor;

		} else if (type.equals(Config.TMX.Tiles.Types.ObstructedFloor)) {

			ObstructedFloor obstructedFloor = Pools.get(ObstructedFloor.class).obtain();
			obstructedFloor.initialize(
					getAnimationDefs(tile),
					origin,
					TiledMapHelper.ParseIntegerProp(tile.getProperties(), Config.TMX.Tiles.ObstructedFloor.ElevationProp, 0)
			);
			body = obstructedFloor;

		} else if (type.equals(Config.TMX.Tiles.Types.Player)) {

			if (_player != null) {
				throw new IllegalArgumentException("There should only be 1 player on the tiled map!");
			}

			_player = Pools.get(Player.class).obtain();
			_player.initialize(getAnimationDefs(tile), TiledStageActor.BodyArea1x1, 1, origin, this);

			_tiledStage.setCameraFocalActor(_player);
			body = _player;

		} else if (type.equals(Config.TMX.Tiles.Types.Button)) {

			Button button = Pools.get(Button.class).obtain();
			button.initialize(
					getAnimationDefs(tile),
					StringToBodyArea(TiledMapHelper.ParseProp(tile.getProperties(), Config.TMX.Tiles.Button.BodyAreaProp), TiledStageActor.BodyArea1x1),
					TiledMapHelper.ParseIntegerProp(tile.getProperties(), Config.TMX.Tiles.Button.BodyWidthProp, 1),
					origin
			);
			body = button;

		} else if (type.equals(Config.TMX.Tiles.Types.Door)) {

			Door door = Pools.get(Door.class).obtain();
			door.initialize(
					getAnimationDefs(tile),
					StringToBodyArea(TiledMapHelper.ParseProp(tile.getProperties(), Config.TMX.Tiles.Door.BodyAreaProp), TiledStageActor.BodyArea1x1),
					TiledMapHelper.ParseIntegerProp(tile.getProperties(), Config.TMX.Tiles.Door.BodyWidthProp, 1),
					origin,
					TiledMapHelper.ParseBooleanProp(tile.getProperties(), Config.TMX.Tiles.Door.IsOpenProp, false)
			);
			body = door;

		} else if (type.equals(Config.TMX.Tiles.Types.Exit)) {

			Exit exit = Pools.get(Exit.class).obtain();
			exit.initialize(getAnimationDefs(tile), origin);
			body = exit;

		}

		if (body == null) throw new IllegalArgumentException("Invalid type: '" + type + "'!");

		addActor(body, tile);
		return body;
	}

	public void addActor(TiledStageActor actor, TiledMapTile tile) {
		processProperties(actor, tile);

		_tiledStage.addActor(actor);
		_bodies.add(actor);

		actor.listeners().add(_tiledStageActorListener);
	}

	public void addLightSource(TiledStageLightSource lightSource) {
		_tiledStage.addLightSource(lightSource);
		_lightSources.add(lightSource);

		lightSource.addListener(new TiledStageLightSource.Listener() {
			@Override
			public void removed(TiledStageLightSource lightSource) {
				_lightSources.remove(lightSource);
			}
		});
	}

	@Override
	public MagneticField spawnMagneticField(TiledStage.Coordinate coordinate) {
		return null;
		// return (MagneticField) spawnActor(_tilesByType.get(GameConfig.TMX.Tiles.Types.MagneticField), coordinate);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0f, 0f, 0f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		_tiledStage.draw();

		// UI
		_UISpriteBatch.begin();

		// Pause screen
		if (_tiledStage.isPaused()) {
			Sprite sprite = _pauseOverlayAnimation.getSprite();
			sprite.setPosition(0, 0);
			sprite.draw(_UISpriteBatch);
			Label pausedText = new Label(Config.PausedText, _skin);
			pausedText.setPosition(10, 10);
			pausedText.draw(_UISpriteBatch, 1f);
		}

		// Player moves counter
		//Label playerMovesText = new Label(String.valueOf(_playerMovesCount), _skin);
		//playerMovesText.setPosition(Gdx.graphics.getWidth() - playerMovesText.getWidth() - 10, 10);
		//playerMovesText.draw(_UISpriteBatch, 1f);

		_UISpriteBatch.end();
	}

	@Override
	public void resize(int width, int height) {
		_tiledStage.setScreenSize(width, height);
	}

	@Override
	public void pause() {
		pauseLevel();
	}

	@Override
	public void resume() {
		unpauseLevel();
	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
		if (_tiledStage != null) _tiledStage.dispose();
		if (_animationsMap != null) {
			_animationDefs.clear();
			_animationsMap.dispose();
		}
	}

	public void resetLevel() {
		solution = new Stack<String>();
		unloadLevel();
		loadLevel(_levelPath);
	}

	public void pauseLevel() {
		_tiledStage.setIsPaused(true);
	}

	public void unpauseLevel() {
		_tiledStage.setIsPaused(false);
	}

	public void exitLevel() {
		unloadLevel();
		System.out.println("Ended");
		for (String dir : solution) System.out.print(dir + " ");
		System.out.print(" (" + solution.size() + ")\n");
		solution = new Stack<String>();
		_commands.exitLevel();
	}

	@Override
	public void endLevel() {
		_toEnd = true;
	}

	public void setZoom(float zoom) {
		_tiledStage.setZoom(zoom);
	}

	public enum SUBTICKS {
		RESET, BUTTON_PRESSES, MAGNETISATION, FORCES, BLOCK_MOVEMENT, GRAPHICS
	}

	public interface Commands {
		void exitLevel();

		TiledMap loadMap(String mapFilePath);

		HashMap<String, AnimationDef> loadAnimations(TiledMap map, float defaultDuration);
	}

	public static class Config {
		public static float GameTickDuration = 0.06f;
		public static String GameAnimationsTMXPath = "";
		public static String GameWallLayer = "Walls";
		public static String AnimationPauseOverlay = "";
		public static float GameUndoTicks = GameTickDuration;
		public static String PausedText = "CONTROLS:\n" +
				"'WASD' or arrow keys to move.\n" +
				"Scroll mouse to zoom in and out.\n" +
				"'Esc' to exit level.\n" +
				"'P' to toggle pause.\n" +
				"'Ctrl + R' to reset level.\n" +
				"'Ctrl + Z' to undo a game tick.";

		public static class TMX {
			public static class Tiles {
				public static String TypeProp = "Type";
				public static String LightingImagePathProp = "Lighting Image Path";
				public static String LightingWidthProp = "Lighting Width";
				public static String LightingHeightProp = "Lighting Height";
				public static String LightingIntensityProp = "Lighting Intensity";
				public static String LightingDisplacementXProp = "Lighting Displacement X";
				public static String LightingDisplacementYProp = "Lighting Displacement Y";
				public static String AnimationPropPrefix = "~";

				public static class Types {
					public static String Lodestone = "Block";
					public static String Door = "Door";
					public static String Player = "Player";
					public static String MagneticSource = "Magnetic Source";
					public static String MagneticFloor = "Magnetic Floor";
					public static String ObstructedFloor = "Obstructed Floor";
					public static String MagneticField = "Magnetic Field";
					public static String Button = "Button";
					public static String Exit = "Exit";
				}

				public static class Door {
					public static String IsOpenProp = "IsOpen";
					public static String BodyWidthProp = "Body Width";
					public static String BodyAreaProp = "Body Area";

					public static class Actions {
						public static String Open = "Open";
						public static String Close = "Close";
					}
				}

				public static class Button {
					public static String BodyWidthProp = "Body Width";
					public static String BodyAreaProp = "Body Area";
				}

				public static class Lodestone {
					public static String IsPushableProp = "IsPushable";
					public static String IsMagnetisableProp = "IsMagnetisable";
					public static String BodyWidthProp = "Body Width";
					public static String BodyAreaProp = "Body Area";
				}

				public static class ObstructedFloor {
					public static String ElevationProp = "Elevation";
				}
			}

			public static class Objects {
				public static String ActionPrefixIdentifier = "+";
				public static String StatusPrefixIdentifier = "_";
				public static String NotLogicOperator = "NOT";
				public static String OrLogicOperator = "OR";
				public static String AndLogicOperator = "AND";
			}
		}
	}

	public class TiledStageListener extends TiledStage.Listener {
		@Override
		public void beforeTick(TiledStage stage) {
			if (_controller.isKeyCtrlHeld() && _controller.isKeyHeld(Input.Keys.Z)) {
				if (_statesHistory.size() > 1) { // Cannot invalidate first state
					HashMap<TiledStageActor, TiledStageActor.State> states = _statesHistory.removeLast(); // states that are invalidated

					for (TiledStageActor body : states.keySet()) {
						// do search down states history to find last state of the body
						int index = _statesHistory.size() - 1;
						while (index >= 0) {
							HashMap<TiledStageActor, TiledStageActor.State> prevStates = _statesHistory.get(index);
							if (prevStates.containsKey(body)) {
								prevStates.get(body).restore(Config.GameUndoTicks);
								break;
							} else {
								index--;
							}
						}

						// Decrease player moves counter (just for development)
						if (body == _player) {
							solution.pop();
							_playerMovesCount--;
						}
					}
					if (!_tiledStage.isPaused()) pauseLevel();
				}
				_toUndo = false;
			}

			// Unpause game when movement keys are pressed
			if (_tiledStage.isPaused() && (_controller.isKeyLeftHeld() || _controller.isKeyRightHeld() ||
					_controller.isKeyUpHeld() || _controller.isKeyDownHeld())) unpauseLevel();

			_stateChangedInTick.clear();
		}

		@Override
		public void ticked(TiledStage stage) {
			if (!_player.isMoving()) {
				TiledStage.DIRECTION direction = null;
				if (_moveQueue.isEmpty()) {
					if (_controller.isKeyLeftHeld() && !_controller.isKeyRightHeld() &&
							!_controller.isKeyUpHeld() && !_controller.isKeyDownHeld()) {
						direction = TiledStage.DIRECTION.WEST;
					} else if (_controller.isKeyRightHeld() && !_controller.isKeyLeftHeld() &&
							!_controller.isKeyUpHeld() && !_controller.isKeyDownHeld()) {
						direction = TiledStage.DIRECTION.EAST;
					} else if (_controller.isKeyUpHeld() && !_controller.isKeyLeftHeld() &&
							!_controller.isKeyRightHeld() && !_controller.isKeyDownHeld()) {
						direction = TiledStage.DIRECTION.NORTH;
					} else if (_controller.isKeyDownHeld() && !_controller.isKeyLeftHeld() &&
							!_controller.isKeyRightHeld() && !_controller.isKeyUpHeld()) {
						direction = TiledStage.DIRECTION.SOUTH;
					}
				} else {
					direction = _moveQueue.removeFirst();
				}

				if (direction != null) {
					if (_player.moveDirection(direction)) {
						solution.add(direction.toString());
						_playerMovesCount++;
					}
				}
			}

			// Check any state changes, save if there are
			if (!_stateChangedInTick.isEmpty()) {
				HashMap<TiledStageActor, TiledStageActor.State> states = new HashMap<TiledStageActor, TiledStageActor.State>();
				for (TiledStageActor body : _stateChangedInTick) {
					TiledStageActor.State state = body.getState();
					states.put(body, state);
				}
				_statesHistory.add(states);
				_stateChangedInTick.clear();
			}
		}

		@Override
		public void drawn(TiledStage stage) {
			if (_toEnd) {
				exitLevel();
				_toEnd = false;
			}
		}
	}

	public class TiledStageCommands implements TiledStage.Commands {
		@Override
		public void spawnObject(TiledStage.Coordinate origin, TiledMapTile tile,
		                        String name, MapProperties properties) {
			if (tile != null) {
				final TiledStageActor actor = spawnActor(tile, origin);
				if (name != null) actor.setName(name);

				// Hook actions to logicmachine
				Iterator<String> props = properties.getKeys();
				while (props.hasNext()) {
					String prop = props.next();

					if (prop.indexOf(Config.TMX.Objects.ActionPrefixIdentifier) == 0) {
						final String action = prop.substring(Config.TMX.Objects.ActionPrefixIdentifier.length());

						String expressionString = TiledMapHelper.ParseProp(properties, prop);

						expressionString = expressionString.replaceAll(Config.TMX.Objects.AndLogicOperator, "&&").
								replaceAll(Config.TMX.Objects.OrLogicOperator, "||").
								replaceAll(Config.TMX.Objects.NotLogicOperator, "!");

						// Hook action of actor to logicmachine expression
						LogicMachine.Expression expression = _logicMachine.addExpression(expressionString, new LogicMachine.Listener() {
							@Override
							public void expressionChanged(boolean isTrue) {
								if (isTrue) doAction(actor, action);
							}
						});
					}
				}
			}
		}
	}

	public class TiledStageActorListener extends TiledStageActor.Listener {
		@Override
		public void stateChanged(TiledStageActor actor) {
			_stateChangedInTick.add(actor);
		}

		@Override
		public void statusAdded(TiledStageActor actor, String status) {
			if (actor.getName() != null) {
				_logicMachine.set(actor.getName() + Config.TMX.Objects.StatusPrefixIdentifier + status, true);
			}
		}

		@Override
		public void statusRemoved(TiledStageActor actor, String status) {
			if (actor.getName() != null) {
				_logicMachine.set(actor.getName() + Config.TMX.Objects.StatusPrefixIdentifier + status, false);
			}
		}

		@Override
		public void removed(AnimatedActor actor) {
			_bodies.remove(actor);
		}
	}

	public class ControllerListener extends Controller.Listener {
		@Override
		public void zoomed(Controller controller, float zoom) {
			setZoom(zoom);
		}

		@Override
		public void keyUpPressed(Controller controller) {
			if (!_tiledStage.isPaused()) _moveQueue.add(TiledStage.DIRECTION.NORTH);
		}

		@Override
		public void keyDownPressed(Controller controller) {
			if (!_tiledStage.isPaused()) _moveQueue.add(TiledStage.DIRECTION.SOUTH);
		}

		@Override
		public void keyLeftPressed(Controller controller) {
			if (!_tiledStage.isPaused()) _moveQueue.add(TiledStage.DIRECTION.WEST);
		}

		@Override
		public void keyRightPressed(Controller controller) {
			if (!_tiledStage.isPaused()) _moveQueue.add(TiledStage.DIRECTION.EAST);
		}

		@Override
		public void keyEscapePressed(Controller controller) {
			exitLevel();
		}

		@Override
		public void keyPressed(Controller controller, int keycode) {
			switch (keycode) {
				case Input.Keys.P:
					if (_tiledStage.isPaused()) {
						unpauseLevel();
					} else {
						pauseLevel();
					}
					break;
			}

			if (controller.isKeyCtrlHeld()) {
				switch (keycode) {
					case Input.Keys.R:
						resetLevel();
						break;
				}
			}
		}
	}
}
