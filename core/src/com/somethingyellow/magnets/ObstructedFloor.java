package com.somethingyellow.magnets;

import com.somethingyellow.graphics.AnimationDef;
import com.somethingyellow.tiled.TiledStage;
import com.somethingyellow.tiled.TiledStageActor;

import java.util.Map;

public class ObstructedFloor extends TiledStageActor {

	private int _elevation;

	public void initialize(Map<String, AnimationDef> animationDefs, TiledStage.Coordinate origin, int elevation) {
		super.initialize(animationDefs, TiledStageActor.BodyArea1x1, 1, origin);

		_elevation = elevation;
		showAnimation(Config.AnimationFloor);
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

	public static class Config {
		public static String AnimationFloor = "Floor";
	}
}
