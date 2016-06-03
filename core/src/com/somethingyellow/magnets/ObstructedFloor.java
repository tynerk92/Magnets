package com.somethingyellow.magnets;

import com.somethingyellow.tiled.TiledStage;
import com.somethingyellow.tiled.TiledStageActor;

import java.util.HashMap;
import java.util.TreeSet;

public class ObstructedFloor extends TiledStageActor {
	public static final String STATE_DEFAULT = "Default";

	private int _elevation;

	public void initialize(TiledStage stage, HashMap<String, FrameSequence> animationFrames,
	                       TiledStage.Coordinate origin, int elevation) {
		super.initialize(stage, TiledStageActor.BodyArea1x1, 1, animationFrames, origin);

		_elevation = elevation;

		addState(STATE_DEFAULT);
	}

	@Override
	public void act(int subtick) {
	}

	public int elevation() {
		return _elevation;
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
