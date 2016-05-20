package com.somethingyellow.tiled;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

import java.util.LinkedList;

public abstract class TiledStageActor extends Actor {
	public static final float MOVE_SPEED = 10f;
	public static BodyArea Body1x1 = new BodyArea(new boolean[]{
			true
	}, 1);
	public static BodyArea Body2x2 = new BodyArea(new boolean[]{
			true, true,
			true, true
	}, 2);
	private TiledStage.Coordinate _origin;
	private Sprite _sprite;
	private TiledStage _stage;
	private int _type;
	private BodyArea _body;
	private boolean _isMoving = false;
	private int _momentumY = 0;
	private int _momentumX = 0;

	@Override
	public void act(float delta) {
		super.act(delta);

		move();
	}

	public void create(TiledStage stage, TiledStage.Coordinate origin, BodyArea body, Sprite sprite, int type) {
		_stage = stage;
		_type = type;
		_body = body;
		_sprite = sprite;
		_origin = origin;

		setOrigin(_origin);
		Vector2 pos = _origin.position();
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
		TiledStage.Coordinate targetCoordinate = origin().getAdjacentCoordinate(direction);
		if (targetCoordinate == null) return false;

		// Check if all coordinates of body can move to their direction
		LinkedList<TiledStage.Coordinate> coordinates = _body.getCoordinates(_stage, targetCoordinate);
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
	@Override
	public void draw(Batch batch, float parentAlpha) {
		_sprite.setPosition(getX(), getY());
		_sprite.draw(batch);
	}

	public TiledStage.Coordinate origin() {
		return _origin;
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

	public LinkedList<TiledStage.Coordinate> bodyCoordinates() {
		return _body.getCoordinates(_stage, _origin);
	}

	public TiledStage getStage() {
		return _stage;
	}

	@Override
	public String toString() {
		return getClass().toString();
	}

	public static class BodyArea {
		private boolean[] _area;
		private int _width;
		private int _height;

		public BodyArea(boolean[] area, int width) {
			if (area.length % width != 0)
				throw new IllegalArgumentException("area's length must be a multiple of width!");

			_area = area;
			_width = width;
			_height = _area.length / _width;
		}

		private static int GetOffsetX(int index, int width) {
			return (index % width);
		}

		private static int GetOffsetY(int index, int width) {
			return Math.floorDiv(index, width);
		}

		public boolean[] area() {
			return _area;
		}

		public int width() {
			return _width;
		}

		public LinkedList<TiledStage.Coordinate> getCoordinates(TiledStage stage, TiledStage.Coordinate origin) {
			LinkedList<TiledStage.Coordinate> coordinates = new LinkedList<TiledStage.Coordinate>();

			for (int i = 0; i < _area.length; i++) {
				if (_area[i])
					coordinates.add(stage.getCoordinate(_height - 1 + origin.row() - GetOffsetY(i, _width), origin.column() + GetOffsetX(i, _width)));
			}

			return coordinates;
		}
	}
}
