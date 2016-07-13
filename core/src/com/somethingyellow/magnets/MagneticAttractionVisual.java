package com.somethingyellow.magnets;

import com.somethingyellow.tiled.TiledStage;
import com.somethingyellow.tiled.TiledStageActor;

public class MagneticAttractionVisual extends TiledStageActor {
	public static final int[] SUBTICKS_STATIC = new int[]{
			PlayScreen.SUBTICKS.START.ordinal(),
			PlayScreen.SUBTICKS.END.ordinal()
	};

	private boolean _hasArrowUp;
	private boolean _hasArrowDown;
	private boolean _hasArrowLeft;
	private boolean _hasArrowRight;

	public MagneticAttractionVisual() {
		super();
		SUBTICKS = SUBTICKS_STATIC;
	}

	@Override
	public void subtick(int subtick) {
		if (subtick == PlayScreen.SUBTICKS.START.ordinal()) {

			_hasArrowUp = false;
			_hasArrowDown = false;
			_hasArrowRight = false;
			_hasArrowLeft = false;

		} else if (subtick == PlayScreen.SUBTICKS.END.ordinal()) {

			// If no arrows pointing, remove itself
			if (!_hasArrowUp && !_hasArrowDown && !_hasArrowLeft && !_hasArrowRight) {
				remove();
			}

		}
	}

	public void addAttraction(TiledStage.DIRECTION direction) {
		switch (direction) {
			case NORTH:
				_hasArrowUp = true;
				break;
			case SOUTH:
				_hasArrowDown = true;
				break;
			case EAST:
				_hasArrowRight = true;
				break;
			case WEST:
				_hasArrowLeft = true;
				break;
		}
	}

	@Override
	public String getName() {
		return "ARROW!!";
	}

	@Override
	public void updateAnimation() {
		setAnimationShown(Config.AnimationArrowUp, _hasArrowUp);
		setAnimationShown(Config.AnimationArrowDown, _hasArrowDown);
		setAnimationShown(Config.AnimationArrowLeft, _hasArrowLeft);
		setAnimationShown(Config.AnimationArrowRight, _hasArrowRight);
	}

	public static class Config {
		public static String AnimationArrowLeft = "Attraction Arrow Left";
		public static String AnimationArrowRight = "Attraction Arrow Right";
		public static String AnimationArrowUp = "Attraction Arrow Up";
		public static String AnimationArrowDown = "Attraction Arrow Down";
	}
}
