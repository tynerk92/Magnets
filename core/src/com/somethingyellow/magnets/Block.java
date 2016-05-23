package com.somethingyellow.magnets;

import com.somethingyellow.tiled.*;

import java.util.HashMap;
import java.util.TreeSet;

public class Block extends TiledStageActor {
	public static final float MOVE_SPEED = 8f;
	public static final String STATE_STILL = "";
	public static final String STATE_MAGNETISED = "Magnetised";
	public static final int MAGNETISED_ATTRACTION_RANGE = 2;
	public static final int MAGNETISED_MAGNETISE_RANGE = 1;
	public static final float MAGNETISED_DURATION = 0.5f;
	public static final int MAGNETISED_ATTRACTION_STRENGTH = 1;

	private boolean _isPushable;
	private boolean _isMagnetisable;
	private boolean _isMagnetised;
	private float _magnetisedDuration;
	private int _forceX;
	private int _forceY;

	public Block(int type, boolean[] bodyArea, int bodyWidth, HashMap<String, Frames> animationFrames,
	             TiledStage stage, String layerName, TiledStage.Coordinate origin, boolean isPushable,
	             boolean isMagnetisable, int actorDepth) {
		super(type, bodyArea, bodyWidth, animationFrames, stage, layerName, origin, actorDepth);
		_isPushable = isPushable;
		_isMagnetisable = isMagnetisable;
		_isMagnetised = false;
		_forceX = 0;
		_forceY = 0;

		addState(STATE_STILL);
	}

	@Override
	public void preAct() {
		super.preAct();

		if (_isMagnetised) {
			// Attract blocks within attraction range
			for (TiledStage.Coordinate bodyCoordinate : bodyCoordinates()) {
				for (TiledStage.Coordinate coordinate : bodyCoordinate.getCoordinatesInRange(MAGNETISED_ATTRACTION_RANGE, false)) {
					for (TiledStageActor actor : coordinate.actors()) {
						if (actor == this) continue;
						if (actor instanceof Block) {
							Block block = (Block) actor;
							TiledStage.DIRECTION direction = bodyCoordinate.getDirectionFrom(block.origin());
							if (direction != null) block.applyForce(direction, MAGNETISED_ATTRACTION_STRENGTH);
						}
					}
				}
			}

			if (_magnetisedDuration > MAGNETISED_DURATION) {
				demagnetise();
			}
		}
	}

	@Override
	public void act(float delta) {
		super.act(delta);

		if (_isMagnetised) {
			_magnetisedDuration += delta;

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

		if (_forceX != 0 || _forceY != 0) {
			moveDirection(TiledStage.GetDirection(_forceY, _forceX), 1 / MOVE_SPEED);
		}
	}

	@Override
	public void postAct() {
		super.postAct();

		_forceX = _forceY = 0;
	}

	public void push(TiledStage.DIRECTION direction) {
		if (_isPushable) {
			applyForce(direction, 10);
		}
	}

	public void applyForce(TiledStage.DIRECTION direction, int magnitude) {
		_forceX += TiledStage.GetUnitColumn(direction) * magnitude;
		_forceY += TiledStage.GetUnitRow(direction) * magnitude;
	}

	public void magnetise() {
		if (_isMagnetisable) {
			_isMagnetised = true;
			_magnetisedDuration = 0f;
			if (!hasState(STATE_MAGNETISED)) addState(STATE_MAGNETISED);
		}
	}

	public void demagnetise() {
		_isMagnetised = false;
		removeState(STATE_MAGNETISED);
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
