package com.somethingyellow.magnets;

import com.somethingyellow.tiled.TiledStage;
import com.somethingyellow.tiled.TiledStageActor;

import java.util.HashMap;
import java.util.TreeSet;

public class MagneticSource extends TiledStageActor {
	public static final String STATE_DEFAULT = "Default";
	public static final int MAGNETISE_RANGE = 1;
	public static final int ATTRACTION_RANGE = 2;
	public static final int ATTRACTION_STRENGTH = 1;
	public static final int[] SUBTICKS = new int[]{
			PlayScreen.SUBTICKS.MAGNETISATION.ordinal(),
			PlayScreen.SUBTICKS.FORCES.ordinal()
	};

	public MagneticSource(HashMap<String, FrameSequence> animationFrames,
	                      TiledStage stage, TiledStage.Coordinate origin, int actorDepth) {
		super(TiledStageActor.BodyArea1x1, 1, animationFrames, stage, origin, actorDepth);

		addState(STATE_DEFAULT);
	}

	@Override
	public void act(int subtick) {
		if (subtick == PlayScreen.SUBTICKS.MAGNETISATION.ordinal()) {

			// Magnetise adjacent blocks
			TreeSet<TiledStage.Coordinate> magnetiseCoodinates = origin().getCoordinatesInRange(MAGNETISE_RANGE, false);
			for (TiledStage.Coordinate coordinate : magnetiseCoodinates) {
				for (TiledStageActor actor : coordinate.actors()) {
					if (actor == this) continue;
					if (actor instanceof Block) {
						Block block = (Block) actor;
						block.magnetise();
					}
				}
			}

		} else if (subtick == PlayScreen.SUBTICKS.FORCES.ordinal()) {

			// Attract blocks within attraction range
			for (TiledStage.Coordinate bodyCoordinate : bodyCoordinates()) {
				for (TiledStage.Coordinate coordinate : bodyCoordinate.getCoordinatesInRange(ATTRACTION_RANGE, false)) {
					for (TiledStageActor actor : coordinate.actors()) {
						if (actor == this) continue;
						if (actor instanceof Block) {
							Block block = (Block) actor;
							if (!block.isMagnetised()) {
								TiledStage.DIRECTION direction = bodyCoordinate.getDirectionFrom(coordinate);
								if (direction != null)
									block.applyForce(direction, ATTRACTION_STRENGTH);
							}
						}
					}
				}
			}

		}
	}

	@Override
	public boolean bodyCanBeAt(TiledStage.Coordinate coordinate) {
		return false;
	}

	// get/set
	// ---------

	@Override
	public int[] SUBTICKS() {
		return SUBTICKS;
	}
}
