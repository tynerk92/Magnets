package com.somethingyellow.magnets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;

import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.utils.Pools;
import com.somethingyellow.LogicMachine;
import com.somethingyellow.graphics.AnimatedActor;
import com.somethingyellow.graphics.AnimationDef;
import com.somethingyellow.tiled.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class PlayScreen implements Screen, Player.Commands, Lodestone.Commands {

	public boolean DEBUG_MODE = false;
	private TiledStage _tiledStage;
	private TiledMap _map;
	private TiledMap _animationsMap;
	private HashMap<String, TiledMapTile> _tilesByType = new HashMap<String, TiledMapTile>();
	private Player _player;
	private LogicMachine _logicMachine = new LogicMachine();
	private HashMap<String, AnimationDef> _animationDefs;
	private HashMap<String, AnimationDef> _tempAnimationDefs = new HashMap<String, AnimationDef>();
	private LinkedList<TiledStageBody> _bodies = new LinkedList<TiledStageBody>();
	private LinkedList<TiledStageLightSource> _lightSources = new LinkedList<TiledStageLightSource>();
	private String _levelPath;
	private Commands _commands;
	// Debugging tools
	private FPSLogger _fpsLogger = new FPSLogger();

	public PlayScreen(Commands commands) {
		_commands = commands;
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
		String lightingImagePath = TiledStage.ParseProp(tile.getProperties(), Config.TMX.Tiles.LightingImagePathProp);
		if (lightingImagePath == null) return null; // no light source

		Texture lightingTexture = new Texture(Gdx.files.internal(lightingImagePath));

		float sizeX = TiledStage.ParseFloatProp(tile.getProperties(), Config.TMX.Tiles.LightingWidthProp, 0f);
		float sizeY = TiledStage.ParseFloatProp(tile.getProperties(), Config.TMX.Tiles.LightingHeightProp, 0f);

		if (sizeX == 0f || sizeY == 0f)
			throw new IllegalArgumentException("`Width` and `Height` of lighting must be defined!");

		float intensity = TiledStage.ParseFloatProp(tile.getProperties(), Config.TMX.Tiles.LightingIntensityProp, 1f);
		float displacementX = TiledStage.ParseIntegerProp(tile.getProperties(), Config.TMX.Tiles.LightingDisplacementXProp, 0);
		float displacementY = TiledStage.ParseIntegerProp(tile.getProperties(), Config.TMX.Tiles.LightingDisplacementYProp, 0);

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
				String animationName = TiledStage.ParseProp(tile.getProperties(), prop);
				if (!_animationDefs.containsKey(animationName))
					throw new IllegalArgumentException("Property '" + prop + "' pointing to missing animation!");

				AnimationDef animationDef = _animationDefs.get(animationName);
				_tempAnimationDefs.put(prop.substring(Config.TMX.Tiles.AnimationPropPrefix.length()), animationDef);
			}
		}

		return _tempAnimationDefs;
	}

	public void processProperties(TiledStageBody body, TiledMapTile tile) {
		// Light source
		final TiledStageLightSource lightSource = getLightSource(tile);
		if (lightSource != null) {
			addLightSource(lightSource);
			lightSource.setPosition(body.getX(), body.getY());
			body.addListener(new TiledStageBody.Listener() {
				@Override
				public void positionChanged(TiledStageBody body) {
					lightSource.setPosition(body.getX(), body.getY());
				}

				@Override
				public void removed(AnimatedActor actor) {
					actor.remove();
				}
			});
		}

		// Shadow
		Integer shadowDisplacementY = TiledStage.ParseIntegerProp(tile.getProperties(), Config.TMX.Tiles.ShadowDisplacementYProp);
		if (shadowDisplacementY != null) {
			body.setHasShadow(true);
			body.setShadowDisplacementY(shadowDisplacementY);
		}
	}

	public void processActions(final TiledStageBody body, TiledStage.TiledObject object) {
		Iterator<String> props = object.properties().getKeys();
		while (props.hasNext()) {
			String prop = props.next();

			if (prop.indexOf(Config.TMX.Objects.ActionPrefixIdentifier) == 0) {
				final String action = prop.substring(Config.TMX.Objects.ActionPrefixIdentifier.length());

				String expressionString = TiledStage.ParseProp(object.properties(), prop);

				// Replace and/or/not and add statement to logicmachine
				expressionString = expressionString.replace(Config.TMX.Objects.LogicExpressionAnd, LogicMachine.TERM_AND).
						replace(Config.TMX.Objects.LogicExpressionOr, LogicMachine.TERM_OR).
						replace(Config.TMX.Objects.LogicExpressionNot, LogicMachine.TERM_NOT);

				// Hook action of actor to logicmachine expression
				LogicMachine.Expression expression = _logicMachine.addExpression(expressionString, new LogicMachine.Listener() {
					@Override
					public void expressionChanged(boolean isTrue) {
						if (isTrue) {
							doAction(body, action);
						}
					}
				});

				// Hook premises of actor states to actor
				for (final String predicate : expression.premises()) {
					if (predicate.indexOf(Config.TMX.Objects.NamePrefixIdentifier) != 0) {
						throw new IllegalArgumentException("Property '" + prop + "' should be a valid expression! Predicate " + predicate + " should be prefixed with an actor's name.");
					}

					String[] parts = predicate.substring(Config.TMX.Objects.NamePrefixIdentifier.length()).split(Config.TMX.Objects.StatePrefixIdentifier);
					if (parts.length != 2)
						throw new IllegalArgumentException("Property '" + prop + "' should be a valid expression! Predicate " + predicate + " should point to the actor's state.");

					TiledStageBody predBody = _tiledStage.getBody(parts[0]);
					if (predBody == null)
						throw new IllegalArgumentException("Property '" + prop + "' should be a valid expression! Predicate " + predicate + " should point a non-null actor.");

					final String predState = parts[1];

					predBody.addListener(new TiledStageBody.Listener() {
						@Override
						public void stateAdded(TiledStageBody body, String state) {
							if (state.equals(predState)) _logicMachine.set(predicate, true);
						}

						@Override
						public void stateRemoved(TiledStageBody body, String state) {
							if (state.equals(predState)) _logicMachine.set(predicate, false);
						}
					});
				}
			}
		}
	}

	public void doAction(TiledStageBody body, String action) {
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
		if (_tiledStage == null) {
			_tiledStage = new TiledStage(Config.GameLayerWalls, Config.GameLayerShadows, SUBTICKS.values().length, Config.GameTickDuration);
		}

		if (_animationsMap == null) {
			_animationsMap = _commands.loadMap(Config.GameAnimationsTMXPath);
			_animationDefs = _commands.loadAnimations(_animationsMap, Config.GameTickDuration);
		}

		Gdx.input.setInputProcessor(_tiledStage);
	}

	public void loadLevel(String levelPath) {
		if (_map != null) unloadLevel();
		_levelPath = levelPath;

		_map = _commands.loadMap(_levelPath);
		_tiledStage.load(_map, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		// Saving first instance of each tile type in tileset
		_tilesByType.clear();
		for (TiledMapTileSet tileset : _map.getTileSets()) {
			Iterator<TiledMapTile> iterator = tileset.iterator();

			while (iterator.hasNext()) {
				TiledMapTile tile = iterator.next();
				String type = TiledStage.ParseProp(tile.getProperties(), Config.TMX.Tiles.TypeProp);
				if (type != null && !_tilesByType.containsKey(type)) {
					_tilesByType.put(type, tile);
				}
			}
		}

		// Spawning objects
		for (TiledStage.TiledObject object : _tiledStage.objects()) {
			TiledMapTile tile = object.tile();
			if (tile == null) continue;
			TiledStageBody body = spawnBody(tile, object.origin());
			if (object.name() != null) body.setName(object.name());
		}

		for (TiledStage.TiledObject object : _tiledStage.objects()) {
			TiledStageBody body = _tiledStage.getBody(object.name());
			if (body == null) continue;
			processActions(body, object);
		}
	}

	public TiledStageBody spawnBody(TiledMapTile tile, TiledStage.Coordinate origin) {
		String type = TiledStage.ParseProp(tile.getProperties(), Config.TMX.Tiles.TypeProp);
		if (type == null)
			throw new IllegalArgumentException("Property '" + Config.TMX.Tiles.TypeProp + "' not found!");

		TiledStageBody body = null;

		if (type.equals(Config.TMX.Tiles.Types.MagneticField)) {

			MagneticField magneticField = Pools.get(MagneticField.class).obtain();
			magneticField.initialize(getAnimationDefs(tile), origin);
			body = magneticField;

		} else if (type.equals(Config.TMX.Tiles.Types.Lodestone)) {

			Lodestone lodestone = Pools.get(Lodestone.class).obtain();
			lodestone.initialize(
					getAnimationDefs(tile),
					StringToBodyArea(TiledStage.ParseProp(tile.getProperties(), Config.TMX.Tiles.Lodestone.BodyAreaProp), TiledStageBody.BodyArea1x1),
					TiledStage.ParseIntegerProp(tile.getProperties(), Config.TMX.Tiles.Lodestone.BodyWidthProp, 1),
					origin,
					TiledStage.ParseBooleanProp(tile.getProperties(), Config.TMX.Tiles.Lodestone.IsPushableProp, false),
					TiledStage.ParseBooleanProp(tile.getProperties(), Config.TMX.Tiles.Lodestone.IsMagnetisableProp, false), this);
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
					TiledStage.ParseIntegerProp(tile.getProperties(), Config.TMX.Tiles.ObstructedFloor.ElevationProp, 0)
			);
			body = obstructedFloor;

		} else if (type.equals(Config.TMX.Tiles.Types.Player)) {

			if (_player != null) {
				throw new IllegalArgumentException("There should only be 1 player on the tiled map!");
			}

			_player = Pools.get(Player.class).obtain();
			_player.initialize(getAnimationDefs(tile), TiledStageActor.BodyArea1x1, 1, origin, this);

			_tiledStage.setCameraFocalActor(_player);
			_tiledStage.setInputFocalActor(_player);
			body = _player;

		} else if (type.equals(Config.TMX.Tiles.Types.Button)) {

			Button button = Pools.get(Button.class).obtain();
			button.initialize(
					getAnimationDefs(tile),
					StringToBodyArea(TiledStage.ParseProp(tile.getProperties(), Config.TMX.Tiles.Button.BodyAreaProp), TiledStageBody.BodyArea1x1),
					TiledStage.ParseIntegerProp(tile.getProperties(), Config.TMX.Tiles.Button.BodyWidthProp, 1),
					origin
			);
			body = button;

		} else if (type.equals(Config.TMX.Tiles.Types.Door)) {

			Door door = Pools.get(Door.class).obtain();
			door.initialize(
					getAnimationDefs(tile),
					StringToBodyArea(TiledStage.ParseProp(tile.getProperties(), Config.TMX.Tiles.Door.BodyAreaProp), TiledStageBody.BodyArea1x1),
					TiledStage.ParseIntegerProp(tile.getProperties(), Config.TMX.Tiles.Door.BodyWidthProp, 1),
					origin,
					TiledStage.ParseBooleanProp(tile.getProperties(), Config.TMX.Tiles.Door.IsOpenProp, false)
			);
			body = door;

		}

		if (body == null) throw new IllegalArgumentException("Invalid type: '" + type + "'!");

		addBody(body, tile);
		return body;
	}

	public void unloadLevel() {
		_logicMachine.clear();
		_player = null;
		for (TiledStageBody body : _bodies.toArray(new TiledStageBody[_bodies.size()])) {
			body.remove();
		}
		_bodies.clear();

		for (TiledStageLightSource lightSource : _lightSources.toArray(new TiledStageLightSource[_lightSources.size()])) {
			lightSource.remove();
		}
		_lightSources.clear();

		_map.dispose();
		_map = null;
	}

	public void addBody(TiledStageBody body, TiledMapTile tile) {
		processProperties(body, tile);

		_tiledStage.addBody(body);
		_bodies.add(body);

		body.addListener(new TiledStageBody.Listener() {
			@Override
			public void removed(AnimatedActor actor) {
				_bodies.remove(actor);
			}
		});
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
		return (MagneticField) spawnBody(_tilesByType.get(Config.TMX.Tiles.Types.MagneticField), coordinate);
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
		if (_animationsMap != null) {
			_animationDefs.clear();
			_animationsMap.dispose();
		}
		_tiledStage.dispose();
	}

	@Override
	public boolean isWall(TiledStage.Coordinate coordinate) {
		return coordinate.getTileProp(Config.GameLayerWalls, Config.TMX.Tiles.TypeProp, "").equals(Config.TMX.Tiles.Types.Wall);
	}

	@Override
	public boolean isExit(TiledStage.Coordinate coordinate) {
		return coordinate.getTileProp(Config.GameLayerWalls, Config.TMX.Tiles.TypeProp, "").equals(Config.TMX.Tiles.Types.Exit);
	}

	@Override
	public void resetLevel() {
		loadLevel(_levelPath);
	}

	@Override
	public void exitLevel() {
		unloadLevel();
		_commands.exitLevel();
	}

	@Override
	public void setZoom(float zoom) {
		_tiledStage.setZoom(zoom);
	}

	public enum SUBTICKS {
		RESET, BUTTON_PRESSES, MAGNETISATION, FORCES, BLOCK_MOVEMENT, PLAYER_MOVEMENT, GRAPHICS
	}

	public interface Commands {
		void exitLevel();

		TiledMap loadMap(String mapFilePath);

		HashMap<String, AnimationDef> loadAnimations(TiledMap map, float defaultDuration);
	}

	public static class Config {
		public static float GameTickDuration = 0.06f;
		public static String GameAnimationsTMXPath = "";
		public static String GameLayerWalls = "Bodies";
		public static String GameLayerShadows = "Shadows";

		public static class TMX {
			public static class Tiles {
				public static String TypeProp = "Type";
				public static String LightingImagePathProp = "Lighting Image Path";
				public static String LightingWidthProp = "Lighting Width";
				public static String LightingHeightProp = "Lighting Height";
				public static String LightingIntensityProp = "Lighting Intensity";
				public static String LightingDisplacementXProp = "Lighting Displacement X";
				public static String LightingDisplacementYProp = "Lighting Displacement Y";
				public static String ShadowDisplacementYProp = "Shadow Displacement Y";
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
					public static String Wall = "Wall";
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
				public static String NamePrefixIdentifier = "#";
				public static String ActionPrefixIdentifier = "+";
				public static String StatePrefixIdentifier = "@";
				public static String LogicExpressionAnd = "AND";
				public static String LogicExpressionOr = "OR";
				public static String LogicExpressionNot = "NOT";
			}
		}
	}
}
