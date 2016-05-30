package com.somethingyellow.magnets;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.somethingyellow.tiled.TiledStage;
import com.somethingyellow.tiled.TiledStageActor;

import java.util.HashMap;

public class Button extends TiledStageActor {
	public static final String STATE_OFF = "Off";
	public static final String STATE_ON = "On";
	public static final String STATE_OFFING = "Offing";
	public static final String STATE_ONING = "Oning";
	public static final int[] SUBTICKS = new int[]{
			PlayScreen.SUBTICKS.RESET.ordinal(),
			PlayScreen.SUBTICKS.GRAPHICS.ordinal()
	};

	private boolean _isOn;

	public Button(HashMap<String, FrameSequence> animationFrames,
	              TiledStage stage, TiledStage.Coordinate origin, int actorDepth) {
		super(TiledStageActor.BodyArea1x1, 1, animationFrames, stage, origin, actorDepth);

		_isOn = false;
		addState(STATE_OFF);

		// Frame events
		final Button button = this;
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
	public void act(int subtick) {
		if (subtick == PlayScreen.SUBTICKS.RESET.ordinal()) {

			_isOn = false;

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

	public void on() {
		_isOn = true;
	}

	@Override
	public boolean bodyCanBeAt(TiledStage.Coordinate coordinate) {
		return true;
	}

	// get/set
	// ---------

	@Override
	public int[] SUBTICKS() {
		return SUBTICKS;
	}
}
