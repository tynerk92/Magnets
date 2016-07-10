package com.somethingyellow.magnets;

import com.somethingyellow.graphics.AnimatedActor;
import com.somethingyellow.graphics.Animation;
import com.somethingyellow.graphics.AnimationDef;
import com.somethingyellow.tiled.TiledStage;
import com.somethingyellow.tiled.TiledStageActor;

import java.util.Map;

public class Button extends TiledStageActor {

	public static final int[] SUBTICKS_STATIC = new int[]{
			PlayScreen.SUBTICKS.BUTTON_PRESSES.ordinal(),
			PlayScreen.SUBTICKS.GRAPHICS.ordinal()
	};

	public void initialize(Map<String, AnimationDef> animationDefs, boolean[] bodyArea, int bodyWidth, TiledStage.Coordinate origin) {
		super.initialize(animationDefs, bodyArea, bodyWidth, origin);
		SUBTICKS = SUBTICKS_STATIC;

		setTransition(Config.AnimationOning, Config.AnimationOn);
		setTransition(Config.AnimationOffing, Config.AnimationOff);
		listeners().add(new AnimatedActor.Listener() {
			@Override
			public void animationShown(AnimatedActor actor, Animation animation) {
				if (animation.tag().equals(Config.AnimationOn)) {
					addStatus(Config.StatusOn);
					removeStatus(Config.StatusOff);
				}
			}

			@Override
			public void animationHidden(AnimatedActor actor, Animation animation) {
				if (animation.tag().equals(Config.AnimationOn)) {
					addStatus(Config.StatusOff);
					removeStatus(Config.StatusOn);
				}
			}
		});
		addStatus(Config.StatusOff);
		showAnimation(Config.AnimationOff);
	}

	@Override
	public void tick(int subtick) {
		if (subtick == PlayScreen.SUBTICKS.BUTTON_PRESSES.ordinal()) {
			boolean isOn = false;
			loop:
			for (TiledStage.Coordinate bodyCoordinate : bodyCoordinates()) {
				for (TiledStageActor actor : bodyCoordinate.actors()) {
					if (actor instanceof Player || actor instanceof Lodestone) {
						isOn = true;
						break loop;
					}
				}
			}
			setStatus(Config.StatusPressed, isOn);

		} else if (subtick == PlayScreen.SUBTICKS.GRAPHICS.ordinal()) {

			if (hasStatus(Config.StatusPressed)) {
				if (isAnimationActive(Config.AnimationOff)) {
					showAnimation(Config.AnimationOning);
					hideAnimation(Config.AnimationOff);
				}
			} else {
				if (isAnimationActive(Config.AnimationOn)) {
					showAnimation(Config.AnimationOffing);
					hideAnimation(Config.AnimationOn);
				}
			}
		}
	}

	@Override
	public boolean bodyCanBeAt(TiledStage.Coordinate coordinate) {
		return true;
	}

	public static class Config {
		public static String AnimationOning = "Oning";
		public static String AnimationOn = "On";
		public static String AnimationOffing = "Offing";
		public static String AnimationOff = "Off";
		public static String StatusOn = "On"; // When button shows animation `On`
		public static String StatusOff = "Off"; // When buttons hides animation `On`
		public static String StatusPressed = "Pressed"; // When something is on top
	}
}
