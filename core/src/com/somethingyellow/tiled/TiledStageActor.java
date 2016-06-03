package com.somethingyellow.tiled;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Pool;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeSet;

public abstract class TiledStageActor extends Actor implements Comparable<TiledStageActor>, Pool.Poolable {
	public static final boolean[] BodyArea1x1 = new boolean[]{
			true
	};
	public static final int[] SUBTICKS = new int[]{0};

	private TiledStage.Coordinate _origin;
	private LinkedList<TiledStage.Coordinate> _bodyCoordinates;
	private int _actorDepth;
	private TiledStage _stage;
	private int _movingTicks;
	private boolean[] _bodyArea;
	private int _bodyWidth;
	private int _bodyHeight;
	private HashMap<String, FrameSequence> _animationFrames;
	private TreeSet<String> _states = new TreeSet<String>();
	private LinkedList<StateListener> _stateListeners = new LinkedList<StateListener>();

	protected void initialize(TiledStage stage, boolean[] bodyArea, int bodyWidth, HashMap<String, FrameSequence> animationFrames,
	                          TiledStage.Coordinate origin, int actorDepth) {
		if (bodyArea.length % bodyWidth != 0)
			throw new IllegalArgumentException("Length of 'Body Area' should be a multiple of 'Body Width'!");

		_stage = stage;
		_movingTicks = 0;
		_bodyArea = bodyArea;
		_bodyWidth = bodyWidth;
		_animationFrames = animationFrames;
		_bodyHeight = bodyArea.length / bodyWidth;
		_actorDepth = actorDepth;

		setOrigin(origin);
		for (TiledStage.Coordinate coordinate : _bodyCoordinates) {
			_stage.addActor(this, coordinate);
		}
		_stage.addActor(this);
		Vector2 pos = _origin.position();
		setPosition(pos.x, pos.y);
	}

	@Override
	public void reset() {
		_states.clear();
		_stateListeners.clear();
	}

	@Override
	public boolean remove() {
		for (TiledStage.Coordinate coordinate : _bodyCoordinates) {
			_stage.removeActor(this, coordinate);
		}

		return super.remove();
	}

	public void act() {
		if (_movingTicks > 0) {
			_movingTicks--;
		}
	}

	public abstract void act(int subtick);
	public abstract boolean bodyCanBeAt(TiledStage.Coordinate coordinate);

	public abstract int[] subticks();

	@Override
	public void act(float delta) {
		super.act(delta);
		for (String state : _states) {
			FrameSequence frames = _animationFrames.get(state);
			if (frames != null) frames.update(delta);
		}
	}

	public boolean canBeAt(TiledStage.Coordinate origin) {
		// Check if all coordinates of body can move to their direction
		LinkedList<TiledStage.Coordinate> targetCoordinates = getBodyCoordinates(origin);
		for (TiledStage.Coordinate bodyCoordinate : targetCoordinates) {
			if (!bodyCanBeAt(bodyCoordinate)) return false;
		}

		return true;
	}

	protected void moveToInstantly(TiledStage.Coordinate targetCoordinate) {
		for (TiledStage.Coordinate coordinate : _bodyCoordinates) {
			_stage.removeActor(this, coordinate);
		}
		setOrigin(targetCoordinate);
		for (TiledStage.Coordinate coordinate : _bodyCoordinates) {
			_stage.addActor(this, coordinate);
		}
	}

	protected void moveTo(TiledStage.Coordinate targetCoordinate, int ticks) {
		_movingTicks = ticks;
		moveToInstantly(targetCoordinate);
		Vector2 pos = targetCoordinate.position();
		addAction(Actions.moveTo(pos.x, pos.y, ticksToTime(ticks)));
	}

	private void setOrigin(TiledStage.Coordinate origin) {
		_origin = origin;
		_bodyCoordinates = getBodyCoordinates(_origin);
	}

	protected boolean moveDirection(TiledStage.DIRECTION direction, int ticks) {
		if (_movingTicks > 0) return false;

		TiledStage.Coordinate checkCoordinate;

		int unitRow = TiledStage.GetUnitRow(direction);
		int unitCol = TiledStage.GetUnitColumn(direction);

		if (unitRow != 0) {
			checkCoordinate = _stage.getCoordinate(_origin.row() + unitRow, _origin.column());
			if (checkCoordinate == null || !canBeAt(checkCoordinate)) {
				return false;
			}
		}

		if (unitCol != 0) {
			checkCoordinate = _stage.getCoordinate(_origin.row(), _origin.column() + unitCol);
			if (checkCoordinate == null || !canBeAt(checkCoordinate)) {
				return false;
			}
		}

		// Checking diagonal coordinate
		if (unitRow != 0 && unitCol != 0) {
			checkCoordinate = _stage.getCoordinate(_origin.row() + unitRow, _origin.column() + unitCol);
			if (checkCoordinate == null || !canBeAt(checkCoordinate)) {
				return false;
			}
		}

		moveTo(_stage.getCoordinate(_origin.row() + unitRow, _origin.column() + unitCol), ticks);
		return true;
	}

	// events
	// -------

	// get/set
	// ---------

	public TiledStage.Coordinate origin() {
		return _origin;
	}

	public Vector2 center() {
		return new Vector2(position().x + (float) _bodyWidth / 2 * _stage.tileWidth(), position().y + (float) _bodyHeight / 2 * _stage.tileHeight());
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
		LinkedList<TiledStage.Coordinate> coordinates = new LinkedList<TiledStage.Coordinate>();

		for (int i = 0; i < _bodyArea.length; i++) {
			if (_bodyArea[i]) {
				int tileRow = _bodyHeight - 1 - Math.floorDiv(i, _bodyWidth) + origin.row();
				int tileCol = (i % _bodyWidth) + origin.column();
				TiledStage.Coordinate coordinate = _stage.getCoordinate(tileRow, tileCol);
				if (coordinate != null) coordinates.add(coordinate);
			}
		}

		return coordinates;
	}

	public LinkedList<TextureRegion> textureRegions() {
		ArrayList<FrameSequence> frameSequenceList = new ArrayList<FrameSequence>(_states.size());

		for (String name : _states) {
			FrameSequence frames = _animationFrames.get(name);
			if (frames != null) frameSequenceList.add(frames);
		}

		frameSequenceList.sort(new Comparator<FrameSequence>() {
			@Override
			public int compare(FrameSequence frameSequence1, FrameSequence frameSequence2) {
				return frameSequence1._renderDepth - frameSequence2._renderDepth;
			}
		});

		LinkedList<TextureRegion> textureRegions = new LinkedList<TextureRegion>();
		for (FrameSequence frameSequence : frameSequenceList) {
			textureRegions.add(frameSequence.textureRegion());
		}

		return textureRegions;
	}

	public boolean hasState(String state) {
		return _states.contains(state);
	}

	public TiledStageActor addState(String state) {
		if (!_states.contains(state)) {
			_states.add(state);
			FrameSequence frames = _animationFrames.get(state);
			if (frames != null) frames.reset();

			for (StateListener listener : _stateListeners) {
				listener.added(state);
			}
		}

		return this;
	}

	public TiledStageActor removeState(String state) {
		if (_states.contains(state)) {
			_states.remove(state);

			for (StateListener listener : _stateListeners) {
				listener.removed(state);
			}
		}

		return this;
	}

	public FrameSequence getStateFrames(String state) {
		return _animationFrames.get(state);
	}

	public TiledStageActor addStateListener(StateListener listener) {
		_stateListeners.add(listener);
		return this;
	}

	public Vector2 position() {
		return new Vector2(getX(), getY());
	}

	public boolean isMoving() {
		return _movingTicks > 0;
	}

	public int actorDepth() {
		return _actorDepth;
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

	@Override
	public TiledStage getStage() {
		return _stage;
	}

	public float ticksToTime(int ticks) {
		return ticks * _stage.tickDuration();
	}

	@Override
	public int compareTo(TiledStageActor actor) {
		TiledStage.Coordinate topLeft = topLeftBodyCoordinate();
		TiledStage.Coordinate actorTopLeft = actor.topLeftBodyCoordinate();

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

	public interface StateListener {
		void added(String state);
		void removed(String state);
	}

	public interface FrameSequenceListener {
		void ended(); // frame sequence just finished playing (will continue in loop)
	}

	public static class Frame {
		private TextureRegion _textureRegion;
		private float _duration;

		public Frame(TextureRegion textureRegion, float duration) {
			_textureRegion = textureRegion;
			_duration = duration;
		}

		public TextureRegion textureRegion() {
			return _textureRegion;
		}

		public float duration() {
			return _duration;
		}
	}

	public static class FrameSequence {
		private float _time;
		private ArrayList<Frame> _frames;
		private int _frameIndex;
		private int _renderDepth;
		private float _duration;
		private FrameSequenceListener _listener;

		public FrameSequence(ArrayList<Frame> frames, int renderDepth) {
			_time = 0f;
			_frameIndex = 0;
			_frames = frames;
			_renderDepth = renderDepth;

			_duration = 0f;
			for (Frame frame : _frames) {
				_duration += frame._duration;
			}
		}

		public static ArrayList<Frame> TileToFrames(TiledMapTile tile, float defaultFrameDuration) {
			ArrayList<Frame> frames;

			if (tile instanceof AnimatedTiledMapTile) {
				AnimatedTiledMapTile animatedTile = (AnimatedTiledMapTile) tile;
				frames = new ArrayList<Frame>(animatedTile.getFrameTiles().length);
				int[] intervals = animatedTile.getAnimationIntervals();
				StaticTiledMapTile[] staticTiles = animatedTile.getFrameTiles();

				for (int i = 0; i < staticTiles.length; i++) {
					frames.add(i, new Frame(staticTiles[i].getTextureRegion(), (float) intervals[i] / 1000));
				}
			} else if (tile instanceof StaticTiledMapTile) {
				frames = new ArrayList<Frame>(1);
				frames.add(new Frame(tile.getTextureRegion(), defaultFrameDuration));
			} else {
				frames = new ArrayList<Frame>();
			}

			return frames;
		}

		public void setListener(FrameSequenceListener listener) {
			_listener = listener;
		}

		public FrameSequence update(float timeDelta) {
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

			return this;
		}

		public float duration() {
			return _duration;
		}

		public TextureRegion textureRegion() {
			return _frames.get(_frameIndex)._textureRegion;
		}

		public FrameSequence reset() {
			_time = 0f;
			_frameIndex = 0;
			return this;
		}
	}
}
