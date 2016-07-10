package com.somethingyellow.magnets;

import com.somethingyellow.graphics.AnimationDef;
import com.somethingyellow.tiled.TiledStage;
import com.somethingyellow.tiled.TiledStageActor;

import java.util.Map;

public class ObstructedFloor extends TiledStageActor {

	public static final int[] SUBTICKS_STATIC = new int[]{
			PlayScreen.SUBTICKS.GRAPHICS.ordinal()
	};

	private int _elevation;

	public void initialize(Map<String, AnimationDef> animationDefs, TiledStage.Coordinate origin, int elevation) {
		super.initialize(animationDefs, TiledStageActor.BodyArea1x1, 1, origin);
		SUBTICKS = SUBTICKS_STATIC;

		_elevation = elevation;
		showAnimation(Config.AnimationFloor);
	}

	@Override
	public void tick(int subtick) {
	}

	public int elevation() {
		return _elevation;
	}

	@Override
	public boolean bodyCanBeAt(TiledStage.Coordinate coordinate) {
		return true;
	}

	public static class Config {
		public static String AnimationFloor = "Floor";
	}
}
