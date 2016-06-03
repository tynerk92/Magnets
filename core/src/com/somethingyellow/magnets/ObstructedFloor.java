package com.somethingyellow.magnets;

import com.somethingyellow.tiled.TiledStage;
import com.somethingyellow.tiled.TiledStageActor;

import java.util.HashMap;
import java.util.TreeSet;

public class ObstructedFloor extends TiledStageActor {
	public static final String STATE_DEFAULT = "Default";

	public void initialize(TiledStage stage, HashMap<String, FrameSequence> animationFrames,
	                       TiledStage.Coordinate origin, int actorDepth) {
		super.initialize(stage, TiledStageActor.BodyArea1x1, 1, animationFrames, origin, actorDepth);
		addState(STATE_DEFAULT);
	}

	@Override
	public void act(int subtick) {
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
