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

	public void initialize(Map<String, AnimationDef> animationDefs, TiledStage.Coordinate origin) {
		super.initialize(animationDefs, origin);

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
		if (hasStatus(Config.StatusOn)) {
			if (!isAnimationActive(Config.AnimationOn) && !isAnimationActive(Config.AnimationOning)) {
				hideAllAnimations();
				showAnimation(Config.AnimationOning);
			}
		} else {
			if (!isAnimationActive(Config.AnimationOff) && !isAnimationActive(Config.AnimationOffing)) {
				hideAllAnimations();
				showAnimation(Config.AnimationOffing);
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
