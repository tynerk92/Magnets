package com.somethingyellow.magnets;

import com.somethingyellow.tiled.TiledStage;
import com.somethingyellow.tiled.TiledStageActor;

import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

public class MagneticFloor extends TiledStageActor {
	public static final String STATE_DEFAULT = "Default";
	public static final int MAGNETISE_RANGE = 0;
	public static final int[] SUBTICKS = new int[]{
			PlayScreen.SUBTICKS.MAGNETISATION.ordinal()
	};


	public void initialize(TiledStage stage, HashMap<String, FrameSequence> animationFrames,
	                       TiledStage.Coordinate origin, int actorDepth) {
		super.initialize(stage, TiledStageActor.BodyArea1x1, 1, animationFrames, origin, actorDepth);
	}

	@Override
	public void reset() {
		super.reset();
		addState(STATE_DEFAULT);
	}


	@Override
	public void act(int subtick) {
		if (subtick == PlayScreen.SUBTICKS.MAGNETISATION.ordinal()) {

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

	@Override
	public boolean bodyCanBeAt(TiledStage.Coordinate coordinate) {
		return true;
	}

	// get/set
	// ---------
	@Override
	public int[] subticks() {
		return SUBTICKS;
	}
}
