package com.somethingyellow.magnets;

import com.somethingyellow.graphics.AnimationDef;
import com.somethingyellow.tiled.TiledStage;
import com.somethingyellow.tiled.TiledStageActor;

import java.util.Map;

public class Exit extends TiledStageActor {

	public static final int[] SUBTICKS_STATIC = new int[]{
			PlayScreen.SUBTICKS.GRAPHICS.ordinal()
	};

	public void initialize(Map<String, AnimationDef> animationDefs, TiledStage.Coordinate origin) {
		super.initialize(animationDefs, TiledStageActor.BodyArea1x1, 1, origin);
		SUBTICKS = SUBTICKS_STATIC;

		showAnimation(Config.AnimationExit);
	}

	@Override
	public boolean bodyCanBeAt(TiledStage.Coordinate coordinate) {
		return true;
	}

	public void tick(int subtick) {
	}

	public static class Config {
		public static String AnimationExit = "Exit";
	}
}
