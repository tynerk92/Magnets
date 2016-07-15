package com.somethingyellow.magnets;

import com.somethingyellow.graphics.AnimationDef;
import com.somethingyellow.tiled.TiledStage;
import com.somethingyellow.tiled.TiledStageActor;

import java.util.Map;

public class MagneticSource extends TiledStageActor {

	public static final int[] SUBTICKS_STATIC = new int[]{
			PlayScreen.SUBTICKS.MAGNETISATION.ordinal(),
			PlayScreen.SUBTICKS.MAGNETIC_ATTRACTION.ordinal()
	};

	private boolean _isSolid;
	private int _magnetisationRange;
	private int _magnetisationStrength;
	private int _attractionRange;
	private int _attractionStrength;

	public MagneticSource() {
		super();
		SUBTICKS = SUBTICKS_STATIC;
	}

	public void initialize(TiledStage stage, Map<String, AnimationDef> animationDefs, TiledStage.Coordinate origin, boolean isSolid,
	                       int magnetisationRange, int magnetisationStrength, int attractionRange, int attractionStrength) {
		super.initialize(stage, animationDefs, origin);

		_isSolid = isSolid;
		_magnetisationRange = magnetisationRange;
		_magnetisationStrength = magnetisationStrength;
		_attractionRange = attractionRange;
		_attractionStrength = attractionStrength;

		showAnimation(Config.AnimationSource);
	}

	@Override
	public void updateAnimation() {
	}

	@Override
	public void subtick(int subtick) {

		if (subtick == PlayScreen.SUBTICKS.MAGNETISATION.ordinal()) {

			for (TiledStage.Coordinate bodyCoordinate : bodyCoordinates()) {

				// Magnetise magnetisable objects within magnetisation range
				for (TiledStage.Coordinate coordinate : bodyCoordinate.getCoordinatesInRange(_magnetisationRange, false)) {
					for (TiledStageActor actor : coordinate.actors()) {
						if (actor != this && actor instanceof Magnetisable) {
							((Magnetisable) actor).magnetise(this, _magnetisationStrength);
						}
					}
				}

			}

		} else if (subtick == PlayScreen.SUBTICKS.MAGNETIC_ATTRACTION.ordinal()) {

			for (TiledStage.Coordinate bodyCoordinate : bodyCoordinates()) {

				// Attract magnetic objects within attraction range
				for (TiledStage.Coordinate coordinate : bodyCoordinate.getCoordinatesInRange(_attractionRange, false)) {
					for (TiledStageActor actor : coordinate.actors()) {
						if (actor != this && actor instanceof Magnetic) {
							TiledStage.DIRECTION direction = bodyCoordinate.getDirectionFrom(coordinate);
							((Magnetic) actor).applyMagneticForce(coordinate, direction, _attractionStrength);
						}
					}
				}

			}
		}
	}

	@Override
	public boolean isSolid() {
		return _isSolid;
	}

	/**
	 * To implement on an actor that can be magnetised and act as a "magnetic source"
	 */
	public interface Magnetisable {
		void magnetise(MagneticSource source, int magnetisationStrength);
	}

	/**
	 * To implement on an actor that can be affected by magnetism
	 */
	public interface Magnetic {
		void applyMagneticForce(TiledStage.Coordinate bodyCoordinateApplied, TiledStage.DIRECTION direction, int attractionStrength);
	}

	public static class Config {
		public static String AnimationSource = "Source";
	}
}
