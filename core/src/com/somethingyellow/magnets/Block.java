package com.somethingyellow.magnets;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.somethingyellow.tiled.*;

import java.util.HashMap;

public class Block extends TiledStageActor {
	public static final float MOVE_SPEED = 5f;

	private boolean _isPushable;
	private boolean _isMagnetisable;
	private boolean _isMagnetised;

	public Block(int type, boolean[] bodyArea, int bodyWidth, HashMap<String, Animation> animations,
	             TiledStage stage, TiledStage.Coordinate origin, boolean isPushable, boolean isMagnetisable, boolean isMagnetised) {
		super(type, bodyArea, bodyWidth, animations, stage, origin);
		_isPushable = isPushable;
		_isMagnetisable = isMagnetisable;
		_isMagnetised = isMagnetised;
	}

	@Override
	public void act(float delta) {
		super.act(delta);
	}

	public void push(TiledStage.DIRECTION direction) {
		if (isPushable()) {
			moveDirection(direction, MOVE_SPEED);
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

	// get/set
	// ---------

	public boolean isPushable() {
		return _isPushable;
	}

	public boolean isMagnetisable() {
		return _isMagnetisable;
	}

	public boolean isMagnetised() {
		return _isMagnetised;
	}
}
