package com.somethingyellow.utility;

/**
 * Wraps a method call and calls it (repeatedly) based on given array of delays
 * End the array with a negative int to denote a recurring delay
 */

public class TimedTrigger {
	private int[] _timings;
	private Trigger _trigger;
	private float _time;
	private int _index;

	public TimedTrigger(Trigger trigger, int[] timings) {
		_time = 0;
		_index = 0;
		_timings = timings;
		_trigger = trigger;
	}

	public void reset() {
		_time = 0;
		_index = 0;
	}

	public void update(float deltaTime) {
		_time += deltaTime;

		while (_index < _timings.length) {
			if (_timings[_index] >= 0) {
				if (_timings[_index] <= _time) {
					_time -= _timings[_index];
					_trigger.activate();
					_index++;
				} else break;
			} else {
				if (-_timings[_index] <= _time) {
					_time -= -_timings[_index];
					_trigger.activate();
				} else break;
			}
		}
	}

	public interface Trigger {
		void activate();
	}
}
