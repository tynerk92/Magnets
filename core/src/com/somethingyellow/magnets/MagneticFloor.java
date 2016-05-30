package com.somethingyellow.magnets;

import com.somethingyellow.tiled.TiledStage;
import com.somethingyellow.tiled.TiledStageActor;

import java.util.HashMap;
import java.util.TreeSet;

public class MagneticFloor extends TiledStageActor {
	public static final String STATE_DEFAULT = "Default";
	public static final int MAGNETISE_RANGE = 0;
	public static final int[] TICKS = new int[]{
			PlayScreen.TICKS.MAGNETISATION.ordinal()
	};

	public MagneticFloor(HashMap<String, FrameSequence> animationFrames,
	                     TiledStage stage, TiledStage.Coordinate origin, int actorDepth) {
		super(TiledStageActor.BodyArea1x1, 1, animationFrames, stage, origin, actorDepth);

		addState(STATE_DEFAULT);
	}

	@Override
	public void act(int tick) {
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

	@Override
	public boolean bodyCanBeAt(TiledStage.Coordinate coordinate) {
		return true;
	}

	// get/set
	// ---------

	@Override
	public int[] TICKS() {
		return TICKS;
	}
}