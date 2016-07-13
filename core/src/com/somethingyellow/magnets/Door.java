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

	public void initialize(Map<String, AnimationDef> animationDefs, TiledStage.Coordinate origin, boolean toOpen) {
		super.initialize(animationDefs, origin);

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
				addStatus(Config.StatusOpened);
			} else {
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

				setStatus(Config.StatusOpened, ifBlocked);
			}
		}
	}

	@Override
	public void updateAnimation() {
		if (hasStatus(Config.StatusOpened)) {
			if (!isAnimationActive(Config.AnimationOpened) && !isAnimationActive(Config.AnimationOpening)) {
				hideAllAnimations();
				showAnimation(Config.AnimationOpening);
			}
		} else {
			if (!isAnimationActive(Config.AnimationClosed) && !isAnimationActive(Config.AnimationClosing)) {
				hideAllAnimations();
				showAnimation(Config.AnimationClosing);
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
