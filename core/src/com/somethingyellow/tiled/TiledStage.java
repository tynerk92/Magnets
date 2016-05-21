package com.somethingyellow.tiled;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TiledStage extends Stage implements Disposable {
	public static final float CAMERA_MAX_OFFSET = 2f;
	public static final float CAMERA_PANNING_SMOOTH_RATIO = 0.1f;
	private TiledMap _map;
	private TiledMapTileLayer _actorsLayer;
	private float _viewSizeX;
	private float _viewSizeY;
	private TiledStageMapRenderer _mapRenderer;
	private OrthographicCamera _camera;
	private TiledStageActor _cameraFocalActor;
	private TiledStageActor _inputFocalActor;
	private int _tileWidth;
	private int _tileHeight;
	private int _tileRows;
	private int _tileColumns;
	private ArrayList<Coordinate> _coordinates;

	public TiledStage(TiledMap map, TiledMapTileLayer actorsLayer, float viewSizeX, float viewSizeY) {
		_viewSizeX = viewSizeX;
		_viewSizeY = viewSizeY;
		_map = map;
		_actorsLayer = actorsLayer;

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

	public void initializeMap() {
		_mapRenderer = new TiledStageMapRenderer(this, _actorsLayer, _map);
		MapProperties props = _map.getProperties();
		_tileWidth = props.get("tilewidth", Integer.class);
		_tileHeight = props.get("tileheight", Integer.class);
		_tileRows = props.get("height", Integer.class);
		_tileColumns = props.get("width", Integer.class);

		_coordinates = new ArrayList<Coordinate>(_tileRows * _tileColumns);

		for (int r = 0; r < _tileRows; r++) {
			for (int c = 0; c < _tileColumns; c++) {
				_coordinates.add(new Coordinate(r, c));
			}
		}
	}

	@Override
	public void draw() {
		act(Gdx.graphics.getDeltaTime());

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

	// visual
	// -------

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

	// get/set
	// --------

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

	public MapLayer actorsLayer() {
		return _actorsLayer;
	}

	public Coordinate getCoordinate(int tileRow, int tileCol) {
		if (tileRow >= _tileRows || tileCol >= _tileColumns || tileRow < 0 || tileCol < 0) return null;
		return _coordinates.get(tileRow * _tileColumns + tileCol);
	}

	public LinkedList<MapLayer> findMapLayers(String propName, boolean value) {
		LinkedList<MapLayer> layers = new LinkedList<MapLayer>();

		for (MapLayer layer : _map.getLayers()) {
			if (ParseBooleanProp(layer.getProperties(), propName) == value) {
				layers.add(layer);
			}
		}

		return layers;
	}

	public LinkedList<Coordinate> findCoordinates(String layerName, String propName, boolean value) {
		LinkedList<Coordinate> coordinates = new LinkedList<Coordinate>();

		for (int r = 0; r < _tileRows; r++) {
			for (int c = 0; c < _tileColumns; c++) {
				Coordinate coordinate = getCoordinate(r, c);
				TiledMapTile tile = coordinate.getTile(layerName);
				if (tile == null) continue;

				if (ParseBooleanProp(tile.getProperties(), propName) == value) {
					coordinates.add(coordinate);
				}
			}
		}

		return coordinates;
	}

	public LinkedList<Coordinate> findCoordinates(String layerName, String propName, String value) {
		LinkedList<Coordinate> coordinates = new LinkedList<Coordinate>();

		for (int r = 0; r < _tileRows; r++) {
			for (int c = 0; c < _tileColumns; c++) {
				Coordinate coordinate = getCoordinate(r, c);
				TiledMapTile tile = coordinate.getTile(layerName);
				if (tile == null) continue;

				if (ParseProp(tile.getProperties(), propName, "").equals(value)) {
					coordinates.add(coordinate);
				}
			}
		}

		return coordinates;
	}

	public LinkedList<Coordinate> findCoordinates(String layerName, String propName, int value) {
		LinkedList<Coordinate> coordinates = new LinkedList<Coordinate>();

		for (int r = 0; r < _tileRows; r++) {
			for (int c = 0; c < _tileColumns; c++) {
				Coordinate coordinate = getCoordinate(r, c);
				TiledMapTile tile = coordinate.getTile(layerName);
				if (tile == null) continue;

				if (ParseIntegerProp(tile.getProperties(), propName) == value) {
					coordinates.add(coordinate);
				}
			}
		}

		return coordinates;
	}

	public MapLayer getMapLayer(String layerName) {
		return _map.getLayers().get(layerName);
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
		LEFT, RIGHT, UP, DOWN
	}

	public class Coordinate {
		private LinkedList<TiledStageActor> _actors;
		private int _row;
		private int _col;

		public Coordinate(int row, int col) {
			_row = row;
			_col = col;
			_actors = new LinkedList<TiledStageActor>();
		}

		public List<TiledStageActor> actors() {
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

		public TiledMapTileLayer.Cell getCell(String layerName) {
			MapLayer layer = _map.getLayers().get(layerName);
			if (layer == null || !(layer instanceof TiledMapTileLayer)) return null;
			return ((TiledMapTileLayer) layer).getCell(_col, _row);
		}

		public TiledMapTile getTile(String layerName) {
			TiledMapTileLayer.Cell cell = getCell(layerName);
			if (cell == null) return null;
			return cell.getTile();
		}

		public TiledMapTile removeTile(String layerName) {
			TiledMapTileLayer.Cell cell = getCell(layerName);
			if (cell == null) return null;
			TiledMapTile tile = cell.getTile();
			cell.setTile(null);
			return tile;
		}

		public Coordinate getAdjacentCoordinate(DIRECTION direction) {
			switch (direction) {
				case UP:
					return getCoordinate(row() + 1, column());
				case DOWN:
					return getCoordinate(row() - 1, column());
				case LEFT:
					return getCoordinate(row(), column() - 1);
				case RIGHT:
					return getCoordinate(row(), column() + 1);
			}

			return null;
		}

		public boolean getTileBooleanProp(String layerName, String propName) {
			TiledMapTile tile = getTile(layerName);
			if (tile == null) return false;
			return ParseBooleanProp(tile.getProperties(), propName);
		}

		public String getTileProp(String layerName, String propName) {
			TiledMapTile tile = getTile(layerName);
			if (tile == null) return "";
			return ParseProp(tile.getProperties(), propName);
		}

		public int row() {
			return _row;
		}

		public int column() {
			return _col;
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
	}
}