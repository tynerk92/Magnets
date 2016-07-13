package com.somethingyellow.magnets;

import com.somethingyellow.graphics.AnimationDef;
import com.somethingyellow.tiled.TiledStage;
import com.somethingyellow.tiled.TiledStageActor;

import java.util.HashSet;
import java.util.Map;

public class Player extends TiledStageActor {

	public static final int[] SUBTICKS_STATIC = new int[]{
			PlayScreen.SUBTICKS.PLAYER_ACTION.ordinal()
	};
	private Commands _commands;
	private PLAYER_ACTION _action;

	public Player() {
		super();
		SUBTICKS = SUBTICKS_STATIC;
	}

	public void initialize(Map<String, AnimationDef> animationDefs, TiledStage.Coordinate origin, Commands commands) {
		super.initialize(animationDefs, origin);
		_commands = commands;
		_action = null;

		showAnimation(Config.AnimationRightIdle);
	}

	@Override
	public void subtick(int subtick) {

		if (subtick == PlayScreen.SUBTICKS.PLAYER_ACTION.ordinal()) {

			for (TiledStage.Coordinate bodyCoordinate : bodyCoordinates()) {
				for (TiledStageActor body : bodyCoordinate.actors()) {
					if (body instanceof Exit) {
						_commands.endLevel();
						return;
					}
				}
			}

			if (_action != null) {
				switch (_action) {
					case MOVE_UP:
						tryToMoveDirection(TiledStage.DIRECTION.NORTH, Config.MoveTicks);
						pushDirection(TiledStage.DIRECTION.NORTH);
						break;
					case MOVE_DOWN:
						tryToMoveDirection(TiledStage.DIRECTION.SOUTH, Config.MoveTicks);
						pushDirection(TiledStage.DIRECTION.SOUTH);
						break;
					case MOVE_LEFT:
						tryToMoveDirection(TiledStage.DIRECTION.WEST, Config.MoveTicks);
						pushDirection(TiledStage.DIRECTION.WEST);
						break;
					case MOVE_RIGHT:
						tryToMoveDirection(TiledStage.DIRECTION.EAST, Config.MoveTicks);
						pushDirection(TiledStage.DIRECTION.EAST);
						break;
				}
			}

			_action = null;
		}
	}

	public void doAction(PLAYER_ACTION action) {
		_action = action;
	}

	@Override
	public void updateAnimation() {
	}

	protected boolean pushDirection(final TiledStage.DIRECTION direction) {
		// check if there're any blocks in direction, push if there are
		TiledStage.Coordinate targetCoordinate = origin().getAdjacentCoordinate(direction);
		if (targetCoordinate == null) return false;
		HashSet<TiledStageActor> actors = targetCoordinate.actors();
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
		public static String AnimationRightIdle = "Right Idle";
		public static String AnimationWalkingLeft = "Walking Left";
		public static String AnimationWalkingRight = "Walking Right";
		public static String AnimationWalkingUp = "Walking Up";
		public static String AnimationWalkingDown = "Walking Down";
		public static String AnimationPushingLeft = "Pushing Left";
		public static String AnimationPushingRight = "Pushing Right";
		public static String AnimationPushingUp = "Pushing Up";
		public static String AnimationPushingDown = "Pushing Down";
	}
}
