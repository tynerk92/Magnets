package com.somethingyellow.magnets;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.somethingyellow.tiled.TiledStage;
import com.somethingyellow.tiled.TiledStageActor;

import java.util.HashMap;

public class Door extends TiledStageActor {
	public static final String STATE_OPENED = "Opened";
	public static final String STATE_OPENING = "Opening";
	public static final String STATE_CLOSING = "Closing";
	public static final String STATE_CLOSED = "Closed";
	public static final String ACTION_OPEN = "Open";
	public static final String ACTION_CLOSE = "Close";
	public static final float OPENING_DURATION = 0.6f;
	public static final float CLOSING_DURATION = 0.6f;

	public static final int[] TICKS = new int[]{
	};

	private boolean _isOpen;

	public Door(HashMap<String, FrameSequence> animationFrames,
	            TiledStage stage, TiledStage.Coordinate origin, int actorDepth, boolean isOpen) {
		super(TiledStageActor.BodyArea1x1, 1, animationFrames, stage, origin, actorDepth);

		_isOpen = isOpen;
		if (_isOpen) addState(STATE_OPENED);
		else addState(STATE_CLOSED);
	}

	@Override
	public void act(int tick) {

	}

	public void open() {
		if (!_isOpen && hasState(STATE_CLOSED)) {
			addState(STATE_OPENING).removeState(STATE_CLOSED);
			final Door door = this;

			addAction(Actions.delay(OPENING_DURATION, Actions.run(new Runnable() {
				@Override
				public void run() {
					door.addState(STATE_OPENED).removeState(STATE_OPENING);
					door._isOpen = true;
				}
			})));
		}
	}

	public void close() {
		if (_isOpen && hasState(STATE_OPENED)) {
			addState(STATE_CLOSING).removeState(STATE_OPENED);
			final Door door = this;

			addAction(Actions.delay(CLOSING_DURATION, Actions.run(new Runnable() {
				@Override
				public void run() {
					door.addState(STATE_CLOSED).removeState(STATE_CLOSED);
					door._isOpen = false;
				}
			})));
		}
	}

	@Override
	public boolean bodyCanBeAt(TiledStage.Coordinate coordinate) {
		return true;
	}

	// get/set
	// ---------

	public boolean isOpen() {
		return _isOpen;
	}

	@Override
	public int[] TICKS() {
		return TICKS;
	}
}
