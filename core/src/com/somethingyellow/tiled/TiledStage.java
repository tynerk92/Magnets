package com.somethingyellow.tiled;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;

public class TiledStage extends Stage implements Disposable {
	public static final float CAMERA_MAX_OFFSET = 2f;
	public static final float CAMERA_PANNING_SMOOTH_RATIO = 0.1f;

	private TiledMap _map;
	private float _viewSizeX;
	private float _viewSizeY;
	private TiledStageMapRenderer _mapRenderer;
	private OrthographicCamera _camera;
	private TiledStageActor _cameraFocalActor;
	private TiledStageActor _inputFocalActor;
	private LinkedList<TiledStageActor> _actors;
	private int _tileWidth;
	private int _tileHeight;
	private int _tileRows;
	private int _tileColumns;
	private int _maxTicks;
	private ArrayList<Coordinate> _coordinates;
	private String _actorsLayerName;
	private HashMap<String, TiledMapTileLayer> _tileLayers;

	public TiledStage(TiledMap map, String actorsLayerName, float viewSizeX, float viewSizeY, int maxTicks) {
		_viewSizeX = viewSizeX;
		_viewSizeY = viewSizeY;
		_actorsLayerName = actorsLayerName;
		_map = map;
		_actors = new LinkedList<TiledStageActor>();
		_maxTicks = maxTicks;

		initializeMap();
		resetCamera();
	}

	public static boolean ParseBooleanProp(MapProperties props, String propName) {
		String prop = (String) props.get(propName);
		return Boolean.parseBoolean(prop);
	}

	public static boolean ParseBooleanProp(MapProperties props, String propName, boolean defaultValue) {
		String prop = (String) props.get(propName);
		if (prop == null) return defaultValue;
		return Boolean.parseBoolean(prop);
	}

	public static int ParseIntegerProp(MapProperties props, String propName) {
		String prop = (String) props.get(propName);
		return Integer.parseInt(prop);
	}

	public static int ParseIntegerProp(MapProperties props, String propName, int defaultValue) {
		String prop = (String) props.get(propName);
		if (prop == null) return defaultValue;
		return Integer.parseInt(prop);
	}

	public static String ParseProp(MapProperties props, String propName) {
		String prop = (String) props.get(propName);
		return prop;
	}

	public static String ParseProp(MapProperties props, String propName, String defaultValue) {
		String prop = (String) props.get(propName);
		if (prop == null) return defaultValue;
		return prop;
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

	// visual
	// -------

	public void initializeMap() {
		_mapRenderer = new TiledStageMapRenderer(this, _map, getBatch(), _actorsLayerName);
		MapProperties props = _map.getProperties();
		_tileWidth = props.get("tilewidth", Integer.class);
		_tileHeight = props.get("tileheight", Integer.class);
		_tileRows = props.get("height", Integer.class);
		_tileColumns = props.get("width", Integer.class);

		_coordinates = new ArrayList<Coordinate>(_tileRows * _tileColumns);
		_tileLayers = new HashMap<String, TiledMapTileLayer>();

		for (MapLayer layer : _map.getLayers()) {
			if (layer instanceof TiledMapTileLayer)
				_tileLayers.put(layer.getName(), (TiledMapTileLayer) layer);
		}

		for (int r = 0; r < _tileRows; r++) {
			for (int c = 0; c < _tileColumns; c++) {
				_coordinates.add(new Coordinate(r, c));
			}
		}
	}

	public void addActor(TiledStageActor actor) {
		super.addActor(actor);
		_actors.add(actor);
	}

	// get/set
	// --------

	public void removeActor(TiledStageActor actor) {
		_actors.remove(actor);
	}

	@Override
	public void draw() {
		float delta = Gdx.graphics.getDeltaTime();

		for (int i = 0; i < _maxTicks; i++) {
			for (TiledStageActor actor : _actors) {
				actor.act(i);
			}

			// TODO: Optimise calling of actors and their required ticks
		}

		super.act(delta);

		// Camera
		Vector2 camPos = new Vector2(_camera.position.x, _camera.position.y);
		float camDistFromFocalActor = Math.abs(_cameraFocalActor.position().dst(camPos));
		if (camDistFromFocalActor > CAMERA_MAX_OFFSET) {
			_camera.position.set(camPos.interpolate(_cameraFocalActor.position(), CAMERA_PANNING_SMOOTH_RATIO, Interpolation.linear), 0);
		}
		_camera.update();

		// Map
		_mapRenderer.setView(_camera);
		_mapRenderer.render();
	}

	public void resetCamera() {
		_camera = new OrthographicCamera();
		_camera.setToOrtho(false, _viewSizeX, _viewSizeY);
		getViewport().setWorldSize(_viewSizeX, _viewSizeY);
		getViewport().setCamera(_camera);
		_camera.update();
	}

	@Override
	public void dispose() {
		_map.dispose();
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

	public Coordinate getCoordinate(int tileRow, int tileCol) {
		if (tileRow >= _tileRows || tileCol >= _tileColumns || tileRow < 0 || tileCol < 0) return null;
		return _coordinates.get(getCoordinateIndex(tileRow, tileCol));
	}

	public Coordinate getCoordinateAt(float x, float y) {
		return getCoordinate(Math.floorDiv((int) y, _tileHeight), Math.floorDiv((int) x, _tileWidth));
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

	public Iterator<TiledMapTile> tilesIterator() {
		return new TilesIterator();
	}

	public Iterator<TiledStageActor> actorsIterator() {
		return new ActorsIterator();
	}

	public TiledStage setViewSize(float viewSizeX, float viewSizeY) {
		_viewSizeX = viewSizeX;
		_viewSizeY = viewSizeY;
		resetCamera();
		return this;
	}

	public TiledStage setCameraFocalActor(TiledStageActor actor) {
		_cameraFocalActor = actor;
		return this;
	}

	public TiledStage setInputFocalActor(TiledStageActor actor) {
		_inputFocalActor = actor;
		setKeyboardFocus(actor);
		setScrollFocus(actor);
		return this;
	}

	public enum DIRECTION {
		WEST, EAST, NORTH, SOUTH, NORTH_WEST, NORTH_EAST, SOUTH_WEST, SOUTH_EAST
	}

	public class Coordinate implements Comparable<Coordinate> {
		private HashSet<TiledStageActor> _actors;
		private int _row;
		private int _col;
		private HashMap<String, Cell> _cells;

		public Coordinate(int row, int col) {
			_row = row;
			_col = col;
			_actors = new HashSet<TiledStageActor>();

			_cells = new HashMap<String, Cell>(_tileLayers.size());
			for (String layerName : _tileLayers.keySet()) {
				TiledMapTileLayer layer = _tileLayers.get(layerName);
				_cells.put(layerName, new Cell(this, layerName));
			}
		}

		public HashSet<TiledStageActor> actors() {
			return _actors;
		}

		public Coordinate addActor(TiledStageActor actor) {
			_actors.add(actor);
			return this;
		}

		public Coordinate removeActor(TiledStageActor actor) {
			_actors.remove(actor);
			return this;
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

		public Vector2 getPosition(float offsetX, float offsetY) {
			return new Vector2((_col + offsetX) * _tileWidth, (_row + offsetY) * _tileHeight);
		}

		@Override
		public String toString() {
			return "(" + _row + ", " + _col + ") with actors: " + _actors.toString();
		}

		@Override
		public int compareTo(Coordinate coordinate) {
			return getCoordinateIndex(_row, _col) - getCoordinateIndex(coordinate._row, coordinate._col);
		}
	}

	public class Cell {
		private Coordinate _coordinate;
		private String _layerName;

		public Cell(Coordinate coordinate, String layerName) {
			_coordinate = coordinate;
			_layerName = layerName;
		}

		public Coordinate coordinate() {
			return _coordinate;
		}

		public String layerName() {
			return _layerName;
		}

		public TiledMapTile tile() {
			TiledMapTileLayer layer = _tileLayers.get(_layerName);
			if (layer == null) return null;
			TiledMapTileLayer.Cell cell = layer.getCell(_coordinate._col, _coordinate._row);
			if (cell == null) return null;
			return cell.getTile();
		}

		public Cell removeTile() {
			TiledMapTileLayer layer = _tileLayers.get(_layerName);
			if (layer == null) return null;
			TiledMapTileLayer.Cell cell = layer.getCell(_coordinate._col, _coordinate._row);
			if (cell == null) return null;
			cell.setTile(null);
			return this;
		}
	}

	private class ActorsIterator implements Iterator<TiledStageActor> {
		private HashSet<TiledStageActor> _prevActors = new HashSet<TiledStageActor>();
		private TiledStageActor[] _nextActors;
		private TiledStageActor _nextActor;
		private int _row = 0;
		private int _col = 0;
		private int _actorIndex = 0;

		public ActorsIterator() {
			next();
		}

		@Override
		public boolean hasNext() {
			return _nextActor != null;
		}

		@Override
		public TiledStageActor next() {
			TiledStageActor actor = _nextActor;
			_nextActor = null;

			while (_row < _tileRows) {
				while (_col < _tileColumns) {
					if (_nextActors == null) {
						HashSet<TiledStageActor> actors = getCoordinate(_row, _col).actors();
						_nextActors = actors.toArray(new TiledStageActor[actors.size()]);
					}

					while (_actorIndex < _nextActors.length) {
						TiledStageActor nextActor = _nextActors[_actorIndex];
						_actorIndex++;
						if (!_prevActors.contains(nextActor)) {
							_nextActor = nextActor;
							_prevActors.add(_nextActor);
							return actor;
						}
					}
					_nextActors = null;
					_actorIndex = 0;
					_col++;
				}
				_col = 0;
				_row++;
			}

			return actor;
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