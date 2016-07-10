package com.somethingyellow.magnets;

import com.somethingyellow.graphics.AnimationDef;
import com.somethingyellow.tiled.TiledStage;
import com.somethingyellow.tiled.TiledStageActor;

import java.util.Map;

public class MagneticField extends TiledStageActor {

	public static final int[] SUBTICKS_STATIC = new int[]{
			PlayScreen.SUBTICKS.GRAPHICS.ordinal()
	};

	public void initialize(Map<String, AnimationDef> animationDefs, TiledStage.Coordinate origin) {
		super.initialize(animationDefs, TiledStageActor.BodyArea1x1, 1, origin);
		SUBTICKS = SUBTICKS_STATIC;
	}

	@Override
	public boolean bodyCanBeAt(TiledStage.Coordinate coordinate) {
		return true;
	}

	// TODO: Code logic

	public void tick(int subtick) {
	}

	public static class Config {
		public static String AnimationVertical = "Vertical";
		public static String AnimationHorizontal = "Horizontal";
		public static String AnimationCross = "Cross";
	}
}
