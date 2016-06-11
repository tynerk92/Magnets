package com.somethingyellow.magnets;

import com.somethingyellow.graphics.AnimatedActor;
import com.somethingyellow.graphics.Animation;
import com.somethingyellow.graphics.AnimationDef;
import com.somethingyellow.tiled.TiledStage;
import com.somethingyellow.tiled.TiledStageActor;

import java.util.Map;

public class Button extends TiledStageActor {

	public static final int[] SUBTICKS = new int[]{
			PlayScreen.SUBTICKS.BUTTON_PRESSES.ordinal(),
			PlayScreen.SUBTICKS.GRAPHICS.ordinal()
	};
	private boolean _isOn;

	public void initialize(Map<String, AnimationDef> animationDefs, boolean[] bodyArea, int bodyWidth, TiledStage.Coordinate origin) {
		super.initialize(animationDefs, bodyArea, bodyWidth, origin);

		_isOn = false;

		setTransition(Config.AnimationOning, Config.AnimationOn);
		setTransition(Config.AnimationOffing, Config.AnimationOff);
		addListener(new AnimatedActor.Listener() {
			@Override
			public void animationShown(AnimatedActor actor, Animation animation) {
				if (animation.tag().equals(Config.AnimationOn)) {
					addState(Config.StateOn);
					removeState(Config.StateOff);
				}
			}

			@Override
			public void animationHidden(AnimatedActor actor, Animation animation) {
				if (animation.tag().equals(Config.AnimationOn)) {
					addState(Config.StateOff);
					removeState(Config.StateOn);
				}
			}
		});
		showAnimation(Config.AnimationOff);
	}

	@Override
	public void act(int subtick) {
		if (subtick == PlayScreen.SUBTICKS.BUTTON_PRESSES.ordinal()) {
			_isOn = false;
			loop:
			for (TiledStage.Coordinate bodyCoordinate : bodyCoordinates()) {
				for (TiledStageActor actor : bodyCoordinate.actors()) {
					if (actor instanceof Player || actor instanceof Lodestone) {
						_isOn = true;
						break loop;
					}
				}
			}

		} else if (subtick == PlayScreen.SUBTICKS.GRAPHICS.ordinal()) {

			if (_isOn) {
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

	// get/set
	// ---------
	@Override
	public int[] subticks() {
		return SUBTICKS;
	}

	public static class Config {
		public static String AnimationOning = "Oning";
		public static String AnimationOn = "On";
		public static String AnimationOffing = "Offing";
		public static String AnimationOff = "Off";
		public static String StateOn = "On";
		public static String StateOff = "Off";
	}
}
