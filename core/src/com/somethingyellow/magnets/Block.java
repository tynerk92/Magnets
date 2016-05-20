package com.somethingyellow.magnets;

import com.somethingyellow.tiled.*;

public class Block extends TiledStageActor {

	private boolean _isPushable;

	public Block(boolean isPushable) {
		_isPushable = isPushable;
	}

	@Override
	public void act(float delta) {
		super.act(delta);
	}

	@Override
	public boolean canMove(TiledStage.Coordinate coordinate, TiledStage.DIRECTION direction) {
		if (!super.canMove(coordinate, direction)) return false;
		if (coordinate.getTileBooleanProp(PlayScreen.LAYER_OBJECTS, PlayScreen.TILE_PROP_WALL))
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
