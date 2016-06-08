package com.somethingyellow.tiled;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeSet;

public abstract class TiledStageBody extends Actor implements Comparable<TiledStageBody>, Pool.Poolable {
	public static final boolean[] BodyArea1x1 = new boolean[]{
			true
	};
	public static final String STATE_DEFAULT = "Default";

	private TiledStage.Coordinate _origin;
	private LinkedList<TiledStage.Coordinate> _bodyCoordinates = new LinkedList<TiledStage.Coordinate>();
	private LinkedList<Frame> _tempFrames = new LinkedList<Frame>();
	private boolean[] _bodyArea;
	private int _bodyWidth;
	private int _bodyHeight;
	private boolean _hasShadow;
	private int _shadowDisplacementY;
	private HashMap<String, FrameSequence> _animationFrames;
	private TreeSet<String> _states = new TreeSet<String>();
	private LinkedList<Listener> _listeners = new LinkedList<Listener>();
	private LinkedList<Listener> _tempListeners = new LinkedList<Listener>();
	private float _z;

	protected void initialize(boolean[] bodyArea, int bodyWidth, HashMap<String, FrameSequence> animationFrames,
	                          TiledStage.Coordinate origin) {
		if (bodyArea.length % bodyWidth != 0)
			throw new IllegalArgumentException("Length of 'Body Area' should be a multiple of 'Body Width'!");

		_z = 0;
		_hasShadow = false;
		_shadowDisplacementY = 0;
		_bodyArea = bodyArea;
		_bodyWidth = bodyWidth;
		_animationFrames = animationFrames;
		_bodyHeight = bodyArea.length / bodyWidth;

		setOrigin(origin);
		Vector2 pos = _origin.position();
		setPosition(pos.x, pos.y);
		addState(STATE_DEFAULT);
	}

	@Override
	public void reset() {
		_states.clear();
		_listeners.clear();
	}

	@Override
	public boolean remove() {
		if (_origin == null) return false;

		stage().removeBody(this);

		for (TiledStage.Coordinate coordinate : _bodyCoordinates) {
			coordinate.remove(this);
		}

		for (Listener listener : _listeners) {
			listener.removed();
		}

		Pools.free(this);
		_origin = null;

		return super.remove();
	}

	public void act() {
		for (String state : _states) {
			FrameSequence frames = _animationFrames.get(state);
			if (frames != null) frames.update(stage().tickDuration());
		}
	}

	protected void setOrigin(TiledStage.Coordinate origin) {
		for (TiledStage.Coordinate coordinate : _bodyCoordinates) {
			coordinate.remove(this);
		}
		_origin = origin;
		_bodyCoordinates = getBodyCoordinates(_origin);
		for (TiledStage.Coordinate coordinate : _bodyCoordinates) {
			coordinate.add(this);
		}
	}

	// get/set
	// ---------

	public TiledStage.Coordinate origin() {
		return _origin;
	}

	public Vector2 center() {
		return new Vector2(getX() + (float) _bodyWidth / 2 * stage().tileWidth(), getY() + (float) _bodyHeight / 2 * stage().tileHeight());
	}

	public TiledStage.Coordinate topLeftBodyCoordinate() {
		TiledStage.Coordinate topLeft = null;
		for (TiledStage.Coordinate bodyCoordinate : _bodyCoordinates) {
			if (topLeft == null || bodyCoordinate.row() > topLeft.row()) { // body coordinate is more top than current topleft coordinate
				topLeft = bodyCoordinate;
			} else if (bodyCoordinate.row() == topLeft.row() && // body coordinate is on the same row than current topleft coordinate
					bodyCoordinate.column() < topLeft.column()) { // but it is more left than current topleft coordinate
				topLeft = bodyCoordinate;
			}
		}

		if (topLeft == null) topLeft = _origin;

		return topLeft;
	}

	public LinkedList<TiledStage.Coordinate> bodyCoordinates() {
		return _bodyCoordinates;
	}

	public LinkedList<TiledStage.Coordinate> getBodyCoordinates(TiledStage.Coordinate origin) {
		LinkedList<TiledStage.Coordinate> tempBodyCoordinates = new LinkedList<TiledStage.Coordinate>();

		for (int i = 0; i < _bodyArea.length; i++) {
			if (_bodyArea[i]) {
				int tileRow = _bodyHeight - 1 - Math.floorDiv(i, _bodyWidth) + origin.row();
				int tileCol = (i % _bodyWidth) + origin.column();
				TiledStage.Coordinate coordinate = stage().getCoordinate(tileRow, tileCol);
				if (coordinate != null) tempBodyCoordinates.add(coordinate);
			}
		}

		return tempBodyCoordinates;
	}

	public LinkedList<Frame> frames() {
		_tempFrames.clear();

		for (String name : _states) {
			FrameSequence frames = _animationFrames.get(name);
			if (frames != null) _tempFrames.add(frames.frame());
		}

		return _tempFrames;
	}

	public boolean hasState(String state) {
		return _states.contains(state);
	}

	public void addState(String state) {
		if (!_states.contains(state)) {
			_states.add(state);
			FrameSequence frames = _animationFrames.get(state);
			if (frames != null) frames.reset();

			for (Listener listener : _listeners) {
				listener.stateAdded(state);
			}
		}
	}

	public void removeState(String state) {
		if (_states.contains(state)) {
			_states.remove(state);

			for (Listener listener : _listeners) {
				listener.stateRemoved(state);
			}
		}

		if (_states.isEmpty()) {
			_states.add(STATE_DEFAULT);
		}
	}

	public FrameSequence getStateFrames(String state) {
		return _animationFrames.get(state);
	}

	public Listener addListener(Listener listener) {
		_listeners.add(listener);
		return listener;
	}

	public void removeListener(Listener listener) {
		_listeners.remove(listener);
	}

	public LinkedList<Listener> listeners() {
		_tempListeners.clear();
		_tempListeners.addAll(_listeners);
		return _tempListeners;
	}

	public int bodyWidth() {
		return _bodyWidth;
	}

	public boolean[] bodyArea() {
		return _bodyArea;
	}

	public int bodyHeight() {
		return _bodyHeight;
	}

	public float getZ() {
		return _z;
	}

	public void setZ(float z) {
		_z = z;
	}

	public void setHasShadow(boolean hasShadow) {
		_hasShadow = hasShadow;
	}

	public boolean hasShadow() {
		return _hasShadow;
	}

	public int shadowDisplacementY() {
		return _shadowDisplacementY;
	}

	public void setShadowDisplacementY(int displacementY) {
		_shadowDisplacementY = displacementY;
	}

	public TiledStage stage() {
		return origin().stage();
	}

	public float ticksToTime(int ticks) {
		return ticks * stage().tickDuration();
	}

	@Override
	public int compareTo(TiledStageBody body) {
		TiledStage.Coordinate topLeft = topLeftBodyCoordinate();
		TiledStage.Coordinate actorTopLeft = body.topLeftBodyCoordinate();

		if (topLeft.row() > actorTopLeft.row()) {
			return -1;
		} else if (topLeft.row() < actorTopLeft.row()) {
			return 1;
		} else {
			if (topLeft.column() < actorTopLeft.column()) {
				return -1;
			} else if (topLeft.column() > actorTopLeft.column()) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	@Override
	public void positionChanged() {
		for (Listener listener : _listeners) {
			listener.positionChanged(getX(), getY());
		}
	}

	public interface FrameSequenceListener {
		void ended(); // frame sequence just finished playing (will continue in loop)
	}

	public abstract static class Listener {
		public void stateAdded(String state) {
		}

		public void stateRemoved(String state) {
		}

		public void removed() {
		}

		public void positionChanged(float x, float y) {
		}
	}

	public static class Frame {
		private TextureRegion _textureRegion;
		private float _duration;
		private int _renderDepth;

		public Frame(TextureRegion textureRegion, float duration, int renderDepth) {
			_textureRegion = textureRegion;
			_duration = duration;
			_renderDepth = renderDepth;
		}

		public TextureRegion getTextureRegionAt(int left, int top, int right, int bottom) {
			return new TextureRegion(_textureRegion, left, top, right - left, bottom - top);
		}

		public int height() {
			return _textureRegion.getRegionHeight();
		}

		public int width() {
			return _textureRegion.getRegionWidth();
		}

		public int renderDepth() {
			return _renderDepth;
		}

		public float duration() {
			return _duration;
		}
	}

	public static class FrameSequence {
		private float _time;
		private ArrayList<Frame> _frames;
		private int _frameIndex;
		private float _duration;
		private FrameSequenceListener _listener;

		public FrameSequence(ArrayList<Frame> frames) {
			_time = 0f;
			_frameIndex = 0;
			_frames = frames;

			_duration = 0f;
			for (Frame frame : _frames) {
				_duration += frame._duration;
			}
		}

		public FrameSequenceListener listener() {
			return _listener;
		}

		public void setListener(FrameSequenceListener listener) {
			_listener = listener;
		}

		public void removeListener() {
			_listener = null;
		}

		public void update(float timeDelta) {
			_time += timeDelta;
			while (_time > _frames.get(_frameIndex)._duration) {
				_time -= _frames.get(_frameIndex)._duration;

				if (_frameIndex < _frames.size() - 1) {
					_frameIndex++;
				} else {
					if (_listener != null) _listener.ended();
					_frameIndex = 0;
				}
			}
		}

		public float duration() {
			return _duration;
		}

		public Frame frame() {
			return _frames.get(_frameIndex);
		}

		public void reset() {
			_time = 0f;
			_frameIndex = 0;
		}
	}
}
