package com.somethingyellow.tiled;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * Tracks changes in a TiledStage over its ticks
 * Supports restoring of the TiledStage to a previous tick
 */

public class TiledStageHistorian {
	private TiledStage _stage;
	private TiledStageListener _tiledStageListener = new TiledStageListener();
	private TiledStageActorListener _tiledStageActorListener = new TiledStageActorListener();
	private LinkedList<HashMap<TiledStageActor, TiledStageActor.State>> _history;
	private HashSet<TiledStageActor> _actorsChangedSinceSave;
	private ArrayList<Integer> _historyTickNos;

	public TiledStageHistorian(TiledStage stage) {
		_stage = stage;
		_history = new LinkedList<HashMap<TiledStageActor, TiledStageActor.State>>();
		_historyTickNos = new ArrayList<Integer>();
		_actorsChangedSinceSave = new HashSet<TiledStageActor>();

		// Add listener to stage
		_stage.listeners().add(_tiledStageListener);

		// Add listeners to the actors already on stage
		for (TiledStageActor actor : _stage.actors()) {
			actor.listeners().add(_tiledStageActorListener);
			_actorsChangedSinceSave.add(actor);
		}
	}

	public void reset() {
		_historyTickNos.clear();
		for (HashMap<TiledStageActor, TiledStageActor.State> states : _history) {
			states.clear();
		}
		_history.clear();
	}

	/**
	 * Save stage at current tick no, returning tick no
	 */
	public int save() {
		if (!_actorsChangedSinceSave.isEmpty()) {
			HashMap<TiledStageActor, TiledStageActor.State> states = new HashMap<TiledStageActor, TiledStageActor.State>();
			for (TiledStageActor actor : _actorsChangedSinceSave) {
				states.put(actor, actor.getState());
			}
			_history.add(states);
			// System.out.println("TICK " + _stage.tickNo() + " SAVED: " + states);
			_historyTickNos.add(_stage.tickNo()); // Remember tick no for stage at this time
			_actorsChangedSinceSave.clear();
		}

		return _stage.tickNo();
	}

	/**
	 * Revert stage to last save
	 */
	public void revert(int time) {
		if (_history.size() > 0) {
			HashMap<TiledStageActor, TiledStageActor.State> states = _history.getLast();
			// System.out.println("INVALIDATING TICK " + _historyTickNos.get(_historyTickNos.size() - 1));
			for (TiledStageActor actor : states.keySet()) {
				// Search up history to find the most recent previous state for actor, and restore it
				int index = _history.size() - 2;
				while (index >= 0 && !_history.get(index).keySet().contains(actor)) index--;
				if (index >= 0) {
					_history.get(index).get(actor).restore(time);
					// System.out.println("RESTORED: " + actor);
				}
			}
			_actorsChangedSinceSave.clear();

			if (_history.size() > 1) {
				states.clear();
				_history.removeLast();
				_historyTickNos.remove(_historyTickNos.size() - 1);
			}
		}
	}

	private class TiledStageListener extends TiledStage.Listener {
		@Override
		public void actorAdded(TiledStage tiledStage, TiledStageActor actor) {
			super.actorAdded(tiledStage, actor);
			actor.listeners().add(_tiledStageActorListener);
			// System.out.println("ADDED: " + actor);
			_actorsChangedSinceSave.add(actor);
		}
	}

	private class TiledStageActorListener extends TiledStageActor.Listener {
		@Override
		public void stateChanged(TiledStageActor actor) {
			super.stateChanged(actor);
			// System.out.println("CHANGED: " + actor);
			_actorsChangedSinceSave.add(actor);
		}
	}
}
