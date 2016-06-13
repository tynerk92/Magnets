package com.somethingyellow.tiled;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.somethingyellow.Listeners;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

public class TiledStage extends Stage {

	private TiledMap _map;
	private TiledStageMapRenderer _mapRenderer;
	private OrthographicCamera _camera = new OrthographicCamera();
	private TiledStageActor _cameraFocalActor;
	private HashMap<String, TiledStageBody> _bodiesByName = new HashMap<String, TiledStageBody>();
	private ArrayList<TiledStageBody> _tempBodies = new ArrayList<TiledStageBody>();
	private HashSet<TiledStageBody> _bodies = new HashSet<TiledStageBody>();
	private HashSet<TiledStageLightSource> _lightSources = new HashSet<TiledStageLightSource>();
	private ArrayList<HashSet<TiledStageActor>> _subTicksToActors;
	private ArrayList<TiledStageActor> _tempActors = new ArrayList<TiledStageActor>();
	private int _tileWidth;
	private int _tileHeight;
	private int _tileRows;
	private int _tileColumns;
	private int _maxSubTicks;
	private float _tickTime;
	private float _tickDuration;
	private float _cameraZoom;
	private ArrayList<Coordinate> _coordinates;
	private String _bodiesLayerName;
	private String _shadowsLayerName;
	private HashMap<String, TiledMapTileLayer> _tileLayers = new HashMap<String, TiledMapTileLayer>();
	private LinkedList<TiledObject> _objects = new LinkedList<TiledObject>();
	private Listeners<Listener> _listeners = new Listeners<Listener>();
	private boolean _isPaused = true;

	public TiledStage(String bodiesLayerName, String shadowsLayerName, int maxSubTicks, float tickDuration) {
		_bodiesLayerName = bodiesLayerName;
		_shadowsLayerName = shadowsLayerName;
		_maxSubTicks = maxSubTicks;
		_tickTime = 0f;
		_cameraZoom = 1f;
		_tickDuration = tickDuration;

		_subTicksToActors = new ArrayList<HashSet<TiledStageActor>>(_maxSubTicks);
		for (int i = 0; i < _maxSubTicks; i++) {
			_subTicksToActors.add(i, new HashSet<TiledStageActor>());
		}

		getViewport().setCamera(_camera);
	}

	public static Boolean ParseBooleanProp(MapProperties props, String propName) {
		Object propObject = props.get(propName);
		return Boolean.parseBoolean(propObject.toString());
	}

	public static boolean ParseBooleanProp(MapProperties props, String propName, boolean defaultValue) {
		Object propObject = props.get(propName);
		if (propObject == null) return defaultValue;
		return Boolean.parseBoolean(propObject.toString());
	}

	public static Integer ParseIntegerProp(MapProperties props, String propName) {
		Object propObject = props.get(propName);
		if (propObject == null) return null;
		if (propObject instanceof Float) {
			return Math.round((Float) propObject);
		} else {
			return Integer.parseInt(propObject.toString());
		}
	}

	public static int ParseIntegerProp(MapProperties props, String propName, int defaultValue) {
		Object propObject = props.get(propName);
		if (propObject instanceof Float) {
			return Math.round((Float) propObject);
		} else {
			if (propObject == null) return defaultValue;
			return Integer.parseInt(propObject.toString());
		}
	}

	public static Float ParseFloatProp(MapProperties props, String propName) {
		Object propObject = props.get(propName);
		if (propObject == null) return null;
		if (propObject instanceof Float) {
			return (Float) propObject;
		} else {
			return Float.parseFloat(propObject.toString());
		}
	}

	public static float ParseFloatProp(MapProperties props, String propName, float defaultValue) {
		Object propObject = props.get(propName);
		if (propObject == null) return defaultValue;
		if (propObject instanceof Float) {
			return (Float) propObject;
		} else {
			return Float.parseFloat(propObject.toString());
		}
	}

	public static String ParseProp(MapProperties props, String propName) {
		Object propObject = props.get(propName);
		if (propObject == null) return null;
		return propObject.toString();
	}

	public static String ParseProp(MapProperties props, String propName, String defaultValue) {
		Object propObject = props.get(propName);
		if (propObject == null) return defaultValue;
		return propObject.toString();
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

	public Listeners<Listener> listeners() {
		return _listeners;
	}

	public boolean isLoaded() {
		return _map != null;
	}

	public void load(TiledMap map, int screenWidth, int screenHeight, float cameraZoom) {
		if (isLoaded()) unload();

		_map = map;
		_mapRenderer = new TiledStageMapRenderer(this, _map, _bodiesLayerName, _shadowsLayerName);
		MapProperties props = _map.getProperties();
		_tileWidth = props.get("tilewidth", Integer.class);
		_tileHeight = props.get("tileheight", Integer.class);
		_tileRows = props.get("height", Integer.class);
		_tileColumns = props.get("width", Integer.class);
		_cameraZoom = cameraZoom;

		_coordinates = new ArrayList<Coordinate>(_tileRows * _tileColumns);

		for (MapLayer layer : _map.getLayers()) {
			if (layer instanceof TiledMapTileLayer) {
				_tileLayers.put(layer.getName(), (TiledMapTileLayer) layer);
			}
		}

		for (int r = 0; r < _tileRows; r++) {
			for (int c = 0; c < _tileColumns; c++) {
				_coordinates.add(new Coordinate(r, c));
			}
		}

		for (MapLayer layer : _map.getLayers()) {
			for (MapObject object : layer.getObjects()) {
				_objects.add(new TiledObject(object));
			}
		}

		_mapRenderer.initialize(screenWidth, screenHeight);
		setScreenSize(screenWidth, screenHeight);

		_isPaused = false;
	}

	public void unload() {
		_bodies.clear();
		_tempBodies.clear();
		_bodiesByName.clear();
		_tileLayers.clear();
		_tempActors.clear();
		_objects.clear();
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

	// visual
	// -------

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
		_tempBodies.clear();
		_tempBodies.addAll(_bodies);
		for (TiledStageBody body : _tempBodies) {
			body.act();
		}

		for (int i = 0; i < _maxSubTicks; i++) {
			_tempActors.clear();
			_tempActors.addAll(_subTicksToActors.get(i));
			Collections.sort(_tempActors);

			for (TiledStageActor actor : _tempActors) {
				actor.act(i);
			}
		}

		for (Listener listener : _listeners) {
			listener.ticked(this);
		}
	}

	public TiledStageMapRenderer mapRenderer() {
		return _mapRenderer;
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

	public void addBody(TiledStageBody body) {
		super.addActor(body);
		_bodies.add(body);

		if (body instanceof TiledStageActor) {
			TiledStageActor actor = (TiledStageActor) body;
			// Considering what subticks the bodies listen to, add to hashmap
			for (int i : actor.subticks()) {
				_subTicksToActors.get(i).add(actor);
			}
		}
	}

	public void addLightSource(TiledStageLightSource lightSource) {
		super.addActor(lightSource);
		_lightSources.add(lightSource);
	}

	public void removeBody(TiledStageBody body) {
		_bodies.remove(body);

		for (HashSet<TiledStageActor> actors : _subTicksToActors) {
			if (body instanceof TiledStageActor) actors.remove(body);
		}
	}

	public void removeLightSource(TiledStageLightSource lightSource) {
		lightSource.dispose();
		_lightSources.remove(lightSource);
		lightSource.remove();
	}

	public TiledStageBody getBody(String name) {
		// Search through bodies in coordinate system if not memoized
		// If found, memoize actor by name
		if (!_bodiesByName.containsKey(name)) {
			for (TiledStageBody body : _bodies) {
				if (body.getName() != null && body.getName().equals(name)) {
					_bodiesByName.put(name, body);
					break;
				}
			}
		}

		// Check if actor exists in coordinate system
		// If not, remove it from hashmap of names
		TiledStageBody body = _bodiesByName.get(name);
		if (body == null || !_bodies.contains(body)) {
			_bodiesByName.remove(name);
			return null;
		} else {
			return body;
		}
	}

	private int getCoordinateIndex(int tileRow, int tileCol) {
		return tileRow * _tileColumns + tileCol;
	}

	public HashMap<String, TiledMapTileLayer> tileLayers() {
		return _tileLayers;
	}

	public Iterator<Cell> cellsIterator() {
		return new CellsIterator();
	}

	public Iterator<Cell> getCells(String layerName) {
		return new CellsIterator(layerName);
	}

	public Set<TiledStageBody> bodies() {
		return _bodies;
	}

	public Set<TiledStageLightSource> lightSources() {
		return _lightSources;
	}

	public LinkedList<TiledObject> objects() {
		return _objects;
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

	public class TiledObject {
		private Coordinate _origin;
		private TiledMapTile _tile;
		private MapObject _object;

		private TiledObject(MapObject object) {
			_object = object;

			// Determine origin of object relative to coordinate system
			int x = ParseIntegerProp(_object.getProperties(), "x", 0);
			int y = ParseIntegerProp(_object.getProperties(), "y", 0) + ParseIntegerProp(_object.getProperties(), "height", 0);
			_origin = getCoordinateAt(x, y);

			// get tile defined by "gid" property of object
			int gid = ParseIntegerProp(_object.getProperties(), "gid", 0);
			_tile = _map.getTileSets().getTile(gid);
		}

		public Coordinate origin() {
			return _origin;
		}

		public TiledMapTile tile() {
			return _tile;
		}

		public String name() {
			return _object.getName();
		}

		public MapProperties properties() {
			return _object.getProperties();
		}
	}

	public class Coordinate implements Comparable<Coordinate> {
		private HashSet<TiledStageBody> _bodies = new HashSet<TiledStageBody>();
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

		public HashSet<TiledStageBody> bodies() {
			return _bodies;
		}

		public TiledStage stage() {
			return TiledStage.this;
		}

		public void add(TiledStageBody body) {
			_bodies.add(body);
			if (body instanceof TiledStageActor) {
				_actors.add((TiledStageActor) body);
			}
		}

		public void remove(TiledStageBody body) {
			_bodies.remove(body);
			if (body instanceof TiledStageActor) _actors.remove(body);
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

		public OrthographicCamera camera() {
			return _camera;
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
			return ParseProp(tile.getProperties(), propName, defaultValue);
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