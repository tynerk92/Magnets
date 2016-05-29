package com.somethingyellow.tiled;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.BatchTiledMapRenderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
	private String _actorsLayerName;
	private ArrayList<HashSet<TiledStageActor>> _actorsOnCoordinates;

	public TiledStageMapRenderer(TiledStage stage, TiledMap map, String actorsLayerName) {
		super(map);
		_stage = stage;
		_actorsLayerName = actorsLayerName;
	}

	public TiledStageMapRenderer(TiledStage stage, TiledMap map, Batch batch, String actorsLayerName) {
		super(map, batch);
		_stage = stage;
		_actorsLayerName = actorsLayerName;
	}

	public TiledStageMapRenderer(TiledStage stage, TiledMap map, float unitScale, String actorsLayerName) {
		super(map, unitScale);
		_stage = stage;
		_actorsLayerName = actorsLayerName;
	}

	public TiledStageMapRenderer(TiledStage stage, TiledMap map, float unitScale, Batch batch, String actorsLayerName) {
		super(map, unitScale, batch);
		_stage = stage;
		_actorsLayerName = actorsLayerName;
	}

	private void initialize() {
		_actorsOnCoordinates = new ArrayList<HashSet<TiledStageActor>>(_stage.tileRows() * _stage.tileColumns());
		for (int i = 0; i < _stage.tileRows() * _stage.tileColumns(); i++) {
			_actorsOnCoordinates.add(i, new HashSet<TiledStageActor>());
		}
	}

	@Override
	public void renderTileLayer(TiledMapTileLayer layer) {
		// TODO: Optimize rendering

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
		if (layer.getName().equals(_actorsLayerName)) {
			updateActorsOnCoordinates();
			isActorsLayer = true;
		}

		for (int row = row2; row >= row1; row--) {
			float x = xStart;

			if (isActorsLayer) {

				actors = new ArrayList<TiledStageActor>();
				for (int col = col1; col <= col2; col++) {
					for (TiledStageActor actor : _actorsOnCoordinates.get(row * _stage.tileColumns() + col)) {
						actors.add(actor);
					}
				}

				Collections.sort(actors);

				// In ascending actor depth
				for (TiledStageActor actor : actors) {
					TiledStage.Coordinate renderOrigin = getRenderOrigin(actor);
					bodyCoordinates = actor.getBodyCoordinates(renderOrigin);

					for (TiledStage.Coordinate coordinate : bodyCoordinates) {
						if (coordinate.row() != row) continue;

						// if there is some actor on both this and the above coordinate on the same layer with a higher actor depth
						// or the actor's body also exists in the above coordinate, do not render protrusion
						boolean ifRenderProtrusion = true;

						int aboveIndex = (coordinate.row() + 1) * _stage.tileColumns() + coordinate.column();
						if (aboveIndex >= 0 && aboveIndex < _actorsOnCoordinates.size()) {
							HashSet<TiledStageActor> aboveActors = _actorsOnCoordinates.get(aboveIndex);
							int index = coordinate.row() * _stage.tileColumns() + coordinate.column();
							for (TiledStageActor aboveActor : aboveActors) {
								if ((aboveActor.actorDepth() > actor.actorDepth() && _actorsOnCoordinates.get(index).contains(aboveActor)) ||
										aboveActor == actor) {
									ifRenderProtrusion = false;
									break;
								}
							}
						}

						textureRegions = actor.textureRegions();
						int rowDiff = coordinate.row() - renderOrigin.row();
						int colDiff = coordinate.column() - renderOrigin.column();

						for (TextureRegion textureRegion : textureRegions) {
							int protrudeHeight = textureRegion.getTexture().getHeight() - actor.bodyHeight() * _stage.tileHeight();
							if (ifRenderProtrusion) {
								if (rowDiff == actor.bodyHeight() - 1) {
									textureRegion.setRegion(colDiff * _stage.tileWidth(), 0, _stage.tileWidth(), protrudeHeight + _stage.tileHeight());
								} else {
									textureRegion.setRegion(colDiff * _stage.tileWidth(), protrudeHeight + (actor.bodyHeight() - 2 - rowDiff) * _stage.tileHeight(), _stage.tileWidth(), _stage.tileHeight() * 2);
								}
							} else {
								textureRegion.setRegion(colDiff * _stage.tileWidth(), protrudeHeight + (actor.bodyHeight() - 1 - rowDiff) * _stage.tileHeight(), _stage.tileWidth(), _stage.tileHeight());
							}

							batch.draw(textureRegion, actor.getX() + _stage.tileWidth() * colDiff, actor.getY() + _stage.tileHeight() * rowDiff);
						}
					}
				}
			}

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

			y -= layerTileHeight;
		}
	}

	private void updateActorsOnCoordinates() {
		if (_actorsOnCoordinates == null) initialize();

		// Clear previous rows of actors
		for (int i = 0; i < _stage.tileRows() * _stage.tileColumns(); i++) {
			_actorsOnCoordinates.get(i).clear();
		}

		for (TiledStageActor actor : _stage.actors()) {
			for (TiledStage.Coordinate bodyCoordinate : actor.getBodyCoordinates(getRenderOrigin(actor))) {
				_actorsOnCoordinates.get(bodyCoordinate.row() * _stage.tileColumns() + bodyCoordinate.column()).add(actor);
			}
		}
	}

	private TiledStage.Coordinate getRenderOrigin(TiledStageActor actor) {
		TiledStage.Coordinate renderOrigin = _stage.getCoordinateAt(actor.getX(), actor.getY());
		return (renderOrigin == null) ? actor.origin() : renderOrigin;
	}
}
