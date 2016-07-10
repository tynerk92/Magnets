package com.somethingyellow;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.somethingyellow.utility.ObjectList;

public class Controller implements InputProcessor {
	private static final Vector2 TempCoords = new Vector2();
	private boolean[] _keysHeld = new boolean[256];
	private float _zoom;
	private ObjectList<Listener> _listeners = new ObjectList<Listener>();

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

	public boolean isKeyCtrlHeld() {
		return _keysHeld[Input.Keys.CONTROL_LEFT] || _keysHeld[Input.Keys.CONTROL_RIGHT];
	}

	public boolean isKeyHeld(int keycode) {
		return _keysHeld[keycode];
	}

	@Override
	public boolean keyDown(int keycode) {
		_keysHeld[keycode] = true;

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

	public ObjectList<Listener> listeners() {
		return _listeners;
	}

	public void reset() {
		for (int i = 0; i < _keysHeld.length; i++) {
			_keysHeld[i] = false;
		}
		_zoom = Config.ZoomDefault;
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
		_zoom = Math.min(Math.max(_zoom + (float) amount / 10, Config.ZoomMin), Config.ZoomMax);
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
		public static float ZoomMin = 0.4f;
		public static float ZoomMax = 2.0f;
		public static float ZoomDefault = 0.75f;
	}
}
