package com.somethingyellow.magnets;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.somethingyellow.tiled.TiledStage;
import com.somethingyellow.tiled.TiledStageActor;

import java.util.HashMap;
import java.util.TreeSet;

public class Button extends TiledStageActor {
	public static final String STATE_OFF = "Off";
	public static final String STATE_ON = "On";
	public static final String STATE_OFFING = "Offing";
	public static final String STATE_ONING = "Oning";
	public static final float OFFING_DURATION = 0.2f;
	public static final float ONING_DURATION = 0.2f;
	public static final int[] TICKS = new int[]{
			PlayScreen.TICKS.RESET.ordinal(),
			PlayScreen.TICKS.GRAPHICS.ordinal()
	};

	private boolean _isOn;

	public Button(HashMap<String, FrameSequence> animationFrames,
	              TiledStage stage, TiledStage.Coordinate origin, int actorDepth) {
		super(TiledStageActor.BodyArea1x1, 1, animationFrames, stage, origin, actorDepth);

		_isOn = false;
		addState(STATE_OFF);
	}

	@Override
	public void act(int tick) {
		if (tick == PlayScreen.TICKS.RESET.ordinal()) {
			_isOn = false;
		} else if (tick == PlayScreen.TICKS.GRAPHICS.ordinal()) {
			if (_isOn) {
				if (hasState(STATE_OFF)) {
					addState(STATE_ONING).removeState(STATE_OFF);
					final Button button = this;

					addAction(Actions.delay(ONING_DURATION, Actions.run(new Runnable() {
						@Override
						public void run() {
							button.addState(STATE_ON).removeState(STATE_ONING);
						}
					})));
				}
			} else {
				if (hasState(STATE_ON)) {
					addState(STATE_OFFING).removeState(STATE_ON);
					final Button button = this;

					addAction(Actions.delay(OFFING_DURATION, Actions.run(new Runnable() {
						@Override
						public void run() {
							button.addState(STATE_OFF).removeState(STATE_OFFING);
						}
					})));
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
	public int[] TICKS() {
		return TICKS;
	}
}
