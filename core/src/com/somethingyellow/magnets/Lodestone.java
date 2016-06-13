package com.somethingyellow.magnets;

import com.somethingyellow.graphics.AnimatedActor;
import com.somethingyellow.graphics.AnimationDef;
import com.somethingyellow.tiled.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Lodestone extends TiledStageActor {

	public static final int MAGNETISED_ATTRACTION_RANGE = 2;
	public static final int MAGNETISED_MAGNETISE_RANGE = 1;
	public static final int MAGNETISED_ATTRACTION_STRENGTH = 1;
	public static final int[] SUBTICKS = new int[]{
			PlayScreen.SUBTICKS.RESET.ordinal(),
			PlayScreen.SUBTICKS.FORCES.ordinal(),
			PlayScreen.SUBTICKS.BLOCK_MOVEMENT.ordinal(),
			PlayScreen.SUBTICKS.GRAPHICS.ordinal()
	};
	private boolean _isPushable;
	private boolean _isMagnetisable;
	private boolean _isMagnetised;
	private int _forceX;
	private int _forceY;
	private Commands _commands;
	private LinkedList<List<Object>> _tempAttractionData = new LinkedList<List<Object>>();

	public void initialize(Map<String, AnimationDef> animationDefs, boolean[] bodyArea, int bodyWidth, TiledStage.Coordinate origin,
	                       boolean isPushable, boolean isMagnetisable, Commands commands) {
		super.initialize(animationDefs, bodyArea, bodyWidth, origin);

		_isPushable = isPushable;
		_isMagnetisable = isMagnetisable;
		_commands = commands;
		_forceX = 0;
		_forceY = 0;
		_isMagnetised = false;
		showAnimation(Config.AnimationLodestone);
	}

	@Override
	public void reset() {
		super.reset();
		_tempAttractionData.clear();
	}

	@Override
	public void act(int subtick) {
		if (subtick == PlayScreen.SUBTICKS.RESET.ordinal()) {

			if (!isMoving()) {
				_isMagnetised = false;
				_forceX = _forceY = 0;
			}

		} else if (subtick == PlayScreen.SUBTICKS.FORCES.ordinal()) {

			if (_isMagnetised) {
				if (!isMoving()) {
					for (TiledStage.Coordinate bodyCoordinate : bodyCoordinates()) {

						// Attract blocks within attraction range
						for (TiledStage.Coordinate coordinate : bodyCoordinate.getCoordinatesAtRange(MAGNETISED_ATTRACTION_RANGE, false)) {
							for (TiledStageActor actor : coordinate.actors()) {
								if (actor == this) continue;
								if (actor instanceof Lodestone) {
									Lodestone lodestone = (Lodestone) actor;
									if (lodestone.isMagnetised() || lodestone.isMoving()) continue;

									TiledStage.DIRECTION direction = bodyCoordinate.getDirectionFrom(coordinate);
									if (direction == null) continue;

									lodestone.attract(direction, coordinate, MAGNETISED_ATTRACTION_STRENGTH);
								}
							}
						}

					}
				}
			}

		} else if (subtick == PlayScreen.SUBTICKS.BLOCK_MOVEMENT.ordinal()) {

			if (!isMoving()) {
				if (_forceX != 0 || _forceY != 0) {
					moveDirection(TiledStage.GetDirection(_forceY, _forceX), Config.MoveTicks);
				}
			}

		} else if (subtick == PlayScreen.SUBTICKS.GRAPHICS.ordinal()) {

			if (!isMoving()) {
				// Didn't move - show magnetic attraction
				for (final List<Object> attractionData : _tempAttractionData) {

					TiledStage.DIRECTION attractionDirection = (TiledStage.DIRECTION) attractionData.get(0);
					TiledStage.Coordinate bodyCoordinate = (TiledStage.Coordinate) attractionData.get(1);

					TiledStage.Coordinate visualCoordinate = bodyCoordinate.getAdjacentCoordinate(attractionDirection);
					if (visualCoordinate == null) continue;

					// Find if there is already a magnetic field in visualCoordinate
					MagneticField magneticField = null;
					for (TiledStageBody body : visualCoordinate.bodies()) {
						if (body instanceof MagneticField) {
							magneticField = (MagneticField) body;
							break;
						}
					}

					// If no magnetic field existing, spawn one
					if (magneticField == null) {
						magneticField = _commands.spawnMagneticField(visualCoordinate);
					}

					// TODO: Code magnetic field logic
				}

				setStatus(Config.StatusMagnetised, _isMagnetised);
				if (hasStatus(Config.StatusMagnetised)) {
					showAnimation(Config.AnimationMagnetisedOverlay);
				} else {
					hideAnimation(Config.AnimationMagnetisedOverlay);
				}
			}

			_tempAttractionData.clear();
		}
	}

	public void applyForce(TiledStage.DIRECTION direction, int magnitude) {
		_forceX += TiledStage.GetUnitColumn(direction) * magnitude;
		_forceY += TiledStage.GetUnitRow(direction) * magnitude;
	}

	public void attract(TiledStage.DIRECTION direction, TiledStage.Coordinate bodyCoordinate, int magnitude) {
		if (!bodyCoordinates().contains(bodyCoordinate)) return;

		applyForce(direction, magnitude);

		_tempAttractionData.add(Arrays.asList((Object) direction, bodyCoordinate));
	}

	public void magnetise() {
		if (isMoving()) return;

		if (_isMagnetisable && !_isMagnetised) {
			_isMagnetised = true;

			for (TiledStage.Coordinate bodyCoordinate : bodyCoordinates()) {

				// Magnetise blocks within magnetisation range
				for (TiledStage.Coordinate coordinate : bodyCoordinate.getCoordinatesInRange(MAGNETISED_MAGNETISE_RANGE, false)) {
					for (TiledStageActor actor : coordinate.actors()) {
						if (actor == this) continue;
						if (actor instanceof Lodestone) {
							Lodestone lodestone = (Lodestone) actor;
							if (!lodestone.isMagnetised()) lodestone.magnetise();
						}
					}
				}

				for (AnimatedActor.Listener listener : listeners()) {
					if (listener instanceof Listener) ((Listener) listener).magnetised();
				}
			}
		}
	}

	public boolean push(TiledStage.DIRECTION direction) {
		return moveDirection(direction, Config.MoveTicks);
	}

	@Override
	public boolean bodyCanBeAt(TiledStage.Coordinate coordinate) {
		if (_commands.isWall(coordinate)) return false;
		for (TiledStageActor actor : coordinate.actors()) {
			if (actor == this) continue;
			if (actor instanceof Player || actor instanceof Lodestone || actor instanceof MagneticSource ||
					actor instanceof ObstructedFloor || (actor instanceof Door && !((Door) actor).isOpen()))
				return false;
		}

		return true;
	}

	public boolean isPushable() {
		return _isPushable;
	}

	public boolean isMagnetisable() {
		return _isMagnetisable;
	}

	public boolean isMagnetised() {
		return _isMagnetised;
	}

	// get/set
	// ---------

	public int[] subticks() {
		return SUBTICKS;
	}

	public interface Commands {
		boolean isWall(TiledStage.Coordinate coordinate);

		MagneticField spawnMagneticField(TiledStage.Coordinate coordinate);
	}

	public static class Config {
		public static int MoveTicks = 3;
		public static String AnimationLodestone = "Lodestone";
		public static String AnimationMagnetisedOverlay = "Magnetised Overlay";
		public static String StatusMagnetised = "Magnetised";
	}

	public abstract static class Listener extends TiledStageActor.Listener {
		public void magnetised() {
		}
	}
}
