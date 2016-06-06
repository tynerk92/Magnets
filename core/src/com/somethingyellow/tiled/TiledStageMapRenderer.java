package com.somethingyellow.tiled;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.BatchTiledMapRenderer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;

import static com.badlogic.gdx.graphics.g2d.Batch.C1;
import static com.badlogic.gdx.graphics.g2d.Batch.C2;
import static com.badlogic.gdx.graphics.g2d.Batch.C3;
import static com.badlogic.gdx.graphics.g2d.Batch.C4;
import static com.badlogic.gdx.graphics.g2d.Batch.U1;
import static com.badlogic.gdx.graphics.g2d.Batch.U2;
import static com.badlogic.gdx.graphics.g2d.Batch.U3;
import static com.badlogic.gdx.graphics.g2d.Batch.U4;
import static com.badlogic.gdx.graphics.g2d.Batch.V1;
import static com.badlogic.gdx.graphics.g2d.Batch.V2;
import static com.badlogic.gdx.graphics.g2d.Batch.V3;
import static com.badlogic.gdx.graphics.g2d.Batch.V4;
import static com.badlogic.gdx.graphics.g2d.Batch.X1;
import static com.badlogic.gdx.graphics.g2d.Batch.X2;
import static com.badlogic.gdx.graphics.g2d.Batch.X3;
import static com.badlogic.gdx.graphics.g2d.Batch.X4;
import static com.badlogic.gdx.graphics.g2d.Batch.Y1;
import static com.badlogic.gdx.graphics.g2d.Batch.Y2;
import static com.badlogic.gdx.graphics.g2d.Batch.Y3;
import static com.badlogic.gdx.graphics.g2d.Batch.Y4;

public class TiledStageMapRenderer extends BatchTiledMapRenderer {
	private TiledStage _stage;
	private String _bodiesLayerName;
	private ArrayList<HashSet<TiledStageBody>> _bodiesOnCoordinates;
	private ArrayList<TiledStageBody> _tempBodies = new ArrayList<TiledStageBody>();

	public TiledStageMapRenderer(TiledStage stage, TiledMap map, String bodiesLayerName) {
		super(map);
		_stage = stage;
		_bodiesLayerName = bodiesLayerName;
	}

	public TiledStageMapRenderer(TiledStage stage, TiledMap map, Batch batch, String bodiesLayerName) {
		super(map, batch);
		_stage = stage;
		_bodiesLayerName = bodiesLayerName;
	}

	public TiledStageMapRenderer(TiledStage stage, TiledMap map, float unitScale, String bodiesLayerName) {
		super(map, unitScale);
		_stage = stage;
		_bodiesLayerName = bodiesLayerName;
	}

	public TiledStageMapRenderer(TiledStage stage, TiledMap map, float unitScale, Batch batch, String bodiesLayerName) {
		super(map, unitScale, batch);
		_stage = stage;
		_bodiesLayerName = bodiesLayerName;
	}

	private void initialize() {
		_bodiesOnCoordinates = new ArrayList<HashSet<TiledStageBody>>(_stage.tileRows() * _stage.tileColumns());
		for (int i = 0; i < _stage.tileRows() * _stage.tileColumns(); i++) {
			_bodiesOnCoordinates.add(i, new HashSet<TiledStageBody>());
		}
	}

	@Override
	public void renderTileLayer(TiledMapTileLayer layer) {
		final Color batchColor = batch.getColor();
		final float color = Color.toFloatBits(batchColor.r, batchColor.g, batchColor.b, batchColor.a * layer.getOpacity());

		final int layerWidth = layer.getWidth();
		final int layerHeight = layer.getHeight();

		final float layerTileWidth = layer.getTileWidth() * unitScale;
		final float layerTileHeight = layer.getTileHeight() * unitScale;

		final int col1 = Math.max(0, (int) (viewBounds.x / layerTileWidth));
		final int col2 = Math.min(layerWidth, (int) ((viewBounds.x + viewBounds.width + layerTileWidth) / layerTileWidth)) - 1;

		final int row1 = Math.max(0, (int) (viewBounds.y / layerTileHeight) - 1);
		final int row2 = Math.min(layerHeight, (int) ((viewBounds.y + viewBounds.height + layerTileHeight) / layerTileHeight)) - 1;

		float y = row2 * layerTileHeight;
		float xStart = col1 * layerTileWidth;
		final float[] vertices = this.vertices;

		ArrayList<TiledStageActor> actors;
		LinkedList<TextureRegion> textureRegions;
		LinkedList<TiledStage.Coordinate> bodyCoordinates;

		boolean isActorsLayer = false;
		if (layer.getName().equals(_bodiesLayerName)) {
			updateBodiesOnCoordinates();
			isActorsLayer = true;
		}

		for (int row = row2; row >= row1; row--) {
			float x = xStart;

			for (int col = col1; col <= col2; col++) {
				final TiledMapTileLayer.Cell cell = layer.getCell(col, row);

				if (cell != null) {
					final TiledMapTile tile = cell.getTile();

					if (tile != null) {
						final boolean flipX = cell.getFlipHorizontally();
						final boolean flipY = cell.getFlipVertically();
						final int rotations = cell.getRotation();

						TextureRegion region = tile.getTextureRegion();

						float x1 = x + tile.getOffsetX() * unitScale;
						float y1 = y + tile.getOffsetY() * unitScale;
						float x2 = x1 + region.getRegionWidth() * unitScale;
						float y2 = y1 + region.getRegionHeight() * unitScale;

						float u1 = region.getU();
						float v1 = region.getV2();
						float u2 = region.getU2();
						float v2 = region.getV();

						vertices[X1] = x1;
						vertices[Y1] = y1;
						vertices[C1] = color;
						vertices[U1] = u1;
						vertices[V1] = v1;

						vertices[X2] = x1;
						vertices[Y2] = y2;
						vertices[C2] = color;
						vertices[U2] = u1;
						vertices[V2] = v2;

						vertices[X3] = x2;
						vertices[Y3] = y2;
						vertices[C3] = color;
						vertices[U3] = u2;
						vertices[V3] = v2;

						vertices[X4] = x2;
						vertices[Y4] = y1;
						vertices[C4] = color;
						vertices[U4] = u2;
						vertices[V4] = v1;

						if (flipX) {
							float temp = vertices[U1];
							vertices[U1] = vertices[U3];
							vertices[U3] = temp;
							temp = vertices[U2];
							vertices[U2] = vertices[U4];
							vertices[U4] = temp;
						}
						if (flipY) {
							float temp = vertices[V1];
							vertices[V1] = vertices[V3];
							vertices[V3] = temp;
							temp = vertices[V2];
							vertices[V2] = vertices[V4];
							vertices[V4] = temp;
						}
						if (rotations != 0) {
							switch (rotations) {
								case TiledMapTileLayer.Cell.ROTATE_90: {
									float tempV = vertices[V1];
									vertices[V1] = vertices[V2];
									vertices[V2] = vertices[V3];
									vertices[V3] = vertices[V4];
									vertices[V4] = tempV;

									float tempU = vertices[U1];
									vertices[U1] = vertices[U2];
									vertices[U2] = vertices[U3];
									vertices[U3] = vertices[U4];
									vertices[U4] = tempU;
									break;
								}
								case TiledMapTileLayer.Cell.ROTATE_180: {
									float tempU = vertices[U1];
									vertices[U1] = vertices[U3];
									vertices[U3] = tempU;
									tempU = vertices[U2];
									vertices[U2] = vertices[U4];
									vertices[U4] = tempU;
									float tempV = vertices[V1];
									vertices[V1] = vertices[V3];
									vertices[V3] = tempV;
									tempV = vertices[V2];
									vertices[V2] = vertices[V4];
									vertices[V4] = tempV;
									break;
								}
								case TiledMapTileLayer.Cell.ROTATE_270: {
									float tempV = vertices[V1];
									vertices[V1] = vertices[V4];
									vertices[V4] = vertices[V3];
									vertices[V3] = vertices[V2];
									vertices[V2] = tempV;

									float tempU = vertices[U1];
									vertices[U1] = vertices[U4];
									vertices[U4] = vertices[U3];
									vertices[U3] = vertices[U2];
									vertices[U2] = tempU;
									break;
								}
							}
						}

						batch.draw(region.getTexture(), vertices, 0, NUM_VERTICES);
					}
				}

				x += layerTileWidth;
			}

			if (isActorsLayer) {

				_tempBodies.clear();
				for (int col = col1; col <= col2; col++) {
					for (TiledStageBody body : _bodiesOnCoordinates.get(row * _stage.tileColumns() + col)) {
						_tempBodies.add(body);
					}
				}
				_tempBodies.sort(new Comparator<TiledStageBody>() {
					@Override
					public int compare(TiledStageBody body1, TiledStageBody body2) {
						return body1.renderDepth() - body2.renderDepth();
					}
				});

				// In ascending actor depth
				for (TiledStageBody body : _tempBodies) {
					TiledStage.Coordinate renderOrigin = getRenderOrigin(body);
					bodyCoordinates = body.getBodyCoordinates(renderOrigin);

					for (TiledStage.Coordinate coordinate : bodyCoordinates) {
						if (coordinate.row() != row) continue;

						// if there is some body on both this and the above coordinate on the same layer with a higher body depth
						// or the body's body also exists in the above coordinate, do not render protrusion
						boolean ifRenderProtrusion = true;

						int aboveIndex = (coordinate.row() + 1) * _stage.tileColumns() + coordinate.column();
						if (aboveIndex >= 0 && aboveIndex < _bodiesOnCoordinates.size()) {
							HashSet<TiledStageBody> aboveActors = _bodiesOnCoordinates.get(aboveIndex);
							int index = coordinate.row() * _stage.tileColumns() + coordinate.column();
							for (TiledStageBody aboveBody : aboveActors) {
								if ((aboveBody.renderDepth() > body.renderDepth() && _bodiesOnCoordinates.get(index).contains(aboveBody)) ||
										aboveBody == body) {
									ifRenderProtrusion = false;
									break;
								}
							}
						}

						textureRegions = body.textureRegions();
						int rowDiff = coordinate.row() - renderOrigin.row();
						int colDiff = coordinate.column() - renderOrigin.column();

						for (TextureRegion textureRegion : textureRegions) {
							int protrudeHeight = textureRegion.getTexture().getHeight() - body.bodyHeight() * _stage.tileHeight();
							if (ifRenderProtrusion) {
								if (rowDiff == body.bodyHeight() - 1) {
									textureRegion.setRegion(colDiff * _stage.tileWidth(), 0, _stage.tileWidth(), protrudeHeight + _stage.tileHeight());
								} else {
									textureRegion.setRegion(colDiff * _stage.tileWidth(), protrudeHeight + (body.bodyHeight() - 2 - rowDiff) * _stage.tileHeight(), _stage.tileWidth(), _stage.tileHeight() * 2);
								}
							} else {
								textureRegion.setRegion(colDiff * _stage.tileWidth(), protrudeHeight + (body.bodyHeight() - 1 - rowDiff) * _stage.tileHeight(), _stage.tileWidth(), _stage.tileHeight());
							}

							batch.draw(textureRegion, body.getX() + _stage.tileWidth() * colDiff, body.getY() + _stage.tileHeight() * rowDiff + body.getZ());
						}
					}
				}
			}

			y -= layerTileHeight;
		}
	}

	private void updateBodiesOnCoordinates() {
		if (_bodiesOnCoordinates == null) initialize();

		// Clear previous rows of bodies
		for (int i = 0; i < _stage.tileRows() * _stage.tileColumns(); i++) {
			_bodiesOnCoordinates.get(i).clear();
		}

		for (TiledStageBody body : _stage.bodies()) {
			for (TiledStage.Coordinate bodyCoordinate : body.getBodyCoordinates(getRenderOrigin(body))) {
				_bodiesOnCoordinates.get(bodyCoordinate.row() * _stage.tileColumns() + bodyCoordinate.column()).add(body);
			}
		}
	}

	private TiledStage.Coordinate getRenderOrigin(TiledStageBody body) {
		TiledStage.Coordinate renderOrigin = _stage.getCoordinateAt(body.getX(), body.getY());
		return (renderOrigin == null) ? body.origin() : renderOrigin;
	}
}
