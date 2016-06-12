package com.somethingyellow.tiled;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.BatchTiledMapRenderer;
import com.somethingyellow.graphics.Animation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

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
	private TiledStage _stage;
	private ShaderProgram _lightingShaderProgram = new ShaderProgram(VERTEX_SHADER, LIGHTING_FRAGMENT_SHADER);
	private ArrayList<HashSet<Animation>> _animationsByCoordinates;
	private HashMap<Animation, TiledStageBody> _animationsToBodies = new HashMap<Animation, TiledStageBody>();
	private ArrayList<Animation> _tempAnimationsArray = new ArrayList<Animation>();
	private ArrayList<Animation> _tempAnimationsArray2 = new ArrayList<Animation>();
	private int _bodyAnimationMinZIndex;
	private int _bodyAnimationMaxZIndex;
	private FrameBuffer _frameBuffer;
	private Texture _blackTexture;
	private float _ambientColorRed;
	private float _ambientColorGreen;
	private float _ambientColorBlue;
	private String _layerNameBodies;
	private String _layerNameShadows;

	public TiledStageMapRenderer(TiledStage stage, TiledMap map, String layerNameBodies, String layerNameShadows) {
		super(map, stage.getBatch());
		_stage = stage;
		_ambientColorRed = Config.AmbientColorRedDefault;
		_ambientColorGreen = Config.AmbientColorGreenDefault;
		_ambientColorBlue = Config.AmbientColorBlueDefault;
		_layerNameBodies = layerNameBodies;
		_layerNameShadows = layerNameShadows;
	}

	public TiledStageMapRenderer(TiledStage stage, TiledMap map, String layerNameBodies, String layerNameShadows, float unitScale) {
		super(map, unitScale, stage.getBatch());
		_stage = stage;
		_ambientColorRed = Config.AmbientColorRedDefault;
		_ambientColorGreen = Config.AmbientColorGreenDefault;
		_ambientColorBlue = Config.AmbientColorBlueDefault;
		_layerNameBodies = layerNameBodies;
		_layerNameShadows = layerNameShadows;
	}

	public void initialize(int screenWidth, int screenHeight) {
		_animationsByCoordinates = new ArrayList<HashSet<Animation>>(_stage.tileRows() * _stage.tileColumns());
		for (int i = 0; i < _stage.tileRows() * _stage.tileColumns(); i++) {
			_animationsByCoordinates.add(i, new HashSet<Animation>());
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
		// Process animations' data
		updateBodiesData();

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

		boolean isBodiesLayer = false;
		if (layer.getName().equals(_layerNameBodies)) {
			isBodiesLayer = true;
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

			if (isBodiesLayer) drawBodiesByRow(row);

			y -= layerTileHeight;
		}

		if (layer.getName().equals(_layerNameShadows)) {
			drawShadows();
		}

		batch.setShader(null);
	}

	private void drawBodiesByRow(int row) {
		for (int col = 0; col < _stage.tileColumns(); col++) {
			HashSet<Animation> animations = _animationsByCoordinates.get(row * _stage.tileColumns() + col);
			// Sort sprites by z index
			_tempAnimationsArray.clear();
			_tempAnimationsArray2.clear();
			_tempAnimationsArray.addAll(animations);
			Collections.sort(_tempAnimationsArray);

			for (Animation animation : _tempAnimationsArray) {
				TiledStageBody body = _animationsToBodies.get(animation);
				Sprite sprite = getSprite(body, animation, row, col);

				int leftProtrusion = (animation.frame().width() - (body.bodyWidth() * _stage.tileWidth())) / 2;
				int topProtrusion = animation.frame().height() - (body.bodyHeight() * _stage.tileHeight());

				// Find relative coordinate within body
				int bodyRow = Math.floorDiv(sprite.getRegionY() - topProtrusion, _stage.tileHeight());
				int bodyCol = Math.floorDiv(sprite.getRegionX() - leftProtrusion, _stage.tileWidth());
				if (bodyCol < 0) bodyCol = 0;
				if (bodyCol >= body.bodyWidth()) bodyCol = body.bodyWidth() - 1;

				if (bodyRow < 0 || !body.getBodyAreaAt(bodyRow, bodyCol)) {
					for (int r = Math.max(0, bodyRow + 1); r < body.bodyHeight(); r ++) {
						if (body.getBodyAreaAt(r, bodyCol)) {
							_tempAnimationsArray2.add(animation);
							break;
						}
					}
				}

				sprite.draw(batch);
			}

			for (Animation animation : _tempAnimationsArray2) {
				TiledStageBody body = _animationsToBodies.get(animation);
				getSprite(body, animation, row, col).draw(batch);
			}
		}
	}

	private Sprite getSprite(TiledStageBody body, Animation animation, int row, int col) {
		Sprite sprite = animation.getSprite();
		int x = col * _stage.tileWidth();
		int y = (row + 1) * _stage.tileHeight();
		int leftProtrusion = (sprite.getRegionWidth() - (body.bodyWidth() * _stage.tileWidth())) / 2;

		int regionTop = sprite.getRegionHeight() - (y - (int) body.getY());
		int regionLeft = x - (int) body.getX() + leftProtrusion;
		int regionWidth = _stage.tileWidth();
		int regionHeight = _stage.tileHeight();

		if (regionTop < 0) {
			regionHeight -= -regionTop;
			regionTop = 0;
		} else if (regionTop + regionHeight > sprite.getRegionHeight()) {
			int diff = (regionTop + regionHeight) - sprite.getRegionHeight();
			regionHeight -= diff;
			y += diff;
		}

		if (regionLeft < 0) {
			regionWidth -= -regionLeft;
			x += -regionLeft;
			regionLeft = 0;
		} else if (regionLeft + regionWidth > sprite.getRegionWidth()) {
			int diff = (regionLeft + regionWidth) - sprite.getRegionWidth();
			regionWidth -= diff;
		}

		sprite.setRegion(regionLeft, regionTop, regionWidth, regionHeight);
		sprite.setSize(sprite.getRegionWidth(), sprite.getRegionHeight());
		sprite.setPosition(x - leftProtrusion, y - _stage.tileHeight());

		return sprite;
	}

	private void drawShadows() {
		for (TiledStageBody body : _stage.bodies()) {
			if (!body.hasShadow()) continue;
			for (Animation animation : body.animations()) {
				if (animation.alpha() <= 0) continue;
				Sprite sprite = animation.getSprite();
				sprite.setPosition(body.getX(), body.getY());
				sprite.setOrigin(sprite.getWidth() / 2, body.shadowDisplacementY());
				sprite.setScale(1f, -Config.ShadowHeight);
				sprite.setColor(0, 0, 0, Config.ShadowIntensity);
				sprite.draw(batch);
			}
		}
	}

	private void updateBodiesData() {
		// Clear previous rows of bodies
		for (HashSet<Animation> set : _animationsByCoordinates) {
			set.clear();
		}

		_animationsToBodies.clear();

		_bodyAnimationMinZIndex = Integer.MAX_VALUE;
		_bodyAnimationMaxZIndex = Integer.MIN_VALUE;
		for (TiledStageBody body : _stage.bodies()) {
			for (Animation animation : body.animations()) {
				float left = body.getX();
				float bottom = body.getY();
				float right = left + animation.frame().width() - 1;
				float top = bottom + animation.frame().height() - 1;
				int leftProtrusion = (animation.frame().width() - (body.bodyWidth() * _stage.tileWidth())) / 2;
				TiledStage.Coordinate bottomLeft = _stage.getCoordinateAt(left - leftProtrusion, bottom);
				TiledStage.Coordinate topRight = _stage.getCoordinateAt(right - leftProtrusion, top);
				for (int row = bottomLeft.row(); row <= topRight.row(); row++) {
					for (int col = bottomLeft.column(); col <= topRight.column(); col++) {
						_animationsByCoordinates.get(row * _stage.tileColumns() + col).add(animation);
					}
				}

				_animationsToBodies.put(animation, body);
			}

			_bodyAnimationMinZIndex = Math.min(_bodyAnimationMinZIndex, body.minZIndex());
			_bodyAnimationMaxZIndex = Math.max(_bodyAnimationMaxZIndex, body.maxZIndex());
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		_blackTexture.dispose();
		_frameBuffer.dispose();
	}

	public static class Config {
		public static float AmbientColorGreenDefault = 0.7f;
		public static float AmbientColorRedDefault = 0.7f;
		public static float AmbientColorBlueDefault = 0.7f;
		public static float ShadowHeight = 0.2f;
		public static float ShadowIntensity = 0.5f;
	}
}
