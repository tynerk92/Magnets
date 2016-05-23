package com.somethingyellow.tiled;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeSet;

public abstract class TiledStageActor extends Actor implements Comparable<TiledStageActor> {
	public static final float MOVE_DELAY = 0.05f;
	public static boolean[] BodyArea1x1 = new boolean[]{
			true
	};
	private TiledStage.Coordinate _origin;
	private String _layerName;
	private int _actorDepth;
	private TiledStage _stage;
	private int _type;
	private boolean _isMoving = false;
	private boolean[] _bodyArea;
	private int _bodyWidth;
	private int _bodyHeight;
	private HashMap<String, Frames> _animationFrames;
	private TreeSet<String> _states;

	public TiledStageActor(int type, boolean[] bodyArea, int bodyWidth, HashMap<String, Frames> animationFrames,
	                       TiledStage stage, String layerName, TiledStage.Coordinate origin, int actorDepth) {
		if (bodyArea.length % bodyWidth != 0)
			throw new IllegalArgumentException("Length of 'Body Area' should be a multiple of 'Body Width'!");

		_type = type;
		_bodyArea = bodyArea;
		_bodyWidth = bodyWidth;
		_layerName = layerName;
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

	@Override
	public void act(float delta) {
		super.act(delta);

		for (String state : _states) {
			_animationFrames.get(state).update(delta);
		}
	}

	public void preAct() {
	}

	public void postAct() {
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
		_isMoving = true;

		final Vector2 pos = targetCoordinate.position();

		addAction(Actions.sequence(
				Actions.moveTo(origin.position().x + (pos.x - origin.position().x) / 2,
						origin.position().y + (pos.y - origin.position().y) / 2, duration / 2),
				Actions.run(new Runnable() {
					@Override
					public void run() {
						if (canBeAt(targetCoordinate)) {
							for (TiledStage.Coordinate coordinate : getBodyCoordinates(targetCoordinate)) {
								coordinate.addActor(actor);
							}
							_origin = targetCoordinate;
							onMovement(_origin, origin);
							for (TiledStage.Coordinate coordinate : getBodyCoordinates(origin)) {
								coordinate.removeActor(actor);
							}

							actor.addAction(Actions.sequence(
									Actions.moveTo(pos.x, pos.y, duration / 2),
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
									Actions.moveTo(origin.position().x, origin.position().y, duration / 2),
									Actions.run(new Runnable() {
										@Override
										public void run() {
											actor._isMoving = false;
										}
									})
							));
						}
					}
				})));
	}

	protected boolean moveDirection(TiledStage.DIRECTION direction, float duration) {
		if (_isMoving) return false;

		TiledStage.Coordinate origin = origin();
		TiledStage.Coordinate coordinate = origin.getAdjacentCoordinate(direction);
		if (coordinate != null) if (!canBeAt(coordinate)) return false;

		switch (direction) {
			case NORTH_EAST:
			case NORTH_WEST:
				coordinate = origin.getAdjacentCoordinate(TiledStage.DIRECTION.NORTH);
				if (coordinate != null) if (!canBeAt(coordinate)) return false;
				break;
			case SOUTH_EAST:
			case SOUTH_WEST:
				coordinate = origin.getAdjacentCoordinate(TiledStage.DIRECTION.SOUTH);
				if (coordinate != null) if (!canBeAt(coordinate)) return false;
				break;
		}

		switch (direction) {
			case NORTH_EAST:
			case SOUTH_EAST:
				coordinate = origin.getAdjacentCoordinate(TiledStage.DIRECTION.EAST);
				if (coordinate != null) if (!canBeAt(coordinate)) return false;
				break;
			case NORTH_WEST:
			case SOUTH_WEST:
				coordinate = origin.getAdjacentCoordinate(TiledStage.DIRECTION.WEST);
				if (coordinate != null) if (!canBeAt(coordinate)) return false;
				break;
		}

		moveTo(origin.getAdjacentCoordinate(direction), duration - MOVE_DELAY);

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

	public String layerName() {
		return _layerName;
	}

	public LinkedList<TiledStage.Coordinate> bodyCoordinates() {
		return getBodyCoordinates(_origin);
	}

	// Coordinate on tiledmap to render textureregion for actor
	public TiledStage.Coordinate renderCoordinate() {
		return _stage.getCoordinate(_bodyHeight - 1 + _origin.row(), _origin.column());
	}

	public LinkedList<TextureRegion> textureRegions() {
		ArrayList<Frames> framesList = new ArrayList<Frames>(_states.size());

		for (String name : _states) {
			framesList.add(_animationFrames.get(name));
		}

		Collections.sort(framesList);
		Collections.reverse(framesList);

		LinkedList<TextureRegion> textureRegions = new LinkedList<TextureRegion>();
		for (Frames frames : framesList) {
			textureRegions.add(frames.textureRegion());
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

	public static class Frames implements Comparable<Frames> {
		private float _time;
		private float _frameDuration;
		private ArrayList<TextureRegion> _textureRegions;
		private int _renderDepth;

		public Frames(ArrayList<TextureRegion> textureRegions, float frameDuration, int renderDepth) {
			_time = 0f;
			_frameDuration = frameDuration;
			_textureRegions = textureRegions;
			_renderDepth = renderDepth;
		}

		public Frames update(float timeDelta) {
			_time += timeDelta;
			return this;
		}

		public TextureRegion textureRegion() {
			return _textureRegions.get(((int) (_time / _frameDuration)) % _textureRegions.size());
		}

		public Frames reset() {
			_time = 0f;
			return this;
		}

		@Override
		public int compareTo(Frames frames) {
			return (_renderDepth - frames._renderDepth);
		}
	}
}
