package com.somethingyellow.tiled;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * Resolves motion of actors in TiledStage
 * To call addMotion() and resolveActorMotion()
 */

public class TiledStageMotionResolver {
	private TiledStage _stage;
	private HashMap<TiledStageActor, Motion> _actorMotions;
	private LinkedList<TiledStageActor> TempActorsList = new LinkedList<TiledStageActor>();
	private HashSet<TiledStage.Coordinate> TempCoordinatesSet = new HashSet<TiledStage.Coordinate>();
	private HashSet<TiledStageActor> TempActorsSet = new HashSet<TiledStageActor>();
	private LinkedList<TiledStageActor> ActorsResolved = new LinkedList<TiledStageActor>();
	private LinkedList<TiledStageActor> InterdependentActors = new LinkedList<TiledStageActor>();
	private HashMap<TiledStageActor, HashSet<TiledStageActor>> ActorMotionDependencies = new HashMap<TiledStageActor, HashSet<TiledStageActor>>();

	public TiledStageMotionResolver(TiledStage stage) {
		_stage = stage;
		_actorMotions = new HashMap<TiledStageActor, Motion>();
	}

	public void addMotion(TiledStageActor actor, Motion motion) {
		_actorMotions.put(actor, motion);
	}

	public boolean hasMotion(TiledStageActor actor) {
		return _actorMotions.containsKey(actor);
	}

	/**
	 * Resolves actors' motion, considering their intended motions together and their bodies
	 */
	public void resolveActorMotion() {
		TempActorsSet.clear();

		// Processing actors by initiative
		// Filter out actors which are not intending to move or cannot move (due to wall/body cannot exist there)
		TempActorsList.clear();
		LinkedList<TiledStageActor> actors = _stage.actorsByInitiative();
		for (TiledStageActor actor : actors) {
			if (_actorMotions.containsKey(actor)) {
				Motion motion = _actorMotions.get(actor);
				if (canBeAt(actor, motion.direction())) {
					TempActorsList.add(actor);
				}
			}
		}
		actors.clear();
		actors.addAll(TempActorsList);

		// Find those without dependencies and move them first
		ActorsResolved.clear();
		do {
			ActorMotionDependencies.clear();
			boolean ifRestart = false;

			for (TiledStageActor actor : actors) {
				Motion motion = _actorMotions.get(actor);

				// Get all actors that are blocking if it moves in its intended direction
				HashSet<TiledStageActor> blockingActors = getActorsBlocking(actor, motion.direction());
				if (blockingActors.isEmpty()) { // Not blocked

					actor.moveDirection(motion.direction(), motion.ticks());
					ActorsResolved.add(actor);
					ifRestart = true;
					break; // Actor moved, need to restart

				} else {
					// For all the blocking actors, simulate all their movements
					// If all of them move (might not be feasible though, but algo accounts for it),
					// if actor is still blocked, actor is definitely unable to move (resolved)

					// If any of the blocking actors are not moving or don't move in the same direction, actor definitely blocked
					// At the same time, for all blocking actors, record which coordinate they block after they move
					TempCoordinatesSet.clear();

					for (TiledStageActor blockingActor : blockingActors) {
						if (!actors.contains(blockingActor) || _actorMotions.get(blockingActor).direction() != motion.direction()) {
							ActorsResolved.add(actor);
							ifRestart = true;
							break;
						}

						// If blocking actor is moving and its motion hasn't been resolved
						Motion blockingActorMotion = _actorMotions.get(blockingActor);
						TiledStage.Coordinate nextOrigin = blockingActor.origin().getAdjacentCoordinate(blockingActorMotion.direction());
						TempCoordinatesSet.addAll(blockingActor.getBodyCoordinates(nextOrigin));
					}

					if (ifRestart) break;

					// All blocking actors are moving, but will they unblock the actor if they move?
					TiledStage.Coordinate nextOrigin = actor.origin().getAdjacentCoordinate(motion.direction());
					LinkedList<TiledStage.Coordinate> nextBodyCoordinates = actor.getBodyCoordinates(nextOrigin);
					for (TiledStage.Coordinate bodyCoordinate : nextBodyCoordinates) {
						if (TempCoordinatesSet.contains(bodyCoordinate)) {
							ActorsResolved.add(actor);
							ifRestart = true;
							break;
						}
					}

					if (ifRestart) continue;

					// If all blocking actors moved, actor will be able to move
					// Record this dependency
					ActorMotionDependencies.put(actor, blockingActors);
				}
			}

			// Remove iteration's resolved actors
			for (TiledStageActor actorResolved : ActorsResolved) {
				actors.remove(actorResolved);
			}
			ActorsResolved.clear();

			if (!ifRestart && actors.size() > 0) {
				// Resolve one set of dependencies
				TiledStageActor actor = actors.getFirst();

				InterdependentActors.clear();
				determineInterdependentActors(ActorMotionDependencies, InterdependentActors, actor);
				for (TiledStageActor interdependentActor : InterdependentActors) {
					Motion motion = _actorMotions.get(interdependentActor);
					interdependentActor.moveDirection(motion.direction(), motion.ticks());
					actors.remove(interdependentActor);
				}
			}

		} while (!actors.isEmpty());

		_actorMotions.clear();
	}

	private void determineInterdependentActors(HashMap<TiledStageActor, HashSet<TiledStageActor>> dependencies,
	                                           LinkedList<TiledStageActor> interdependentActors, TiledStageActor actor) {
		interdependentActors.add(actor);
		HashSet<TiledStageActor> dependentActors = dependencies.get(actor);
		for (TiledStageActor dependentActor : dependentActors) {
			if (!interdependentActors.contains(dependentActor)) {
				determineInterdependentActors(dependencies, interdependentActors, dependentActor);
			}
		}
	}

	private HashSet<TiledStage.Coordinate> getOriginsToCheck(TiledStageActor actor, TiledStage.DIRECTION direction) {
		TempCoordinatesSet.clear();
		TiledStage.Coordinate checkCoordinate;
		int unitRow = TiledStage.GetUnitRow(direction);
		int unitCol = TiledStage.GetUnitColumn(direction);

		if (unitRow != 0) {
			checkCoordinate = _stage.getCoordinate(actor.origin().row() + unitRow, actor.origin().column());
			if (checkCoordinate != null) TempCoordinatesSet.add(checkCoordinate);
		}

		if (unitCol != 0) {
			checkCoordinate = _stage.getCoordinate(actor.origin().row(), actor.origin().column() + unitCol);
			if (checkCoordinate != null) TempCoordinatesSet.add(checkCoordinate);
		}

		// Checking diagonal coordinate
		if (unitRow != 0 && unitCol != 0) {
			checkCoordinate = _stage.getCoordinate(actor.origin().row() + unitRow, actor.origin().column() + unitCol);
			if (checkCoordinate != null) TempCoordinatesSet.add(checkCoordinate);
		}

		return TempCoordinatesSet;
	}

	private boolean canBeAt(TiledStageActor actor, TiledStage.DIRECTION direction) {
		for (TiledStage.Coordinate origin : getOriginsToCheck(actor, direction)) {
			if (!canBeAt(actor, origin)) {
				return false;
			}
		}

		return true;
	}

	private HashSet<TiledStageActor> getActorsBlocking(TiledStageActor actor, TiledStage.DIRECTION direction) {
		HashSet<TiledStageActor> actors = new HashSet<TiledStageActor>();
		for (TiledStage.Coordinate origin : getOriginsToCheck(actor, direction)) {
			actors.addAll(getActorsBlocking(actor, origin));
		}
		return actors;
	}

	private boolean canBeAt(TiledStageActor actor, TiledStage.Coordinate origin) {
		// If actor occupies its coordinate
		if (actor.isSolid()) {
			// Check if all coordinates of actor's actor can move to their direction
			LinkedList<TiledStage.Coordinate> targetCoordinates = actor.getBodyCoordinates(origin);
			for (TiledStage.Coordinate bodyCoordinate : targetCoordinates) {
				if (!actor.bodyCanBeAt(bodyCoordinate)) return false;
			}
		}

		return true;
	}

	private HashSet<TiledStageActor> getActorsBlocking(TiledStageActor actor, TiledStage.Coordinate origin) {
		TempActorsSet.clear();

		LinkedList<TiledStage.Coordinate> targetCoordinates = actor.getBodyCoordinates(origin);
		loop:
		for (TiledStage.Coordinate bodyCoordinate : targetCoordinates) {
			for (TiledStageActor otherActor : bodyCoordinate.actors()) {
				if (otherActor != actor && otherActor.isSolid()) {
					TempActorsSet.add(otherActor);
				}
			}
		}

		return TempActorsSet;
	}

	public static class Motion {
		private TiledStage.DIRECTION _direction;
		private int _ticks;

		public Motion() {
			_ticks = 0;
			_direction = null;
		}

		public Motion initialize(TiledStage.DIRECTION direction, int ticks) {
			_direction = direction;
			_ticks = ticks;
			return this;
		}

		public int ticks() {
			return _ticks;
		}

		public TiledStage.DIRECTION direction() {
			return _direction;
		}

		@Override
		public String toString() {
			return _direction.toString();
		}
	}
}