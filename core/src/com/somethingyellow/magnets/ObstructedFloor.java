package com.somethingyellow.magnets;

import com.somethingyellow.tiled.TiledStage;
import com.somethingyellow.tiled.TiledStageActor;

import java.util.HashMap;
import java.util.TreeSet;

public class ObstructedFloor extends TiledStageActor {
	public static final String STATE_DEFAULT = "Default";

	public ObstructedFloor(HashMap<String, FrameSequence> animationFrames,
	                       TiledStage stage, TiledStage.Coordinate origin, int actorDepth) {
		super(TiledStageActor.BodyArea1x1, 1, animationFrames, stage, origin, actorDepth);

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
