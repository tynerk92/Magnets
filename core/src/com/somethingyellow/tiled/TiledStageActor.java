package com.somethingyellow.tiled;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

import java.util.HashMap;
import java.util.LinkedList;

public abstract class TiledStageActor extends TiledStageBody {
	public static final int[] SUBTICKS = new int[]{0};

	private int _movingTicks;

	protected void initialize(boolean[] bodyArea, int bodyWidth, HashMap<String, FrameSequence> animationFrames,
	                          TiledStage.Coordinate origin) {
		super.initialize(bodyArea, bodyWidth, animationFrames, origin);
		_movingTicks = 0;
	}

	@Override
	public void act() {
		super.act();
		if (_movingTicks > 0) {
			_movingTicks--;
		}
	}

	public abstract void act(int subtick);
	public abstract boolean bodyCanBeAt(TiledStage.Coordinate coordinate);
	public abstract int[] subticks();

	public boolean canBeAt(TiledStage.Coordinate origin) {
		// Check if all coordinates of body can move to their direction
		LinkedList<TiledStage.Coordinate> targetCoordinates = getBodyCoordinates(origin);
		for (TiledStage.Coordinate bodyCoordinate : targetCoordinates) {
			if (!bodyCanBeAt(bodyCoordinate)) return false;
		}

		return true;
	}

	protected void moveTo(TiledStage.Coordinate targetCoordinate, int ticks) {
		_movingTicks = ticks;
		TiledStage.Coordinate origin = origin();
		setOrigin(targetCoordinate);
		Vector2 pos = targetCoordinate.position();
		addAction(Actions.moveTo(pos.x, pos.y, ticksToTime(ticks)));

		for (TiledStageBody.Listener listener : listeners()) {
			if (listener instanceof Listener) ((Listener) listener).moved(origin, targetCoordinate);
		}
	}

	protected boolean moveDirection(TiledStage.DIRECTION direction, int ticks) {
		if (_movingTicks > 0) return false;

		TiledStage.Coordinate checkCoordinate;

		int unitRow = TiledStage.GetUnitRow(direction);
		int unitCol = TiledStage.GetUnitColumn(direction);

		if (unitRow != 0) {
			checkCoordinate = stage().getCoordinate(origin().row() + unitRow, origin().column());
			if (checkCoordinate == null || !canBeAt(checkCoordinate)) {
				return false;
			}
		}

		if (unitCol != 0) {
			checkCoordinate = stage().getCoordinate(origin().row(), origin().column() + unitCol);
			if (checkCoordinate == null || !canBeAt(checkCoordinate)) {
				return false;
			}
		}

		// Checking diagonal coordinate
		if (unitRow != 0 && unitCol != 0) {
			checkCoordinate = stage().getCoordinate(origin().row() + unitRow, origin().column() + unitCol);
			if (checkCoordinate == null || !canBeAt(checkCoordinate)) {
				return false;
			}
		}

		moveTo(stage().getCoordinate(origin().row() + unitRow, origin().column() + unitCol), ticks);
		return true;
	}

	public boolean isMoving() {
		return _movingTicks > 0;
	}

	// get/set
	// ---------

	public abstract static class Listener extends TiledStageBody.Listener {
		public void moved(TiledStage.Coordinate from, TiledStage.Coordinate to) {
		}

		public void movedDirection(TiledStage.DIRECTION direction) {
		}
	}
}
