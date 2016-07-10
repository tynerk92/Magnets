package com.somethingyellow.graphics;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.somethingyellow.utility.ObjectList;

/**
 * Represents an animation
 * Stores an array of AnimationFrame, an associated z-index, an associated String tag
 * To call update() to advance timer
 * Manages its animation timer and getSprite() returns the correct AnimationFrame Sprite for that timing
 * When the animation ends, it loops
 * At the start, it is shown
 */

public class Animation implements Comparable<Animation> {
	private float _time;
	private String _tag;
	private AnimationFrame[] _frames;
	private int _frameIndex;
	private int _zIndex;

	private float _duration;
	private float _alpha;
	private boolean _isActive;
	private ObjectList<Listener> _listeners;

	public Animation(AnimationDef def) {
		this(def, null);
	}

	public Animation(AnimationDef def, String tag) {
		this(def.frames(), def.zIndex(), tag);
	}

	public Animation(AnimationFrame[] frames) {
		this(frames, 0);
	}

	public Animation(AnimationFrame[] frames, int zIndex) {
		this(frames, zIndex, null);
	}

	public Animation(AnimationFrame[] frames, int zIndex, String tag) {
		_time = 0f;
		_tag = tag;
		_frameIndex = 0;
		_alpha = 1f;
		_isActive = true;
		_frames = frames;
		_zIndex = zIndex;

		_duration = 0f;
		for (AnimationFrame frame : _frames) {
			_duration += frame.duration();
		}

		_listeners = new ObjectList<Listener>();
	}

	public void update(float timeDelta) {
		if (_alpha <= 0) return;

		_time += timeDelta;
		while (_time > _frames[_frameIndex].duration()) {
			_time -= _frames[_frameIndex].duration();

			if (_frameIndex < _frames.length - 1) {
				_frameIndex++;
			} else {
				for (Listener listener : _listeners) listener.animationEnded(this);
				_frameIndex = 0;
			}
		}
	}

	public Sprite getSprite() {
		Sprite sprite = frame().getSprite();
		sprite.setAlpha(_alpha);
		return sprite;
	}

	public void show() {
		if (!_isActive) {
			_isActive = true;
			_alpha = 1f;
			_time = 0f;
			_frameIndex = 0;
		}
	}

	public void hide() {
		_isActive = false;
		_alpha = 0f;
	}

	public AnimationFrame frame() {
		return _frames[_frameIndex];
	}

	public int zIndex() {
		return _zIndex;
	}

	public float duration() {
		return _duration;
	}

	public String tag() {
		return _tag;
	}

	public float alpha() {
		return _alpha;
	}

	public boolean isActive() {
		return _isActive;
	}

	public ObjectList<Listener> listeners() {
		return _listeners;
	}

	@Override
	public int compareTo(Animation animation) {
		return _zIndex - animation._zIndex;
	}

	public static abstract class Listener {
		/**
		 * When the animation ends (after which it will loop)
		 */
		public void animationEnded(Animation animation) {
		}
	}
}