package com.somethingyellow;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapImageLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class TiledStage extends Stage implements Disposable {
	private TiledMap _map;
	private float _viewSizeX;
	private float _viewSizeY;

	private OrthogonalTiledMapRenderer _mapRenderer;
	private OrthographicCamera _camera;
	private TiledStageActor _cameraFocalActor;
	private TiledStageActor _inputFocalActor;

	private int _tileWidth;
	private int _tileHeight;
	private int _tileRows;
	private int _tileColumns;
	private ArrayList<Coordinate> _coordinates;
	private HashMap<TiledStageActor, ActorOnCoordinate> _actors;
	private ArrayList<Integer> _topLayers;
	private ArrayList<Integer> _hiddenLayers;

	public TiledStage(TiledMap map, float viewSizeX, float viewSizeY) {
		_viewSizeX = viewSizeX;
		_viewSizeY = viewSizeY;
		_map = map;

		initializeMap();
		resetCamera();
	}

	public void initializeMap() {
		_mapRenderer = new OrthogonalTiledMapRenderer(_map);
		MapProperties props = _map.getProperties();
		_tileWidth = props.get("tilewidth", Integer.class);
		_tileHeight = props.get("tileheight", Integer.class);
		_tileRows = props.get("height", Integer.class);
		_tileColumns = props.get("width", Integer.class);

		_actors = new HashMap<TiledStageActor, ActorOnCoordinate>();

		_coordinates = new ArrayList<Coordinate>(_tileRows * _tileColumns);

		for (int r = 0; r < _tileRows; r++) {
			for (int c = 0; c < _tileColumns; c++) {
				_coordinates.add(new Coordinate(r, c));
			}
		}

		_topLayers = new ArrayList<Integer>();
		_hiddenLayers = new ArrayList<Integer>();
	}

	public void addActor(TiledStageActor actor, Coordinate coordinate) {
		super.addActor(actor);
		actor.create(this, coordinate);
		_actors.put(actor, new ActorOnCoordinate(actor, coordinate));
	}

	public void moveActor(TiledStageActor actor, Coordinate coordinate) {
		ActorOnCoordinate actorOnCoordinate = _actors.get(actor);
		coordinate.addActor(actor);
		getCoordinate(actorOnCoordinate.row(), actorOnCoordinate.column()).removeActor(actor);
		actorOnCoordinate.setCoordinate(coordinate);
	}

	public void removeActor(TiledStageActor actor) {
		_actors.remove(actor);
		actor.destroy();
		actor.remove();
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		_camera.translate((_cameraFocalActor.getX() - _camera.position.x) / 10, (_cameraFocalActor.getY() - _camera.position.y) / 10);
	}

	@Override
	public void draw() {
		act(Gdx.graphics.getDeltaTime());

		_mapRenderer.setView(_camera);

		_mapRenderer.getBatch().begin();

		for (int i = 0; i < _map.getLayers().getCount(); i++) {
			if (Collections.binarySearch(_topLayers, i) >= 0) continue;
			if (Collections.binarySearch(_hiddenLayers, i) >= 0) continue;
			MapLayer layer = _map.getLayers().get(i);
			if (layer instanceof TiledMapTileLayer)
				_mapRenderer.renderTileLayer((TiledMapTileLayer) layer);
			if (layer instanceof TiledMapImageLayer)
				_mapRenderer.renderImageLayer((TiledMapImageLayer) layer);
		}

		_mapRenderer.getBatch().end();

		super.draw();

		_mapRenderer.getBatch().begin();

		for (int i = 0; i < _map.getLayers().getCount(); i++) {
			if (Collections.binarySearch(_topLayers, i) < 0) continue;
			if (Collections.binarySearch(_hiddenLayers, i) >= 0) continue;
			MapLayer layer = _map.getLayers().get(i);
			if (layer instanceof TiledMapTileLayer)
				_mapRenderer.renderTileLayer((TiledMapTileLayer) layer);
			if (layer instanceof TiledMapImageLayer)
				_mapRenderer.renderImageLayer((TiledMapImageLayer) layer);
		}

		_mapRenderer.getBatch().end();
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

	public OrthogonalTiledMapRenderer mapRenderer() {
		return _mapRenderer;
	}

	public TiledStage setMapRenderer(OrthogonalTiledMapRenderer mapRenderer) {
		_mapRenderer = mapRenderer;
		return this;
	}


	// get/set
	// --------

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
		if (tileRow >= _tileRows || tileCol >= _tileColumns || tileRow < 0 || tileCol < 0) {
			throw new IllegalArgumentException("Invalid tileRow/tileCol!");
		}

		return _coordinates.get(tileRow * _tileColumns + tileCol);
	}

	public TiledStage addTopLayers(List<MapLayer> layers) {
		for (MapLayer layer : layers) {
			int index = _map.getLayers().getIndex(layer);
			if (index < 0) throw new IllegalArgumentException("Map layer in layers does not exist!");

			_topLayers.add(index);
		}

		Collections.sort(_topLayers);
		return this;
	}

	public TiledStage addHiddenLayers(List<MapLayer> layers) {
		for (MapLayer layer : layers) {
			int index = _map.getLayers().getIndex(layer);
			if (index < 0) throw new IllegalArgumentException("Map layer in layers does not exist!");

			_hiddenLayers.add(index);
		}

		Collections.sort(_hiddenLayers);
		return this;
	}

	public LinkedList<MapLayer> findLayers(String propName, boolean value) {
		LinkedList<MapLayer> layers = new LinkedList<MapLayer>();

		for (MapLayer layer : _map.getLayers()) {
			if (Boolean.parseBoolean((String) layer.getProperties().get(propName)) == value) {
				layers.add(layer);
			}
		}

		return layers;
	}

	public LinkedList<Coordinate> findCoordinates(String layerName, String propName, boolean value) {
		LinkedList<Coordinate> coordinates = new LinkedList<Coordinate>();

		MapLayer layer = _map.getLayers().get(layerName);
		if (layer == null) return coordinates;
		if (!(layer instanceof TiledMapTileLayer)) return coordinates;
		TiledMapTileLayer tiledLayer = (TiledMapTileLayer) layer;

		for (int r = 0; r < _tileRows; r++) {
			for (int c = 0; c < _tileColumns; c++) {
				Coordinate coordinate = getCoordinate(r, c);
				TiledMapTile tile = coordinate.getTile(layerName);
				if (tile == null) continue;

				if (Boolean.parseBoolean((String) tile.getProperties().get(propName)) == value) {
					coordinates.add(coordinate);
				}
			}
		}

		return coordinates;
	}

	public MapLayer getLayer(String layerName) {
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

		public TiledMapTile getTile(String layerName) {
			MapLayer layer = _map.getLayers().get(layerName);
			if (layer == null || !(layer instanceof TiledMapTileLayer)) return null;

			TiledMapTileLayer.Cell cell = ((TiledMapTileLayer) layer).getCell(_col, _row);
			if (cell == null) return null;

			return cell.getTile();
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
			return new Vector2((_col + offsetX + 0.5f) * _tileWidth, (_row + offsetY + 0.5f) * _tileHeight);
		}
	}

	public class ActorOnCoordinate {
		private TiledStageActor _actor;
		private Coordinate _coordinate;

		public ActorOnCoordinate(TiledStageActor actor, Coordinate coordinate) {
			_actor = actor;
			_coordinate = coordinate;
		}

		public TiledStageActor actor() {
			return _actor;
		}

		public int row() {
			return _coordinate.row();
		}

		public int column() {
			return _coordinate.column();
		}

		public ActorOnCoordinate setCoordinate(Coordinate coordinate) {
			_coordinate = coordinate;
			return this;
		}
	}
}