package com.somethingyellow.tiled;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Disposable;

public class TiledStageActor extends Actor implements Disposable {

	private TiledStage.Coordinate _coordinate;
	private TiledStage _stage;
	private boolean _isMoving = false;

	public TiledStageActor() {
		super();
	}

	@Override
	public void act(float delta) {
		super.act(delta);
	}

	public void create(TiledStage stage, TiledStage.Coordinate coordinate) {
		_stage = stage;
		_coordinate = coordinate;

		Vector2 pos = _coordinate.position();
		setPosition(pos.x, pos.y);
	}

	protected boolean canMoveTo(TiledStage.Coordinate coordinate) {
		return true;
	}

	public boolean tryMoveTo(TiledStage.Coordinate coordinate, float speed) {
		if (_isMoving) return false;
		if (coordinate.row() >= _stage.tileRows() || coordinate.column() >= _stage.tileColumns() ||
				coordinate.row() < 0 || coordinate.column() < 0) return false;
		if (!canMoveTo(coordinate)) return false;

		Vector2 pos = coordinate.position();

		_isMoving = true;
		addAction(Actions.sequence(
				Actions.moveTo(pos.x, pos.y, 1 / speed),
				Actions.run(new Runnable() {
					@Override
					public void run() {
						_isMoving = false;
					}
				})));

		setCoordinate(coordinate);
		_stage.moveActor(this, coordinate);

		return true;
	}

	public void destroy() {
	}

	@Override
	public void dispose() {
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

	@Override
	public TiledStage getStage() {
		return _stage;
	}
}
