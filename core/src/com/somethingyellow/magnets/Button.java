package com.somethingyellow.magnets;

import com.somethingyellow.tiled.TiledStage;
import com.somethingyellow.tiled.TiledStageActor;

import java.util.HashMap;

public class Button extends TiledStageActor {
	public static final int[] SUBTICKS = new int[]{
			PlayScreen.SUBTICKS.BUTTON_PRESSES.ordinal(),
			PlayScreen.SUBTICKS.GRAPHICS.ordinal()
	};

	private boolean _isOn;

	public void initialize(boolean[] bodyArea, int bodyWidth, HashMap<String, FrameSequence> animationFrames,
	                       TiledStage.Coordinate origin) {
		super.initialize(bodyArea, bodyWidth, animationFrames, origin);

		_isOn = false;

		// Frame events
		getStateFrames(Config.BUTTON_STATE_ONING).setListener(new TiledStageActor.FrameSequenceListener() {
			@Override
			public void ended() {
				addState(Config.BUTTON_STATE_ON);
				removeState(Config.BUTTON_STATE_ONING);
			}
		});

		getStateFrames(Config.BUTTON_STATE_OFFING).setListener(new TiledStageActor.FrameSequenceListener() {
			@Override
			public void ended() {
				addState(STATE_DEFAULT);
				removeState(Config.BUTTON_STATE_OFFING);
			}
		});
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
				if (hasState(STATE_DEFAULT)) {
					addState(Config.BUTTON_STATE_ONING);
					removeState(STATE_DEFAULT);
				}
			} else {
				if (hasState(Config.BUTTON_STATE_ON)) {
					addState(Config.BUTTON_STATE_OFFING);
					removeState(Config.BUTTON_STATE_ON);
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
}
