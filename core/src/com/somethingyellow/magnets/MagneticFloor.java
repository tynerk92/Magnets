package com.somethingyellow.magnets;

import com.somethingyellow.graphics.AnimationDef;
import com.somethingyellow.tiled.TiledStage;
import com.somethingyellow.tiled.TiledStageActor;

import java.util.Map;
import java.util.TreeSet;

public class MagneticFloor extends TiledStageActor {

	public static final int MAGNETISE_RANGE = 0;
	public static final int[] SUBTICKS_STATIC = new int[]{
			PlayScreen.SUBTICKS.MAGNETISATION.ordinal()
	};

	public void initialize(Map<String, AnimationDef> animationDefs, TiledStage.Coordinate origin) {
		super.initialize(animationDefs, TiledStageActor.BodyArea1x1, 1, origin);
		SUBTICKS = SUBTICKS_STATIC;

		showAnimation(Config.AnimationFloor);
	}

	@Override
	public void tick(int subtick) {
		if (subtick == PlayScreen.SUBTICKS.MAGNETISATION.ordinal()) {

			TreeSet<TiledStage.Coordinate> magnetiseCoodinates = origin().getCoordinatesInRange(MAGNETISE_RANGE, false);
			for (TiledStage.Coordinate coordinate : magnetiseCoodinates) {
				for (TiledStageActor actor : coordinate.actors()) {
					if (actor == this) continue;
					if (actor instanceof Lodestone) {
						Lodestone lodestone = (Lodestone) actor;
						lodestone.magnetise();
					}
				}
			}
		}
	}

	@Override
	public boolean bodyCanBeAt(TiledStage.Coordinate coordinate) {
		return true;
	}

	public static class Config {
		public static String AnimationFloor = "Floor";
	}
}
