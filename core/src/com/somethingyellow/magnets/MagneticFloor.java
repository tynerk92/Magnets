package com.somethingyellow.magnets;

import com.somethingyellow.tiled.TiledStage;
import com.somethingyellow.tiled.TiledStageActor;

import java.util.HashMap;
import java.util.TreeSet;

public class MagneticFloor extends TiledStageActor {
	public static final String STATE_STILL = "";
	public static final int MAGNETISE_RANGE = 0;

	public MagneticFloor(int type, HashMap<String, FrameSequence> animationFrames,
	                     TiledStage stage, TiledStage.Coordinate origin, int actorDepth) {
		super(type, TiledStageActor.BodyArea1x1, 1, animationFrames, stage, origin, actorDepth);

		addState(STATE_STILL);
	}

	@Override
	public void act(int tick) {
		super.act(tick);

		if (tick == PlayScreen.TICKS.MAGNETISATION.ordinal()) {

			TreeSet<TiledStage.Coordinate> magnetiseCoodinates = origin().getCoordinatesInRange(MAGNETISE_RANGE, false);
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

	// get/set
	// ---------
}
