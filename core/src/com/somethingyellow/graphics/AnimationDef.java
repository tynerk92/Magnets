package com.somethingyellow.graphics;

import java.util.ArrayList;

public class AnimationDef {
	private ArrayList<Frame> _frames;
	private int _zIndex;

	public AnimationDef(ArrayList<Frame> frames, int zIndex) {
		_frames = frames;
		_zIndex = zIndex;
	}

	public Animation instantiate(String tag) {
		return new Animation(tag, _frames, _zIndex);
	}

	public Animation instantiate(String tag, Animation.Listener listener) {
		return new Animation(tag, _frames, _zIndex, listener);
	}
}