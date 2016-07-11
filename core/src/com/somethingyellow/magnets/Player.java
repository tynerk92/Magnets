package com.somethingyellow.magnets;

import com.somethingyellow.graphics.AnimationDef;
import com.somethingyellow.tiled.TiledStage;
import com.somethingyellow.tiled.TiledStageActor;

import java.util.HashSet;
import java.util.Map;

public class Player extends TiledStageActor {

	public static final int[] SUBTICKS_STATIC = new int[]{
			PlayScreen.SUBTICKS.GRAPHICS.ordinal()
	};
	private Commands _commands;

	public void initialize(Map<String, AnimationDef> animationDefs, boolean[] bodyArea, int bodyWidth, TiledStage.Coordinate origin, Commands commands) {
		super.initialize(animationDefs, bodyArea, bodyWidth, origin);
		SUBTICKS = SUBTICKS_STATIC;
		_commands = commands;

		showAnimation(Config.AnimationStanding);
	}

	@Override
	public void tick(int subtick) {
		if (subtick == PlayScreen.SUBTICKS.GRAPHICS.ordinal()) {
			for (TiledStage.Coordinate bodyCoordinate : bodyCoordinates()) {
				for (TiledStageActor body : bodyCoordinate.actors()) {
					if (body instanceof Exit) {
						_commands.endLevel();
					}
				}
			}
		}
	}

	protected boolean pushDirection(final TiledStage.DIRECTION direction) {
		// check if there're any blocks in direction, push if there are
		TiledStage.Coordinate targetCoordinate = origin().getAdjacentCoordinate(direction);
		if (targetCoordinate == null) return false;
		HashSet<TiledStageActor> actors = targetCoordinate.actors();
		for (TiledStageActor actor : actors) {
			if (actor instanceof Lodestone) {
				Lodestone lodestone = (Lodestone) actor;

				if (lodestone.isPushable()) {
					TiledStage.Coordinate origin = origin();
					setOrigin(targetCoordinate);
					if (lodestone.push(direction)) { // if lodestone can really move to its pushed position (not considering actor)
						setOrigin(origin);
						moveDirection(direction);
					} else {
						setOrigin(origin);
					}
				}

				return true;
			}
		}
		return false;
	}

	public boolean moveDirection(TiledStage.DIRECTION direction) {
		if (pushDirection(direction)) return true;
		return moveDirection(direction, Config.MoveTicks);
	}

	@Override
	public boolean occupiesCoordinate() {
		return true;
	}

	public interface Commands {
		void endLevel();
	}

	public static class Config {
		public static int MoveTicks = 3;
		public static String AnimationStanding = "Standing";
	}
}
