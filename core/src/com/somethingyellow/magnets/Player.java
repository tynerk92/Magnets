package com.somethingyellow.magnets;

import com.somethingyellow.graphics.AnimatedActor;
import com.somethingyellow.graphics.AnimationDef;
import com.somethingyellow.tiled.TiledStage;
import com.somethingyellow.tiled.TiledStageActor;

import java.util.Collection;
import java.util.Map;

public class Player extends TiledStageActor {

	public static final int[] SUBTICKS_STATIC = new int[]{
			PlayScreen.SUBTICKS.START.ordinal(),
			PlayScreen.SUBTICKS.PLAYER_ACTION.ordinal(),
			PlayScreen.SUBTICKS.END.ordinal()
	};
	private Commands _commands;
	private PLAYER_ACTION _action;
	private boolean _ifTriedMoving;

	public Player() {
		super();
		SUBTICKS = SUBTICKS_STATIC;
	}

	public void initialize(TiledStage stage, Map<String, AnimationDef> animationDefs, TiledStage.Coordinate origin, Commands commands) {
		super.initialize(stage, animationDefs, origin);
		_commands = commands;
		_action = null;
		_ifTriedMoving = false;

		setTransition(Config.AnimationPushingDownToIdle, Config.AnimationIdleDown);
		setTransition(Config.AnimationPushingRightToIdle, Config.AnimationIdleRight);
		setTransition(Config.AnimationPushingLeftToIdle, Config.AnimationIdleLeft);
		setTransition(Config.AnimationPushingUpToIdle, Config.AnimationIdleUp);
		setTransition(Config.AnimationIdleToPushingDown, Config.AnimationPushingDown);
		setTransition(Config.AnimationIdleToPushingRight, Config.AnimationPushingRight);
		setTransition(Config.AnimationIdleToPushingLeft, Config.AnimationPushingLeft);
		setTransition(Config.AnimationIdleToPushingUp, Config.AnimationPushingUp);
		addStatus(Config.StatusFacingRight);
		showAnimation(Config.AnimationIdleRight);
	}

	@Override
	public void subtick(int subtick) {
		if (subtick == PlayScreen.SUBTICKS.START.ordinal()) {

			_ifTriedMoving = false;

		} else if (subtick == PlayScreen.SUBTICKS.PLAYER_ACTION.ordinal()) {

			// Check if player exited the level
			if (!isMoving()) {
				for (TiledStage.Coordinate bodyCoordinate : bodyCoordinates()) {
					for (TiledStageActor body : bodyCoordinate.actors()) {
						if (body instanceof Exit) {
							_commands.endLevel();
							return;
						}
					}
				}
			}

			if (_action != null) {

				TiledStage.DIRECTION direction = null;
				switch (_action) {
					case MOVE_UP:
						direction = TiledStage.DIRECTION.NORTH;
						break;
					case MOVE_DOWN:
						direction = TiledStage.DIRECTION.SOUTH;
						break;
					case MOVE_LEFT:
						direction = TiledStage.DIRECTION.WEST;
						break;
					case MOVE_RIGHT:
						direction = TiledStage.DIRECTION.EAST;
						break;
				}

				if (direction != null) {

					if (!isMoving()) {
						tryToMoveDirection(direction, Config.MoveTicks);
						_ifTriedMoving = true;
					}

					if (pushDirection(direction)) {
						setStatus(Config.StatusPushing, true);
					} else {
						setStatus(Config.StatusPushing, false);
					}

					setStatus(Config.StatusFacingRight, direction == TiledStage.DIRECTION.EAST);
					setStatus(Config.StatusFacingLeft, direction == TiledStage.DIRECTION.WEST);
					setStatus(Config.StatusFacingUp, direction == TiledStage.DIRECTION.NORTH);
					setStatus(Config.StatusFacingDown, direction == TiledStage.DIRECTION.SOUTH);
				}

			} else {
				if (!isMoving()) setStatus(Config.StatusPushing, false);
			}

			_action = null;

		} else if (subtick == PlayScreen.SUBTICKS.END.ordinal()) {

			if (isMoving()) {
				setStatus(Config.StatusMoving, true);
				if (_ifTriedMoving) {
					for (AnimatedActor.Listener listener : listeners()) {
						if (listener instanceof Listener) ((Listener) listener).moved(this);
					}
				}
			} else {
				setStatus(Config.StatusMoving, false);
			}
		}
	}

	public void doAction(PLAYER_ACTION action) {
		_action = action;
	}

	@Override
	public void updateAnimation() {

		// TODO: When the player is walking against both pushable and unpushable lodestones, play pushing animation
		// TODO: Player can change direction without moving
		// TODO: When rewinding, don't play player animations
		// If animation is pushing and it is not longer pushing that direction, animate back to idle first
		if (isAnimationActive(Config.AnimationPushingLeft)) {
			if (!hasStatuses(Config.StatusPushing, Config.StatusFacingLeft)) {
				hideAllButAnimations(Config.AnimationPushingLeftToIdle);
			}
		} else if (isAnimationActive(Config.AnimationPushingRight)) {
			if (!hasStatuses(Config.StatusPushing, Config.StatusFacingRight)) {
				hideAllButAnimations(Config.AnimationPushingRightToIdle);
			}
		} else if (isAnimationActive(Config.AnimationPushingDown)) {
			if (!hasStatuses(Config.StatusPushing, Config.StatusFacingDown)) {
				hideAllButAnimations(Config.AnimationPushingDownToIdle);
			}
		} else if (isAnimationActive(Config.AnimationPushingUp)) {
			if (!hasStatuses(Config.StatusPushing, Config.StatusFacingUp)) {
				hideAllButAnimations(Config.AnimationPushingUpToIdle);
			}
		} else if (isAnyAnimationActive(
				Config.AnimationIdleDown,
				Config.AnimationIdleRight,
				Config.AnimationIdleLeft,
				Config.AnimationIdleUp,
				Config.AnimationWalkingDown,
				Config.AnimationWalkingUp,
				Config.AnimationWalkingLeft,
				Config.AnimationWalkingRight)) {

			if (hasStatuses(Config.StatusPushing, Config.StatusFacingUp)) {
				hideAllButAnimations(Config.AnimationIdleToPushingUp);
			} else if (hasStatuses(Config.StatusPushing, Config.StatusFacingDown)) {
				hideAllButAnimations(Config.AnimationIdleToPushingDown);
			} else if (hasStatuses(Config.StatusPushing, Config.StatusFacingLeft)) {
				hideAllButAnimations(Config.AnimationIdleToPushingLeft);
			} else if (hasStatuses(Config.StatusPushing, Config.StatusFacingRight)) {
				hideAllButAnimations(Config.AnimationIdleToPushingRight);
			} else if (hasStatuses(Config.StatusMoving, Config.StatusFacingUp)) {
				hideAllButAnimations(Config.AnimationWalkingUp);
			} else if (hasStatuses(Config.StatusMoving, Config.StatusFacingDown)) {
				hideAllButAnimations(Config.AnimationWalkingDown);
			} else if (hasStatuses(Config.StatusMoving, Config.StatusFacingLeft)) {
				hideAllButAnimations(Config.AnimationWalkingLeft);
			} else if (hasStatuses(Config.StatusMoving, Config.StatusFacingRight)) {
				hideAllButAnimations(Config.AnimationWalkingRight);
			} else if (hasStatuses(Config.StatusFacingRight)) {
				hideAllButAnimations(Config.AnimationIdleRight);
			} else if (hasStatuses(Config.StatusFacingLeft)) {
				hideAllButAnimations(Config.AnimationIdleLeft);
			} else if (hasStatuses(Config.StatusFacingUp)) {
				hideAllButAnimations(Config.AnimationIdleUp);
			} else if (hasStatuses(Config.StatusFacingDown)) {
				hideAllButAnimations(Config.AnimationIdleDown);
			}
		}
	}

	protected boolean pushDirection(final TiledStage.DIRECTION direction) {
		// check if there're any blocks in direction, push if there are
		TiledStage.Coordinate targetCoordinate = origin().getAdjacentCoordinate(direction);
		if (targetCoordinate == null) return false;
		Collection<TiledStageActor> actors = targetCoordinate.actors();
		for (TiledStageActor actor : actors) {
			if (actor instanceof Pushable) {
				((Pushable) actor).push(direction);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isSolid() {
		return true;
	}

	public enum PLAYER_ACTION {
		MOVE_LEFT, MOVE_UP, MOVE_DOWN, MOVE_RIGHT
	}

	public interface Commands {
		void endLevel();
	}

	public interface Pushable {
		void push(TiledStage.DIRECTION direction);
	}

	public static class Config {
		public static int MoveTicks = 3;
		public static String AnimationIdleLeft = "Idle Left";
		public static String AnimationIdleRight = "Idle Right";
		public static String AnimationIdleUp = "Idle Up";
		public static String AnimationIdleDown = "Idle Down";
		public static String AnimationWalkingLeft = "Walking Left";
		public static String AnimationWalkingRight = "Walking Right";
		public static String AnimationWalkingUp = "Walking Up";
		public static String AnimationWalkingDown = "Walking Down";
		public static String AnimationPushingLeft = "Pushing Left";
		public static String AnimationPushingRight = "Pushing Right";
		public static String AnimationPushingUp = "Pushing Up";
		public static String AnimationPushingDown = "Pushing Down";
		public static String AnimationPushingLeftToIdle = "Pushing Left To Idle";
		public static String AnimationPushingRightToIdle = "Pushing Right To Idle";
		public static String AnimationPushingUpToIdle = "Pushing Up To Idle";
		public static String AnimationPushingDownToIdle = "Pushing Down To Idle";
		public static String AnimationIdleToPushingLeft = "Idle To Pushing Left";
		public static String AnimationIdleToPushingRight = "Idle To Pushing Right";
		public static String AnimationIdleToPushingUp = "Idle To Pushing Up";
		public static String AnimationIdleToPushingDown = "Idle To Pushing Down";
		public static String StatusFacingLeft = "Facing Left";
		public static String StatusFacingRight = "Facing Right";
		public static String StatusFacingUp = "Facing Up";
		public static String StatusFacingDown = "Facing Down";
		public static String StatusPushing = "Pushing";
		public static String StatusMoving = "Moving";
	}

	public abstract static class Listener extends TiledStageActor.Listener {
		public void moved(Player player) {
		}
	}
}
