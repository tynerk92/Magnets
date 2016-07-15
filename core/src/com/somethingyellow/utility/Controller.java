package com.somethingyellow.utility;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;

/**
 * Convenience class which implements InputProcessor to process and abstract input events to methods
 */

public class Controller implements InputProcessor {
	private static final Vector2 TempCoords = new Vector2();
	private boolean[] _keysHeld = new boolean[256];
	private boolean[] _keysPressed = new boolean[256];
	private float _zoom;
	private ObjectSet<Listener> _listeners = new ObjectSet<Listener>();

	public Controller() {
		_zoom = Config.ZoomDefault;
	}

	public float zoom() {
		return _zoom;
	}

	public boolean isKeyLeftHeld() {
		return _keysHeld[Input.Keys.LEFT] || _keysHeld[Input.Keys.A];
	}

	public boolean isKeyRightHeld() {
		return _keysHeld[Input.Keys.RIGHT] || _keysHeld[Input.Keys.D];
	}

	public boolean isKeyUpHeld() {
		return _keysHeld[Input.Keys.UP] || _keysHeld[Input.Keys.W];
	}

	public boolean isKeyDownHeld() {
		return _keysHeld[Input.Keys.DOWN] || _keysHeld[Input.Keys.S];
	}

	public boolean wasKeyLeftPressed() {
		return _keysPressed[Input.Keys.LEFT] || _keysPressed[Input.Keys.A];
	}

	public boolean wasKeyRightPressed() {
		return _keysPressed[Input.Keys.RIGHT] || _keysPressed[Input.Keys.D];
	}

	public boolean wasKeyUpPressed() {
		return _keysPressed[Input.Keys.UP] || _keysPressed[Input.Keys.W];
	}

	public boolean wasKeyDownPressed() {
		return _keysPressed[Input.Keys.DOWN] || _keysPressed[Input.Keys.S];
	}

	public boolean isKeyCtrlHeld() {
		return _keysHeld[Input.Keys.CONTROL_LEFT] || _keysHeld[Input.Keys.CONTROL_RIGHT];
	}

	public boolean isKeyHeld(int keycode) {
		return _keysHeld[keycode];
	}

	public boolean wasKeyPressed(int keycode) {
		return _keysPressed[keycode];
	}

	/**
	 * Clears keys that are considered "was pressed"
	 */
	public void clearKeysPressed() {
		for (int i = 0; i < _keysPressed.length; i++) {
			_keysPressed[i] = false;
		}
	}

	@Override
	public boolean keyDown(int keycode) {
		_keysHeld[keycode] = true;
		_keysPressed[keycode] = true;

		for (Listener listener : _listeners) {
			listener.keyPressed(this, keycode);
		}

		switch (keycode) {
			case Input.Keys.LEFT:
			case Input.Keys.A:
				for (Listener listener : _listeners) {
					listener.keyLeftPressed(this);
				}
				break;
			case Input.Keys.RIGHT:
			case Input.Keys.D:
				for (Listener listener : _listeners) {
					listener.keyRightPressed(this);
				}
				break;
			case Input.Keys.UP:
			case Input.Keys.W:
				for (Listener listener : _listeners) {
					listener.keyUpPressed(this);
				}
				break;
			case Input.Keys.DOWN:
			case Input.Keys.S:
				for (Listener listener : _listeners) {
					listener.keyDownPressed(this);
				}
				break;
			case Input.Keys.ESCAPE:
				for (Listener listener : _listeners) {
					listener.keyEscapePressed(this);
				}
				break;
		}

		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		_keysHeld[keycode] = false;
		return true;
	}

	public ObjectSet<Listener> listeners() {
		return _listeners;
	}

	public void reset() {
		for (int i = 0; i < _keysHeld.length; i++) {
			_keysHeld[i] = false;
		}
		_zoom = Config.ZoomDefault;
	}

	public void setZoom(float zoom) {
		_zoom = zoom;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		_zoom = _zoom + (float) amount / 10;
		for (Listener listener : _listeners) {
			listener.zoomed(this, _zoom);
		}
		return true;
	}

	public abstract static class Listener {
		public void zoomed(Controller controller, float zoom) {
		}

		public void keyUpPressed(Controller controller) {
		}

		public void keyDownPressed(Controller controller) {
		}

		public void keyLeftPressed(Controller controller) {
		}

		public void keyRightPressed(Controller controller) {
		}

		public void keyEscapePressed(Controller controller) {
		}

		public void keyPressed(Controller controller, int keycode) {
		}
	}

	public static class Config {
		public static float ZoomDefault = 1f;
	}
}
