package com.somethingyellow.magnets;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.somethingyellow.graphics.AnimationDef;
import com.somethingyellow.tiled.*;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

public class Player extends TiledStagePlayer {

	public static final int[] SUBTICKS = new int[]{
			PlayScreen.SUBTICKS.PLAYER_MOVEMENT.ordinal(),
			PlayScreen.SUBTICKS.GRAPHICS.ordinal()
	};
	private Commands _commands;
	private float _zoom;
	private LinkedList<TiledStage.DIRECTION> _moveCommands = new LinkedList<TiledStage.DIRECTION>();

	public void initialize(Map<String, AnimationDef> animationDefs, boolean[] bodyArea, int bodyWidth, TiledStage.Coordinate origin, Commands commands) {
		super.initialize(animationDefs, bodyArea, bodyWidth, origin);
		_commands = commands;

		_zoom = Config.ZoomDefault;
		_commands.setZoom(_zoom);
		showAnimation(Config.AnimationStanding);
	}

	@Override
	public void reset() {
		super.reset();
		_moveCommands.clear();
	}

	@Override
	public void act(int subtick) {
		if (subtick == PlayScreen.SUBTICKS.PLAYER_MOVEMENT.ordinal()) {

			if (!isMoving()) {
				if (_commands.isExit(origin())) {
					_commands.exitLevel();
				} else if (_moveCommands.isEmpty()) {
					if (isKeyLeftHeld() && !isKeyRightHeld() && !isKeyUpHeld() && !isKeyDownHeld()) {
						moveDirection(TiledStage.DIRECTION.WEST);
					} else if (isKeyRightHeld() && !isKeyLeftHeld() && !isKeyUpHeld() && !isKeyDownHeld()) {
						moveDirection(TiledStage.DIRECTION.EAST);
					} else if (isKeyUpHeld() && !isKeyLeftHeld() && !isKeyRightHeld() && !isKeyDownHeld()) {
						moveDirection(TiledStage.DIRECTION.NORTH);
					} else if (isKeyDownHeld() && !isKeyLeftHeld() && !isKeyRightHeld() && !isKeyUpHeld()) {
						moveDirection(TiledStage.DIRECTION.SOUTH);
					}
				} else {
					moveDirection(_moveCommands.removeFirst());
				}
			}

		} else if (subtick == PlayScreen.SUBTICKS.GRAPHICS.ordinal()) {

			setZ(0);
			for (TiledStageActor actor : origin().actors()) {
				if (actor instanceof ObstructedFloor) {
					ObstructedFloor obstructedFloor = (ObstructedFloor) actor;
					setZ(obstructedFloor.elevation());
					break;
				}
			}

		}
	}

	protected boolean pushDirection(final TiledStage.DIRECTION direction) {
		// check if there're any blocks in direction, push if there are
		TiledStage.Coordinate targetCoordinate = origin().getAdjacentCoordinate(direction);
		if (targetCoordinate == null) return false;
		HashSet<TiledStageActor> actors = targetCoordinate.actors();
		for (TiledStageActor actor : actors) {
			if (actor instanceof Lodestone) {
				Lodestone lodestone = (Lodestone) actor;

				if (lodestone.isPushable()) {
					TiledStage.Coordinate origin = origin();
					setOrigin(targetCoordinate);
					if (lodestone.push(direction)) { // if lodestone can really move to its pushed position (not considering actor)
						setOrigin(origin);
						moveDirection(direction);
					} else {
						setOrigin(origin);
					}
				}

				return true;
			}
		}
		return false;
	}

	protected void moveDirection(TiledStage.DIRECTION direction) {
		if (!pushDirection(direction)) {
			moveDirection(direction, Config.MoveTicks);
		}
	}

	@Override
	public boolean bodyCanBeAt(TiledStage.Coordinate coordinate) {
		if (_commands.isWall(coordinate)) return false;

		for (TiledStageActor actor : coordinate.actors()) {
			if (actor == this) continue;
			if (actor instanceof Player || actor instanceof Lodestone || actor instanceof MagneticSource ||
					(actor instanceof Door && !((Door) actor).isOpen())) return false;
		}

		return true;
	}

	@Override
	protected boolean keyDown(InputEvent event, int keycode) {
		super.keyDown(event, keycode);
		switch (keycode) {
			case Input.Keys.R:
				_commands.resetLevel();
				break;
			case Input.Keys.ESCAPE:
				_commands.exitLevel();
				break;
			case Input.Keys.LEFT:
			case Input.Keys.A:
				_moveCommands.add(TiledStage.DIRECTION.WEST);
				break;
			case Input.Keys.RIGHT:
			case Input.Keys.D:
				_moveCommands.add(TiledStage.DIRECTION.EAST);
				break;
			case Input.Keys.UP:
			case Input.Keys.W:
				_moveCommands.add(TiledStage.DIRECTION.NORTH);
				break;
			case Input.Keys.DOWN:
			case Input.Keys.S:
				_moveCommands.add(TiledStage.DIRECTION.SOUTH);
				break;
		}

		return true;
	}

	protected boolean scrolled(InputEvent event, float x, float y, int amount) {
		super.scrolled(event, x, y, amount);
		_zoom = Math.min(Math.max(_zoom + (float) amount / 10, Config.ZoomMin), Config.ZoomMax);
		_commands.setZoom(_zoom);
		return true;
	}

	// get/set
	// ---------
	@Override
	public int[] subticks() {
		return SUBTICKS;
	}

	public interface Commands {
		boolean isWall(TiledStage.Coordinate coordinate);

		boolean isExit(TiledStage.Coordinate coordinate);
		void resetLevel();

		void exitLevel();

		void setZoom(float zoom);
	}

	public static class Config {
		public static int MoveTicks = 3;
		public static float ZoomMin = 0.5f;
		public static float ZoomMax = 1.5f;
		public static float ZoomDefault = 0.8f;
		public static String AnimationStanding = "Standing";
	}
}
