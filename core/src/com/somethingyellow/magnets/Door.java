package com.somethingyellow.magnets;

import com.somethingyellow.graphics.AnimatedActor;
import com.somethingyellow.graphics.Animation;
import com.somethingyellow.graphics.AnimationDef;
import com.somethingyellow.tiled.TiledStage;
import com.somethingyellow.tiled.TiledStageActor;

import java.util.Map;

public class Door extends TiledStageActor {

	public static final int[] SUBTICKS = new int[]{
			PlayScreen.SUBTICKS.GRAPHICS.ordinal()
	};
	private boolean _isOpen; // whether the door IS open
	private boolean _toOpen; // whether the door SHOULD be open

	public void initialize(Map<String, AnimationDef> animationDefs, boolean[] bodyArea, int bodyWidth, TiledStage.Coordinate origin, boolean toOpen) {
		super.initialize(animationDefs, bodyArea, bodyWidth, origin);

		_toOpen = toOpen;
		_isOpen = false;

		setTransition(Config.AnimationOpening, Config.AnimationOpened);
		setTransition(Config.AnimationClosing, Config.AnimationClosed);
		listeners().add(new AnimatedActor.Listener() {
			@Override
			public void animationShown(AnimatedActor actor, Animation animation) {
				if (animation.tag().equals(Config.AnimationOpened)) {
					addStatus(Config.StateOpened);
					removeStatus(Config.StateClosed);
				}
			}

			@Override
			public void animationHidden(AnimatedActor actor, Animation animation) {
				if (animation.tag().equals(Config.AnimationOpened)) {
					addStatus(Config.StateClosed);
					removeStatus(Config.StateOpened);
				}
			}
		});

		showAnimation(Config.AnimationClosed);
	}

	@Override
	public void act(int subtick) {
		if (subtick == PlayScreen.SUBTICKS.GRAPHICS.ordinal()) {

			if (_toOpen && !_isOpen) {
				_isOpen = true;

			} else if (!_toOpen && _isOpen) {
				// Check if blocked by anything to prevent it from opening
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
					_isOpen = false;
				}
			}


			if (_isOpen) {
				if (isAnimationActive(Config.AnimationClosed)) {
					showAnimation(Config.AnimationOpening);
					hideAnimation(Config.AnimationClosed);
				}
			} else {
				if (isAnimationActive(Config.AnimationOpened)) {
					showAnimation(Config.AnimationClosing);
					hideAnimation(Config.AnimationOpened);
				}
			}
		}
	}

	public void open() {
		_toOpen = true;
	}

	public void close() {
		_toOpen = false;
	}

	@Override
	public boolean bodyCanBeAt(TiledStage.Coordinate coordinate) {
		return true;
	}

	public boolean isOpen() {
		return _isOpen;
	}

	// get/set
	// ---------

	@Override
	public int[] subticks() {
		return SUBTICKS;
	}

	public static class Config {
		public static String AnimationOpening = "Opening";
		public static String AnimationOpened = "Opened";
		public static String AnimationClosing = "Closing";
		public static String AnimationClosed = "Closed";
		public static String StateOpened = "Opened";
		public static String StateClosed = "Closed";
	}
}
