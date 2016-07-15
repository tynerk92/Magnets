package com.somethingyellow.magnets;

import com.somethingyellow.graphics.AnimationDef;
import com.somethingyellow.tiled.TiledStage;
import com.somethingyellow.tiled.TiledStageActor;

import java.util.Map;

public class Button extends TiledStageActor {

	public static final int[] SUBTICKS_STATIC = new int[]{
			PlayScreen.SUBTICKS.BUTTONS.ordinal()
	};

	public Button() {
		super();
		SUBTICKS = SUBTICKS_STATIC;
	}

	public void initialize(TiledStage stage, Map<String, AnimationDef> animationDefs, TiledStage.Coordinate origin) {
		super.initialize(stage, animationDefs, origin);

		setTransition(Config.AnimationOning, Config.AnimationOn);
		setTransition(Config.AnimationOffing, Config.AnimationOff);
		showAnimation(Config.AnimationOff);
	}

	@Override
	public void subtick(int subtick) {
		if (subtick == PlayScreen.SUBTICKS.BUTTONS.ordinal()) {

			boolean isOn = false;
			loop:
			for (TiledStage.Coordinate bodyCoordinate : bodyCoordinates()) {
				for (TiledStageActor actor : bodyCoordinate.actors()) {
					if (actor != this && actor.isSolid()) {
						isOn = true;
						break loop;
					}
				}
			}

			setStatus(Config.StatusOn, isOn);

		}
	}

	@Override
	public void updateAnimation() {
		// TODO: Add delay to button on-ing and off-ing

		if (isAnimationActive(Config.AnimationOn)) {
			if (!hasStatus(Config.StatusOn)) {
				hideAllButAnimations(Config.AnimationOffing);
			}
		} else if (isAnimationActive(Config.AnimationOff)) {
			if (hasStatus(Config.StatusOn)) {
				hideAllButAnimations(Config.AnimationOning);
			}
		}
	}

	public static class Config {
		public static String AnimationOning = "Oning";
		public static String AnimationOn = "On";
		public static String AnimationOffing = "Offing";
		public static String AnimationOff = "Off";
		public static String StatusOn = "On"; // When something is on top
	}
}
