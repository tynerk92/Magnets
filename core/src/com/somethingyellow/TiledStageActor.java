package com.somethingyellow;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Disposable;

public class TiledStageActor extends Actor implements Disposable, EventListener {

	private static final Vector2 TempCoords = new Vector2();
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

	public boolean isMoving() {
		return _isMoving;
	}

	@Override
	public TiledStage getStage() {
		return _stage;
	}

	// event listener
	// ---------------
	@Override
	public boolean handle(Event event) {
		if (event instanceof InputEvent) {
			InputEvent e = (InputEvent) event;
			switch (e.getType()) {
				case keyDown:
					return keyDown(e, e.getKeyCode());
				case keyUp:
					return keyUp(e, e.getKeyCode());
				case keyTyped:
					return keyTyped(e, e.getCharacter());
			}

			e.toCoordinates(event.getListenerActor(), TempCoords);

			switch (e.getType()) {
				case touchDown:
					return touchDown(e, TempCoords.x, TempCoords.y, e.getPointer(), e.getButton());
				case touchUp:
					touchUp(e, TempCoords.x, TempCoords.y, e.getPointer(), e.getButton());
					return true;
				case touchDragged:
					touchDragged(e, TempCoords.x, TempCoords.y, e.getPointer());
					return true;
				case mouseMoved:
					return mouseMoved(e, TempCoords.x, TempCoords.y);
				case scrolled:
					return scrolled(e, TempCoords.x, TempCoords.y, e.getScrollAmount());
				case enter:
					enter(e, TempCoords.x, TempCoords.y, e.getPointer(), e.getRelatedActor());
					return false;
				case exit:
					exit(e, TempCoords.x, TempCoords.y, e.getPointer(), e.getRelatedActor());
					return false;
			}

		}

		return false;
	}

	public boolean keyDown(InputEvent event, int keycode) {
		return false;
	}

	public boolean keyUp(InputEvent event, int keycode) {
		return false;
	}

	public boolean keyTyped(InputEvent event, int keycode) {
		return false;
	}

	public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
		return false;
	}

	public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
	}

	public void touchDragged(InputEvent event, float x, float y, int pointer) {
	}

	public boolean mouseMoved(InputEvent event, float x, float y) {
		return false;
	}

	public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
	}

	public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
	}

	public boolean scrolled(InputEvent event, float x, float y, int amount) {
		return false;
	}
}
