package com.somethingyellow.tiled;

import java.util.HashMap;

public class TiledStageVisual extends TiledStageBody {

	private boolean _removeAtEnd;
	private int _duration;

	public void initialize(boolean[] bodyArea, int bodyWidth, FrameSequence frameSequence, TiledStage.Coordinate origin) {

		HashMap<String, TiledStageActor.FrameSequence> frameSequenceList = new HashMap<String, TiledStageActor.FrameSequence>();
		frameSequenceList.put(STATE_DEFAULT, frameSequence);

		super.initialize(bodyArea, bodyWidth, frameSequenceList, origin);
		_removeAtEnd = false;
		_duration = Integer.MAX_VALUE;
	}

	@Override
	public void act() {
		super.act();
		if (_duration > 0) {
			_duration--;
		} else {
			remove();
		}
	}

	public void setDuration(int ticks) {
		_duration = ticks;
	}

	public int duration() {
		return _duration;
	}

	public void setRemoveAtEnd(boolean removeAtEnd) {
		_removeAtEnd = removeAtEnd;

		FrameSequence frames = getStateFrames(STATE_DEFAULT);
		if (_removeAtEnd) {
			// When frame sequence ends, remove visual
			final TiledStageVisual me = this;
			frames.setListener(new FrameSequenceListener() {
				@Override
				public void ended() {
					me.remove();
				}
			});
		} else {
			frames.removeListener();
		}
	}
}
