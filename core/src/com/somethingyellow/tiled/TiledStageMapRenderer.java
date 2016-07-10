package com.somethingyellow.tiled;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;
import com.somethingyellow.graphics.Animation;

import java.util.ArrayList;
import java.util.Collections;
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

/**
 * Map renderer for TiledStage
 * Manages rendering of TiledStageActors and TiledStageLightSources in TiledStage
 * Renders TiledStageActors within "wall" layer of TiledStage, in pseudo-3D
 * (Actors in foremost rows are rendered on top of actors behind)
 * For exclusive use in TiledStage
 * Image and object layers are ignored during rendering
 * Draws on Batch of TiledStage
 */

public class TiledStageMapRenderer implements Disposable {
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
	private static final int NUM_VERTICES = 20;
	private float _vertices[] = new float[NUM_VERTICES];
	private Batch _batch;
	private Rectangle _viewBounds = new Rectangle();
	private TiledStage _stage;
	private ShaderProgram _lightingShaderProgram = new ShaderProgram(VERTEX_SHADER, LIGHTING_FRAGMENT_SHADER);
	private ArrayList<HashSet<Animation>> _animationsByCoordinates;
	private HashMap<Animation, TiledStageActor> _animationToActors = new HashMap<Animation, TiledStageActor>();
	private ArrayList<Animation> _tempAnimationsArray = new ArrayList<Animation>();
	private ArrayList<Animation> _tempAnimationsArray2 = new ArrayList<Animation>();
	private FrameBuffer _frameBuffer;
	private Texture _blackTexture;
	private float _ambientColorRed;
	private float _ambientColorGreen;
	private float _ambientColorBlue;
	private TiledMapTileLayer[] _tileLayers;

	public TiledStageMapRenderer(TiledStage stage) {
		_stage = stage;
		_batch = _stage.getBatch();

		LinkedList<TiledMapTileLayer> layers = new LinkedList<TiledMapTileLayer>();
		for (MapLayer layer : stage.map().getLayers()) {
			if (layer instanceof TiledMapTileLayer) layers.add((TiledMapTileLayer) layer);
		}
		_tileLayers = layers.toArray(new TiledMapTileLayer[layers.size()]);
	}

	public void initialize(int screenWidth, int screenHeight) {
		_ambientColorRed = Config.AmbientColorRedDefault;
		_ambientColorGreen = Config.AmbientColorGreenDefault;
		_ambientColorBlue = Config.AmbientColorBlueDefault;

		_animationsByCoordinates = new ArrayList<HashSet<Animation>>(_stage.tileRows() * _stage.tileColumns());
		for (int i = 0; i < _stage.tileRows() * _stage.tileColumns(); i++) {
			_animationsByCoordinates.add(i, new HashSet<Animation>());
		}

		// Prepare black texture for shadows
		Pixmap blackPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
		blackPixmap.setColor(Color.BLACK);
		blackPixmap.fill();
		_blackTexture = new Texture(blackPixmap);
		blackPixmap.dispose();

		setScreenSize(screenWidth, screenHeight);
	}

	public void setView(OrthographicCamera camera) {
		_stage.getBatch().setProjectionMatrix(camera.combined);
		float width = camera.viewportWidth * camera.zoom;
		float height = camera.viewportHeight * camera.zoom;
		_viewBounds.set(camera.position.x - width / 2, camera.position.y - height / 2, width, height);
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

	public void render() {
		// Process animations' data
		updateActorsData();

		// Prepare shader based on light sources
		prepareLightingShader();

		// Render layers
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		AnimatedTiledMapTile.updateAnimationBaseTime();
		_batch.begin();
		for (TiledMapTileLayer layer : _tileLayers) {
			for (int row = layer.getHeight() - 1; row >= 0; row--) {
				drawLayerByRow(layer, row);
				if (layer == _stage.wallLayer()) drawActorsByRow(row);
			}
		}

		_batch.end();
	}

	private void prepareLightingShader() {
		_frameBuffer.begin();
		_batch.setShader(null);
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		_batch.begin();
		for (TiledStageLightSource lightSource : _stage.lightSources()) {
			_batch.setColor(1, 1, 1, lightSource.intensity());
			_batch.draw(lightSource.texture(), lightSource.renderX(), lightSource.renderY(),
					lightSource.getWidth(), lightSource.getHeight());
		}
		_batch.end();
		_batch.setColor(Color.WHITE);
		_frameBuffer.end();

		_lightingShaderProgram.begin();
		_lightingShaderProgram.setUniformi("u_lightmap", 1);
		_lightingShaderProgram.setUniformf("ambientColor", _ambientColorRed, _ambientColorGreen, _ambientColorBlue, 1f);
		_lightingShaderProgram.setUniformf("resolution", _frameBuffer.getWidth(), _frameBuffer.getHeight());
		_lightingShaderProgram.end();
		_frameBuffer.getColorBufferTexture().bind(1);
		_blackTexture.bind(0);
		_batch.setShader(_lightingShaderProgram);
	}

	private void drawLayerByRow(TiledMapTileLayer layer, int row) {
		float layerTileWidth = layer.getTileWidth();
		float layerTileHeight = layer.getTileHeight();

		int row1 = Math.max(0, (int) (_viewBounds.y / layerTileHeight) - 1);
		int row2 = Math.min(layer.getHeight(), (int) ((_viewBounds.y + _viewBounds.height + layerTileHeight) / layerTileHeight)) - 1;
		if (row < row1 || row > row2) return; // no need to draw row: out of viewbounds

		Color batchColor = _batch.getColor();
		float color = Color.toFloatBits(batchColor.r, batchColor.g, batchColor.b, batchColor.a * layer.getOpacity());

		float y = row * layer.getTileHeight();

		int col1 = Math.max(0, (int) (_viewBounds.x / layerTileWidth));
		int col2 = Math.min(layer.getWidth(), (int) ((_viewBounds.x + _viewBounds.width + layerTileWidth) / layerTileWidth)) - 1;

		float x = col1 * layerTileWidth;
		for (int col = col1; col <= col2; col++) {
			TiledMapTileLayer.Cell cell = layer.getCell(col, row);

			if (cell != null) {
				TiledMapTile tile = cell.getTile();

				if (tile != null) {
					boolean flipX = cell.getFlipHorizontally();
					boolean flipY = cell.getFlipVertically();
					int rotations = cell.getRotation();

					TextureRegion region = tile.getTextureRegion();

					float x1 = x + tile.getOffsetX();
					float y1 = y + tile.getOffsetY();
					float x2 = x1 + region.getRegionWidth();
					float y2 = y1 + region.getRegionHeight();

					float u1 = region.getU();
					float v1 = region.getV2();
					float u2 = region.getU2();
					float v2 = region.getV();

					_vertices[X1] = x1;
					_vertices[Y1] = y1;
					_vertices[C1] = color;
					_vertices[U1] = u1;
					_vertices[V1] = v1;

					_vertices[X2] = x1;
					_vertices[Y2] = y2;
					_vertices[C2] = color;
					_vertices[U2] = u1;
					_vertices[V2] = v2;

					_vertices[X3] = x2;
					_vertices[Y3] = y2;
					_vertices[C3] = color;
					_vertices[U3] = u2;
					_vertices[V3] = v2;

					_vertices[X4] = x2;
					_vertices[Y4] = y1;
					_vertices[C4] = color;
					_vertices[U4] = u2;
					_vertices[V4] = v1;

					if (flipX) {
						float temp = _vertices[U1];
						_vertices[U1] = _vertices[U3];
						_vertices[U3] = temp;
						temp = _vertices[U2];
						_vertices[U2] = _vertices[U4];
						_vertices[U4] = temp;
					}
					if (flipY) {
						float temp = _vertices[V1];
						_vertices[V1] = _vertices[V3];
						_vertices[V3] = temp;
						temp = _vertices[V2];
						_vertices[V2] = _vertices[V4];
						_vertices[V4] = temp;
					}
					if (rotations != 0) {
						switch (rotations) {
							case TiledMapTileLayer.Cell.ROTATE_90: {
								float tempV = _vertices[V1];
								_vertices[V1] = _vertices[V2];
								_vertices[V2] = _vertices[V3];
								_vertices[V3] = _vertices[V4];
								_vertices[V4] = tempV;

								float tempU = _vertices[U1];
								_vertices[U1] = _vertices[U2];
								_vertices[U2] = _vertices[U3];
								_vertices[U3] = _vertices[U4];
								_vertices[U4] = tempU;
								break;
							}
							case TiledMapTileLayer.Cell.ROTATE_180: {
								float tempU = _vertices[U1];
								_vertices[U1] = _vertices[U3];
								_vertices[U3] = tempU;
								tempU = _vertices[U2];
								_vertices[U2] = _vertices[U4];
								_vertices[U4] = tempU;
								float tempV = _vertices[V1];
								_vertices[V1] = _vertices[V3];
								_vertices[V3] = tempV;
								tempV = _vertices[V2];
								_vertices[V2] = _vertices[V4];
								_vertices[V4] = tempV;
								break;
							}
							case TiledMapTileLayer.Cell.ROTATE_270: {
								float tempV = _vertices[V1];
								_vertices[V1] = _vertices[V4];
								_vertices[V4] = _vertices[V3];
								_vertices[V3] = _vertices[V2];
								_vertices[V2] = tempV;

								float tempU = _vertices[U1];
								_vertices[U1] = _vertices[U4];
								_vertices[U4] = _vertices[U3];
								_vertices[U3] = _vertices[U2];
								_vertices[U2] = tempU;
								break;
							}
						}
					}

					_batch.draw(region.getTexture(), _vertices, 0, NUM_VERTICES);
				}
			}

			x += layerTileWidth;
		}
	}

	private void drawActorsByRow(int row) {
		for (int col = 0; col < _stage.tileColumns(); col++) {
			HashSet<Animation> animations = _animationsByCoordinates.get(row * _stage.tileColumns() + col);

			// Sort sprites by z index
			_tempAnimationsArray.clear();
			_tempAnimationsArray2.clear();
			_tempAnimationsArray.addAll(animations);
			Collections.sort(_tempAnimationsArray);

			for (Animation animation : _tempAnimationsArray) {
				TiledStageActor actor = _animationToActors.get(animation);
				Sprite sprite = getSprite(actor, animation, row, col);

				int leftProtrusion = (animation.frame().width() - (actor.bodyWidth() * _stage.tileWidth())) / 2;
				int topProtrusion = animation.frame().height() - (actor.bodyHeight() * _stage.tileHeight());

				// Find relative coordinate within actor
				int bodyRow = Math.floorDiv(sprite.getRegionY() - topProtrusion, _stage.tileHeight());
				int bodyCol = Math.floorDiv(sprite.getRegionX() - leftProtrusion, _stage.tileWidth());
				if (bodyCol < 0) bodyCol = 0;
				if (bodyCol >= actor.bodyWidth()) bodyCol = actor.bodyWidth() - 1;

				if (bodyRow < 0 || !actor.getBodyAreaAt(bodyRow, bodyCol)) {
					for (int r = Math.max(0, bodyRow + 1); r < actor.bodyHeight(); r++) {
						if (actor.getBodyAreaAt(r, bodyCol)) {
							_tempAnimationsArray2.add(animation);
							break;
						}
					}
				}

				if (sprite.getBoundingRectangle().overlaps(_viewBounds)) sprite.draw(_batch);
			}

			for (Animation animation : _tempAnimationsArray2) {
				TiledStageActor actor = _animationToActors.get(animation);
				Sprite sprite = getSprite(actor, animation, row, col);
				if (sprite.getBoundingRectangle().overlaps(_viewBounds)) sprite.draw(_batch);
			}
		}
	}

	private Sprite getSprite(TiledStageActor body, Animation animation, int row, int col) {
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

	private void updateActorsData() {
		// Clear previous rows of bodies
		for (HashSet<Animation> set : _animationsByCoordinates) {
			set.clear();
		}

		_animationToActors.clear();

		for (TiledStageActor actor : _stage.actors()) {
			for (Animation animation : actor.animations()) {
				float left = actor.getX();
				float bottom = actor.getY();
				float right = left + animation.frame().width() - 1;
				float top = bottom + animation.frame().height() - 1;
				int leftProtrusion = (animation.frame().width() - (actor.bodyWidth() * _stage.tileWidth())) / 2;
				TiledStage.Coordinate bottomLeft = _stage.getCoordinateAt(left - leftProtrusion, bottom);
				TiledStage.Coordinate topRight = _stage.getCoordinateAt(right - leftProtrusion, top);
				for (int row = bottomLeft.row(); row <= topRight.row(); row++) {
					for (int col = bottomLeft.column(); col <= topRight.column(); col++) {
						_animationsByCoordinates.get(row * _stage.tileColumns() + col).add(animation);
					}
				}

				_animationToActors.put(animation, actor);
			}
		}
	}

	@Override
	public void dispose() {
		_blackTexture.dispose();
		_frameBuffer.dispose();
	}

	public static class Config {
		public static float AmbientColorGreenDefault = 0.7f;
		public static float AmbientColorRedDefault = 0.7f;
		public static float AmbientColorBlueDefault = 0.7f;
	}
}
