package com.somethingyellow.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Disposable;

import java.util.Collection;

/**
 * Convenience class to generate and apply a lighting shader to a batch, based on a list of actors as light sources
 * Uses LightSource class
 */

public class LightingShaderGenerator implements Disposable {
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

	private ShaderProgram _lightingShaderProgram = new ShaderProgram(VERTEX_SHADER, LIGHTING_FRAGMENT_SHADER);
	private FrameBuffer _frameBuffer;
	private Texture _blackTexture;
	private float _ambientColorRed = Config.AmbientColorRedDefault;
	private float _ambientColorGreen = Config.AmbientColorGreenDefault;
	private float _ambientColorBlue = Config.AmbientColorBlueDefault;
	private float _ambientAlpha = Config.AmbientAlphaDefault;

	public LightingShaderGenerator() {
		if (!_lightingShaderProgram.isCompiled()) {
			throw new RuntimeException("Cannot compile lighting shader!");
		}
	}

	public void setAmbientColor(float red, float green, float blue, float alpha) {
		_ambientColorBlue = blue;
		_ambientColorGreen = green;
		_ambientColorRed = red;
		_ambientAlpha = alpha;
	}

	public void applyLightingShader(Batch batch, Collection<LightSource> lightSources) {
		prepareFrameBuffer();

		_frameBuffer.begin();
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.setShader(null);
		batch.begin();
		for (LightSource lightSource : lightSources) {
			lightSource.getSprite().draw(batch, 1f);
		}
		batch.end();
		batch.setColor(Color.WHITE);
		_frameBuffer.end();

		_frameBuffer.getColorBufferTexture().bind(1);
		_blackTexture.bind(0);

		batch.setShader(_lightingShaderProgram);
	}

	public void prepareFrameBuffer() {
		if (_blackTexture == null) {
			Pixmap blackPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
			blackPixmap.setColor(Color.BLACK);
			blackPixmap.fill();
			_blackTexture = new Texture(blackPixmap);
			blackPixmap.dispose();
		}

		if (_frameBuffer == null || _frameBuffer.getWidth() != Gdx.graphics.getWidth() ||
				_frameBuffer.getHeight() != Gdx.graphics.getHeight()) {
			if (_frameBuffer != null) _frameBuffer.dispose();
			_frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);

			_lightingShaderProgram.begin();
			_lightingShaderProgram.setUniformi("u_lightmap", 1);
			_lightingShaderProgram.setUniformf("ambientColor", _ambientColorRed, _ambientColorGreen, _ambientColorBlue, _ambientAlpha);
			_lightingShaderProgram.setUniformf("resolution", _frameBuffer.getWidth(), _frameBuffer.getHeight());
			_lightingShaderProgram.end();
		}
	}

	@Override
	public void dispose() {
		if (_frameBuffer != null) {
			_frameBuffer.dispose();
			_frameBuffer = null;
		}

		if (_blackTexture != null) {
			_blackTexture.dispose();
			_blackTexture = null;
		}

		_lightingShaderProgram.dispose();
	}

	public static class Config {
		public static float AmbientColorGreenDefault = 0.6f;
		public static float AmbientColorRedDefault = 0.6f;
		public static float AmbientColorBlueDefault = 0.6f;
		public static float AmbientAlphaDefault = 1f;
	}
}
