package com.somethingyellow.magnets;

import com.somethingyellow.graphics.AnimationDef;
import com.somethingyellow.tiled.TiledStage;
import com.somethingyellow.tiled.TiledStageBody;

import java.util.Map;

public class Exit extends TiledStageBody {

	public void initialize(Map<String, AnimationDef> animationDefs, TiledStage.Coordinate origin) {
		super.initialize(animationDefs, TiledStageBody.BodyArea1x1, 1, origin);

		showAnimation(Config.AnimationExit);
	}

	public static class Config {
		public static String AnimationExit = "Exit";
	}
}
