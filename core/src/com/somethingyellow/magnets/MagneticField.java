package com.somethingyellow.magnets;

import com.somethingyellow.graphics.AnimatedActor;
import com.somethingyellow.graphics.Animation;
import com.somethingyellow.graphics.AnimationDef;
import com.somethingyellow.tiled.TiledStage;
import com.somethingyellow.tiled.TiledStageActor;
import com.somethingyellow.tiled.TiledStageBody;

import java.util.Map;

public class MagneticField extends TiledStageBody {

	public void initialize(Map<String, AnimationDef> animationDefs, TiledStage.Coordinate origin) {
		super.initialize(animationDefs, TiledStageBody.BodyArea1x1, 1, origin);
	}

	public static class Config {
		public static String AnimationVertical = "Vertical";
		public static String AnimationHorizontal = "Horizontal";
		public static String AnimationCross = "Cross";
	}

	// TODO: Code logic
}
