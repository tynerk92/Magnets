package com.somethingyellow.magnets;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.somethingyellow.tiled.TiledStage;
import com.somethingyellow.tiled.TiledStageActor;

import java.util.HashMap;

public class MagneticSource extends Block {

	public MagneticSource(int type, boolean[] bodyArea, int bodyWidth, HashMap<String, Animation> animations,
	                      TiledStage stage, TiledStage.Coordinate origin, boolean isPushable) {
		super(type, bodyArea, bodyWidth, animations, stage, origin, isPushable);
	}

	@Override
	public void act(float delta) {
		super.act(delta);
	}

	// get/set
	// ---------
}
