package com.somethingyellow.magnets;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.somethingyellow.tiled.*;

import java.util.HashMap;

public class Block extends TiledStageActor {

	private boolean _isPushable;

	public Block(int type, boolean[] bodyArea, int bodyWidth, HashMap<String, Animation> animations,
	             TiledStage stage, TiledStage.Coordinate origin, boolean isPushable) {
		super(type, bodyArea, bodyWidth, animations, stage, origin);
		_isPushable = isPushable;
	}

	@Override
	public void act(float delta) {
		super.act(delta);
	}

	public void push(TiledStage.DIRECTION direction) {
		if (isPushable()) addMomentum(direction, 1);
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

	// get/set
	// ---------

	public boolean isPushable() {
		return _isPushable;
	}
}
