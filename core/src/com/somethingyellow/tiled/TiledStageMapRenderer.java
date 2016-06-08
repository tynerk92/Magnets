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
import java.util.HashMap;
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
					"varying LOWP vec4 v_color;\n" +
					"varying vec2 v_texCoord;\n" +
					"\n" +
					"// texture samplers\n" +
					"uniform sampler2D u_texture; // diffuse map\n" +
					"uniform sampler2D u_lightmap;   // light map\n" +
					"\n" +
					"// additional parameters for the shader\n" +
					"uniform vec2 resolution; // resolution of screen\n" +
					"uniform LOWP vec4 ambientColor; // ambient RGB, alpha channel is intensity\n" +
					"\n" +
					"void main() {\n" +
					"\tvec4 diffuseColor = texture2D(u_texture, v_texCoord);\n" +
					"\tvec2 lightCoord = (gl_FragCoord.xy / resolution.xy);\n" +
					"\tvec4 light = texture2D(u_lightmap, lightCoord);\n" +
					"\n" +
					"\tvec3 ambient = ambientColor.rgb * ambientColor.a;\n" +
					"\tvec3 intensity = ambient + light.rgb;\n" +
					" \tvec3 finalColor = diffuseColor.rgb * intensity;\n" +
					"\n" +
					"\tgl_FragColor = v_color * vec4(finalColor, diffuseColor.a);\n" +
					"}\n";
	public static final String SHADOW_FRAGMENT_SHADER =
			"#ifdef GL_ES\n" +
					"    #define LOWP lowp\n" +
					"    precision mediump float;\n" +
					"#else\n" +
					"    #define LOWP\n" +
					"#endif\n" +
					"\n" +
					"varying LOWP vec4 v_color;\n" +
					"varying vec2 v_texCoord;\n" +
					"\n" +
					"uniform sampler2D u_texture; // diffuse map\n" +
					"uniform float intensity;\n" +
					"\n" +
					"void main() {\n" +
					"\tgl_FragColor = v_color * texture2D(u_texture, v_texCoord);\n" +
					"\n" +
					"    if (gl_FragColor[3] > 0.0) {\n" +
					"        gl_FragColor = vec4(0.0, 0.0, 0.0, intensity);\n" +
					"    }\n" +
					"}\n";
	public static final String VERTEX_SHADER =
			"attribute vec4 a_position;\n" +
					"attribute vec4 a_color;\n" +
					"attribute vec2 a_texCoord0;\n" +
					"uniform mat4 u_projTrans;\n" +
					"varying vec4 v_color;\n" +
					"varying vec2 v_texCoord;\n" +
					"\n" +
					"void main() {\n" +
					"\tv_color = a_color;\n" +
					"\tv_color.a = v_color.a * (255.0/254.0);\n" +
					"\tv_texCoord = a_texCoord0;\n" +
					"\tgl_Position = u_projTrans * a_position;\n" +
					"}";
	public static float ambientColorGreenDefault = 1f;
	public static float ambientColorRedDefault = 1f;
	public static float ambientColorBlueDefault = 1f;
	public static String layerNameBodies = "Bodies";
	public static String layerNameShadows = "Shadows";
	public static float shadowHeight = 0.2f;
	public static float shadowIntensity = 0.1f;

	private TiledStage _stage;
	private ShaderProgram _lightingShaderProgram = new ShaderProgram(VERTEX_SHADER, LIGHTING_FRAGMENT_SHADER);
	private ShaderProgram _shadowShaderProgram = new ShaderProgram(VERTEX_SHADER, SHADOW_FRAGMENT_SHADER);
	private ArrayList<HashSet<TiledStageBody.Frame>> _framesOnCoordinates;
	private HashMap<TiledStageBody.Frame, TiledStageBody> _framesToBodies = new HashMap<TiledStageBody.Frame, TiledStageBody>();
	private ArrayList<TiledStageBody.Frame> _tempFramesArray = new ArrayList<TiledStageBody.Frame>();
	private FrameBuffer _frameBuffer;
	private Texture _blackTexture;
	private float _ambientColorRed = ambientColorRedDefault;
	private float _ambientColorGreen = ambientColorGreenDefault;
	private float _ambientColorBlue = ambientColorBlueDefault;

	public TiledStageMapRenderer(TiledStage stage, TiledMap map) {
		super(map);
		_stage = stage;
	}

	public TiledStageMapRenderer(TiledStage stage, TiledMap map, Batch batch) {
		super(map, batch);
		System.out.println(_shadowShaderProgram.getLog());
		_stage = stage;
	}

	public TiledStageMapRenderer(TiledStage stage, TiledMap map, float unitScale) {
		super(map, unitScale);
		_stage = stage;
	}

	public TiledStageMapRenderer(TiledStage stage, TiledMap map, float unitScale, Batch batch) {
		super(map, unitScale, batch);
		_stage = stage;
	}

	public void initialize(int screenWidth, int screenHeight) {
		_framesOnCoordinates = new ArrayList<HashSet<TiledStageBody.Frame>>(_stage.tileRows());
		for (int i = 0; i < _stage.tileRows(); i++) {
			_framesOnCoordinates.add(i, new HashSet<TiledStageBody.Frame>());
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
		// Process frames' data
		updateFramesData();

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
		_frameBuffer.getColorBufferTexture().bind(1);
		_blackTexture.bind(0);

		_shadowShaderProgram.begin();
		_shadowShaderProgram.setUniformf("intensity", shadowIntensity);
		_shadowShaderProgram.end();

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		super.render();
	}

	@Override
	public void renderTileLayer(TiledMapTileLayer layer) {

		batch.setShader(_lightingShaderProgram);

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

		boolean isActorsLayer = false;
		if (layer.getName().equals(layerNameBodies)) {
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

			if (isActorsLayer) drawActorsOnRow(row, false);

			y -= layerTileHeight;
		}

		if (layer.getName().equals(layerNameShadows)) {
			batch.setShader(_shadowShaderProgram);

			for (int row = 0; row < _stage.tileRows(); row++) {
				drawActorsOnRow(row, true);
			}
		}

		batch.setShader(null);
	}

	private void drawActorsOnRow(int row, boolean isShadow) {
		_tempFramesArray.clear();
		_tempFramesArray.addAll(_framesOnCoordinates.get(row));

		if (!isShadow) {
			_tempFramesArray.sort(new Comparator<TiledStageBody.Frame>() {
				@Override
				public int compare(TiledStageBody.Frame frame1, TiledStageBody.Frame frame2) {
					return frame1.renderDepth() - frame2.renderDepth();
				}
			});
		}

		// In ascending render depth
		for (TiledStageBody.Frame frame : _tempFramesArray) {
			TiledStageBody body = _framesToBodies.get(frame);

			if (isShadow && !body.hasShadow()) continue;

			TiledStage.Coordinate renderOrigin = getRenderOrigin(body);
			LinkedList<TiledStage.Coordinate> bodyCoordinates = body.getBodyCoordinates(renderOrigin);

			int rowDiff = (body.bodyHeight() - 1) - (row - renderOrigin.row());

			for (TiledStage.Coordinate bodyCoordinate : bodyCoordinates) {
				if (bodyCoordinate.row() != row) continue;

				int colDiff = bodyCoordinate.column() - renderOrigin.column();

				int topProtrusion = frame.height() - body.bodyHeight() * _stage.tileHeight();
				int leftProtrusion = (frame.width() - body.bodyWidth() * _stage.tileWidth()) / 2;
				int top = topProtrusion + rowDiff * _stage.tileHeight();
				int left = leftProtrusion + colDiff * _stage.tileWidth();
				int bottom = top + _stage.tileHeight();
				int right = left + _stage.tileWidth();

				int r = row + 1;
				TiledStage.Coordinate aboveCoordinate;
				loop:
				while ((aboveCoordinate = _stage.getCoordinate(r, bodyCoordinate.column())) != null && top > 0) {
					if (bodyCoordinates.contains(aboveCoordinate)) break;

					// if there is some body on both this and the above coordinate on the same layer with a frame of a higher render depth
					for (TiledStageBody aboveBody : aboveCoordinate.bodies()) {
						if (bodyCoordinate.bodies().contains(aboveBody)) {
							for (TiledStageBody.Frame aboveBodyFrame : aboveBody.frames()) {
								if (aboveBodyFrame.renderDepth() > frame.renderDepth()) break loop;
							}
						}
					}

					top = Math.max(0, top - _stage.tileHeight());
					r++;
				}

				if (colDiff == 0) left -= leftProtrusion;
				if (colDiff == body.bodyWidth() - 1) right += leftProtrusion;

				TextureRegion textureRegion = frame.getTextureRegionAt(left, top, right, bottom);

				if (isShadow) {
					batch.draw(textureRegion, body.getX() + _stage.tileWidth() * colDiff - leftProtrusion,
							body.getY() + _stage.tileHeight() * ((body.bodyHeight() - 1) - rowDiff) + body.getZ(),
							frame.width() / 2, body.shadowDisplacementY(), textureRegion.getRegionWidth(), textureRegion.getRegionHeight(), 1f, -shadowHeight, 0f);
				} else {
					batch.draw(textureRegion, body.getX() + _stage.tileWidth() * colDiff - leftProtrusion,
							body.getY() + _stage.tileHeight() * ((body.bodyHeight() - 1) - rowDiff) + body.getZ());
				}
			}
		}
	}

	private void updateFramesData() {
		// Clear previous rows of bodies
		for (int i = 0; i < _stage.tileRows(); i++) {
			_framesOnCoordinates.get(i).clear();
		}

		_framesToBodies.clear();

		for (TiledStageBody body : _stage.bodies()) {
			TiledStage.Coordinate renderOrigin = getRenderOrigin(body);
			for (int r = 0; r < body.bodyHeight(); r++) {
				for (TiledStageBody.Frame frame : body.frames()) {
					_framesToBodies.put(frame, body);
					_framesOnCoordinates.get(renderOrigin.row() + r).add(frame);
				}
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
