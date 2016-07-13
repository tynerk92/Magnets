package com.somethingyellow.magnets;

import com.somethingyellow.graphics.AnimationDef;
import com.somethingyellow.tiled.TiledStage;
import com.somethingyellow.tiled.TiledStageActor;

import java.util.Map;

public class Exit extends TiledStageActor {
	public static final int[] SUBTICKS_STATIC = new int[]{ };

	public Exit() {
		super();
		SUBTICKS = SUBTICKS_STATIC;
	}

	public void initialize(Map<String, AnimationDef> animationDefs, TiledStage.Coordinate origin) {
		super.initialize(animationDefs, origin);

		showAnimation(Config.AnimationExit);
	}

	@Override
	public void subtick(int subtick) {
	}

	@Override
	public void updateAnimation() {
	}

	public static class Config {
		public static String AnimationExit = "Exit";
	}
}
