package com.somethingyellow.magnets;

import com.somethingyellow.tiled.*;

import java.util.HashMap;
import java.util.HashSet;

public class Player extends PlayerActor {
	public static final float MOVE_SPEED = 5f;
	public static final String STATE_STANDING = "";
	public static final String STATE_WALKING = "Walking";

	public Player(int type, boolean[] bodyArea, int bodyWidth, HashMap<String, Frames> animationFrames,
	              TiledStage stage, String layerName, TiledStage.Coordinate origin, int actorDepth) {
		super(type, bodyArea, bodyWidth, animationFrames, stage, layerName, origin, actorDepth);
		addState(STATE_STANDING);
	}

	@Override
	public void preAct() {
		checkPushes();
	}


	@Override
	public void act(float delta) {
		super.act(delta);

		checkMovement();
	}

	private boolean checkPushes() {
		if (!isMoving()) {
			if (isKeyLeftHeld()) return pushDirection(TiledStage.DIRECTION.WEST);
			if (isKeyRightHeld()) return pushDirection(TiledStage.DIRECTION.EAST);
			if (isKeyUpHeld()) return pushDirection(TiledStage.DIRECTION.NORTH);
			if (isKeyDownHeld()) return pushDirection(TiledStage.DIRECTION.SOUTH);
		}

		return false;
	}

	private boolean checkMovement() {
		if (!isMoving()) {
			if (isKeyLeftHeld() && isKeyUpHeld()) return moveDirection(TiledStage.DIRECTION.NORTH_WEST);
			if (isKeyLeftHeld() && isKeyDownHeld()) return moveDirection(TiledStage.DIRECTION.SOUTH_WEST);
			if (isKeyRightHeld() && isKeyUpHeld()) return moveDirection(TiledStage.DIRECTION.NORTH_EAST);
			if (isKeyRightHeld() && isKeyDownHeld())
				return moveDirection(TiledStage.DIRECTION.SOUTH_EAST);
			if (isKeyLeftHeld()) return moveDirection(TiledStage.DIRECTION.WEST);
			if (isKeyRightHeld()) return moveDirection(TiledStage.DIRECTION.EAST);
			if (isKeyUpHeld()) return moveDirection(TiledStage.DIRECTION.NORTH);
			if (isKeyDownHeld()) return moveDirection(TiledStage.DIRECTION.SOUTH);
		}

		return false;
	}

	protected boolean pushDirection(final TiledStage.DIRECTION direction) {
		// check if there're any blocks in direction, push if there are
		final TiledStage.Coordinate coordinate = origin().getAdjacentCoordinate(direction);
		if (coordinate == null) return false;
		HashSet<TiledStageActor> actors = coordinate.actors();
		for (final TiledStageActor actor : actors) {
			if (actor instanceof Block) {
				((Block) actor).push(direction);
				moveTo(coordinate, 1 / MOVE_SPEED);
				return true;
			}
		}
		return false;
	}

	protected boolean moveDirection(TiledStage.DIRECTION direction) {
		if (moveDirection(direction, 1 / MOVE_SPEED)) {
			addState(STATE_WALKING).removeState(STATE_STANDING);
			return true;
		}
		return false;
	}

	@Override
	protected void onMovementEnd(TiledStage.Coordinate origin, TiledStage.Coordinate target) {
		super.onMovementEnd(origin, target);

		if (!checkMovement()) {
			addState(STATE_STANDING).removeState(STATE_WALKING);
		}
	}

	@Override
	public boolean bodyCanBeAt(TiledStage.Coordinate coordinate) {
		if (!super.bodyCanBeAt(coordinate)) return false;
		if (coordinate.getTileProp(PlayScreen.LAYER_OBJECTS, PlayScreen.TILE_TYPE).equals(PlayScreen.TILE_TYPE_WALL))
			return false;
		for (TiledStageActor actor : coordinate.actors()) {
			if (actor == this) continue;
			if (actor.type() == PlayScreen.OBJECT_TYPES.PLAYER.ordinal() ||
					actor.type() == PlayScreen.OBJECT_TYPES.BLOCK.ordinal()) return false;
		}

		return true;
	}
}
