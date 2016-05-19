package com.somethingyellow.tiled;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Disposable;

public abstract class TiledStageActor extends Actor implements Disposable {
	public static final float MOVE_SPEED = 10f;

	private TiledStage.Coordinate _coordinate;
	private TiledStage _stage;
	private int _type;

	private boolean _isMoving = false;
	private int _momentumY = 0;
	private int _momentumX = 0;

	public TiledStageActor() {
		super();
	}

	@Override
	public void act(float delta) {
		super.act(delta);

		move();
	}

	public void create(TiledStage stage, TiledStage.Coordinate coordinate, int type) {
		_stage = stage;
		_coordinate = coordinate;
		_type = type;

		Vector2 pos = _coordinate.position();
		setPosition(pos.x, pos.y);
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
		TiledStage.Coordinate targetCoordinate = coordinate().getAdjacentCoordinate(direction);
		if (targetCoordinate == null) return false;
		if (!canMove(targetCoordinate, direction)) return false;

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

		setCoordinate(targetCoordinate);
		_stage.moveActor(this, targetCoordinate);

		return true;
	}

	public void destroy() {
	}

	@Override
	public void dispose() {
	}

	public TiledStageActor addMomentumY(int offset) {
		_momentumY += offset;
		return this;
	}

	public TiledStageActor addMomentumX(int offset) {
		_momentumX += offset;
		return this;
	}

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

	// get/set
	// ---------

	public TiledStage.Coordinate coordinate() {
		return _coordinate;
	}

	protected TiledStageActor setCoordinate(TiledStage.Coordinate coordinate) {
		_coordinate = coordinate;
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

	@Override
	public TiledStage getStage() {
		return _stage;
	}
}
