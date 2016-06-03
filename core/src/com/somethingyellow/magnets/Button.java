package com.somethingyellow.magnets;

import com.somethingyellow.tiled.TiledStage;
import com.somethingyellow.tiled.TiledStageActor;

import java.util.HashMap;
import java.util.Set;

public class Button extends TiledStageActor {
	public static final String STATE_OFF = "Off";
	public static final String STATE_ON = "On";
	public static final String STATE_OFFING = "Offing";
	public static final String STATE_ONING = "Oning";
	public static final int[] SUBTICKS = new int[]{
			PlayScreen.SUBTICKS.BUTTON_PRESSES.ordinal(),
			PlayScreen.SUBTICKS.GRAPHICS.ordinal()
	};

	private boolean _isOn;

	public void initialize(TiledStage stage, boolean[] bodyArea, int bodyWidth, HashMap<String, FrameSequence> animationFrames,
	                       TiledStage.Coordinate origin) {
		super.initialize(stage, bodyArea, bodyWidth, animationFrames, origin);

		addState(STATE_OFF);

		// Frame events
		getStateFrames(STATE_ONING).setListener(new TiledStageActor.FrameSequenceListener() {
			@Override
			public void ended() {
				addState(STATE_ON).removeState(STATE_ONING);
			}
		});

		getStateFrames(STATE_OFFING).setListener(new TiledStageActor.FrameSequenceListener() {
			@Override
			public void ended() {
				addState(STATE_OFF).removeState(STATE_OFFING);
			}
		});
	}

	@Override
	public void reset() {
		super.reset();
		_isOn = false;
	}

	@Override
	public void act(int subtick) {
		if (subtick == PlayScreen.SUBTICKS.BUTTON_PRESSES.ordinal()) {

			_isOn = false;
			loop:
			for (TiledStage.Coordinate bodyCoordinate : bodyCoordinates()) {
				for (TiledStageActor actor : bodyCoordinate.actors()) {
					if (actor instanceof Player || actor instanceof Block) {
						_isOn = true;
						break loop;
					}
				}
			}

		} else if (subtick == PlayScreen.SUBTICKS.GRAPHICS.ordinal()) {

			if (_isOn) {
				if (hasState(STATE_OFF)) {
					addState(STATE_ONING).removeState(STATE_OFF);
				}
			} else {
				if (hasState(STATE_ON)) {
					addState(STATE_OFFING).removeState(STATE_ON);
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
