package com.somethingyellow.magnets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Pools;
import com.somethingyellow.LogicMachine;
import com.somethingyellow.graphics.AnimatedActor;
import com.somethingyellow.graphics.Animation;
import com.somethingyellow.graphics.AnimationDef;
import com.somethingyellow.tiled.TiledStage;
import com.somethingyellow.tiled.TiledStageActor;
import com.somethingyellow.tiled.TiledStageHistorian;
import com.somethingyellow.tiled.TiledStageLightSource;
import com.somethingyellow.utility.Controller;
import com.somethingyellow.utility.TiledMapHelper;
import com.somethingyellow.utility.TimedTrigger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

public class PlayScreen implements Screen {
	private TiledStage _tiledStage;
	private TiledMap _animationsMap;
	private Controller _controller = new Controller();
	private Player _player;
	private LogicMachine _logicMachine = new LogicMachine();
	private HashMap<String, AnimationDef> _animationDefs;
	private HashMap<String, AnimationDef> _tempAnimationDefs = new HashMap<String, AnimationDef>();
	private HashMap<String, TiledMapTile> _tilesByType;
	private String _levelPath;
	private LinkedList<Player.PLAYER_ACTION> _moveQueue = new LinkedList<Player.PLAYER_ACTION>();
	private TiledStageListener _tiledStageListener = new TiledStageListener();
	private TiledStageCommands _tiledStageCommands = new TiledStageCommands();
	private PlayerCommands _playerCommands = new PlayerCommands();
	private LodestoneCommands _lodestoneCommands = new LodestoneCommands();
	private TiledStageHistorian _tiledStageHistorian;
	private TiledStageActorListener _tiledStageActorListener = new TiledStageActorListener();
	private ControllerListener _controllerListener = new ControllerListener();
	private UndoTimeTrigger _undoTimeTriggerTrigger = new UndoTimeTrigger();
	private boolean _toEndLevel = false;
	private boolean _toSaveStates = false;
	private TimedTrigger _undoTimeTrigger;
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
		_tiledStageHistorian = new TiledStageHistorian(_tiledStage);
		_tiledStage.listeners().add(_tiledStageListener);
		_undoTimeTrigger = new TimedTrigger(_undoTimeTriggerTrigger, Config.UndoTriggerTimingChart);
		_UISpriteBatch = new SpriteBatch();
		_tilesByType = new HashMap<String, TiledMapTile>();
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
		String lightingAnimation = TiledMapHelper.ParseProp(tile.getProperties(), Config.TMX.Tiles.LightingAnimationProp);
		if (lightingAnimation == null) return null; // no light source

		float intensity = TiledMapHelper.ParseFloatProp(tile.getProperties(), Config.TMX.Tiles.LightingIntensityProp, 1f);
		float displacementX = TiledMapHelper.ParseIntegerProp(tile.getProperties(), Config.TMX.Tiles.LightingDisplacementXProp, 0);
		float displacementY = TiledMapHelper.ParseIntegerProp(tile.getProperties(), Config.TMX.Tiles.LightingDisplacementYProp, 0);

		TiledStageLightSource lightSource = Pools.obtain(TiledStageLightSource.class);
		lightSource.initialize(_animationDefs.get(lightingAnimation), displacementX, displacementY);
		lightSource.setIntensity(intensity);
		return lightSource;
	}

	public HashMap<String, AnimationDef> getAnimationDefs(TiledMapTile tile) {
		_tempAnimationDefs.clear();
		Iterator<String> props = tile.getProperties().getKeys();

		while (props.hasNext()) {
			String prop = props.next();

			if (prop.indexOf(Config.TMX.Tiles.StatusAnimationPropPrefix) == 0) {
				String animationName = TiledMapHelper.ParseProp(tile.getProperties(), prop);
				if (!_animationDefs.containsKey(animationName)) {
					throw new IllegalArgumentException("Property '" + prop + "' pointing to missing animation: '" + animationName + "'!");
				}

				AnimationDef animationDef = _animationDefs.get(animationName);
				_tempAnimationDefs.put(prop.substring(Config.TMX.Tiles.StatusAnimationPropPrefix.length()), animationDef);
			}
		}

		return _tempAnimationDefs;
	}

	public void processProperties(TiledStageActor actor, TiledMapTile tile) {
		// Body area
		actor.setBody(
				StringToBodyArea(TiledMapHelper.ParseProp(tile.getProperties(), Config.TMX.Tiles.BodyAreaProp), TiledStageActor.BodyArea1x1),
				TiledMapHelper.ParseIntegerProp(tile.getProperties(), Config.TMX.Tiles.BodyWidthProp, 1)
		);

		// Light source
		final TiledStageLightSource lightSource = getLightSource(tile);
		if (lightSource != null) {
			addLightSource(lightSource);
			lightSource.setPosition(actor.getX(), actor.getY());
			actor.listeners().add(new TiledStageActor.Listener() {
				@Override
				public void positionChanged(TiledStageActor body) {
					lightSource.setPosition(body.getX(), body.getY());
				}

				@Override
				public void removed(AnimatedActor actor) {
					lightSource.remove();
				}
			});
		}

	}

	public void doAction(TiledStageActor actor, String action) {
		if (actor instanceof Door) {
			Door door = (Door) actor;
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
		if (!_tilesByType.containsKey(Config.TMX.Tiles.Types.MagneticAttraction))
			throw new IllegalArgumentException("Need tile of type for MagneticAttraction!");

		// Controls
		_controller.listeners().add(_controllerListener);
		_moveQueue.clear();

		_tiledStageHistorian.save();

		_playerMovesCount = 0;
	}

	public void unloadLevel() {
		_controller.listeners().clear();
		_controller.reset();
		_logicMachine.clear();
		_player = null;
		_tiledStage.unload();
		_tilesByType.clear();
		_tiledStageHistorian.reset();
	}

	public TiledStageActor spawnActor(TiledMapTile tile, TiledStage.Coordinate origin) {
		String type = TiledMapHelper.ParseProp(tile.getProperties(), Config.TMX.Tiles.TypeProp);
		if (type == null) {
			throw new IllegalArgumentException("Property '" + Config.TMX.Tiles.TypeProp + "' not found!");
		}

		TiledStageActor actor = null;

		if (type.equals(Config.TMX.Tiles.Types.Lodestone)) {

			Lodestone lodestone = Pools.get(Lodestone.class).obtain();
			lodestone.initialize(
					getAnimationDefs(tile),
					origin,
					TiledMapHelper.ParseBooleanProp(tile.getProperties(), Config.TMX.Tiles.Lodestone.IsPushableProp, false),
					TiledMapHelper.ParseBooleanProp(tile.getProperties(), Config.TMX.Tiles.Lodestone.IsMagnetisableProp, false),
					TiledMapHelper.ParseIntegerProp(tile.getProperties(), Config.TMX.Tiles.Lodestone.MagneticRangeProp, 1),
					TiledMapHelper.ParseIntegerProp(tile.getProperties(), Config.TMX.Tiles.Lodestone.AttractionRangeProp, 2),
					_lodestoneCommands);
			actor = lodestone;

		} else if (type.equals(Config.TMX.Tiles.Types.MagneticSource)) {

			MagneticSource magneticSource = Pools.get(MagneticSource.class).obtain();
			magneticSource.initialize(
					getAnimationDefs(tile),
					origin,
					TiledMapHelper.ParseBooleanProp(tile.getProperties(), Config.TMX.Tiles.MagneticSource.isSolidProp, true),
					TiledMapHelper.ParseIntegerProp(tile.getProperties(), Config.TMX.Tiles.MagneticSource.MagnetisationRangeProp, 1),
					TiledMapHelper.ParseIntegerProp(tile.getProperties(), Config.TMX.Tiles.MagneticSource.MagnetisationStrengthProp, 1),
					TiledMapHelper.ParseIntegerProp(tile.getProperties(), Config.TMX.Tiles.MagneticSource.AttractionRangeProp, 2),
					TiledMapHelper.ParseIntegerProp(tile.getProperties(), Config.TMX.Tiles.MagneticSource.AttractionStrengthProp, 1)
			);
			actor = magneticSource;

		} else if (type.equals(Config.TMX.Tiles.Types.Player)) {

			if (_player != null) {
				throw new IllegalArgumentException("There should only be 1 player on the tiled map!");
			}

			_player = Pools.get(Player.class).obtain();
			_player.initialize(getAnimationDefs(tile), origin, _playerCommands);

			_tiledStage.setCameraFocalActor(_player);
			actor = _player;

		} else if (type.equals(Config.TMX.Tiles.Types.Button)) {

			Button button = Pools.get(Button.class).obtain();
			button.initialize(
					getAnimationDefs(tile),
					origin
			);
			actor = button;

		} else if (type.equals(Config.TMX.Tiles.Types.Door)) {

			Door door = Pools.get(Door.class).obtain();
			door.initialize(
					getAnimationDefs(tile),
					origin,
					TiledMapHelper.ParseBooleanProp(tile.getProperties(), Config.TMX.Tiles.Door.IsOpenProp, false)
			);
			actor = door;

		} else if (type.equals(Config.TMX.Tiles.Types.Exit)) {

			Exit exit = Pools.get(Exit.class).obtain();
			exit.initialize(getAnimationDefs(tile), origin);
			actor = exit;
		}

		if (actor == null) throw new IllegalArgumentException("Invalid type: '" + type + "'!");

		addActor(actor, tile);
		return actor;
	}

	public void addActor(TiledStageActor actor, TiledMapTile tile) {
		processProperties(actor, tile);
		actor.listeners().add(_tiledStageActorListener);
	}

	public void addLightSource(TiledStageLightSource lightSource) {
		_tiledStage.addLightSource(lightSource);
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

	public boolean isPaused() {
		return _tiledStage.isPaused();
	}

	public void exitLevel() {
		unloadLevel();
		System.out.println("Ended");
		for (String dir : solution) System.out.print(dir + " ");
		System.out.print(" (" + solution.size() + ")\n");
		solution = new Stack<String>();
		_commands.exitLevel();
	}

	public void setZoom(float zoom) {
		_tiledStage.setZoom(zoom);
	}

	public Player.PLAYER_ACTION checkPlayerInput() {
		Player.PLAYER_ACTION action = null;
		if (!_player.isMoving()) {
			// If player is not moving, move player if keys are held or move queue is not empty
			if (_moveQueue.isEmpty()) {
				if (_controller.isKeyLeftHeld() && !_controller.isKeyRightHeld() &&
						!_controller.isKeyUpHeld() && !_controller.isKeyDownHeld()) {
					action = Player.PLAYER_ACTION.MOVE_LEFT;
				} else if (_controller.isKeyRightHeld() && !_controller.isKeyLeftHeld() &&
						!_controller.isKeyUpHeld() && !_controller.isKeyDownHeld()) {
					action = Player.PLAYER_ACTION.MOVE_RIGHT;
				} else if (_controller.isKeyUpHeld() && !_controller.isKeyLeftHeld() &&
						!_controller.isKeyRightHeld() && !_controller.isKeyDownHeld()) {
					action = Player.PLAYER_ACTION.MOVE_UP;
				} else if (_controller.isKeyDownHeld() && !_controller.isKeyLeftHeld() &&
						!_controller.isKeyRightHeld() && !_controller.isKeyUpHeld()) {
					action = Player.PLAYER_ACTION.MOVE_DOWN;
				}
			} else {
				action = _moveQueue.removeFirst();
			}
		}

		if (action != null) {
			_toSaveStates = true;
			_playerMovesCount++;
		}

		return action;
	}

	public enum SUBTICKS {
		START, MAGNETISATION, MAGNETIC_ATTRACTION, BLOCK_MOVEMENT, BUTTONS, DOORS, PLAYER_ACTION, END
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
		public static int[] UndoTriggerTimingChart = new int[]{1, 10, 3, 3, 3, 3, 3, -1};
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
				public static String LightingAnimationProp = "Lighting Animation";
				public static String LightingIntensityProp = "Lighting Intensity";
				public static String LightingDisplacementXProp = "Lighting Displacement X";
				public static String LightingDisplacementYProp = "Lighting Displacement Y";
				public static String ElevationProp = "Elevation";
				public static String StatusAnimationPropPrefix = "~";
				public static String BodyWidthProp = "Body Width";
				public static String BodyAreaProp = "Body Area";

				public static class Types {
					public static String Lodestone = "Block";
					public static String Door = "Door";
					public static String Player = "Player";
					public static String MagneticSource = "Magnetic Source";
					public static String Button = "Button";
					public static String Exit = "Exit";
					public static String MagneticAttraction = "Magnetic Attraction";
				}

				public static class Door {
					public static String IsOpenProp = "Is Open";

					public static class Actions {
						public static String Open = "Open";
						public static String Close = "Close";
					}
				}

				public static class MagneticSource {
					public static String isSolidProp = "Is Solid";
					public static String MagnetisationStrengthProp = "Magnetisation Strength";
					public static String MagnetisationRangeProp = "Magnetisation Range";
					public static String AttractionStrengthProp = "Attraction Strength";
					public static String AttractionRangeProp = "Attraction Range";
				}

				public static class Button {
				}

				public static class Lodestone {
					public static String IsPushableProp = "IsPushable";
					public static String IsMagnetisableProp = "IsMagnetisable";
					public static String MagneticRangeProp = "Magnetic Range";
					public static String AttractionRangeProp = "Attraction Range";
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
		public void subticked(TiledStage tiledstage, int subtick) {
			super.subticked(tiledstage, subtick);

			if (subtick == SUBTICKS.BLOCK_MOVEMENT.ordinal()) {

				_tiledStage.motionResolver().resolveActorMotion();

			} else if (subtick == SUBTICKS.PLAYER_ACTION.ordinal()) {

				_tiledStage.motionResolver().resolveActorMotion();

			}
		}

		@Override
		public void beforeTick(TiledStage stage) {
			super.beforeTick(stage);

			Player.PLAYER_ACTION action = checkPlayerInput();
			if (action != null) _player.doAction(action);
		}

		@Override
		public void afterTick(TiledStage stage) {
			super.afterTick(stage);

			if (_controller.isKeyHeld(Input.Keys.Z)) {
				_undoTimeTrigger.update(1);
			} else {
				_undoTimeTrigger.reset();

				if (_toSaveStates) {
					_tiledStageHistorian.save();
					_toSaveStates = false;
				}
			}

			if (_controller.wasKeyPressed(Input.Keys.R)) {
				resetLevel();
			}

			if (_controller.wasKeyPressed(Input.Keys.P)) {
				if (!isPaused()) pauseLevel();
				else unpauseLevel();
			}

			// Unpause game when movement keys are pressed
			if (_tiledStage.isPaused() && (_controller.wasKeyLeftPressed() || _controller.wasKeyRightPressed() ||
					_controller.wasKeyUpPressed() || _controller.wasKeyDownPressed())) {
				unpauseLevel();
			}

			if (_controller.wasKeyPressed(Input.Keys.ESCAPE)) {
				_toEndLevel = true;
			}

			_controller.clearKeysPressed();
		}

		@Override
		public void drawn(TiledStage tiledStage) {
			super.drawn(tiledStage);

			if (_toEndLevel) {
				exitLevel();
				_toEndLevel = false;
			}
		}
	}

	public class UndoTimeTrigger implements TimedTrigger.Trigger {
		@Override
		public void activate() {
			_tiledStageHistorian.save();
			_tiledStageHistorian.revert(Config.GameUndoTicks);
			if (!_tiledStage.isPaused()) pauseLevel();
		}
	}

	public class PlayerCommands implements Player.Commands {
		@Override
		public void endLevel() {
			_toEndLevel = true;
		}
	}

	public class LodestoneCommands implements Lodestone.Commands {
		@Override
		public MagneticAttractionVisual spawnMagneticAttractionVisual(TiledStage.Coordinate origin) {
			// Try to find a magnetic attraction visual at coordinate
			for (TiledStageActor actor : origin.actors()) {
				if (actor instanceof MagneticAttractionVisual) {
					return (MagneticAttractionVisual) actor;
				}
			}

			// If not, spawn new one
			MagneticAttractionVisual visual = Pools.get(MagneticAttractionVisual.class).obtain();
			TiledMapTile tile = _tilesByType.get(Config.TMX.Tiles.Types.MagneticAttraction);
			visual.initialize(
					getAnimationDefs(tile),
					origin
			);

			addActor(visual, tile);

			return visual;
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

		@Override
		public void processCoordinate(TiledStage.Coordinate coordinate) {
			// For each coordinate, iterate through all its cells and check if it has elevation
			int elevation = 0;
			for (TiledStage.Cell cell : coordinate.cells().values()) {
				TiledMapTile tile = cell.tile();
				if (tile != null) {
					elevation = Math.max(elevation, TiledMapHelper.ParseIntegerProp(tile.getProperties(), Config.TMX.Tiles.ElevationProp, 0));
				}
			}
			coordinate.setElevation(elevation);
		}

		@Override
		public void processTile(TiledMapTile tile) {
			String type = TiledMapHelper.ParseProp(tile.getProperties(), Config.TMX.Tiles.TypeProp);
			if (type != null) _tilesByType.put(type, tile);
		}
	}

	public class TiledStageActorListener extends TiledStageActor.Listener {
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
	}

	public class ControllerListener extends Controller.Listener {
		@Override
		public void zoomed(Controller controller, float zoom) {
			setZoom(zoom);
		}

		@Override
		public void keyUpPressed(Controller controller) {
			if (!_tiledStage.isPaused()) _moveQueue.add(Player.PLAYER_ACTION.MOVE_UP);
		}

		@Override
		public void keyDownPressed(Controller controller) {
			if (!_tiledStage.isPaused()) _moveQueue.add(Player.PLAYER_ACTION.MOVE_DOWN);
		}

		@Override
		public void keyLeftPressed(Controller controller) {
			if (!_tiledStage.isPaused()) _moveQueue.add(Player.PLAYER_ACTION.MOVE_LEFT);
		}

		@Override
		public void keyRightPressed(Controller controller) {
			if (!_tiledStage.isPaused()) _moveQueue.add(Player.PLAYER_ACTION.MOVE_RIGHT);
		}
	}
}
