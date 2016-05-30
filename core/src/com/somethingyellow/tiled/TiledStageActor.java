package com.somethingyellow.tiled;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeSet;

public abstract class TiledStageActor extends Actor implements Comparable<TiledStageActor> {
	public static boolean[] BodyArea1x1 = new boolean[]{
			true
	};
	private TiledStage.Coordinate _origin;
	private int _actorDepth;
	private TiledStage _stage;
	private boolean _isMoving = false;
	private boolean[] _bodyArea;
	private int _bodyWidth;
	private int _bodyHeight;
	private HashMap<String, FrameSequence> _animationFrames;
	private TreeSet<String> _states;
	private LinkedList<StateListener> _stateListeners;

	public TiledStageActor(boolean[] bodyArea, int bodyWidth, HashMap<String, FrameSequence> animationFrames,
	                       TiledStage stage, TiledStage.Coordinate origin, int actorDepth) {
		if (bodyArea.length % bodyWidth != 0)
			throw new IllegalArgumentException("Length of 'Body Area' should be a multiple of 'Body Width'!");

		_bodyArea = bodyArea;
		_bodyWidth = bodyWidth;
		_animationFrames = animationFrames;
		_bodyHeight = bodyArea.length / bodyWidth;
		_stage = stage;
		_states = new TreeSet<String>();
		_origin = origin;
		_actorDepth = actorDepth;
		_stateListeners = new LinkedList<StateListener>();

		for (TiledStage.Coordinate coordinate : getBodyCoordinates(_origin)) {
			_stage.addActor(this, coordinate);
		}

		_stage.addActor(this);
		Vector2 pos = _origin.position();
		setPosition(pos.x, pos.y);
	}

	@Override
	public boolean remove() {
		for (TiledStage.Coordinate coordinate : bodyCoordinates()) {
			_stage.removeActor(this, coordinate);
		}

		return super.remove();
	}

	public abstract void act(int tick);

	public abstract int[] SUBTICKS();

	public abstract boolean bodyCanBeAt(TiledStage.Coordinate coordinate);

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

	protected void moveTo(final TiledStage.Coordinate targetCoordinate, final int ticks) {
		final TiledStage.Coordinate origin = origin();
		final TiledStageActor actor = this;

		Vector2 pos = targetCoordinate.position();
		_isMoving = true;

		for (TiledStage.Coordinate coordinate : getBodyCoordinates(origin)) {
			_stage.removeActor(this, coordinate);
		}

		for (TiledStage.Coordinate coordinate : getBodyCoordinates(targetCoordinate)) {
			_stage.addActor(this, coordinate);
		}

		_origin = targetCoordinate;

		addAction(Actions.sequence(
				Actions.moveTo(pos.x, pos.y, ticksToTime(ticks)),
				Actions.run(new Runnable() {
						@Override
						public void run() {
							actor._isMoving = false;
						}
				})
		));
	}

	protected boolean moveDirection(TiledStage.DIRECTION direction, int ticks) {
		if (_isMoving) return false;

		TiledStage.Coordinate checkCoordinate;

		int unitRow = TiledStage.GetUnitRow(direction);
		int unitCol = TiledStage.GetUnitColumn(direction);

		if (unitRow != 0) {
			checkCoordinate = _stage.getCoordinate(_origin.row() + unitRow, _origin.column());
			if (checkCoordinate == null || !canBeAt(checkCoordinate)) {
				unitRow = 0;
			}
		}

		if (unitCol != 0) {
			checkCoordinate = _stage.getCoordinate(_origin.row(), _origin.column() + unitCol);
			if (checkCoordinate == null || !canBeAt(checkCoordinate)) {
				unitCol = 0;
			}
		}

		// Checking diagonal coordinate
		if (unitRow != 0 && unitCol != 0) {
			checkCoordinate = _stage.getCoordinate(_origin.row() + unitRow, _origin.column() + unitCol);
			if (checkCoordinate == null || !canBeAt(checkCoordinate)) {
				unitRow = 0;
				unitCol = 0;
			}
		}

		if (unitRow == 0 && unitCol == 0) return false;
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

	public LinkedList<TiledStage.Coordinate> bodyCoordinates() {
		return getBodyCoordinates(_origin);
	}

	public LinkedList<TextureRegion> textureRegions() {
		ArrayList<FrameSequence> frameSequenceList = new ArrayList<FrameSequence>(_states.size());

		for (String name : _states) {
			FrameSequence frames = _animationFrames.get(name);
			if (frames != null) frameSequenceList.add(frames);
		}

		Collections.sort(frameSequenceList);

		LinkedList<TextureRegion> textureRegions = new LinkedList<TextureRegion>();
		for (FrameSequence frameSequence : frameSequenceList) {
			textureRegions.add(frameSequence.textureRegion());
		}

		return textureRegions;
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
		return _isMoving;
	}

	public TiledStageActor setIsMoving(boolean isMoving) {
		_isMoving = isMoving;
		return this;
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
		return (_actorDepth - actor._actorDepth);
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

	public static class FrameSequence implements Comparable<FrameSequence> {
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

		@Override
		public int compareTo(FrameSequence frameSequence) {
			return (_renderDepth - frameSequence._renderDepth);
		}
	}
}
