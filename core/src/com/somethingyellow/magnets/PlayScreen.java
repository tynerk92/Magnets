package com.somethingyellow.magnets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
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

public class PlayScreen implements Screen, Player.ActionListener, Lodestone.ActionListener, MagneticSource.ActionListener {

	public boolean DEBUG_MODE = false;

	private TiledStage _tiledStage;
	private TiledMap _map;
	private Player _player;
	private HashMap<String, TiledMapTile> _tilesByReference = new HashMap<String, TiledMapTile>();
	private HashMap<TiledMapTile, ArrayList<TiledStageActor.Frame>> _tileFramesByTile = new HashMap<TiledMapTile, ArrayList<TiledStageActor.Frame>>();
	private LogicMachine _logicMachine = new LogicMachine();
	private TmxMapLoader _tmxMapLoader = new TmxMapLoader();
	private LinkedList<TiledStageBody> _bodies = new LinkedList<TiledStageBody>();
	private LinkedList<TiledStageLightSource> _lightSources = new LinkedList<TiledStageLightSource>();
	private String _levelPath;
	private ActionListener _actionListener;

	// Debugging tools
	private FPSLogger _fpsLogger = new FPSLogger();

	public PlayScreen(ActionListener actionListener) {
		_actionListener = actionListener;

		// Default settings of classes based on Config
		TiledStage.cameraZoomDefault = Config.CAMERA_ZOOM_DEFAULT;
		TiledStage.cameraPanningSmoothRatio = Config.CAMERA_PANNING_SMOOTH_RATIO;
		TiledStage.cameraZoomSmoothRatio = Config.CAMERA_ZOOM_SMOOTH_RATIO;
		TiledStageMapRenderer.ambientColorRedDefault = Config.MAP_AMBIENT_COLOR_RED_DEFAULT;
		TiledStageMapRenderer.ambientColorGreenDefault = Config.MAP_AMBIENT_COLOR_GREEN_DEFAULT;
		TiledStageMapRenderer.ambientColorBlueDefault = Config.MAP_AMBIENT_COLOR_BLUE_DEFAULT;
	}

	public static boolean[] ExtractBodyArea(TiledMapTile tile) {
		String bodyArea = TiledStage.ParseProp(tile.getProperties(), Config.TILE_BODY_AREA);
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
		return TiledStage.ParseIntegerProp(tile.getProperties(), Config.TILE_BODY_WIDTH, 1);
	}

	public static boolean ExtractIsPushable(TiledMapTile tile) {
		return TiledStage.ParseBooleanProp(tile.getProperties(), Config.TILE_ISPUSHABLE, false);
	}

	public static boolean ExtractIsMagnetisable(TiledMapTile tile) {
		return TiledStage.ParseBooleanProp(tile.getProperties(), Config.TILE_ISMAGNETISABLE, false);
	}

	public static int ExtractRenderDepth(TiledMapTile tile) {
		return TiledStage.ParseIntegerProp(tile.getProperties(), Config.TILE_RENDER_DEPTH, 0);
	}

	public static int ExtractElevation(TiledMapTile tile) {
		return TiledStage.ParseIntegerProp(tile.getProperties(), Config.TILE_ELEVATION, 0);
	}

	public static boolean ExtractIsOpen(TiledMapTile tile) {
		return TiledStage.ParseBooleanProp(tile.getProperties(), Config.TILE_ISOPEN, false);
	}

	public TiledStageLightSource getLightSource(TiledMapTile tile) {
		String lightingImagePath = TiledStage.ParseProp(tile.getProperties(), Config.TILE_LIGHTING_IMAGE_PATH);
		if (lightingImagePath == null) return null; // no light source

		Texture lightingTexture = new Texture(Gdx.files.internal(lightingImagePath));

		float sizeX = TiledStage.ParseFloatProp(tile.getProperties(), Config.TILE_LIGHTING_WIDTH, 0f);
		float sizeY = TiledStage.ParseFloatProp(tile.getProperties(), Config.TILE_LIGHTING_HEIGHT, 0f);

		if (sizeX == 0f || sizeY == 0f)
			throw new IllegalArgumentException("`Width` and `Height` of lighting must be defined!");

		float intensity = TiledStage.ParseFloatProp(tile.getProperties(), Config.TILE_LIGHTING_INTENSITY, 1f);
		float displacementX = TiledStage.ParseIntegerProp(tile.getProperties(), Config.TILE_LIGHTING_DISPLACEMENT_X, 0);
		float displacementY = TiledStage.ParseIntegerProp(tile.getProperties(), Config.TILE_LIGHTING_DISPLACEMENT_Y, 0);

		TiledStageLightSource lightSource = Pools.obtain(TiledStageLightSource.class);
		lightSource.initialize(lightingTexture);
		lightSource.setIntensity(intensity);
		lightSource.setSize(sizeX, sizeY);
		lightSource.setRenderDisplacement(sizeX / 2 - displacementX, sizeY / 2 - displacementY);
		return lightSource;
	}

	public HashMap<String, TiledStageActor.FrameSequence> getAnimations(TiledMapTile tile) {
		HashMap<String, TiledStageActor.FrameSequence> animationFrames = new HashMap<String, TiledStageActor.FrameSequence>();
		Iterator<String> props = tile.getProperties().getKeys();

		animationFrames.put(TiledStageBody.STATE_DEFAULT, getFrameSequence(tile));

		while (props.hasNext()) {
			String prop = props.next();

			if (prop.indexOf(Config.TILE_PREFIX_STATE) == 0) {
				String reference = TiledStage.ParseProp(tile.getProperties(), prop);

				if (reference.indexOf(Config.TILE_PREFIX_REFERENCE) == 0) {
					TiledMapTile animationTile = _tilesByReference.get(reference.substring(Config.TILE_PREFIX_REFERENCE.length()));
					if (animationTile != null) {
						animationFrames.put(prop.substring(Config.TILE_PREFIX_STATE.length()), getFrameSequence(animationTile));
					} else {
						throw new IllegalArgumentException("Property '" + prop + "' should '" + Config.TILE_PREFIX_REFERENCE + "tileReference' to a valid tile!");
					}
				}
			}
		}

		return animationFrames;
	}

	public TiledStageBody.FrameSequence getFrameSequence(TiledMapTile tile) {
		if (!_tileFramesByTile.containsKey(tile)) {
			_tileFramesByTile.put(tile, TiledStageActor.FrameSequence.TileToFrames(tile, _tiledStage.tickDuration()));
		}

		return new TiledStageActor.FrameSequence(_tileFramesByTile.get(tile), ExtractRenderDepth(tile));
	}

	public void processProperties(TiledStageBody body, TiledMapTile tile) {
		body.setRenderDepth(ExtractRenderDepth(tile));

		final TiledStageLightSource lightSource = getLightSource(tile);
		if (lightSource != null) {
			addLightSource(lightSource);
			lightSource.setPosition(body.getX(), body.getY());
			body.addListener(new TiledStageBody.Listener() {
				@Override
				public void positionChanged(float x, float y) {
					lightSource.setPosition(x, y);
				}

				@Override
				public void removed() {
					lightSource.remove();
				}
			});
		}
	}

	public void processActions(final TiledStageBody body, TiledStage.TiledObject object) {
		Iterator<String> props = object.properties().getKeys();
		while (props.hasNext()) {
			String prop = props.next();

			if (prop.indexOf(Config.TILE_PREFIX_ACTION) == 0) {
				final String action = prop.substring(Config.TILE_PREFIX_ACTION.length());

				String expressionString = TiledStage.ParseProp(object.properties(), prop);
				final String actionName = Config.TILE_PREFIX_NAME + body.getName() + Config.TILE_PREFIX_ACTION + action;

				// Replace and/or/not and add statement to logicmachine
				expressionString = expressionString.replace(Config.TILE_EXPRESSION_AND, LogicMachine.TERM_AND).
						replace(Config.TILE_EXPRESSION_OR, LogicMachine.TERM_OR).
						replace(Config.TILE_EXPRESSION_NOT, LogicMachine.TERM_NOT);

				// Hook action of actor to logicmachine expression
				LogicMachine.Expression expression = _logicMachine.addExpression(expressionString, new LogicMachine.Listener() {
					@Override
					public void expressionChanged(boolean isTrue) {
						if (isTrue) doAction(body, action);
					}
				});

				// Hook premises of actor states to actor
				for (final String predicate : expression.premises()) {
					if (predicate.indexOf(Config.TILE_PREFIX_NAME) != 0) {
						throw new IllegalArgumentException("Property '" + prop + "' should be a valid expression! Predicate " + predicate + " should be prefixed with an actor's name.");
					}

					String[] parts = predicate.substring(Config.TILE_PREFIX_NAME.length()).split(Config.TILE_PREFIX_STATE);
					if (parts.length != 2)
						throw new IllegalArgumentException("Property '" + prop + "' should be a valid expression! Predicate " + predicate + " should point to the actor's state.");

					TiledStageBody predBody = _tiledStage.getBody(parts[0]);
					if (predBody == null)
						throw new IllegalArgumentException("Property '" + prop + "' should be a valid expression! Predicate " + predicate + " should point a non-null actor.");

					final String predState = parts[1];
					predBody.addListener(new TiledStageBody.Listener() {
						@Override
						public void stateAdded(String state) {
							if (state.equals(predState)) _logicMachine.set(predicate, true);
						}

						@Override
						public void stateRemoved(String state) {
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
			if (action.equals(Config.DOOR_ACTION_OPEN)) {
				door.open();
			} else if (action.equals(Config.DOOR_ACTION_CLOSE)) {
				door.close();
			}
		}
	}

	@Override
	public void show() {
		if (_tiledStage == null) {
			_tiledStage = new TiledStage(Config.ACTORS_LAYER_NAME, SUBTICKS.values().length, Config.GAME_TICK_DURATION);
		}

		Gdx.input.setInputProcessor(_tiledStage);
	}

	public void loadLevel(String levelPath) {
		if (_map != null) unloadLevel();

		_levelPath = levelPath;

		_map = _tmxMapLoader.load(_levelPath);
		_tiledStage.load(_map, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		Iterator<TiledMapTile> tiles = _tiledStage.tilesIterator();
		while (tiles.hasNext()) {
			TiledMapTile tile = tiles.next();

			String reference = TiledStage.ParseProp(tile.getProperties(), Config.TILE_PREFIX_REFERENCE);
			if (reference != null) {
				_tilesByReference.put(reference, tile);
			}
		}

		for (TiledStage.TiledObject object : _tiledStage.objects()) {
			TiledMapTile tile = object.tile();
			if (tile == null) continue;

			String type = TiledStage.ParseProp(tile.getProperties(), Config.TILE_TYPE);
			TiledStageBody body = null;
			if (type == null) continue;

			if (type.equals(Config.TILE_TYPE_LODESTONE)) {
				body = createBlock(object);
			} else if (type.equals(Config.TILE_TYPE_MAGNETIC_SOURCE)) {
				body = createMagneticSource(object);
			} else if (type.equals(Config.TILE_TYPE_MAGNETIC_FLOOR)) {
				body = createMagneticFloor(object);
			} else if (type.equals(Config.TILE_TYPE_OBSTRUCTED_FLOOR)) {
				body = createObstructedFloor(object);
			} else if (type.equals(Config.TILE_TYPE_PLAYER)) {
				body = createPlayer(object);
			} else if (type.equals(Config.TILE_TYPE_BUTTON)) {
				body = createButton(object);
			} else if (type.equals(Config.TILE_TYPE_DOOR)) {
				body = createDoor(object);
			}

			if (body == null) continue;

			addBody(body, tile);
			if (object.name() != null) body.setName(object.name());
		}

		for (TiledStage.TiledObject object : _tiledStage.objects()) {
			TiledStageBody body = _tiledStage.getBody(object.name());

			if (body == null) continue;

			processActions(body, object);
		}
	}

	public void unloadLevel() {
		_tilesByReference.clear();
		_tileFramesByTile.clear();
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

	public TiledStageBody createPlayer(TiledStage.TiledObject object) {
		if (_player != null) {
			throw new IllegalArgumentException("There should only be 1 player on the tiled map!");
		}

		// Add player to stage
		_player = Pools.get(Player.class).obtain();
		_player.initialize(TiledStageActor.BodyArea1x1, 1, getAnimations(object.tile()), object.origin(), this);

		_tiledStage.setCameraFocalActor(_player);
		_tiledStage.setInputFocalActor(_player);

		return _player;
	}

	public TiledStageBody createBlock(TiledStage.TiledObject object) {
		Lodestone lodestone = Pools.get(Lodestone.class).obtain();
		lodestone.initialize(ExtractBodyArea(object.tile()), ExtractBodyWidth(object.tile()),
				getAnimations(object.tile()), object.origin(), ExtractIsPushable(object.tile()), ExtractIsMagnetisable(object.tile()), this);
		return lodestone;
	}

	public TiledStageBody createMagneticSource(TiledStage.TiledObject object) {
		MagneticSource magneticSource = Pools.get(MagneticSource.class).obtain();
		magneticSource.initialize(getAnimations(object.tile()), object.origin(), this);
		return magneticSource;
	}

	public TiledStageBody createMagneticFloor(TiledStage.TiledObject object) {
		MagneticFloor magneticFloor = Pools.get(MagneticFloor.class).obtain();
		magneticFloor.initialize(getAnimations(object.tile()), object.origin());
		return magneticFloor;
	}

	public TiledStageBody createObstructedFloor(TiledStage.TiledObject object) {
		ObstructedFloor obstructedFloor = Pools.get(ObstructedFloor.class).obtain();
		obstructedFloor.initialize(getAnimations(object.tile()), object.origin(), ExtractElevation(object.tile()));
		return obstructedFloor;
	}

	public TiledStageBody createDoor(TiledStage.TiledObject object) {
		Door door = Pools.get(Door.class).obtain();
		door.initialize(ExtractBodyArea(object.tile()), ExtractBodyWidth(object.tile()),
				getAnimations(object.tile()), object.origin(), ExtractIsOpen(object.tile()));
		return door;
	}

	public TiledStageBody createButton(TiledStage.TiledObject object) {
		Button button = Pools.get(Button.class).obtain();
		button.initialize(ExtractBodyArea(object.tile()), ExtractBodyWidth(object.tile()),
				getAnimations(object.tile()), object.origin());
		return button;
	}

	public void addBody(final TiledStageBody body, TiledMapTile tile) {
		processProperties(body, tile);

		_tiledStage.addBody(body);
		_bodies.add(body);

		body.addListener(new TiledStageBody.Listener() {
			@Override
			public void removed() {
				_bodies.remove(body);
			}
		});
	}

	public void addLightSource(final TiledStageLightSource lightSource) {
		_tiledStage.addLightSource(lightSource);
		_lightSources.add(lightSource);

		lightSource.addListener(new TiledStageLightSource.Listener() {
			@Override
			public void removed() {
				_lightSources.remove(lightSource);
			}
		});
	}

	@Override
	public TiledStageVisual spawnMagneticAttractionVisual(TiledStage.Coordinate coordinate, TiledStage.DIRECTION direction) {
		TiledStageVisual visual = Pools.get(TiledStageVisual.class).obtain();

		TiledMapTile bodyTile = null;
		switch (direction) {
			case NORTH:
			case SOUTH:
				bodyTile = _tilesByReference.get(Config.TILE_REFERENCE_MAGNETIC_ATTRACTION_VERTICAL);
				break;
			case EAST:
			case WEST:
				bodyTile = _tilesByReference.get(Config.TILE_REFERENCE_MAGNETIC_ATTRACTION_HORIZONTAL);
				break;
		}

		if (bodyTile != null) {
			visual.initialize(TiledStageBody.BodyArea1x1, 1, getFrameSequence(bodyTile), coordinate);
			addBody(visual, bodyTile);
			return visual;
		} else {
			return null;
		}
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
		_actionListener.exitLevel();
	}

	@Override
	public void setZoom(float zoom) {
		_tiledStage.setZoom(zoom);
	}

	public enum SUBTICKS {
		RESET, BUTTON_PRESSES, MAGNETISATION, FORCES, BLOCK_MOVEMENT, PLAYER_MOVEMENT, GRAPHICS
	}

	public interface ActionListener {
		void exitLevel();
	}
}
