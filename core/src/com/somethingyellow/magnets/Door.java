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

	public static final int[] SUBTICKS = new int[]{
			PlayScreen.SUBTICKS.GRAPHICS.ordinal()
	};

	private boolean _isOpen;

	public Door(HashMap<String, FrameSequence> animationFrames,
	            TiledStage stage, TiledStage.Coordinate origin, int actorDepth, boolean isOpen) {
		super(TiledStageActor.BodyArea1x1, 1, animationFrames, stage, origin, actorDepth);

		_isOpen = isOpen;
		if (_isOpen) {
			addState(STATE_OPENED);
		} else {
			addState(STATE_CLOSED);
		}

		// Frame events
		final Door door = this;
		getStateFrames(STATE_OPENING).setListener(new TiledStageActor.FrameSequenceListener() {
			@Override
			public void ended() {
				addState(STATE_OPENED).removeState(STATE_OPENING);
			}
		});

		getStateFrames(STATE_CLOSING).setListener(new TiledStageActor.FrameSequenceListener() {
			@Override
			public void ended() {
				addState(STATE_CLOSED).removeState(STATE_CLOSING);
			}
		});
	}

	@Override
	public void act(int subtick) {
		if (subtick == PlayScreen.SUBTICKS.GRAPHICS.ordinal()) {
			if (_isOpen) {
				if (hasState(STATE_CLOSED)) {
					addState(STATE_OPENING).removeState(STATE_CLOSED);
				}
			} else {
				if (hasState(STATE_OPENED)) {
					addState(STATE_CLOSING).removeState(STATE_OPENED);
				}
			}
		}
	}

	public void open() {
		_isOpen = true;
	}

	public void close() {
		_isOpen = false;
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
	public int[] SUBTICKS() {
		return SUBTICKS;
	}
}
