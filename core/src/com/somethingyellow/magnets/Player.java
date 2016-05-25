package com.somethingyellow.magnets;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.somethingyellow.tiled.*;

import java.util.HashMap;
import java.util.HashSet;

public class Player extends PlayerActor {
	public static final float MOVE_SPEED = 5f;
	public static final String STATE_STANDING = "";
	public static final String STATE_WALKING = "Walking";

	public TiledStage.DIRECTION _pushingDirection;

	public Player(int type, boolean[] bodyArea, int bodyWidth, HashMap<String, FrameSequence> animationFrames,
	              TiledStage stage, String layerName, TiledStage.Coordinate origin, int actorDepth) {
		super(type, bodyArea, bodyWidth, animationFrames, stage, layerName, origin, actorDepth);
		addState(STATE_STANDING);
	}

	@Override
	public void act(float delta, int tick) {
		super.act(delta, tick);

		if (tick == PlayScreen.TICKS.FORCES.ordinal()) {

			checkPushes();

		} else if (tick == PlayScreen.TICKS.PLAYER_MOVEMENT.ordinal()) {

			checkMovement();

		}
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
				Block block = (Block) actor;
				block.push(direction);
				_pushingDirection = direction;

				return true;
			}
		}
		return false;
	}

	protected boolean moveDirection(TiledStage.DIRECTION direction) {
		TiledStage.Coordinate coordinate;

		switch (direction) {
			case NORTH_EAST:
			case NORTH_WEST:
				coordinate = origin().getAdjacentCoordinate(TiledStage.DIRECTION.NORTH);
				if (coordinate != null) if (!canBeAt(coordinate)) return false;
				break;
			case SOUTH_EAST:
			case SOUTH_WEST:
				coordinate = origin().getAdjacentCoordinate(TiledStage.DIRECTION.SOUTH);
				if (coordinate != null) if (!canBeAt(coordinate)) return false;
				break;
		}

		switch (direction) {
			case NORTH_EAST:
			case SOUTH_EAST:
				coordinate = origin().getAdjacentCoordinate(TiledStage.DIRECTION.EAST);
				if (coordinate != null) if (!canBeAt(coordinate)) return false;
				break;
			case NORTH_WEST:
			case SOUTH_WEST:
				coordinate = origin().getAdjacentCoordinate(TiledStage.DIRECTION.WEST);
				if (coordinate != null) if (!canBeAt(coordinate)) return false;
				break;
		}

		if (!moveDirection(direction, 1 / MOVE_SPEED)) return false;

		addState(STATE_WALKING).removeState(STATE_STANDING);
		return true;
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
		if (coordinate.getTileProp(PlayScreen.LAYER_ACTORS, PlayScreen.TILE_TYPE, "").equals(PlayScreen.TILE_TYPE_WALL))
			return false;
		for (TiledStageActor actor : coordinate.actors()) {
			if (actor == this) continue;
			if (actor.type() == PlayScreen.OBJECT_TYPES.PLAYER.ordinal() ||
					actor.type() == PlayScreen.OBJECT_TYPES.BLOCK.ordinal()) return false;
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

}
