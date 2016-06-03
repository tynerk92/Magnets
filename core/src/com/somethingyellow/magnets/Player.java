package com.somethingyellow.magnets;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.somethingyellow.tiled.*;

import java.util.HashMap;
import java.util.HashSet;

public class Player extends PlayerActor {
	public static final int MOVE_TICKS = 3;
	public static final float MIN_ZOOM = 0.5f;
	public static final float MAX_ZOOM = 1.5f;
	public static final String STATE_STANDING = "Standing";
	public static final String STATE_WALKING = "Walking";
	public static final int[] SUBTICKS = new int[]{
			PlayScreen.SUBTICKS.PLAYER_MOVEMENT.ordinal()
	};

	private Listener _listener;
	private float _zoom;

	public Player(boolean[] bodyArea, int bodyWidth, HashMap<String, FrameSequence> animationFrames,
	              TiledStage stage, TiledStage.Coordinate origin, int actorDepth, Listener listener) {
		super(bodyArea, bodyWidth, animationFrames, stage, origin, actorDepth);
		_listener = listener;
		_zoom = 1f;
		addState(STATE_STANDING);
	}

	@Override
	public void act(int subtick) {
		if (subtick == PlayScreen.SUBTICKS.PLAYER_MOVEMENT.ordinal()) {
			if (!checkPushes()) {
				if (checkMovement()) {
					if (!hasState(STATE_WALKING)) {
						addState(STATE_WALKING).removeState(STATE_STANDING);
					}
				} else {
					if (hasState(STATE_WALKING)) {
						addState(STATE_STANDING).removeState(STATE_WALKING);
					}
				}
			}
		}
	}


	private boolean checkPushes() {
		if (!isMoving()) {
			if (isKeyLeftHeld() && !isKeyRightHeld() && !isKeyUpHeld() && !isKeyDownHeld())
				return pushDirection(TiledStage.DIRECTION.WEST);
			else if (isKeyRightHeld() && !isKeyLeftHeld() && !isKeyUpHeld() && !isKeyDownHeld())
				return pushDirection(TiledStage.DIRECTION.EAST);
			else if (isKeyUpHeld() && !isKeyLeftHeld() && !isKeyRightHeld() && !isKeyDownHeld())
				return pushDirection(TiledStage.DIRECTION.NORTH);
			else if (isKeyDownHeld() && !isKeyLeftHeld() && !isKeyRightHeld() && !isKeyUpHeld())
				return pushDirection(TiledStage.DIRECTION.SOUTH);
		}

		return false;
	}

	private boolean checkMovement() {
		if (!isMoving()) {
			if (isKeyLeftHeld() && !isKeyRightHeld() && !isKeyUpHeld() && !isKeyDownHeld())
				return moveDirection(TiledStage.DIRECTION.WEST);
			else if (isKeyRightHeld() && !isKeyLeftHeld() && !isKeyUpHeld() && !isKeyDownHeld())
				return moveDirection(TiledStage.DIRECTION.EAST);
			else if (isKeyUpHeld() && !isKeyLeftHeld() && !isKeyRightHeld() && !isKeyDownHeld())
				return moveDirection(TiledStage.DIRECTION.NORTH);
			else if (isKeyDownHeld() && !isKeyLeftHeld() && !isKeyRightHeld() && !isKeyUpHeld())
				return moveDirection(TiledStage.DIRECTION.SOUTH);

			return false;
		} else {
			return true;
		}
	}

	protected boolean pushDirection(final TiledStage.DIRECTION direction) {
		// check if there're any blocks in direction, push if there are
		TiledStage.Coordinate targetCoordinate = origin().getAdjacentCoordinate(direction);
		if (targetCoordinate == null) return false;
		HashSet<TiledStageActor> actors = targetCoordinate.actors();
		for (TiledStageActor actor : actors) {
			if (actor instanceof Block) {
				Block block = (Block) actor;

				if (block.isPushable()) {
					TiledStage.Coordinate origin = origin();
					moveToInstantly(targetCoordinate);
					if (block.push(direction)) { // if block can really move to its pushed position (not considering actor)
						moveToInstantly(origin);
						moveDirection(direction);
					} else {
						moveToInstantly(origin);
					}
				}

				return true;
			}
		}
		return false;
	}

	protected boolean moveDirection(TiledStage.DIRECTION direction) {
		return moveDirection(direction, MOVE_TICKS);
	}

	@Override
	public boolean bodyCanBeAt(TiledStage.Coordinate coordinate) {
		if (coordinate.getTileProp(PlayScreen.LAYER_ACTORS, PlayScreen.TILE_TYPE, "").equals(PlayScreen.TILE_TYPE_WALL))
			return false;
		for (TiledStageActor actor : coordinate.actors()) {
			if (actor == this) continue;
			if (actor instanceof  Player || actor instanceof  Block || actor instanceof  MagneticSource ||
					(actor instanceof Door && !((Door)actor).isOpen())) return false;
		}

		return true;
	}

	@Override
	protected boolean keyDown(InputEvent event, int keycode) {
		super.keyDown(event, keycode);
		switch (keycode) {
			case Input.Keys.R:
				_listener.resetLevel();
				break;
			case Input.Keys.ESCAPE:
				_listener.exitLevel();
				break;
		}

		return true;
	}

	protected boolean scrolled(InputEvent event, float x, float y, int amount) {
		super.scrolled(event, x, y, amount);
		_zoom = Math.min(Math.max(_zoom + (float) amount / 10, MIN_ZOOM), MAX_ZOOM);
		_listener.setZoom(_zoom);
		return true;
	}

	// get/set
	// ---------
	@Override
	public int[] subticks() {
		return SUBTICKS;
	}

	public interface Listener {
		void resetLevel();

		void exitLevel();

		void setZoom(float zoom);
	}
}
