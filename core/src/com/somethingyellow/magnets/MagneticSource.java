package com.somethingyellow.magnets;

import com.somethingyellow.graphics.AnimationDef;
import com.somethingyellow.tiled.TiledStage;
import com.somethingyellow.tiled.TiledStageActor;

import java.util.Map;

public class MagneticSource extends TiledStageActor {

	public static final int MAGNETISE_RANGE = 1;
	public static final int ATTRACTION_RANGE = 2;
	public static final int ATTRACTION_STRENGTH = 1;
	public static final int[] SUBTICKS_STATIC = new int[]{
			PlayScreen.SUBTICKS.MAGNETISATION.ordinal(),
			PlayScreen.SUBTICKS.FORCES.ordinal()
	};

	public void initialize(Map<String, AnimationDef> animationDefs, TiledStage.Coordinate origin) {
		super.initialize(animationDefs, TiledStageActor.BodyArea1x1, 1, origin);
		SUBTICKS = SUBTICKS_STATIC;

		showAnimation(Config.AnimationSource);
	}

	@Override
	public void tick(int subtick) {
		if (subtick == PlayScreen.SUBTICKS.MAGNETISATION.ordinal()) {

			for (TiledStage.Coordinate bodyCoordinate : bodyCoordinates()) {

				// Magnetise blocks within magnetisation range
				for (TiledStage.Coordinate coordinate : bodyCoordinate.getCoordinatesInRange(MAGNETISE_RANGE, false)) {
					for (TiledStageActor actor : coordinate.actors()) {
						if (actor == this) continue;
						if (actor instanceof Lodestone) {
							Lodestone lodestone = (Lodestone) actor;
							if (!lodestone.isMagnetised()) lodestone.magnetise();
						}
					}
				}

			}

		} else if (subtick == PlayScreen.SUBTICKS.FORCES.ordinal()) {

			for (TiledStage.Coordinate bodyCoordinate : bodyCoordinates()) {

				// Attract blocks within attraction range
				for (TiledStage.Coordinate coordinate : bodyCoordinate.getCoordinatesAtRange(ATTRACTION_RANGE, false)) {
					for (TiledStageActor actor : coordinate.actors()) {
						if (actor == this) continue;
						if (actor instanceof Lodestone) {
							Lodestone lodestone = (Lodestone) actor;
							if (lodestone.isMagnetised() || lodestone.isMoving()) continue;

							TiledStage.DIRECTION direction = bodyCoordinate.getDirectionFrom(coordinate);
							if (direction == null) continue;

							lodestone.attract(direction, coordinate, ATTRACTION_STRENGTH);
						}
					}
				}
			}
		}
	}

	@Override
	public boolean bodyCanBeAt(TiledStage.Coordinate coordinate) {
		return false;
	}

	public static class Config {
		public static String AnimationSource = "Source";
	}
}
