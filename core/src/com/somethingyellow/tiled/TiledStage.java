package com.somethingyellow.tiled;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Pools;
import com.somethingyellow.graphics.AnimatedActor;
import com.somethingyellow.graphics.LightSource;
import com.somethingyellow.utility.ObjectSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

/**
 * Represents a stage with a tiled coordinate system
 * Tightly coupled with TiledStageActor, TiledStageMapRenderer, TiledStageMotionResolver, TiledStageHistorian
 * Delegates rendering of actors and map to TiledStageMapRenderer
 * Delegates motion resolution to TiledStageMotionResolver
 * To call load() to load a tiled map
 */

public class TiledStage extends Stage {
	private TiledMap _map;
	private TiledStageMapRenderer _mapRenderer;
	private OrthographicCamera _camera = new OrthographicCamera();
	private TiledStageActor _cameraFocalActor;
	private HashMap<String, TiledStageActor> _actorsByName = new HashMap<String, TiledStageActor>();
	private HashSet<LightSource> _lightSources = new HashSet<LightSource>();
	private ArrayList<HashSet<TiledStageActor>> _subTicksToActors;
	private HashSet<TiledStageActor> _actors = new HashSet<TiledStageActor>();
	private LinkedList<TiledStageActor> _tempActors = new LinkedList<TiledStageActor>();
	private int _tileWidth;
	private int _tileHeight;
	private int _tileRows;
	private int _tileColumns;
	private int _tickNo;
	private int _maxSubTicks;
	private float _tickTime;
	private float _tickDuration;
	private float _cameraZoom;
	private Commands _commands;
	private TiledMapTileLayer _wallLayer;
	private ArrayList<Coordinate> _coordinates;
	private ActorsComparator _actorsComparator = new ActorsComparator();
	private HashMap<String, TiledMapTileLayer> _tileLayers = new HashMap<String, TiledMapTileLayer>();
	private ObjectSet<Listener> _listeners = new ObjectSet<Listener>();
	private TiledStageMotionResolver _motionResolver;
	private boolean _isPaused;

	public TiledStage(int maxSubTicks, float tickDuration, Commands commands) {
		super();

		_maxSubTicks = maxSubTicks;
		_tickTime = 0f;
		_cameraZoom = 1f;
		_tickDuration = tickDuration;
		_motionResolver = new TiledStageMotionResolver(this);
		_mapRenderer = new TiledStageMapRenderer(this);
		_tickNo = 0;
		_commands = commands;
		_isPaused = true;

		_subTicksToActors = new ArrayList<HashSet<TiledStageActor>>(_maxSubTicks);
		for (int i = 0; i < _maxSubTicks; i++) {
			_subTicksToActors.add(i, new HashSet<TiledStageActor>());
		}

		getViewport().setCamera(_camera);
	}

	public static DIRECTION ReverseDirection(DIRECTION direction) {
		switch (direction) {
			case WEST:
				return DIRECTION.EAST;
			case EAST:
				return DIRECTION.WEST;
			case NORTH:
				return DIRECTION.SOUTH;
			case SOUTH:
				return DIRECTION.NORTH;
			case NORTH_WEST:
				return DIRECTION.SOUTH_EAST;
			case NORTH_EAST:
				return DIRECTION.SOUTH_WEST;
			case SOUTH_WEST:
				return DIRECTION.NORTH_EAST;
			case SOUTH_EAST:
				return DIRECTION.NORTH_WEST;
		}
		return null;
	}

	public static DIRECTION GetDirection(int rowDiff, int colDiff) {
		if (rowDiff == 0 && colDiff == 0) return null;

		if (Math.abs(rowDiff) > Math.abs(colDiff)) colDiff = 0;
		else if (Math.abs(colDiff) > Math.abs(rowDiff)) rowDiff = 0;

		double angle = Math.atan2(rowDiff, colDiff);
		int angleUnit = (int) Math.round(angle / (Math.PI / 4));

		switch (angleUnit) {
			case 0:
				return DIRECTION.EAST;
			case 1:
				return DIRECTION.NORTH_EAST;
			case 2:
				return DIRECTION.NORTH;
			case 3:
				return DIRECTION.NORTH_WEST;
			case 4:
			case -4:
				return DIRECTION.WEST;
			case -3:
				return DIRECTION.SOUTH_WEST;
			case -2:
				return DIRECTION.SOUTH;
			case -1:
				return DIRECTION.SOUTH_EAST;
		}

		return null;
	}

	public static int GetUnitRow(DIRECTION direction) {
		switch (direction) {
			case NORTH:
			case NORTH_EAST:
			case NORTH_WEST:
				return 1;
			case SOUTH:
			case SOUTH_EAST:
			case SOUTH_WEST:
				return -1;
		}
		return 0;
	}

	public static int GetUnitColumn(DIRECTION direction) {
		switch (direction) {
			case EAST:
			case NORTH_EAST:
			case SOUTH_EAST:
				return 1;
			case WEST:
			case NORTH_WEST:
			case SOUTH_WEST:
				return -1;
		}
		return 0;
	}

	public float ticksToTime(int ticks) {
		return ticks * _tickDuration;
	}

	public ObjectSet<Listener> listeners() {
		return _listeners;
	}

	public boolean isLoaded() {
		return _map != null;
	}

	/**
	 * Loads a TiledMap, unloading if it is still loaded
	 */

	public void load(TiledMap map, String wallLayerName) {
		if (isLoaded()) unload();

		_map = map;
		MapProperties props = _map.getProperties();
		_tileWidth = props.get("tilewidth", Integer.class);
		_tileHeight = props.get("tileheight", Integer.class);
		_tileRows = props.get("height", Integer.class);
		_tileColumns = props.get("width", Integer.class);
		_wallLayer = null;
		_coordinates = new ArrayList<Coordinate>(_tileRows * _tileColumns);

		// Iterate through layers
		for (MapLayer layer : _map.getLayers()) {
			if (layer instanceof TiledMapTileLayer) {
				TiledMapTileLayer tileLayer = (TiledMapTileLayer) layer;
				_tileLayers.put(layer.getName(), tileLayer);
				if (tileLayer.getName().equals(wallLayerName)) _wallLayer = tileLayer;
			}
		}

		// Create Coordinates
		for (int r = 0; r < _tileRows; r++) {
			for (int c = 0; c < _tileColumns; c++) {
				_coordinates.add(new Coordinate(r, c));
			}
		}

		// Iterate through objects
		for (MapLayer layer : _map.getLayers()) {
			for (MapObject object : layer.getObjects()) {

				// Determine origin of object relative to coordinate system
				int x = TiledMapHelper.ParseIntegerProp(object.getProperties(), "x", 0);
				int y = TiledMapHelper.ParseIntegerProp(object.getProperties(), "y", 0)
						+ TiledMapHelper.ParseIntegerProp(object.getProperties(), "height", 0);
				Coordinate origin = getCoordinateAt(x, y);

				// get tile defined by "gid" property of object
				int gid = TiledMapHelper.ParseIntegerProp(object.getProperties(), "gid", 0);
				TiledMapTile tile = _map.getTileSets().getTile(gid);

				_commands.spawnObject(origin, tile, object.getName(), object.getProperties());
			}
		}

		// Process tiles
		TilesIterator tilesIterator = new TilesIterator();
		while (tilesIterator.hasNext()) {
			_commands.processTile(tilesIterator.next());
		}

		// Process coordinates
		for (Coordinate coordinate : _coordinates) {
			_commands.processCoordinate(coordinate);
		}

		_mapRenderer.load(_map);

		_isPaused = false;
	}

	public void unload() {
		// Remove all actors and free them
		_tempActors.clear();
		_tempActors.addAll(_actors);
		for (TiledStageActor actor : _tempActors) {
			actor.setOrigin(null, true);
			Pools.free(actor);
		}
		_actors.clear();
		_tempActors.clear();
		_actorsByName.clear();
		for (int i = 0; i < _maxSubTicks; i++) {
			_subTicksToActors.get(i).clear();
		}
		_cameraFocalActor = null;

		// Remove all lightsources
		_lightSources.clear();
		System.out.println(_lightSources);

		_tileLayers.clear();
		_mapRenderer.unload();
		_map.dispose();
		_isPaused = true;
		_tickNo = 0;
		_map = null;

		for (Listener listener : _listeners) listener.unloaded(this);
	}

	@Override
	public void draw() {
		if (_map == null) return;

		for (Listener listener : _listeners) listener.beforeDraw(this);

		float delta = Gdx.graphics.getDeltaTime();

		_tickTime += delta;

		while (_tickTime >= _tickDuration) {
			for (Listener listener : _listeners) listener.beforeTick(this);
			if (!_isPaused) tick();
			for (Listener listener : _listeners) listener.afterTick(this);
			_tickTime -= _tickDuration;
		}

		super.act(delta);

		// Camera
		Vector2 camPos = new Vector2(_camera.position.x, _camera.position.y);
		if (_cameraFocalActor != null) {
			_camera.position.set(camPos.interpolate(_cameraFocalActor.center(), Config.CameraPanningSmoothRatio, Interpolation.linear), 0);
		}
		_camera.zoom += (_cameraZoom - _camera.zoom) * Config.CameraZoomSmoothRatio;
		_camera.update();

		// Map
		for (TiledStageActor actor : _actors) actor.updateAnimation();
		_mapRenderer.setView(_camera);
		_mapRenderer.render(_lightSources);

		for (Listener listener : _listeners) listener.drawn(this);
	}

	private void tick() {
		for (int i = 0; i < _maxSubTicks; i ++) {
			_tempActors.clear();
			_tempActors.addAll(_subTicksToActors.get(i));
			Collections.sort(_tempActors, _actorsComparator);

			for (TiledStageActor actor : _tempActors) actor.subtick(i);
			for (Listener listener : _listeners) listener.subticked(this, i);
		}

		_tempActors.clear();
		_tempActors.addAll(_actors);
		for (TiledStageActor actor : _tempActors) actor.subtick();

		_tickNo++;
	}

	public Set<TiledStageActor> actors() {
		return _actors;
	}

	public LinkedList<TiledStageActor> actorsByInitiative() {
		_tempActors.clear();
		_tempActors.addAll(_actors);
		Collections.sort(_tempActors, _actorsComparator);
		return _tempActors;
	}

	public TiledMap map() {
		return _map;
	}

	public int tileWidth() {
		return _tileWidth;
	}

	public int tileHeight() {
		return _tileHeight;
	}

	public int tileRows() {
		return _tileRows;
	}

	public int tileColumns() {
		return _tileColumns;
	}

	public float tickDuration() {
		return _tickDuration;
	}

	public float cameraZoom() {
		return _cameraZoom;
	}

	public TiledStageMotionResolver motionResolver() {
		return _motionResolver;
	}

	public int tickNo() {
		return _tickNo;
	}

	public TiledMapTileLayer wallLayer() {
		return _wallLayer; }

	public boolean isPaused() {
		return _isPaused;
	}

	public void setIsPaused(boolean isPaused) {

		if (_isPaused != isPaused) {
			for (AnimatedActor actor : actors()) {
				actor.setIsPaused(isPaused);
			}
		}

		_isPaused = isPaused;
	}

	public Coordinate getCoordinate(int tileRow, int tileCol) {
		if (tileRow >= _tileRows || tileCol >= _tileColumns || tileRow < 0 || tileCol < 0) return null;
		return _coordinates.get(getCoordinateIndex(tileRow, tileCol));
	}

	public Coordinate getCoordinateAt(float x, float y) {
		return getCoordinate(Math.floorDiv((int) y, _tileHeight), Math.floorDiv((int) x, _tileWidth));
	}

	public void addActor(TiledStageActor actor) {
		super.addActor(actor);
		_actors.add(actor);
		// Considering what subticks the bodies listen to, add to hashmap
		for (int i : actor.SUBTICKS) {
			_subTicksToActors.get(i).add(actor);
		}

		for (Listener listener : _listeners) listener.actorAdded(this, actor);
	}

	public void addLightSource(LightSource lightSource) {
		_lightSources.add(lightSource);
	}

	public void removeLightSource(LightSource lightSource) {
		_lightSources.remove(lightSource);
	}

	public void removeActor(TiledStageActor actor) {
		_actors.remove(actor);

		for (HashSet<TiledStageActor> actors : _subTicksToActors) {
			actors.remove(actor);
		}

		for (Listener listener : _listeners) listener.actorRemoved(this, actor);
	}

	public TiledStageActor getActorByName(String name) {
		// Search through actors in coordinate system if not memoized
		// If found, memoize actor by name
		if (!_actorsByName.containsKey(name)) {
			for (TiledStageActor actor : _actors) {
				if (actor.getName() != null && actor.getName().equals(name)) {
					_actorsByName.put(name, actor);
					break;
				}
			}
		}

		// Check if actor exists in coordinate system
		// If not, remove it from hashmap of names
		TiledStageActor actor = _actorsByName.get(name);
		if (actor == null || !_actors.contains(actor)) {
			_actorsByName.remove(name);
			return null;
		} else {
			return actor;
		}
	}

	private int getCoordinateIndex(int tileRow, int tileCol) {
		return tileRow * _tileColumns + tileCol;
	}

	public Set<LightSource> lightSources() {
		return _lightSources;
	}

	public void setScreenSize(int screenWidth, int screenHeight) {
		_camera.setToOrtho(false, screenWidth, screenHeight);
		if (_cameraFocalActor != null) _camera.position.set(_cameraFocalActor.center(), 0);
	}

	public void setCameraFocalActor(TiledStageActor actor) {
		_cameraFocalActor = actor;
		_camera.position.set(_cameraFocalActor.center(), 0);
	}

	/**
	 * Sets camera zoom, returning the clamped value of final zoom set (considering min and max zoom)
	 */
	public float setCameraZoom(float zoom) {
		_cameraZoom = Math.min(Math.max(zoom, Config.CameraZoomMin), Config.CameraZoomMax);
		return _cameraZoom;
	}

	public Iterator<TiledMapTile> tilesIterator() {
		return new TilesIterator();
	}

	@Override
	public void dispose() {
		super.dispose();
		_mapRenderer.dispose();
	}

	public enum DIRECTION {
		WEST, EAST, NORTH, SOUTH, NORTH_WEST, NORTH_EAST, SOUTH_WEST, SOUTH_EAST
	}

	public interface Commands {
		void spawnObject(Coordinate origin, TiledMapTile tile, String name, MapProperties properties);

		void processCoordinate(Coordinate coordinate);

		void processTile(TiledMapTile tile);
	}

	public static class Config {
		public static float CameraPanningSmoothRatio = 0.1f;
		public static float CameraZoomSmoothRatio = 0.1f;
		public static float CameraZoomMin = 0.4f;
		public static float CameraZoomMax = 2.0f;
	}

	public abstract static class Listener {
		/**
		 * When a TiledStageActor is added
		 */
		public void actorAdded(TiledStage tiledStage, TiledStageActor actor) {
		}

		/**
		 * When a TiledStageActor is removed
		 */
		public void actorRemoved(TiledStage tiledStage, TiledStageActor actor) {
		}

		/**
		 * When a subtick happens
		 */
		public void subticked(TiledStage tiledstage, int subtick) {
		}

		/**
		 * Before rendering of map and actors
		 */
		public void beforeDraw(TiledStage tiledStage) {
		}

		/**
		 * Before a game tick
		 * Happens even when paused
		 */
		public void beforeTick(TiledStage tiledStage) {
		}

		/**
		 * After a game tick
		 * Happens even when paused
		 */
		public void afterTick(TiledStage tiledStage) {
		}

		/**
		 * After rending of map and actors
		 */
		public void drawn(TiledStage tiledStage) {
		}

		/**
		 * When map is unloaded
		 */
		public void unloaded(TiledStage tiledStage) {
		}
	}

	public class Coordinate implements Comparable<Coordinate> {
		private ObjectSet<TiledStageActor> _actors = new ObjectSet<TiledStageActor>();
		private ObjectSet<Coordinate> TempCoordinates = new ObjectSet<Coordinate>();
		private int _row;
		private int _col;
		private int _elevation;
		private HashMap<String, Cell> _cells;

		public Coordinate(int row, int col) {
			_row = row;
			_col = col;

			_cells = new HashMap<String, Cell>(_tileLayers.size());
			for (String layerName : _tileLayers.keySet()) {
				_cells.put(layerName, new Cell(this, layerName));
			}
		}

		public Collection<TiledStageActor> actors() {
			return _actors;
		}

		public TiledStage stage() {
			return TiledStage.this;
		}

		public void add(TiledStageActor actor) {
			_actors.add(actor);
		}

		public void remove(TiledStageActor actor) {
			_actors.remove(actor);
		}

		public Coordinate getRelativeCoordinate(int rowOffset, int colOffset) {
			return getCoordinate(rowOffset + _row, colOffset + _col);
		}

		public DIRECTION getDirectionFrom(Coordinate sourceCoordinate) {
			if (this == sourceCoordinate) return null;

			int rowDiff = _row - sourceCoordinate._row;
			int colDiff = _col - sourceCoordinate._col;

			return GetDirection(rowDiff, colDiff);
		}

		public boolean isInRange(Coordinate targetCoordinate, int range, boolean includeDiagonally) {
			if (includeDiagonally) {
				return Math.abs(targetCoordinate._row - _row) <= range &&
						Math.abs(targetCoordinate._col - _col) <= range;
			} else {
				return (Math.abs(targetCoordinate._row - _row) <= range && targetCoordinate._col == _col) ||
						(Math.abs(targetCoordinate._col - _col) <= range && targetCoordinate._row == _row);
			}
		}

		public ObjectSet<Coordinate> getCoordinatesAtRange(int range, boolean includeDiagonally) {
			TempCoordinates.clear();
			Coordinate coordinate;

			if (range == 0) {
				TempCoordinates.add(this);
				return TempCoordinates;
			}

			if (includeDiagonally) {
				for (int r = _row - range; r <= _row + range; r += 2 * range) {
					for (int c = _col - range; c <= _col + range; c += 2 * range) {
						coordinate = getCoordinate(r, c);
						if (coordinate != null) TempCoordinates.add(coordinate);
					}
				}
			} else {
				for (int r = _row - range; r <= _row + range; r += 2 * range) {
					coordinate = getCoordinate(r, _col);
					if (coordinate != null) TempCoordinates.add(coordinate);
				}
				for (int c = _col - range; c <= _col + range; c += 2 * range) {
					coordinate = getCoordinate(_row, c);
					if (coordinate != null) TempCoordinates.add(coordinate);
				}
			}

			return TempCoordinates;
		}

		public ObjectSet<Coordinate> getCoordinatesInRange(int range, boolean includeDiagonally) {
			TempCoordinates.clear();
			Coordinate coordinate;

			if (includeDiagonally) {
				for (int r = _row - range; r <= _row + range; r++) {
					for (int c = _col - range; c <= _col + range; c ++) {
						coordinate = getCoordinate(r, c);
						if (coordinate != null) TempCoordinates.add(coordinate);
					}
				}
			} else {
				for (int r = _row - range; r <= _row + range; r ++) {
					coordinate = getCoordinate(r, _col);
					if (coordinate != null) TempCoordinates.add(coordinate);
				}
				for (int c = _col - range; c <= _col + range; c ++) {
					coordinate = getCoordinate(_row, c);
					if (coordinate != null) TempCoordinates.add(coordinate);
				}
			}

			return TempCoordinates;
		}

		public Coordinate getAdjacentCoordinate(DIRECTION direction) {
			return getCoordinate(row() + GetUnitRow(direction),
					column() + GetUnitColumn(direction));
		}

		public int row() {
			return _row;
		}

		public int column() {
			return _col;
		}

		public int elevation() {
			return _elevation;
		}

		public boolean isWall() {
			return _wallLayer.getCell(_col, _row) != null; }

		public void setElevation(int elevation) {
			_elevation = elevation;
		}

		public Cell getCell(String layerName) {
			return _cells.get(layerName);
		}

		public HashMap<String, Cell> cells() {
			return _cells;
		}

		public TiledMapTile getTile(String layerName) {
			Cell cell = _cells.get(layerName);
			if (cell == null) return null;
			return cell.tile();
		}

		public String getTileProp(String layerName, String propName, String defaultValue) {
			TiledMapTile tile = getTile(layerName);
			if (tile == null) return defaultValue;
			return TiledMapHelper.ParseProp(tile.getProperties(), propName, defaultValue);
		}

		public Vector2 position() {
			return getPosition(0, 0);
		}

		public int index() {
			return getCoordinateIndex(_row, _col);
		}

		public Vector2 getPosition(float offsetX, float offsetY) {
			return new Vector2((_col + offsetX) * _tileWidth, (_row + offsetY) * _tileHeight);
		}

		@Override
		public String toString() {
			return "(" + _row + ", " + _col + ")";
		}

		@Override
		public int compareTo(Coordinate coordinate) {
			return -(index() - coordinate.index());
		}
	}

	public class Cell {
		private Coordinate _coordinate;
		private String _layerName;
		private TiledMapTileLayer _layer;
		private TiledMapTileLayer.Cell _cell;

		public Cell(Coordinate coordinate, String layerName) {
			_coordinate = coordinate;
			_layerName = layerName;
			_layer = _tileLayers.get(_layerName);
			if (_layer != null) {
				_cell = _layer.getCell(_coordinate._col, _coordinate._row);
			}
		}

		public Coordinate coordinate() {
			return _coordinate;
		}

		public TiledMapTileLayer layer() {
			return _layer;
		}

		public TiledMapTileLayer.Cell cell() {
			return _cell;
		}

		public TiledMapTile tile() {
			if (_cell == null) return null;
			return _cell.getTile();
		}

		public void removeTile() {
			if (_cell != null) _cell.setTile(null);
		}
	}

	private class CoordinatesIterator implements Iterator<Coordinate> {
		private int _row = 0;
		private int _col = 0;

		@Override
		public boolean hasNext() {
			return (_row < _tileRows && _col < _tileColumns);
		}

		@Override
		public Coordinate next() {
			Coordinate coordinate = getCoordinate(_row, _col);

			_col++;
			if (_col >= _tileColumns) {
				_col = 0;
				_row++;
			}

			return coordinate;
		}
	}

	private class CellsIterator implements Iterator<Cell> {
		private Iterator<Coordinate> _coordinatesIterator = new CoordinatesIterator();
		private Iterator<Cell> _cellsIterator;
		private String _onlyLayerName;

		public CellsIterator() {
			if (_coordinatesIterator.hasNext())
				_cellsIterator = _coordinatesIterator.next().cells().values().iterator();
		}

		public CellsIterator(String onlyLayerName) {
			_onlyLayerName = onlyLayerName;
		}

		@Override
		public boolean hasNext() {
			return (_cellsIterator != null && _cellsIterator.hasNext());
		}

		@Override
		public Cell next() {
			Cell cell;
			do {
				cell = _cellsIterator.next();
			} while (_onlyLayerName != null && !cell._layerName.equals(_onlyLayerName));

			if (!_cellsIterator.hasNext() && _coordinatesIterator.hasNext())
				_cellsIterator = _coordinatesIterator.next().cells().values().iterator();

			return cell;
		}
	}

	private class TilesIterator implements Iterator<TiledMapTile> {
		private Iterator<TiledMapTileSet> _tilesetsIterator = _map.getTileSets().iterator();
		private Iterator<TiledMapTile> _tilesIterator;

		public TilesIterator() {
			if (_tilesetsIterator.hasNext()) _tilesIterator = _tilesetsIterator.next().iterator();
		}

		@Override
		public boolean hasNext() {
			return (_tilesIterator != null && _tilesIterator.hasNext());
		}

		@Override
		public TiledMapTile next() {
			TiledMapTile tile = _tilesIterator.next();
			if (!_tilesIterator.hasNext() && _tilesetsIterator.hasNext())
				_tilesIterator = _tilesetsIterator.next().iterator();

			return tile;
		}
	}

	private class ActorsComparator implements Comparator<TiledStageActor> {
		@Override
		public int compare(TiledStageActor actor1, TiledStageActor actor2) {
			// TODO: Test initiative
			return actor1.initiative() - actor2.initiative();
		}
	}
}