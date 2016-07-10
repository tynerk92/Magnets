package com.somethingyellow.magnets;

import com.somethingyellow.graphics.AnimatedActor;
import com.somethingyellow.graphics.Animation;
import com.somethingyellow.graphics.AnimationDef;
import com.somethingyellow.tiled.TiledStage;
import com.somethingyellow.tiled.TiledStageActor;

import java.util.Map;

public class Door extends TiledStageActor {

	public static final int[] SUBTICKS_STATIC = new int[]{
			PlayScreen.SUBTICKS.GRAPHICS.ordinal()
	};

	public void initialize(Map<String, AnimationDef> animationDefs, boolean[] bodyArea, int bodyWidth, TiledStage.Coordinate origin, boolean toOpen) {
		super.initialize(animationDefs, bodyArea, bodyWidth, origin);
		SUBTICKS = SUBTICKS_STATIC;

		setTransition(Config.AnimationOpening, Config.AnimationOpened);
		setTransition(Config.AnimationClosing, Config.AnimationClosed);
		listeners().add(new AnimatedActor.Listener() {
			@Override
			public void animationShown(AnimatedActor actor, Animation animation) {
				if (animation.tag().equals(Config.AnimationOpened)) {
					addStatus(Config.StatusOpened);
					removeStatus(Config.StatusClosed);
				}
			}

			@Override
			public void animationHidden(AnimatedActor actor, Animation animation) {
				if (animation.tag().equals(Config.AnimationOpened)) {
					addStatus(Config.StatusClosed);
					removeStatus(Config.StatusOpened);
				}
			}
		});

		setStatus(Config.StatusToOpen, toOpen);
		addStatus(Config.StatusClosed);
		showAnimation(Config.AnimationClosed);
	}

	@Override
	public void tick(int subtick) {
		if (subtick == PlayScreen.SUBTICKS.GRAPHICS.ordinal()) {

			if (hasStatus(Config.StatusToOpen)) {
				if (isAnimationActive(Config.AnimationClosed)) {
					showAnimation(Config.AnimationOpening);
					hideAnimation(Config.AnimationClosed);
				}
			} else { // To close the door
				if (isAnimationActive(Config.AnimationOpened)) {
					// Check if blocked by anything to prevent it from closing
					boolean ifBlocked = false;

					loop:
					for (TiledStage.Coordinate bodyCoordinate : bodyCoordinates()) {
						for (TiledStageActor actor : bodyCoordinate.actors()) {
							if (actor instanceof Lodestone || actor instanceof Player) {
								ifBlocked = true;
								break loop;
							}
						}
					}

					if (!ifBlocked) {
						showAnimation(Config.AnimationClosing);
						hideAnimation(Config.AnimationOpened);
					}
				}
			}
		}
	}

	public void open() {
		setStatus(Config.StatusToOpen, true);
	}

	public void close() {
		setStatus(Config.StatusToOpen, false);
	}

	@Override
	public boolean bodyCanBeAt(TiledStage.Coordinate coordinate) {
		return true;
	}

	public boolean isOpen() {
		return hasStatus(Config.StatusOpened);
	}

	public static class Config {
		public static String AnimationOpening = "Opening";
		public static String AnimationOpened = "Opened";
		public static String AnimationClosing = "Closing";
		public static String AnimationClosed = "Closed";
		public static String StatusOpened = "Opened"; // When door shows `Opened` animation
		public static String StatusClosed = "Closed"; // When door hides `Opened` animation
		public static String StatusToOpen = "ToOpen"; // When door is to open, before considering any objects on top
	}
}
