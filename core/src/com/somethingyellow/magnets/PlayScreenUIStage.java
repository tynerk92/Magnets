package com.somethingyellow.magnets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Pools;
import com.somethingyellow.graphics.AnimatedActor;
import com.somethingyellow.graphics.AnimationDef;
import com.somethingyellow.utility.Controller;
import com.somethingyellow.utility.TimedTrigger;

import java.util.HashMap;

public class PlayScreenUIStage extends Stage {
	private Controller _controller = new Controller();
	private TiledMap _animationsMap;
	private HashMap<String, AnimationDef> _animationDefs;
	private AnimatedActor _pauseOverlay;
	private Commands _commands;
	private Label _playerMovesLabel;
	private TimedTrigger _undoTimeTrigger;
	private UndoTimeTrigger _undoTimeTriggerTrigger = new UndoTimeTrigger();
	private ControllerListener _controllerListener = new ControllerListener();
	private Skin _skin;

	public PlayScreenUIStage(Skin skin, Commands commands) {
		super();
		_commands = commands;
		_skin = skin;
		_animationsMap = _commands.loadMap(Config.AnimationsTMXPath);
		_animationDefs = _commands.loadAnimations(_animationsMap, 1f);
		_undoTimeTrigger = new TimedTrigger(_undoTimeTriggerTrigger, Config.UndoTriggerTimingChart);

		_pauseOverlay = Pools.get(AnimatedActor.class).obtain();
		_pauseOverlay.initialize(_animationDefs.get(Config.AnimationPauseOverlay));
		_pauseOverlay.setPosition(0, 0);
		addActor(_pauseOverlay);

		// Player moves counter
		_playerMovesLabel = new Label("", _skin);
		addActor(_playerMovesLabel);

		_controller.listeners().add(_controllerListener);
	}

	public void focus() {
		Gdx.input.setInputProcessor(_controller);
	}

	@Override
	public void act(float delta) {
		super.act(delta);

		if (_controller.isKeyHeld(Input.Keys.Z)) {
			_undoTimeTrigger.update(delta / PlayScreen.Config.GameTickDuration);
		} else {
			_undoTimeTrigger.reset();
		}
	}

	@Override
	public void draw() {
		act(Gdx.graphics.getDeltaTime());

		super.draw();
	}

	public void setPlayerMoveCount(int count) {
		_playerMovesLabel.setText(String.valueOf(count));
		_playerMovesLabel.setPosition(Gdx.graphics.getWidth() - _playerMovesLabel.getWidth() - 50, 25);
	}

	public void setIsPaused(boolean isPaused) {
		_pauseOverlay.setIsVisible(isPaused);
	}

	@Override
	public void dispose() {
		super.dispose();

		if (_animationsMap != null) {
			_animationDefs.clear();
			_animationsMap.dispose();
		}
	}

	public float zoom() {
		return _controller.zoom();
	}

	public boolean moveLeftHeld() {
		return _controller.isKeyLeftHeld() && !_controller.isKeyRightHeld() &&
				!_controller.isKeyUpHeld() && !_controller.isKeyDownHeld();
	}

	public boolean moveRightHeld() {
		return _controller.isKeyRightHeld() && !_controller.isKeyLeftHeld() &&
				!_controller.isKeyUpHeld() && !_controller.isKeyDownHeld();
	}

	public boolean moveUpHeld() {
		return _controller.isKeyUpHeld() && !_controller.isKeyLeftHeld() &&
				!_controller.isKeyRightHeld() && !_controller.isKeyDownHeld();
	}

	public boolean moveDownHeld() {
		return _controller.isKeyDownHeld() && !_controller.isKeyLeftHeld() &&
				!_controller.isKeyRightHeld() && !_controller.isKeyUpHeld();
	}

	public interface Commands {
		TiledMap loadMap(String mapFilePath);

		HashMap<String, AnimationDef> loadAnimations(TiledMap map, float defaultDuration);

		void undo();

		void reset();

		void togglePause();

		void exit();

		void setCameraMode(boolean cameraMode);

		void moveUp();

		void moveDown();

		void moveLeft();

		void moveRight();

		float zoom(float zoom);

		void dragged(float displacementX, float displacementY);
	}

	public static class Config {
		public static String AnimationsTMXPath = null;
		public static String AnimationPauseOverlay = null;
		public static int[] UndoTriggerTimingChart = new int[]{1, 10, 3, 3, 3, 3, 3, -1};
	}

	public class ControllerListener extends Controller.Listener {
		@Override
		public void zoomed(Controller controller, float zoom) {
			controller.setZoom(_commands.zoom(zoom));
		}

		@Override
		public void keyUpPressed(Controller controller) {
			super.keyUpPressed(controller);
			_commands.moveUp();
		}

		@Override
		public void keyDownPressed(Controller controller) {
			super.keyDownPressed(controller);
			_commands.moveDown();
		}

		@Override
		public void keyLeftPressed(Controller controller) {
			super.keyLeftPressed(controller);
			_commands.moveLeft();
		}

		@Override
		public void keyRightPressed(Controller controller) {
			super.keyRightPressed(controller);
			_commands.moveRight();
		}

		@Override
		public void keyPressed(Controller controller, int keycode) {
			super.keyPressed(controller, keycode);

			switch (keycode) {
				case Input.Keys.R:
					_commands.reset();
					break;
				case Input.Keys.P:
					_commands.togglePause();
					break;
				case Input.Keys.ESCAPE:
					_commands.exit();
					break;
			}
		}
	}

	public class UndoTimeTrigger implements TimedTrigger.Trigger {
		@Override
		public void activate() {
			_commands.undo();
		}
	}
}
