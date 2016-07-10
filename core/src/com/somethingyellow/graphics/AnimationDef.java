package com.somethingyellow.graphics;

/**
 * Template for an Animation
 * Stores an ArrayList of AnimationFrame and an associated z-index for the animation
 */

public class AnimationDef {
	private AnimationFrame[] _frames;
	private int _zIndex;

	public AnimationDef(AnimationFrame[] frames) {
		this(frames, 0);
	}

	public AnimationDef(AnimationFrame[] frames, int zIndex) {
		_frames = frames;
		_zIndex = zIndex;
	}

	public AnimationFrame[] frames() {
		return _frames;
	}

	public int zIndex() {
		return _zIndex;
	}
}