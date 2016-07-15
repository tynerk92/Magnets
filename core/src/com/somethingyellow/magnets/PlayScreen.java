package com.somethingyellow.magnets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Pools;
import com.somethingyellow.LogicMachine;
import com.somethingyellow.graphics.AnimationDef;
import com.somethingyellow.graphics.LightSource;
import com.somethingyellow.tiled.TiledMapHelper;
import com.somethingyellow.tiled.TiledStage;
import com.somethingyellow.tiled.TiledStageActor;
import com.somethingyellow.tiled.TiledStageHistorian;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class PlayScreen implements Screen, PlayScreenUIStage.Commands {
	private TiledStage _tiledStage;
	private TiledMap _animationsMap;
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
	private boolean _toClearLevel = false;
	private boolean _toSaveStates = false;
	private boolean _toUndo = false;
	private boolean _toReset = false;
	private int _playerMovesCount = 0;
	private Commands _commands;
	private PlayScreenUIStage _UIStage;

	public PlayScreen(Skin skin, Commands commands) {
		_commands = commands;
		_tiledStage = new TiledStage(SUBTICKS.values().length, Config.GameTickDuration, _tiledStageCommands);
		_UIStage = new PlayScreenUIStage(skin, this);
		_tiledStageHistorian = new TiledStageHistorian(_tiledStage);
		_tiledStage.listeners().add(_tiledStageListener);
		_tilesByType = new HashMap<String, TiledMapTile>();
		_animationsMap = _commands.loadMap(Config.AnimationsTMXPath);
		_animationDefs = _commands.loadAnimations(_animationsMap, Config.GameTickDuration);
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

	public LightSource getLightSource(TiledMapTile tile, TiledStageActor actor) {
		String lightingAnimation = TiledMapHelper.ParseProp(tile.getProperties(), Config.TMX.Tiles.LightingAnimationProp);
		if (lightingAnimation == null) return null; // no light source

		float intensity = TiledMapHelper.ParseFloatProp(tile.getProperties(), Config.TMX.Tiles.LightingIntensityProp, 1f);
		float displacementX = TiledMapHelper.ParseIntegerProp(tile.getProperties(), Config.TMX.Tiles.LightingDisplacementXProp, 0);
		float displacementY = TiledMapHelper.ParseIntegerProp(tile.getProperties(), Config.TMX.Tiles.LightingDisplacementYProp, 0);

		LightSource lightSource = new LightSource(_animationDefs.get(lightingAnimation), actor, -displacementX, -displacementY);
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

	public void processProperties(final TiledStageActor actor, TiledMapTile tile) {
		// Body area
		actor.setBody(
				StringToBodyArea(TiledMapHelper.ParseProp(tile.getProperties(), Config.TMX.Tiles.BodyAreaProp), TiledStageActor.BodyArea1x1),
				TiledMapHelper.ParseIntegerProp(tile.getProperties(), Config.TMX.Tiles.BodyWidthProp, 1)
		);

		// Light source
		final LightSource lightSource = getLightSource(tile, actor);
		if (lightSource != null) {
			addLightSource(lightSource);

			_tiledStage.listeners().add(new TiledStage.Listener() {
				@Override
				public void actorAdded(TiledStage tiledStage, TiledStageActor actorAdded) {
					super.actorAdded(tiledStage, actorAdded);
					if (actorAdded == actor) tiledStage.addLightSource(lightSource);
				}

				@Override
				public void actorRemoved(TiledStage tiledStage, TiledStageActor actorRemoved) {
					super.actorRemoved(tiledStage, actorRemoved);
					if (actorRemoved == actor) tiledStage.removeLightSource(lightSource);
				}

				@Override
				public void unloaded(TiledStage tiledStage) {
					super.unloaded(tiledStage);
					tiledStage.listeners().remove(this);
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
		_UIStage.focus();
	}

	public void loadLevel(String levelPath) {
		if (_tiledStage.isLoaded()) unloadLevel();

		_levelPath = levelPath;
		TiledMap map = _commands.loadMap(_levelPath);
		_tiledStage.load(map, Config.GameWallLayer);
		if (_player == null) throw new IllegalArgumentException("No player on the map!");
		if (!_tilesByType.containsKey(Config.TMX.Tiles.Types.MagneticAttraction))
			throw new IllegalArgumentException("Need tile of type for MagneticAttraction!");

		_moveQueue.clear();
		_toClearLevel = _toReset = _toSaveStates = _toUndo = false;
		_playerMovesCount = 0;
		_UIStage.setPlayerMoveCount(_playerMovesCount);

		_tiledStageHistorian.save();
	}

	public void unloadLevel() {
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
					_tiledStage,
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
					_tiledStage,
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
			_player.initialize(
					_tiledStage,
					getAnimationDefs(tile),
					origin,
					_playerCommands);

			_player.listeners().add(new Player.Listener() {
				@Override
				public void moved(Player player) {
					super.moved(player);
					_playerMovesCount ++;
					_toSaveStates = true;
					_UIStage.setPlayerMoveCount(_playerMovesCount);
				}
			});

			_tiledStage.setCameraFocalActor(_player);
			actor = _player;

		} else if (type.equals(Config.TMX.Tiles.Types.Button)) {

			Button button = Pools.get(Button.class).obtain();
			button.initialize(
					_tiledStage,
					getAnimationDefs(tile),
					origin
			);
			actor = button;

		} else if (type.equals(Config.TMX.Tiles.Types.Door)) {

			Door door = Pools.get(Door.class).obtain();
			door.initialize(
					_tiledStage,
					getAnimationDefs(tile),
					origin,
					TiledMapHelper.ParseBooleanProp(tile.getProperties(), Config.TMX.Tiles.Door.IsOpenProp, false)
			);
			actor = door;

		} else if (type.equals(Config.TMX.Tiles.Types.Exit)) {

			Exit exit = Pools.get(Exit.class).obtain();
			exit.initialize(
					_tiledStage,
					getAnimationDefs(tile),
					origin);
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

	public void addLightSource(LightSource lightSource) {
		_tiledStage.addLightSource(lightSource);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0f, 0f, 0f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		_tiledStage.draw();
		_UIStage.draw();
	}

	@Override
	public void resize(int width, int height) {
		_tiledStage.setScreenSize(width, height);
	}

	@Override
	public void pause() {
		setIsPaused(true);
	}

	@Override
	public void resume() {
		setIsPaused(false);
	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
		if (_tiledStage != null) _tiledStage.dispose();
		if (_UIStage != null) _UIStage.dispose();
		if (_animationsMap != null) {
			_animationDefs.clear();
			_animationsMap.dispose();
		}
	}

	public void resetLevel() {
		unloadLevel();
		loadLevel(_levelPath);
	}

	public boolean isPaused() {
		return _tiledStage.isPaused();
	}

	public void exitLevel() {
		unloadLevel();
		_commands.exitLevel();
	}

	public Player.PLAYER_ACTION getPlayerAction() {
		Player.PLAYER_ACTION action = null;

		if (!_player.isMoving()) {
			// If player is not moving, move player if keys are held or move queue is not empty
			if (_moveQueue.isEmpty()) {
				if (_UIStage.moveLeftHeld()) {
					action = Player.PLAYER_ACTION.MOVE_LEFT;
				} else if (_UIStage.moveRightHeld()) {
					action = Player.PLAYER_ACTION.MOVE_RIGHT;
				} else if (_UIStage.moveUpHeld()) {
					action = Player.PLAYER_ACTION.MOVE_UP;
				} else if (_UIStage.moveDownHeld()) {
					action = Player.PLAYER_ACTION.MOVE_DOWN;
				}
			} else {
				action = _moveQueue.removeFirst();
			}
		}

		return action;
	}

	@Override
	public TiledMap loadMap(String mapFilePath) {
		return _commands.loadMap(mapFilePath);
	}

	@Override
	public HashMap<String, AnimationDef> loadAnimations(TiledMap map, float defaultDuration) {
		return _commands.loadAnimations(map, defaultDuration);
	}

	@Override
	public void undo() {
		_toUndo = true;
	}

	@Override
	public void reset() {
		_toReset = true;
	}

	public void setIsPaused(boolean isPaused) {
		_tiledStage.setIsPaused(isPaused);
		_UIStage.setIsPaused(isPaused);
	}

	@Override
	public void togglePause() {
		setIsPaused(!isPaused());
	}

	@Override
	public void exit() {
		exitLevel();
	}

	@Override
	public void setCameraMode(boolean cameraMode) {

	}

	@Override
	public void moveUp() {
		_moveQueue.add(Player.PLAYER_ACTION.MOVE_UP);
		setIsPaused(false);
	}

	@Override
	public void moveDown() {
		_moveQueue.add(Player.PLAYER_ACTION.MOVE_DOWN);
		setIsPaused(false);
	}

	@Override
	public void moveLeft() {
		_moveQueue.add(Player.PLAYER_ACTION.MOVE_LEFT);
		setIsPaused(false);
	}

	@Override
	public void moveRight() {
		_moveQueue.add(Player.PLAYER_ACTION.MOVE_RIGHT);
		setIsPaused(false);
	}

	@Override
	public float zoom(float zoom) {
		return _tiledStage.setCameraZoom(zoom);
	}

	@Override
	public void dragged(float displacementX, float displacementY) {

	}

	public enum SUBTICKS {
		START, BUTTONS, DOORS, MAGNETISATION, MAGNETIC_ATTRACTION, BLOCK_MOVEMENT, PLAYER_ACTION, END
	}

	public interface Commands {
		void exitLevel();

		TiledMap loadMap(String mapFilePath);

		HashMap<String, AnimationDef> loadAnimations(TiledMap map, float defaultDuration);
	}

	public static class Config {
		public static float GameTickDuration = 0.06f;
		public static int GameUndoTicks = 1;
		public static String AnimationsTMXPath = null;
		public static String GameWallLayer = null;

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

			Player.PLAYER_ACTION action = getPlayerAction();
			if (action != null) _player.doAction(action);
		}

		@Override
		public void afterTick(TiledStage stage) {
			super.afterTick(stage);

			if (_toReset) {

				resetLevel();
				_toReset = false;

			} else if (_toUndo) {

				_tiledStageHistorian.save();
				_tiledStageHistorian.revert(Config.GameUndoTicks);
				setIsPaused(true);
				_toUndo = false;

			} else if (_toSaveStates) {

				_tiledStageHistorian.save();
				_toSaveStates = false;

			}

		}

		@Override
		public void drawn(TiledStage tiledStage) {
			super.drawn(tiledStage);

			if (_toClearLevel) {
				exitLevel();
				_toClearLevel = false;
			}
		}
	}

	public class PlayerCommands implements Player.Commands {
		@Override
		public void endLevel() {
			_toClearLevel = true;
		}
	}

	public class LodestoneCommands implements Lodestone.Commands {
		@Override
		public MagneticAttractionVisual spawnMagneticAttractionVisual() {
			MagneticAttractionVisual visual = Pools.get(MagneticAttractionVisual.class).obtain();
			TiledMapTile tile = _tilesByType.get(Config.TMX.Tiles.Types.MagneticAttraction);
			visual.initialize(
					_tiledStage,
					getAnimationDefs(tile)
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
}
