package com.somethingyellow.magnets;

import com.somethingyellow.graphics.AnimationDef;
import com.somethingyellow.tiled.TiledStage;
import com.somethingyellow.tiled.TiledStageActor;

import java.util.HashMap;
import java.util.Map;

public class Lodestone extends TiledStageActor implements Player.Pushable,
		MagneticSource.Magnetic, MagneticSource.Magnetisable {

	public static final int[] SUBTICKS_STATIC = new int[]{
			PlayScreen.SUBTICKS.START.ordinal(),
			PlayScreen.SUBTICKS.MAGNETIC_ATTRACTION.ordinal(),
			PlayScreen.SUBTICKS.BLOCK_MOVEMENT.ordinal()
	};
	private boolean _isPushable;
	private boolean _isMagnetisable;
	private int _magneticRange;
	private int _attractionRange;
	private int _forceX;
	private int _forceY;
	private int _magneticStrength;
	private Commands _commands;
	private HashMap<Integer, MagneticAttractionVisual> _attractionVisuals = new HashMap<Integer, MagneticAttractionVisual>();

	public Lodestone() {
		super();
		SUBTICKS = SUBTICKS_STATIC;
	}

	public void initialize(TiledStage stage, Map<String, AnimationDef> animationDefs, TiledStage.Coordinate origin,
	                       boolean isPushable, boolean isMagnetisable, int magneticRange,
	                       int attractionRange, Commands commands) {
		super.initialize(stage, animationDefs, origin);

		_isPushable = isPushable;
		_isMagnetisable = isMagnetisable;
		_magneticRange = magneticRange;
		_attractionRange = attractionRange;
		_commands = commands;
		_forceX = 0;
		_forceY = 0;
		_magneticStrength = 0;
		setStatus(Config.StatusMagnetised, false);
		showAnimation(Config.AnimationLodestone);
	}

	@Override
	public void reset() {
		super.reset();
		_attractionVisuals.clear();
	}

	@Override
	public void subtick(int subtick) {

		if (subtick == PlayScreen.SUBTICKS.START.ordinal()) {

			_magneticStrength = 0;
			_forceX = 0;
			_forceY = 0;

		} else if (subtick == PlayScreen.SUBTICKS.MAGNETIC_ATTRACTION.ordinal()) {

			if (!isMoving()) setStatus(Config.StatusMagnetised, _magneticStrength > 0);

			// If lodestone is magnetised and is not moving
			if (_magneticStrength > 0 && !isMoving()) {
				for (TiledStage.Coordinate bodyCoordinate : bodyCoordinates()) {

					// Attract other magnetic objects within attraction range
					for (TiledStage.Coordinate coordinate : bodyCoordinate.getCoordinatesInRange(_attractionRange, false)) {
						for (TiledStageActor actor : coordinate.actors()) {
							if (actor != this && actor instanceof MagneticSource.Magnetic) {
								TiledStage.DIRECTION direction = bodyCoordinate.getDirectionFrom(coordinate);
								((MagneticSource.Magnetic) actor).applyMagneticForce(coordinate, direction, _magneticStrength);
							}
						}
					}
				}
			}

		} else if (subtick == PlayScreen.SUBTICKS.BLOCK_MOVEMENT.ordinal()) {

			if (!isMoving()) {
				if (_forceX != 0 || _forceY != 0) {
					tryToMoveDirection(TiledStage.GetDirection(_forceY, _forceX), Config.MoveTicks);
				}
			}
		}
	}

	public void push(TiledStage.DIRECTION direction) {
		if (_isPushable && !isMoving()) tryToMoveDirection(direction, Config.PushTicks);
	}

	@Override
	public void updateAnimation() {
		if (hasStatus(Config.StatusMagnetised)) {
			showAnimation(Config.AnimationMagnetisedOverlay);
		} else {
			hideAnimation(Config.AnimationMagnetisedOverlay);
		}
	}

	@Override
	public boolean isSolid() {
		return true;
	}

	@Override
	public boolean bodyCanBeAt(TiledStage.Coordinate coordinate) {
		if (coordinate.elevation() > getZ()) return false;
		return super.bodyCanBeAt(coordinate);
	}

	@Override
	public void magnetise(MagneticSource source, int magnetisationStrength) {
		if (_isMagnetisable && _magneticStrength == 0) {
			_magneticStrength = magnetisationStrength;

			for (TiledStage.Coordinate bodyCoordinate : bodyCoordinates()) {

				// Magnetise other magnetic objects within magnetisation range
				for (TiledStage.Coordinate coordinate : bodyCoordinate.getCoordinatesInRange(_magneticRange, false)) {
					for (TiledStageActor actor : coordinate.actors()) {
						if (actor != this && actor instanceof MagneticSource.Magnetisable) {
							((MagneticSource.Magnetisable) actor).magnetise(source, _magneticStrength);
						}
					}
				}
			}
		}
	}

	private void applyForce(TiledStage.DIRECTION direction, int magnitude) {
		_forceX += TiledStage.GetUnitColumn(direction) * magnitude;
		_forceY += TiledStage.GetUnitRow(direction) * magnitude;
	}

	@Override
	public void applyMagneticForce(TiledStage.Coordinate coordinate, TiledStage.DIRECTION direction, int attractionStrength) {
		// If not magnetised, then can have magnetic forces acted upon
		if (_magneticStrength == 0) {
			applyForce(direction, attractionStrength);

			// Show attraction arrow for body coordinate
			int bodyIndex = getBodyIndex(coordinate);

			if (bodyIndex != -1) {
				MagneticAttractionVisual visual;
				if (!_attractionVisuals.containsKey(bodyIndex)) {
					visual = _commands.spawnMagneticAttractionVisual();
					bindActor(visual, getBodyRow(coordinate), getBodyColumn(coordinate));
					_attractionVisuals.put(bodyIndex, visual);
				} else {
					visual = _attractionVisuals.get(bodyIndex);
				}

				visual.addAttraction(direction);
			}
		}
	}

	@Override
	public TiledStageActor.State getState() {
		return new State();
	}

	public interface Commands {
		MagneticAttractionVisual spawnMagneticAttractionVisual();
	}

	public static class Config {
		public static int MoveTicks = 3;
		public static int PushTicks = 3;
		public static String AnimationLodestone = "Lodestone";
		public static String AnimationMagnetisedOverlay = "Magnetised Overlay";
		public static String StatusMagnetised = "Magnetised";
	}

	public class State extends TiledStageActor.State {
		private HashMap<Integer, MagneticAttractionVisual> _attractionVisuals;

		protected State() {
			_attractionVisuals = Lodestone.this._attractionVisuals;
		}

		@Override
		public void restore(int time) {
			super.restore(time);

			Lodestone.this._attractionVisuals.clear();
			Lodestone.this._attractionVisuals.putAll(_attractionVisuals);
		}
	}
}
