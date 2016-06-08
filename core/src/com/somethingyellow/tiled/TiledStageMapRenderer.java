package com.somethingyellow.tiled;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
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
	public static final String LIGHTING_FRAGMENT_SHADER =
			"#ifdef GL_ES\n" +
					"    #define LOWP lowp\n" +
					"    precision mediump float;\n" +
					"#else\n" +
					"    #define LOWP\n" +
					"#endif\n" +
					"\n" +
					"varying LOWP vec4 vColor;\n" +
					"varying vec2 vTexCoord;\n" +
					"\n" +
					"//texture samplers\n" +
					"uniform sampler2D u_texture; //diffuse map\n" +
					"uniform sampler2D u_lightmap;   //light map\n" +
					"\n" +
					"//additional parameters for the shader\n" +
					"uniform vec2 resolution; //resolution of screen\n" +
					"uniform LOWP vec4 ambientColor; //ambient RGB, alpha channel is intensity\n" +
					"\n" +
					"void main() {\n" +
					"\tvec4 diffuseColor = texture2D(u_texture, vTexCoord);\n" +
					"\tvec2 lighCoord = (gl_FragCoord.xy / resolution.xy);\n" +
					"\tvec4 light = texture2D(u_lightmap, lighCoord);\n" +
					"\n" +
					"\tvec3 ambient = ambientColor.rgb * ambientColor.a;\n" +
					"\tvec3 intensity = ambient + light.rgb;\n" +
					" \tvec3 finalColor = diffuseColor.rgb * intensity;\n" +
					"\n" +
					"\tgl_FragColor = vColor * vec4(finalColor, diffuseColor.a);\n" +
					"}\n";
	public static final String LIGHTING_VERTEX_SHADER =
			"attribute vec4 a_position;\n" +
					"attribute vec4 a_color;\n" +
					"attribute vec2 a_texCoord0;\n" +
					"uniform mat4 u_projTrans;\n" +
					"varying vec4 vColor;\n" +
					"varying vec2 vTexCoord;\n" +
					"\n" +
					"void main() {\n" +
					"\tvColor = a_color;\n" +
					"\tvTexCoord = a_texCoord0;\n" +
					"\tgl_Position = u_projTrans * a_position;\n" +
					"}";
	public static float ambientColorGreenDefault = 1f;
	public static float ambientColorRedDefault = 1f;
	public static float ambientColorBlueDefault = 1f;
	private TiledStage _stage;
	private String _bodiesLayerName;
	private ShaderProgram _lightingShaderProgram = new ShaderProgram(LIGHTING_VERTEX_SHADER, LIGHTING_FRAGMENT_SHADER);
	private ArrayList<HashSet<TiledStageBody>> _bodiesOnCoordinates;
	private ArrayList<TiledStageBody> _tempBodies = new ArrayList<TiledStageBody>();
	private FrameBuffer _frameBuffer;
	private Texture _blackTexture;
	private float _ambientColorRed = ambientColorRedDefault;
	private float _ambientColorGreen = ambientColorGreenDefault;
	private float _ambientColorBlue = ambientColorBlueDefault;

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

	public void initialize(int screenWidth, int screenHeight) {
		_bodiesOnCoordinates = new ArrayList<HashSet<TiledStageBody>>(_stage.tileRows() * _stage.tileColumns());
		for (int i = 0; i < _stage.tileRows() * _stage.tileColumns(); i++) {
			_bodiesOnCoordinates.add(i, new HashSet<TiledStageBody>());
		}

		Pixmap blackPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
		blackPixmap.setColor(Color.BLACK);
		blackPixmap.fill();
		_blackTexture = new Texture(blackPixmap);
		blackPixmap.dispose();

		setScreenSize(screenWidth, screenHeight);
	}

	public void setScreenSize(int screenWidth, int screenHeight) {
		if (_frameBuffer != null) {
			if (_frameBuffer.getWidth() == screenWidth && _frameBuffer.getHeight() == screenHeight)
				return;
			_frameBuffer.dispose();
		}
		_frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, screenWidth, screenHeight, false);
	}

	public void setAmbientColor(float red, float green, float blue) {
		_ambientColorBlue = blue;
		_ambientColorGreen = green;
		_ambientColorRed = red;
	}

	@Override
	public void render() {
		// Prepare shader based on light sources
		_frameBuffer.begin();
		batch.setShader(null);
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		for (TiledStageLightSource lightSource : _stage.lightSources()) {
			batch.setColor(1, 1, 1, lightSource.intensity());
			batch.draw(lightSource.texture(), lightSource.renderX(), lightSource.renderY(),
					lightSource.getWidth(), lightSource.getHeight());
		}
		batch.end();
		batch.setColor(Color.WHITE);
		_frameBuffer.end();

		_lightingShaderProgram.begin();
		_lightingShaderProgram.setUniformi("u_lightmap", 1);
		_lightingShaderProgram.setUniformf("ambientColor", _ambientColorRed, _ambientColorGreen, _ambientColorBlue, 1f);
		_lightingShaderProgram.setUniformf("resolution", _frameBuffer.getWidth(), _frameBuffer.getHeight());
		_lightingShaderProgram.end();

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setShader(_lightingShaderProgram);
		_frameBuffer.getColorBufferTexture().bind(1);
		_blackTexture.bind(0);

		super.render();
	}

	@Override
	public void renderTileLayer(TiledMapTileLayer layer) {

		// Draw tiled map
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

	@Override
	public void dispose() {
		super.dispose();
		_blackTexture.dispose();
		_frameBuffer.dispose();
	}
}
