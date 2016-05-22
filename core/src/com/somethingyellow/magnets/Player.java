package com.somethingyellow.magnets;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.somethingyellow.tiled.*;

import java.util.HashMap;
import java.util.List;

public class Player extends PlayerActor {
	public static final float MOVE_SPEED = 5f;
	public static final String STATE_WALKING = "Walking";

	public Player(int type, boolean[] bodyArea, int bodyWidth, HashMap<String, Animation> animations,
	              TiledStage stage, TiledStage.Coordinate origin) {
		super(type, bodyArea, bodyWidth, animations, stage, origin);
	}


	@Override
	public void act(float delta) {
		super.act(delta);

		checkKeyMovement();
	}

	private boolean checkKeyMovement() {
		if (!isMoving()) {
			if (isKeyLeftHeld()) return walkDirection(TiledStage.DIRECTION.WEST);
			else if (isKeyRightHeld()) return walkDirection(TiledStage.DIRECTION.EAST);
			else if (isKeyUpHeld()) return walkDirection(TiledStage.DIRECTION.NORTH);
			else if (isKeyDownHeld()) return walkDirection(TiledStage.DIRECTION.SOUTH);
		}

		return false;
	}

	protected boolean walkDirection(TiledStage.DIRECTION direction) {
		// check if there're any blocks in direction, push if there are
		List<TiledStageActor> actors = origin().getAdjacentCoordinate(direction).actors();
		for (TiledStageActor actor : actors) {
			if (actor instanceof Block) {
				((Block) actor).push(direction);
				return false;
			}
		}

		// if not, move
		moveDirection(direction, MOVE_SPEED);
		setState(STATE_WALKING);
		return true;
	}

	@Override
	protected void onStopMoving() {
		if (!checkKeyMovement()) {
			if (state().equals(STATE_WALKING)) {
				setState(STATE_DEFAULT);
			}
		}
	}

	@Override
	public boolean canMove(TiledStage.Coordinate coordinate, TiledStage.DIRECTION direction) {
		if (!super.canMove(coordinate, direction)) return false;
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
