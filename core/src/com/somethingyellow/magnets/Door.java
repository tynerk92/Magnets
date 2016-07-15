package com.somethingyellow.magnets;

import com.somethingyellow.graphics.AnimationDef;
import com.somethingyellow.tiled.TiledStage;
import com.somethingyellow.tiled.TiledStageActor;

import java.util.Map;

public class Door extends TiledStageActor {

	public static final int[] SUBTICKS_STATIC = new int[]{
			PlayScreen.SUBTICKS.DOORS.ordinal()
	};

	public Door() {
		super();
		SUBTICKS = SUBTICKS_STATIC;
	}

	public void initialize(TiledStage stage, Map<String, AnimationDef> animationDefs, TiledStage.Coordinate origin, boolean toOpen) {
		super.initialize(stage, animationDefs, origin);

		setTransition(Config.AnimationOpening, Config.AnimationOpened);
		setTransition(Config.AnimationClosing, Config.AnimationClosed);
		setStatus(Config.StatusToOpen, toOpen);
		setStatus(Config.StatusOpened, true);
		showAnimation(Config.AnimationOpened);
	}

	@Override
	public void subtick(int subtick) {
		if (subtick == PlayScreen.SUBTICKS.DOORS.ordinal()) {

			if (hasStatus(Config.StatusToOpen)) {
				setStatus(Config.StatusOpened, true);
			} else {
				if (hasStatus(Config.StatusOpened)) {
					// To close the door
					// Check if blocked by anything to prevent it from closing
					boolean ifBlocked = false;
					loop:
					for (TiledStage.Coordinate bodyCoordinate : bodyCoordinates()) {
						for (TiledStageActor actor : bodyCoordinate.actors()) {
							if (actor != this && actor.isSolid()) {
								ifBlocked = true;
								break loop;
							}
						}
					}

					if (!ifBlocked) setStatus(Config.StatusOpened, false);
				}
			}
		}
	}

	@Override
	public void updateAnimation() {

		if (isAnimationActive(Config.AnimationOpened)) {
			if (!hasStatus(Config.StatusOpened)) {
				hideAllButAnimations(Config.AnimationClosing);
			}
		} else if (isAnimationActive(Config.AnimationClosed)) {
			if (hasStatus(Config.StatusOpened)) {
				hideAllButAnimations(Config.AnimationOpening);
			}
		}
	}

	public void open() { setToOpen(true); }

	public void close() {
		setToOpen(false);
	}

	private void setToOpen(boolean toOpen) {
		setStatus(Config.StatusToOpen, toOpen);
	}

	@Override
	public boolean isSolid() {
		return !hasStatus(Config.StatusOpened);
	}

	public boolean isOpen() {
		return hasStatus(Config.StatusOpened);
	}

	public static class Config {
		public static String AnimationOpening = "Opening";
		public static String AnimationOpened = "Opened";
		public static String AnimationClosing = "Closing";
		public static String AnimationClosed = "Closed";
		public static String StatusToOpen = "ToOpen"; // When door should be opened
		public static String StatusOpened = "Opened"; // When door is opened
	}
}
