package com.somethingyellow.tiled;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.somethingyellow.magnets.MagneticSource;
import com.somethingyellow.magnets.PlayScreen;

import java.util.HashMap;
import java.util.LinkedList;

public abstract class TiledStageActor extends Actor {

	public static final float MOVE_SPEED = 10f;
	public static boolean[] BodyArea1x1 = new boolean[]{
			true
	};
	private TiledStage.Coordinate _origin;
	private TiledStage _stage;
	private int _type;
	private boolean _isMoving = false;
	private int _momentumY = 0;
	private int _momentumX = 0;
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
		_state = "";

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
		move();
	}

	protected boolean canMove(TiledStage.Coordinate coordinate, TiledStage.DIRECTION direction) {
		return true;
	}

	private void move() {
		if (!_isMoving) {
			if (_momentumY != 0) {
				TiledStage.DIRECTION dir = (_momentumY > 0) ? TiledStage.DIRECTION.UP : TiledStage.DIRECTION.DOWN;
				moveDirection(dir, Math.abs(_momentumY) * MOVE_SPEED);
				_momentumY -= Math.signum(_momentumY);

			} else if (_momentumX != 0) {
				TiledStage.DIRECTION dir = (_momentumX > 0) ? TiledStage.DIRECTION.RIGHT : TiledStage.DIRECTION.LEFT;
				moveDirection(dir, Math.abs(_momentumX) * MOVE_SPEED);
				_momentumX -= Math.signum(_momentumX);
			}
		}
	}

	private boolean moveDirection(TiledStage.DIRECTION direction, float speed) {
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
					}
				})));

		setOrigin(targetCoordinate);

		return true;
	}

	public TiledStageActor addMomentumY(int offset) {
		_momentumY += offset;
		return this;
	}

	public TiledStageActor addMomentumX(int offset) {
		_momentumX += offset;
		return this;
	}

	// get/set
	// ---------

	public TiledStageActor addMomentum(TiledStage.DIRECTION direction, int magnitude) {
		switch (direction) {
			case UP:
				_momentumY += magnitude;
				break;
			case DOWN:
				_momentumY -= magnitude;
				break;
			case LEFT:
				_momentumX -= magnitude;
				break;
			case RIGHT:
				_momentumX += magnitude;
				break;
		}

		return this;
	}

	// visual
	// ----------

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

	public int momentumY() {
		return _momentumY;
	}

	public int momentumX() {
		return _momentumX;
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
