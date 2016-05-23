package com.somethingyellow.tiled;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

import java.util.HashMap;

public class PlayerActor extends TiledStageActor implements EventListener {
	private static final Vector2 TempCoords = new Vector2();
	private boolean _isKeyLeftHeld = false;
	private boolean _isKeyRightHeld = false;
	private boolean _isKeyUpHeld = false;
	private boolean _isKeyDownHeld = false;

	public PlayerActor(int type, boolean[] bodyArea, int bodyWidth, HashMap<String, Frames> animationFrames,
	                   TiledStage stage, String layerName, TiledStage.Coordinate origin, int actorDepth) {
		super(type, bodyArea, bodyWidth, animationFrames, stage, layerName, origin, actorDepth);
		addListener(this);
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

	public boolean keyDown(InputEvent event, int keycode) {
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

	public boolean keyUp(InputEvent event, int keycode) {
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
