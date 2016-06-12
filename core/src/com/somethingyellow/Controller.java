package com.somethingyellow;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.somethingyellow.graphics.Animation;
import com.somethingyellow.graphics.AnimationDef;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Controller implements InputProcessor {
	private static final Vector2 TempCoords = new Vector2();
	private boolean _isKeyLeftHeld = false;
	private boolean _isKeyRightHeld = false;
	private boolean _isKeyUpHeld = false;
	private boolean _isKeyDownHeld = false;
	private float _zoom;
	private Listeners<Listener> _listeners = new Listeners<Listener>();

	public Controller() {
		_zoom = Config.ZoomDefault;
	}

	public float zoom() {
		return _zoom;
	}

	public boolean isKeyLeftHeld() {
		return _isKeyLeftHeld;
	}

	public boolean isKeyRightHeld() {
		return _isKeyRightHeld;
	}

	public boolean isKeyUpHeld() {
		return _isKeyUpHeld;
	}

	public boolean isKeyDownHeld() {
		return _isKeyDownHeld;
	}

	@Override
	public boolean keyDown(int keycode) {
		switch (keycode) {
			case Input.Keys.LEFT:
			case Input.Keys.A:
				_isKeyLeftHeld = true;
				for (Listener listener : _listeners) {
					listener.keyLeftPressed(this);
				}
				break;
			case Input.Keys.RIGHT:
			case Input.Keys.D:
				_isKeyRightHeld = true;
				for (Listener listener : _listeners) {
					listener.keyRightPressed(this);
				}
				break;
			case Input.Keys.UP:
			case Input.Keys.W:
				_isKeyUpHeld = true;
				for (Listener listener : _listeners) {
					listener.keyUpPressed(this);
				}
				break;
			case Input.Keys.DOWN:
			case Input.Keys.S:
				_isKeyDownHeld = true;
				for (Listener listener : _listeners) {
					listener.keyDownPressed(this);
				}
				break;
			case Input.Keys.ESCAPE:
				for (Listener listener : _listeners) {
					listener.keyEscapePressed(this);
				}
				break;
			case Input.Keys.R:
				for (Listener listener : _listeners) {
					listener.keyRPressed(this);
				}
				break;
			case Input.Keys.P:
				for (Listener listener : _listeners) {
					listener.keyPPressed(this);
				}
				break;
			case Input.Keys.BACKSPACE:
				for (Listener listener : _listeners) {
					listener.keyBackSpacePressed(this);
				}
				break;
		}

		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		switch (keycode) {
			case Input.Keys.LEFT:
			case Input.Keys.A:
				_isKeyLeftHeld = false;
				break;
			case Input.Keys.RIGHT:
			case Input.Keys.D:
				_isKeyRightHeld = false;
				break;
			case Input.Keys.UP:
			case Input.Keys.W:
				_isKeyUpHeld = false;
				break;
			case Input.Keys.DOWN:
			case Input.Keys.S:
				_isKeyDownHeld = false;
				break;
		}

		return true;
	}

	public Listeners<Listener> listeners() {
		return _listeners;
	}

	public void reset() {
		_isKeyDownHeld = false;
		_isKeyLeftHeld = false;
		_isKeyRightHeld = false;
		_isKeyUpHeld = false;
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

		public void keyRPressed(Controller controller) {
		}

		public void keyPPressed(Controller controller) {
		}

		public void keyBackSpacePressed(Controller controller) {
		}
	}

	public static class Config {
		public static float ZoomMin = 0.5f;
		public static float ZoomMax = 1.5f;
		public static float ZoomDefault = 0.75f;
	}
}
