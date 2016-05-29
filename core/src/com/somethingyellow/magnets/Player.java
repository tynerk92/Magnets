package com.somethingyellow.magnets;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.somethingyellow.tiled.*;

import java.util.HashMap;
import java.util.HashSet;

public class Player extends PlayerActor {
	public static final float MOVE_SPEED = 5f;
	public static final float TRY_MOVE_DURATION = 0.2f;
	public static final float TRY_MOVE_DISTANCE = 5f;
	public static final String STATE_STANDING = "Standing";
	public static final String STATE_WALKING = "Walking";
	public static final int[] TICKS = new int[] {
			PlayScreen.TICKS.FORCES.ordinal(),
			PlayScreen.TICKS.PLAYER_MOVEMENT.ordinal(),
			PlayScreen.TICKS.BUTTON_PRESSES.ordinal()
	};

	public TiledStage.DIRECTION _pushingDirection;

	public Player(boolean[] bodyArea, int bodyWidth, HashMap<String, FrameSequence> animationFrames,
	              TiledStage stage, TiledStage.Coordinate origin, int actorDepth) {
		super(bodyArea, bodyWidth, animationFrames, stage, origin, actorDepth);
		addState(STATE_STANDING);
		_pushingDirection = null;
	}

	@Override
	public void act(int tick) {
		if (tick == PlayScreen.TICKS.FORCES.ordinal()) {

			checkPushes();

		} else if (tick == PlayScreen.TICKS.PLAYER_MOVEMENT.ordinal()) {

			if (checkMovement()) {
				if (!hasState(STATE_WALKING)) {
					addState(STATE_WALKING).removeState(STATE_STANDING);
				}
			} else {
				if (hasState(STATE_WALKING)) {
					addState(STATE_STANDING).removeState(STATE_WALKING);
				}
			}

		} else if (tick == PlayScreen.TICKS.BUTTON_PRESSES.ordinal()) {

			for (TiledStage.Coordinate bodyCoordinate : bodyCoordinates()) {
				for (TiledStageActor actor : bodyCoordinate.actors()) {
					if (actor instanceof Button) {
						Button button = (Button)actor;
						button.on();
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
			else if (isKeyDownHeld() && !isKeyLeftHeld() && !isKeyUpHeld() && !isKeyUpHeld())
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

			if (isKeyLeftHeld() && isKeyUpHeld()) return moveDirection(TiledStage.DIRECTION.NORTH_WEST);
			if (isKeyLeftHeld() && isKeyDownHeld()) return moveDirection(TiledStage.DIRECTION.SOUTH_WEST);
			if (isKeyRightHeld() && isKeyUpHeld()) return moveDirection(TiledStage.DIRECTION.NORTH_EAST);
			if (isKeyRightHeld() && isKeyDownHeld())
				return moveDirection(TiledStage.DIRECTION.SOUTH_EAST);
			if (isKeyLeftHeld()) return moveDirection(TiledStage.DIRECTION.WEST);
			if (isKeyRightHeld()) return moveDirection(TiledStage.DIRECTION.EAST);
			if (isKeyUpHeld()) return moveDirection(TiledStage.DIRECTION.NORTH);
			if (isKeyDownHeld()) return moveDirection(TiledStage.DIRECTION.SOUTH);

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
				block.push(direction);
				_pushingDirection = direction;

				return true;
			}
		}
		return false;
	}

	protected boolean moveDirection(TiledStage.DIRECTION direction) {
		if (moveDirection(direction, 1 / MOVE_SPEED)) {
			return true;
		} else {
			if (!isMoving()) {
				TiledStage.Coordinate target = origin().getAdjacentCoordinate(direction);
				float distance = origin().position().dst(target.position());
				Vector2 tryMovePos = origin().position().lerp(target.position(), TRY_MOVE_DISTANCE / distance);

				setIsMoving(true);
				final Player player = this;
				addAction(Actions.sequence(
						Actions.moveTo(tryMovePos.x, tryMovePos.y, TRY_MOVE_DURATION / 2),
						Actions.moveTo(origin().position().x, origin().position().y, TRY_MOVE_DURATION / 2),
						Actions.run(new Runnable() {
							@Override
							public void run() {
								player.setIsMoving(false);
							}
						})
				));
			}

			return false;
		}
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
	public int[] TICKS() {
		return TICKS;
	}
}
