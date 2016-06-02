package com.somethingyellow.magnets;

import com.somethingyellow.tiled.TiledStage;
import com.somethingyellow.tiled.TiledStageActor;

import java.util.HashMap;
import java.util.Set;

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

	private boolean _isOpen; // whether the door IS open
	private boolean _toOpen; // whether the door SHOULD be open

	public Door(boolean[] bodyArea, int bodyWidth, HashMap<String, FrameSequence> animationFrames,
	            TiledStage stage, TiledStage.Coordinate origin, int actorDepth, boolean toOpen) {
		super(bodyArea, bodyWidth, animationFrames, stage, origin, actorDepth);

		_toOpen = toOpen;
		_isOpen = false;
		addState(STATE_CLOSED);

		// Frame events
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

			if (_toOpen && !_isOpen) {
				_isOpen = true;

			} else if (!_toOpen && _isOpen) {
				// Check if blocked by anything to prevent it from opening
				boolean ifBlocked = false;

				loop:
				for (TiledStage.Coordinate bodyCoordinate : bodyCoordinates()) {
					for (TiledStageActor actor : bodyCoordinate.actors()) {
						if (actor instanceof Block || actor instanceof Player) {
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
		_toOpen = true;
	}

	public void close() {
		_toOpen = false;
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
	public int[] subticks() {
		return SUBTICKS;
	}
}
