package com.somethingyellow.tiled;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.somethingyellow.graphics.AnimationDef;

import java.util.LinkedList;
import java.util.Map;

public abstract class TiledStageActor extends TiledStageBody {
	public static final int[] SUBTICKS = new int[]{0};


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

	public boolean moveDirection(TiledStage.DIRECTION direction, int ticks) {
		if (isMoving()) return false;

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
}
