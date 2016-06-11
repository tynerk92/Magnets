package com.somethingyellow.graphics;

import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.ArrayList;

public class Animation implements Comparable<Animation> {
	private float _time;
	private String _tag;
	private ArrayList<Frame> _frames;
	private int _frameIndex;
	private int _zIndex;

	private float _duration;
	private float _alpha;
	private boolean _isActive;
	private Listener _listener;

	public Animation(String tag, ArrayList<Frame> frames, int zIndex) {
		_time = 0f;
		_tag = tag;
		_frameIndex = 0;
		_alpha = 0f;
		_isActive = false;
		_frames = frames;
		_zIndex = zIndex;

		_duration = 0f;
		for (Frame frame : _frames) {
			_duration += frame.duration();
		}
	}

	public Animation(String tag, ArrayList<Frame> frames, int zIndex, Listener listener) {
		this(tag, frames, zIndex);
		_listener = listener;
	}

	public void update(float timeDelta) {
		if (_alpha <= 0) return;

		_time += timeDelta;
		while (_time > _frames.get(_frameIndex).duration()) {
			_time -= _frames.get(_frameIndex).duration();

			if (_frameIndex < _frames.size() - 1) {
				_frameIndex++;
			} else {
				if (_listener != null) _listener.animationEnded(this);
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

	public Frame frame() {
		return _frames.get(_frameIndex);
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

	@Override
	public int compareTo(Animation animation) {
		return _zIndex - animation._zIndex;
	}

	public interface Listener {
		void animationEnded(Animation animation);
	}
}