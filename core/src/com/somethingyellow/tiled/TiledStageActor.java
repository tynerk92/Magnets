package com.somethingyellow.tiled;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

import java.util.HashMap;
import java.util.LinkedList;

public abstract class TiledStageActor extends Actor {
	public static final String STATE_DEFAULT = "";

	public static boolean[] BodyArea1x1 = new boolean[]{
			true
	};
	private TiledStage.Coordinate _origin;
	private TiledStage _stage;
	private int _type;
	private boolean _isMoving = false;
	private boolean[] _bodyArea;
	private int _bodyWidth;
	private int _bodyHeight;
	private HashMap<String, Animation> _animations;
	private float _animationTime;
	private String _state;

	public TiledStageActor(int type, boolean[] bodyArea, int bodyWidth, HashMap<String, Animation> animations,
	                       TiledStage stage, TiledStage.Coordinate origin) {
		if (bodyArea.length % bodyWidth != 0)
			throw new IllegalArgumentException("Length of 'Body Area' should be a multiple of 'Body Width'!");

		_type = type;
		_bodyArea = bodyArea;
		_bodyWidth = bodyWidth;
		_bodyHeight = bodyArea.length / bodyWidth;
		_animations = animations;
		_stage = stage;
		_origin = origin;
		_animationTime = 0f;
		_state = STATE_DEFAULT;

		_stage.addActor(this);
		setOrigin(_origin);
		Vector2 pos = _origin.position();
		setPosition(pos.x, pos.y);
	}

	@Override
	public boolean remove() {
		for (TiledStage.Coordinate coordinate : bodyCoordinates()) {
			coordinate.removeActor(this);
		}

		return super.remove();
	}

	@Override
	public void act(float delta) {
		super.act(delta);

		_animationTime += delta;
	}

	protected boolean canMove(TiledStage.Coordinate coordinate, TiledStage.DIRECTION direction) {
		return true;
	}

	protected boolean moveDirection(TiledStage.DIRECTION direction, float speed) {
		TiledStage.Coordinate targetCoordinate = origin().getAdjacentCoordinate(direction);
		if (targetCoordinate == null) return false;

		// Check if all coordinates of body can move to their direction
		LinkedList<TiledStage.Coordinate> coordinates = getBodyCoordinates(targetCoordinate);
		for (TiledStage.Coordinate coordinate : coordinates) {
			if (!canMove(coordinate, direction)) return false;
		}

		Vector2 pos = targetCoordinate.position();

		_isMoving = true;
		addAction(Actions.sequence(
				Actions.moveTo(pos.x, pos.y, 1 / speed),
				Actions.run(new Runnable() {
					@Override
					public void run() {
						_isMoving = false;
						onStopMoving();
					}
				})));

		setOrigin(targetCoordinate);

		return true;
	}

	// events
	// -------
	protected void onStopMoving() {
	}

	// get/set
	// ---------

	public TiledStage.Coordinate origin() {
		return _origin;
	}

	public LinkedList<TiledStage.Coordinate> bodyCoordinates() {
		return getBodyCoordinates(_origin);
	}

	// Coordinate on tiledmap to render textureregion for actor
	public TiledStage.Coordinate renderCoordinate() {
		return _stage.getCoordinate(_bodyHeight - 1 + _origin.row(), _origin.column());
	}

	public TextureRegion textureRegion() {
		Animation animation = _animations.get(_state);
		if (animation != null) return animation.getKeyFrame(_animationTime);
		return null;
	}

	public LinkedList<TiledStage.Coordinate> getBodyCoordinates(TiledStage.Coordinate origin) {
		LinkedList<TiledStage.Coordinate> coordinates = new LinkedList<TiledStage.Coordinate>();

		for (int i = 0; i < _bodyArea.length; i++) {
			if (_bodyArea[i]) {
				int tileRow = _bodyHeight - 1 - Math.floorDiv(i, _bodyWidth) + origin.row();
				int tileCol = (i % _bodyWidth) + origin.column();
				coordinates.add(_stage.getCoordinate(tileRow, tileCol));
			}
		}

		return coordinates;
	}

	public TiledStageActor setOrigin(TiledStage.Coordinate origin) {
		for (TiledStage.Coordinate coordinate : bodyCoordinates()) {
			coordinate.removeActor(this);
		}
		_origin = origin;
		for (TiledStage.Coordinate coordinate : bodyCoordinates()) {
			coordinate.addActor(this);
		}

		return this;
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

	public String state() {
		return _state;
	}

	public TiledStageActor setState(String state) {
		_state = state;
		_animationTime = 0f;
		return this;
	}

	@Override
	public String toString() {
		return getClass().toString();
	}
}
