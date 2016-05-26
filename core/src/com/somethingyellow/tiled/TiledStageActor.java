package com.somethingyellow.tiled;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
	public static final float TRY_MOVE_DISTANCE = 1.5f;
	public static final float TRY_MOVE_DURATION = 0.1f;
	public static boolean[] BodyArea1x1 = new boolean[]{
			true
	};
	private TiledStage.Coordinate _origin;
	private int _actorDepth;
	private TiledStage _stage;
	private int _type;
	private boolean _isMoving = false;
	private boolean[] _bodyArea;
	private int _bodyWidth;
	private int _bodyHeight;
	private HashMap<String, FrameSequence> _animationFrames;
	private TreeSet<String> _states;

	public TiledStageActor(int type, boolean[] bodyArea, int bodyWidth, HashMap<String, FrameSequence> animationFrames,
	                       TiledStage stage, TiledStage.Coordinate origin, int actorDepth) {
		if (bodyArea.length % bodyWidth != 0)
			throw new IllegalArgumentException("Length of 'Body Area' should be a multiple of 'Body Width'!");

		_type = type;
		_bodyArea = bodyArea;
		_bodyWidth = bodyWidth;
		_animationFrames = animationFrames;
		_bodyHeight = bodyArea.length / bodyWidth;
		_stage = stage;
		_states = new TreeSet<String>();
		_origin = origin;
		_actorDepth = actorDepth;

		for (TiledStage.Coordinate coordinate : getBodyCoordinates(_origin)) {
			coordinate.addActor(this);
		}

		_stage.addActor(this);
		Vector2 pos = _origin.position();
		setPosition(pos.x, pos.y);
	}

	@Override
	public boolean remove() {
		for (TiledStage.Coordinate coordinate : bodyCoordinates()) {
			coordinate.removeActor(this);
		}

		_stage.removeActor(this);
		return super.remove();
	}

	public void act(int tick) {
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		for (String state : _states) {
			_animationFrames.get(state).update(delta);
		}
	}

	protected boolean bodyCanBeAt(TiledStage.Coordinate coordinate) {
		return true;
	}

	public boolean canBeAt(TiledStage.Coordinate origin) {
		// Check if all coordinates of body can move to their direction
		LinkedList<TiledStage.Coordinate> targetCoordinates = getBodyCoordinates(origin);
		for (TiledStage.Coordinate bodyCoordinate : targetCoordinates) {
			if (!bodyCanBeAt(bodyCoordinate)) return false;
		}

		return true;
	}

	protected void moveTo(final TiledStage.Coordinate targetCoordinate, final float duration) {
		if (targetCoordinate == null) return;

		final TiledStage.Coordinate origin = origin();
		if (origin == targetCoordinate) return;

		final TiledStageActor actor = this;

		final Vector2 pos = targetCoordinate.position();
		final float distance = origin.position().dst(pos);
		Vector2 tryToMovePos = origin.position().lerp(pos, TRY_MOVE_DISTANCE / distance);

		if (canBeAt(targetCoordinate)) {
			_isMoving = true;

			for (TiledStage.Coordinate coordinate : getBodyCoordinates(origin)) {
				coordinate.removeActor(actor);
			}
			onMovement(origin, targetCoordinate);
			_origin = targetCoordinate;
			for (TiledStage.Coordinate coordinate : getBodyCoordinates(targetCoordinate)) {
				coordinate.addActor(actor);
			}

			actor.addAction(Actions.sequence(
					Actions.moveTo(pos.x, pos.y, duration),
					Actions.run(new Runnable() {
						@Override
						public void run() {
							actor._isMoving = false;
							onMovementEnd(origin, targetCoordinate);
						}
					})
			));

		} else {
			actor.addAction(Actions.sequence(
					Actions.moveTo(tryToMovePos.x, tryToMovePos.y, TRY_MOVE_DURATION / 2),
					Actions.moveTo(origin().position().x, origin().position().y, TRY_MOVE_DURATION / 2)
			));
		}
	}

	protected boolean moveDirection(TiledStage.DIRECTION direction, float duration) {
		if (_isMoving) return false;
		moveTo(origin().getAdjacentCoordinate(direction), duration);
		return true;
	}

	// events
	// -------
	protected void onMovement(TiledStage.Coordinate origin, TiledStage.Coordinate target) {
	}

	protected void onMovementEnd(TiledStage.Coordinate origin, TiledStage.Coordinate target) {
	}

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
			frameSequenceList.add(_animationFrames.get(name));
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

	public Vector2 position() {
		return new Vector2(getX(), getY());
	}

	public boolean isMoving() {
		return _isMoving;
	}

	public int type() {
		return _type;
	}

	public int actorDepth() {
		return _actorDepth;
	}

	public int bodyWidth() {
		return _bodyWidth;
	}

	public int bodyHeight() {
		return _bodyHeight;
	}

	@Override
	public TiledStage getStage() {
		return _stage;
	}

	public boolean hasState(String state) {
		return _states.contains(state);
	}

	public TiledStageActor addState(String state) {
		_states.add(state);
		_animationFrames.get(state).reset();
		return this;
	}

	public TiledStageActor removeState(String state) {
		_states.remove(state);
		return this;
	}

	@Override
	public String toString() {
		return getClass().toString();
	}

	@Override
	public int compareTo(TiledStageActor actor) {
		return (_actorDepth - actor._actorDepth);
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

		public FrameSequence(ArrayList<Frame> frames, int renderDepth) {
			_time = 0f;
			_frameIndex = 0;
			_frames = frames;
			_renderDepth = renderDepth;
		}

		public static ArrayList<Frame> TileToFrames(TiledMapTile tile) {
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
				frames.add(new Frame(tile.getTextureRegion(), 1f));
			} else {
				frames = new ArrayList<Frame>();
			}

			return frames;
		}

		public FrameSequence update(float timeDelta) {
			_time += timeDelta;
			while (_time > _frames.get(_frameIndex)._duration) {
				_time -= _frames.get(_frameIndex)._duration;
				_frameIndex = (_frameIndex + 1) % _frames.size();
			}

			return this;
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
