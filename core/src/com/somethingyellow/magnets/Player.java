package com.somethingyellow.magnets;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.somethingyellow.tiled.*;

import java.util.HashMap;
import java.util.HashSet;

public class Player extends PlayerActor {
	public static final int MOVE_TICKS = 2;
	public static final int TRY_MOVE_TICKS = 1;
	public static final float TRY_MOVE_DISTANCE = 3f;
	public static final String STATE_STANDING = "Standing";
	public static final String STATE_WALKING = "Walking";
	public static final int PLAYER_PUSH_FORCE = 100;
	public static final int[] SUBTICKS = new int[]{
			PlayScreen.SUBTICKS.FORCES.ordinal(),
			PlayScreen.SUBTICKS.PLAYER_MOVEMENT.ordinal(),
			PlayScreen.SUBTICKS.GRAPHICS.ordinal()
	};

	public TiledStage.DIRECTION _pushingDirection;
	private boolean _toMoveLeft = false;
	private boolean _toMoveRight = false;
	private boolean _toMoveUp = false;
	private boolean _toMoveDown = false;

	public Player(boolean[] bodyArea, int bodyWidth, HashMap<String, FrameSequence> animationFrames,
	              TiledStage stage, TiledStage.Coordinate origin, int actorDepth) {
		super(bodyArea, bodyWidth, animationFrames, stage, origin, actorDepth);
		addState(STATE_STANDING);
		_pushingDirection = null;
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		if (isKeyLeftHeld()) _toMoveLeft = true;
		if (isKeyRightHeld()) _toMoveRight = true;
		if (isKeyUpHeld()) _toMoveUp = true;
		if (isKeyDownHeld()) _toMoveDown = true;
	}

	@Override
	public void act(int subtick) {
		if (subtick == PlayScreen.SUBTICKS.FORCES.ordinal()) {

			checkPushes();

		} else if (subtick == PlayScreen.SUBTICKS.PLAYER_MOVEMENT.ordinal()) {

			if (checkMovement()) {
				if (!hasState(STATE_WALKING)) {
					addState(STATE_WALKING).removeState(STATE_STANDING);
				}
			} else {
				if (hasState(STATE_WALKING)) {
					addState(STATE_STANDING).removeState(STATE_WALKING);
				}
			}

			_toMoveLeft = _toMoveRight = _toMoveUp = _toMoveDown = false;

		}
	}


	private boolean checkPushes() {
		if (!isMoving()) {
			if (_toMoveLeft && !_toMoveRight && !_toMoveUp && !_toMoveDown)
				return pushDirection(TiledStage.DIRECTION.WEST);
			else if (_toMoveRight && !_toMoveLeft && !_toMoveUp && !_toMoveDown)
				return pushDirection(TiledStage.DIRECTION.EAST);
			else if (_toMoveUp && !_toMoveLeft && !_toMoveRight && !_toMoveDown)
				return pushDirection(TiledStage.DIRECTION.NORTH);
			else if (_toMoveDown && !_toMoveLeft && !_toMoveRight && !_toMoveUp)
				return pushDirection(TiledStage.DIRECTION.SOUTH);
		}

		return false;
	}

	private boolean checkMovement() {
		if (!isMoving()) {
			if (_pushingDirection != null) {
				TiledStage.DIRECTION direction = _pushingDirection;
				_pushingDirection = null;
				return moveDirection(direction);
			}

			if (_toMoveLeft && !_toMoveRight && !_toMoveUp && !_toMoveDown)
				return moveDirection(TiledStage.DIRECTION.WEST);
			else if (_toMoveRight && !_toMoveLeft && !_toMoveUp && !_toMoveDown)
				return moveDirection(TiledStage.DIRECTION.EAST);
			else if (_toMoveUp && !_toMoveLeft && !_toMoveRight && !_toMoveDown)
				return moveDirection(TiledStage.DIRECTION.NORTH);
			else if (_toMoveDown && !_toMoveLeft && !_toMoveRight && !_toMoveUp)
				return moveDirection(TiledStage.DIRECTION.SOUTH);

			return false;
		} else {
			return true;
		}
	}

	protected boolean pushDirection(final TiledStage.DIRECTION direction) {
		// check if there're any blocks in direction, push if there are
		final TiledStage.Coordinate coordinate = origin().getAdjacentCoordinate(direction);
		if (coordinate == null) return false;
		HashSet<TiledStageActor> actors = coordinate.actors();
		for (TiledStageActor actor : actors) {
			if (actor instanceof Block) {
				Block block = (Block) actor;
				if (block.isPushable()) block.applyForce(direction, PLAYER_PUSH_FORCE);
				_pushingDirection = direction;

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
	public boolean keyDown(InputEvent event, int keycode) {
		super.keyDown(event, keycode);
		switch (keycode) {
			case Input.Keys.R:
				Main.PlayScreen.dispose();
				Main.PlayScreen.show();
				break;
		}

		return true;
	}

	// get/set
	// ---------

	@Override
	public int[] SUBTICKS() {
		return SUBTICKS;
	}
}
