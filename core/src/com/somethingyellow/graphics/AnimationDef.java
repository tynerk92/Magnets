package com.somethingyellow.graphics;

/**
 * Template for an Animation
 * Stores an ArrayList of AnimationFrame and an associated z-index for the animation
 */

public class AnimationDef {
	private AnimationFrame[] _frames;
	private int _zIndex;
	private float _renderDisplacementX;
	private float _renderDisplacementY;

	public AnimationDef(AnimationFrame[] frames) {
		this(frames, 0, 0f, 0f);
	}

	public AnimationDef(AnimationFrame[] frames, int zIndex, float renderDisplacementX, float renderDisplacementY) {
		_frames = frames;
		_zIndex = zIndex;

		_renderDisplacementX = renderDisplacementX;
		_renderDisplacementY = renderDisplacementY;
	}

	public AnimationFrame[] frames() {
		return _frames;
	}

	public int zIndex() {
		return _zIndex;
	}

	public float renderDisplacementX() {
		return _renderDisplacementX;
	}

	public float renderDisplacementY() {
		return _renderDisplacementY;
	}
}