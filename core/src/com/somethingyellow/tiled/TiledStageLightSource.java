package com.somethingyellow.tiled;

import com.somethingyellow.graphics.AnimatedActor;
import com.somethingyellow.graphics.AnimationDef;

import java.util.HashMap;

public class TiledStageLightSource extends AnimatedActor {
	private static final HashMap<String, AnimationDef> TempAnimationDefs = new HashMap<String, AnimationDef>();
	private float _renderDisplacementX = 0f;
	private float _renderDisplacementY = 0f;
	private float _intensity;

	public void initialize(AnimationDef def, float renderDisplacementX, float renderDisplacementY) {
		TempAnimationDefs.clear();
		TempAnimationDefs.put("", def);
		super.initialize(TempAnimationDefs);

		setRenderDisplacement(renderDisplacementX, renderDisplacementY);
		_intensity = 1f;
	}

	public void setRenderDisplacement(float x, float y) {
		_renderDisplacementX = x;
		_renderDisplacementY = y;
	}

	public void setIntensity(float intensity) {
		_intensity = intensity;
	}

	@Override
	public float getX() {
		return super.getX() - _renderDisplacementX;
	}

	@Override
	public float getY() {
		return super.getY() - _renderDisplacementY;
	}

	public float intensity() {
		return _intensity;
	}
}
