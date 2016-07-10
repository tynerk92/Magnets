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
import com.somethingyellow.utility.ObjectList;
import com.somethingyellow.utility.TiledMapHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

/**
 * Represents a stage with a tiled coordinate system
 * Tightly coupled with TiledStageActor, TiledStageLightSource, TiledStageMapRenderer
 * Delegates rendering of actors and map to TiledStageMapRenderer
 */

public class TiledStage extends Stage {
	private TiledMap _map;
	private TiledStageMapRenderer _mapRenderer;
	private OrthographicCamera _camera = new OrthographicCamera();
	private TiledStageActor _cameraFocalActor;
	private HashMap<String, TiledStageActor> _actorsByName = new HashMap<String, TiledStageActor>();
	private HashSet<TiledStageLightSource> _lightSources = new HashSet<TiledStageLightSource>();
	private ArrayList<HashSet<TiledStageActor>> _subTicksToActors;
	private HashSet<TiledStageActor> _actors = new HashSet<TiledStageActor>();
	private LinkedList<TiledStageActor> _tempActors = new LinkedList<TiledStageActor>();
	private int _tileWidth;
	private int _tileHeight;
	private int _tileRows;
	private int _tileColumns;
	private int _maxSubTicks;
	private float _tickTime;
	private float _tickDuration;
	private float _cameraZoom;
	private Commands _commands;
	private TiledMapTileLayer _wallLayer;
	private ArrayList<Coordinate> _coordinates;
	private HashMap<String, TiledMapTileLayer> _tileLayers = new HashMap<String, TiledMapTileLayer>();
	private ObjectList<Listener> _listeners = new ObjectList<Listener>();
	private boolean _isPaused = true;

	public TiledStage(int maxSubTicks, float tickDuration, Commands commands) {
		_maxSubTicks = maxSubTicks;
		_tickTime = 0f;
		_cameraZoom = 1f;
		_tickDuration = tickDuration;
		_commands = commands;

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

	public ObjectList<Listener> listeners() {
		return _listeners;
	}

	public boolean isLoaded() {
		return _map != null;
	}

	/**
	 * Loads a TiledMap, unloading if it is still loaded
	 *
	 * @param map
	 * @param screenWidth
	 * @param screenHeight
	 * @param cameraZoom    Initial zoom ratio of camera
	 * @param wallLayerName Name of the "wall" layer in the map
	 */

	public void load(TiledMap map, int screenWidth, int screenHeight, float cameraZoom,
	                 String wallLayerName) {
		if (isLoaded()) unload();

		_map = map;
		_mapRenderer = new TiledStageMapRenderer(this);
		MapProperties props = _map.getProperties();
		_tileWidth = props.get("tilewidth", Integer.class);
		_tileHeight = props.get("tileheight", Integer.class);
		_tileRows = props.get("height", Integer.class);
		_tileColumns = props.get("width", Integer.class);
		_cameraZoom = cameraZoom;
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

		_mapRenderer.initialize(screenWidth, screenHeight);
		setScreenSize(screenWidth, screenHeight);

		_isPaused = false;
	}

	public void unload() {
		_actorsByName.clear();
		_tempActors.clear();
		_tileLayers.clear();
		_actors.clear();
		for (int i = 0; i < _maxSubTicks; i++) {
			_subTicksToActors.get(i).clear();
		}
		for (TiledStageLightSource lightSource : _lightSources) {
			lightSource.dispose();
		}
		_lightSources.clear();
		_cameraFocalActor = null;

		_mapRenderer.dispose();
		_mapRenderer = null;
		_map.dispose();
		_isPaused = true;
		_map = null;
	}

	@Override
	public void draw() {
		if (_map == null) return;

		for (Listener listener : _listeners) {
			listener.beforeDraw(this);
		}

		float delta = Gdx.graphics.getDeltaTime();

		_tickTime += delta;

		while (_tickTime >= _tickDuration) {
			for (Listener listener : _listeners) {
				listener.beforeTick(this);
			}
			if (!_isPaused) tick();
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
		_mapRenderer.setView(_camera);
		_mapRenderer.render();

		for (Listener listener : _listeners) {
			listener.drawn(this);
		}
	}

	private void tick() {
		_tempActors.clear();
		_tempActors.addAll(_actors);
		for (TiledStageActor actor : _tempActors) actor.tick();

		for (int i = 0; i < _maxSubTicks; i ++) {
			_tempActors.clear();
			_tempActors.addAll(_subTicksToActors.get(i));
			Collections.sort(_tempActors);

			for (TiledStageActor actor : _tempActors) {
				actor.tick(i);
			}
		}

		for (Listener listener : _listeners) {
			listener.ticked(this);
		}
	}

	public Set<TiledStageActor> actors() {
		return _actors;
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

	public TiledMapTileLayer wallLayer() {
		return _wallLayer; }

	public boolean isPaused() {
		return _isPaused;
	}

	public void setIsPaused(boolean isPaused) {
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
	}

	public void addLightSource(TiledStageLightSource lightSource) {
		super.addActor(lightSource);
		_lightSources.add(lightSource);
	}

	public void removeActor(TiledStageActor actor) {
		_actors.remove(actor);

		for (HashSet<TiledStageActor> actors : _subTicksToActors) {
			actors.remove(actor);
		}
	}

	public void removeLightSource(TiledStageLightSource lightSource) {
		lightSource.dispose();
		_lightSources.remove(lightSource);
		lightSource.remove();
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

	public Set<TiledStageLightSource> lightSources() {
		return _lightSources;
	}

	public void setScreenSize(int screenWidth, int screenHeight) {
		_camera.setToOrtho(false, screenWidth, screenHeight);
		if (_cameraFocalActor != null) _camera.position.set(_cameraFocalActor.center(), 0);
		if (_mapRenderer != null) _mapRenderer.setScreenSize(screenWidth, screenHeight);
	}

	public void setCameraFocalActor(TiledStageActor actor) {
		_cameraFocalActor = actor;
		_camera.position.set(_cameraFocalActor.center(), 0);
	}

	public void setZoom(float zoom) {
		_cameraZoom = zoom;
	}

	public Iterator<TiledMapTile> tilesIterator() {
		return new TilesIterator();
	}

	public enum DIRECTION {
		WEST, EAST, NORTH, SOUTH, NORTH_WEST, NORTH_EAST, SOUTH_WEST, SOUTH_EAST
	}

	public interface Commands {
		void spawnObject(Coordinate origin, TiledMapTile tile, String name, MapProperties properties);
	}

	public static class Config {
		public static float CameraPanningSmoothRatio = 0.1f;
		public static float CameraZoomSmoothRatio = 0.1f;
	}

	public abstract static class Listener {
		public void beforeDraw(TiledStage tileStage) {
		}

		public void beforeTick(TiledStage tiledStage) {
		}

		public void ticked(TiledStage tiledStage) {
		}

		public void drawn(TiledStage tiledStage) {
		}
	}

	public class Coordinate implements Comparable<Coordinate> {
		private HashSet<TiledStageActor> _actors = new HashSet<TiledStageActor>();
		private int _row;
		private int _col;
		private HashMap<String, Cell> _cells;

		public Coordinate(int row, int col) {
			_row = row;
			_col = col;

			_cells = new HashMap<String, Cell>(_tileLayers.size());
			for (String layerName : _tileLayers.keySet()) {
				_cells.put(layerName, new Cell(this, layerName));
			}
		}

		public HashSet<TiledStageActor> actors() {
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

		public TreeSet<Coordinate> getCoordinatesAtRange(int range, boolean includeDiagonally) {
			TreeSet<Coordinate> coordinates = new TreeSet<Coordinate>();
			Coordinate coordinate;

			if (includeDiagonally) {
				for (int r = _row - range; r <= _row + range; r += 2 * range) {
					for (int c = _col - range; c <= _col + range; c += 2 * range) {
						coordinate = getCoordinate(r, c);
						if (coordinate != null) coordinates.add(coordinate);
					}
				}
			} else {
				for (int r = _row - range; r <= _row + range; r += 2 * range) {
					coordinate = getCoordinate(r, _col);
					if (coordinate != null) coordinates.add(coordinate);
				}
				for (int c = _col - range; c <= _col + range; c += 2 * range) {
					coordinate = getCoordinate(_row, c);
					if (coordinate != null) coordinates.add(coordinate);
				}
			}

			return coordinates;
		}

		public TreeSet<Coordinate> getCoordinatesInRange(int range, boolean includeDiagonally) {
			TreeSet<Coordinate> coordinates = new TreeSet<Coordinate>();
			Coordinate coordinate;

			if (includeDiagonally) {
				for (int r = _row - range; r <= _row + range; r++) {
					for (int c = _col - range; c <= _col + range; c++) {
						coordinate = getCoordinate(r, c);
						if (coordinate != null) coordinates.add(coordinate);
					}
				}
			} else {
				for (int r = _row - range; r <= _row + range; r++) {
					coordinate = getCoordinate(r, _col);
					if (coordinate != null) coordinates.add(coordinate);
				}
				for (int c = _col - range; c <= _col + range; c++) {
					coordinate = getCoordinate(_row, c);
					if (coordinate != null) coordinates.add(coordinate);
				}
			}

			return coordinates;
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

		public boolean isWall() {
			return _wallLayer.getCell(_col, _row) != null; }

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
}