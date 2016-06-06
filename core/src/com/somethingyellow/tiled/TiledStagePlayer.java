package com.somethingyellow.tiled;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

import java.util.HashMap;

public abstract class TiledStagePlayer extends TiledStageActor implements EventListener {
	private static final Vector2 TempCoords = new Vector2();
	private boolean _isKeyLeftHeld;
	private boolean _isKeyRightHeld;
	private boolean _isKeyUpHeld;
	private boolean _isKeyDownHeld;

	public TiledStagePlayer() {
		super();
		addListener(this);
	}


	@Override
	public void initialize(boolean[] bodyArea, int bodyWidth, HashMap<String, FrameSequence> animationFrames,
	                       TiledStage.Coordinate origin) {
		super.initialize(bodyArea, bodyWidth, animationFrames, origin);
		_isKeyLeftHeld = false;
		_isKeyRightHeld = false;
		_isKeyUpHeld = false;
		_isKeyDownHeld = false;
	}

	// event listener
	// ---------------

	protected boolean isKeyLeftHeld() {
		return _isKeyLeftHeld;
	}

	protected boolean isKeyRightHeld() {
		return _isKeyRightHeld;
	}

	protected boolean isKeyUpHeld() {
		return _isKeyUpHeld;
	}

	protected boolean isKeyDownHeld() {
		return _isKeyDownHeld;
	}

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

	protected boolean keyDown(InputEvent event, int keycode) {
		switch (keycode) {
			case Input.Keys.LEFT:
				_isKeyLeftHeld = true;
				break;
			case Input.Keys.RIGHT:
				_isKeyRightHeld = true;
				break;
			case Input.Keys.UP:
				_isKeyUpHeld = true;
				break;
			case Input.Keys.DOWN:
				_isKeyDownHeld = true;
				break;
		}

		return true;
	}

	protected boolean keyUp(InputEvent event, int keycode) {
		switch (keycode) {
			case Input.Keys.LEFT:
				_isKeyLeftHeld = false;
				break;
			case Input.Keys.RIGHT:
				_isKeyRightHeld = false;
				break;
			case Input.Keys.UP:
				_isKeyUpHeld = false;
				break;
			case Input.Keys.DOWN:
				_isKeyDownHeld = false;
				break;
		}

		return true;
	}

	protected boolean keyTyped(InputEvent event, int keycode) {
		return false;
	}

	protected boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
		return false;
	}

	protected void touchUp(InputEvent event, float x, float y, int pointer, int button) {
	}

	protected void touchDragged(InputEvent event, float x, float y, int pointer) {
	}

	protected boolean mouseMoved(InputEvent event, float x, float y) {
		return false;
	}

	protected void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
	}

	protected void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
	}

	protected boolean scrolled(InputEvent event, float x, float y, int amount) {
		return false;
	}
}
