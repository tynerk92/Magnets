package com.somethingyellow.magnets;

import com.somethingyellow.tiled.*;

import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

public class Block extends TiledStageActor {
	public static final int MOVE_TICKS = 3;
	public static final String STATE_DEFAULT = "Default";
	public static final String STATE_MAGNETISED = "Magnetised";
	public static final int MAGNETISED_ATTRACTION_RANGE = 2;
	public static final int MAGNETISED_MAGNETISE_RANGE = 1;
	public static final int MAGNETISED_ATTRACTION_STRENGTH = 1;
	public static final int[] SUBTICKS = new int[]{
			PlayScreen.SUBTICKS.RESET.ordinal(),
			PlayScreen.SUBTICKS.FORCES.ordinal(),
			PlayScreen.SUBTICKS.BLOCK_MOVEMENT.ordinal()
	};

	private boolean _isPushable;
	private boolean _isMagnetisable;
	private boolean _isMagnetised;
	private int _forceX;
	private int _forceY;

	public void initialize(TiledStage stage, boolean[] bodyArea, int bodyWidth, HashMap<String, FrameSequence> animationFrames,
	                       TiledStage.Coordinate origin, boolean isPushable, boolean isMagnetisable, int actorDepth) {
		super.initialize(stage, bodyArea, bodyWidth, animationFrames, origin, actorDepth);
		_isPushable = isPushable;
		_isMagnetisable = isMagnetisable;
		addState(STATE_DEFAULT);
	}

	@Override
	public void reset() {
		super.reset();
		_forceX = 0;
		_forceY = 0;
		_isMagnetised = false;
	}


	@Override
	public void act(int subtick) {
		if (subtick == PlayScreen.SUBTICKS.RESET.ordinal()) {

			_isMagnetised = false;
			_forceX = _forceY = 0;

		} else if (subtick == PlayScreen.SUBTICKS.FORCES.ordinal()) {

			if (_isMagnetised) {
				if (!isMoving()) {
					// Attract blocks within attraction range
					for (TiledStage.Coordinate bodyCoordinate : bodyCoordinates()) {
						for (TiledStage.Coordinate coordinate : bodyCoordinate.getCoordinatesInRange(MAGNETISED_ATTRACTION_RANGE, false)) {
							for (TiledStageActor actor : coordinate.actors()) {
								if (actor == this) continue;
								if (actor instanceof Block) {
									Block block = (Block) actor;
									if (!block.isMagnetised()) {
										TiledStage.DIRECTION direction = bodyCoordinate.getDirectionFrom(coordinate);
										if (direction != null)
											block.applyForce(direction, MAGNETISED_ATTRACTION_STRENGTH);
									}
								}
							}
						}
					}
				}
				if (!hasState(STATE_MAGNETISED)) addState(STATE_MAGNETISED);
			} else {
				if (hasState(STATE_MAGNETISED)) removeState(STATE_MAGNETISED);
			}


		} else if (subtick == PlayScreen.SUBTICKS.BLOCK_MOVEMENT.ordinal()) {

			if (_forceX != 0 || _forceY != 0) {
				moveDirection(TiledStage.GetDirection(_forceY, _forceX), MOVE_TICKS);
			}

		}
	}

	public void applyForce(TiledStage.DIRECTION direction, int magnitude) {
		_forceX += TiledStage.GetUnitColumn(direction) * magnitude;
		_forceY += TiledStage.GetUnitRow(direction) * magnitude;
	}

	public void magnetise() {
		if (isMoving()) return;

		if (_isMagnetisable && !_isMagnetised) {
			_isMagnetised = true;

			// Magnetise blocks within magnetisation range
			TreeSet<TiledStage.Coordinate> magnetiseCoodinates = new TreeSet<TiledStage.Coordinate>();

			for (TiledStage.Coordinate coordinate : bodyCoordinates()) {
				magnetiseCoodinates.addAll(coordinate.getCoordinatesInRange(MAGNETISED_MAGNETISE_RANGE, false));
			}

			for (TiledStage.Coordinate coordinate : magnetiseCoodinates) {
				for (TiledStageActor actor : coordinate.actors()) {
					if (actor == this) continue;
					if (actor instanceof Block) {
						Block block = (Block) actor;
						block.magnetise();
					}
				}
			}
		}
	}

	public boolean push(TiledStage.DIRECTION direction) {
		return moveDirection(direction, MOVE_TICKS);
	}

	@Override
	public boolean bodyCanBeAt(TiledStage.Coordinate coordinate) {
		if (coordinate.getTileProp(PlayScreen.LAYER_ACTORS, PlayScreen.TILE_TYPE, "").equals(PlayScreen.TILE_TYPE_WALL))
			return false;
		for (TiledStageActor actor : coordinate.actors()) {
			if (actor == this) continue;
			if (actor instanceof Player || actor instanceof Block || actor instanceof MagneticSource ||
					actor instanceof ObstructedFloor || (actor instanceof Door && !((Door) actor).isOpen()))
				return false;
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

	public int[] subticks() {
		return SUBTICKS;
	}
}
