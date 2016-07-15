package com.somethingyellow.magnets;

import com.somethingyellow.graphics.AnimationDef;
import com.somethingyellow.tiled.TiledStage;
import com.somethingyellow.tiled.TiledStageActor;

import java.util.Map;

public class MagneticAttractionVisual extends TiledStageActor {
	public static final int[] SUBTICKS_STATIC = new int[]{
			PlayScreen.SUBTICKS.START.ordinal(),
			PlayScreen.SUBTICKS.END.ordinal()
	};

	private boolean _hasArrowUp = false;
	private boolean _hasArrowDown = false;
	private boolean _hasArrowLeft = false;
	private boolean _hasArrowRight = false;

	public MagneticAttractionVisual() {
		super();
		SUBTICKS = SUBTICKS_STATIC;
	}

	public void initialize(TiledStage stage, Map<String, AnimationDef> animationDefs) {
		super.initialize(stage, animationDefs, null);
	}

	@Override
	public void subtick(int subtick) {
		if (subtick == PlayScreen.SUBTICKS.START.ordinal()) {

			_hasArrowUp = false;
			_hasArrowDown = false;
			_hasArrowRight = false;
			_hasArrowLeft = false;

		} else if (subtick == PlayScreen.SUBTICKS.END.ordinal()) {

			setStatus(Config.StatusAttractedUp, _hasArrowUp);
			setStatus(Config.StatusAttractedDown, _hasArrowDown);
			setStatus(Config.StatusAttractedLeft, _hasArrowLeft);
			setStatus(Config.StatusAttractedRight, _hasArrowRight);

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
	public void updateAnimation() {
		setAnimationShown(Config.AnimationArrowUp, hasStatus(Config.StatusAttractedUp));
		setAnimationShown(Config.AnimationArrowDown, hasStatus(Config.StatusAttractedDown));
		setAnimationShown(Config.AnimationArrowLeft, hasStatus(Config.StatusAttractedLeft));
		setAnimationShown(Config.AnimationArrowRight, hasStatus(Config.StatusAttractedRight));
	}

	public static class Config {
		public static String AnimationArrowLeft = "Attraction Arrow Left";
		public static String AnimationArrowRight = "Attraction Arrow Right";
		public static String AnimationArrowUp = "Attraction Arrow Up";
		public static String AnimationArrowDown = "Attraction Arrow Down";
		public static String StatusAttractedLeft = "Attracted Left";
		public static String StatusAttractedRight = "Attracted Right";
		public static String StatusAttractedUp = "Attracted Up";
		public static String StatusAttractedDown = "Attracted Down";
	}
}
