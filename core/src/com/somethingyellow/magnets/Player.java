package com.somethingyellow.magnets;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.somethingyellow.tiled.*;

import java.util.HashMap;
import java.util.List;

public class Player extends PlayerActor {

	public Player(int type, boolean[] bodyArea, int bodyWidth, HashMap<String, Animation> animations,
	              TiledStage stage, TiledStage.Coordinate origin) {
		super(type, bodyArea, bodyWidth, animations, stage, origin);
	}


	@Override
	public void act(float delta) {
		super.act(delta);

		if (!isMoving() && momentumX() == 0 && momentumY() == 0) {
			if (isKeyLeftHeld()) walkDirection(TiledStage.DIRECTION.LEFT);
			else if (isKeyRightHeld()) walkDirection(TiledStage.DIRECTION.RIGHT);
			else if (isKeyUpHeld()) walkDirection(TiledStage.DIRECTION.UP);
			else if (isKeyDownHeld()) walkDirection(TiledStage.DIRECTION.DOWN);
		}
	}

	private void walkDirection(TiledStage.DIRECTION direction) {
		addMomentum(direction, 1);

		// check if there're any blocks in direction, push if there are
		List<TiledStageActor> actors = origin().getAdjacentCoordinate(direction).actors();
		for (TiledStageActor actor : actors) {
			if (actor instanceof Block) {
				((Block) actor).push(direction);
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
