package com.somethingyellow.magnets;

import com.somethingyellow.tiled.TiledStage;
import com.somethingyellow.tiled.TiledStageActor;

import java.util.HashMap;

public class Door extends TiledStageActor {
	public static final int[] SUBTICKS = new int[]{
			PlayScreen.SUBTICKS.GRAPHICS.ordinal()
	};

	private boolean _isOpen; // whether the door IS open
	private boolean _toOpen; // whether the door SHOULD be open

	public void initialize(boolean[] bodyArea, int bodyWidth, HashMap<String, FrameSequence> animationFrames,
	                       TiledStage.Coordinate origin, boolean toOpen) {
		super.initialize(bodyArea, bodyWidth, animationFrames, origin);

		_toOpen = toOpen;
		_isOpen = false;

		// Frame events
		getStateFrames(Config.DOOR_STATE_OPENING).setListener(new TiledStageActor.FrameSequenceListener() {
			@Override
			public void ended() {
				addState(Config.DOOR_STATE_OPENED);
				removeState(Config.DOOR_STATE_OPENING);
			}
		});

		getStateFrames(Config.DOOR_STATE_CLOSING).setListener(new TiledStageActor.FrameSequenceListener() {
			@Override
			public void ended() {
				addState(STATE_DEFAULT);
				removeState(Config.DOOR_STATE_CLOSING);
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
				if (hasState(STATE_DEFAULT)) {
					addState(Config.DOOR_STATE_OPENING);
					removeState(STATE_DEFAULT);
				}
			} else {
				if (hasState(Config.DOOR_STATE_OPENED)) {
					addState(Config.DOOR_STATE_CLOSING);
					removeState(Config.DOOR_STATE_OPENED);
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
