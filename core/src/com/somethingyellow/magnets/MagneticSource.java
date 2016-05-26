package com.somethingyellow.magnets;

import com.somethingyellow.tiled.TiledStage;
import com.somethingyellow.tiled.TiledStageActor;

import java.util.HashMap;
import java.util.TreeSet;

public class MagneticSource extends TiledStageActor {
	public static final String STATE_STILL = "";
	public static final int MAGNETISE_RANGE = 1;
	public static final int ATTRACTION_RANGE = 2;
	public static final int ATTRACTION_STRENGTH = 1;

	public MagneticSource(int type, boolean[] bodyArea, int bodyWidth, HashMap<String, FrameSequence> animationFrames,
	                      TiledStage stage, TiledStage.Coordinate origin, int actorDepth) {
		super(type, bodyArea, bodyWidth, animationFrames, stage, origin, actorDepth);

		addState(STATE_STILL);
	}

	@Override
	public void act(float delta, int tick) {
		super.act(delta, tick);

		if (tick == PlayScreen.TICKS.MAGNETISATION.ordinal()) {

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

		} else if (tick == PlayScreen.TICKS.FORCES.ordinal()) {
			// Attract blocks within attraction range
			for (TiledStage.Coordinate bodyCoordinate : bodyCoordinates()) {
				for (TiledStage.Coordinate coordinate : bodyCoordinate.getCoordinatesInRange(ATTRACTION_RANGE, false)) {
					for (TiledStageActor actor : coordinate.actors()) {
						if (actor == this) continue;
						if (actor instanceof Block) {
							Block block = (Block) actor;
							if (!block.isMagnetised()) {
								TiledStage.DIRECTION direction = bodyCoordinate.getDirectionFrom(block.origin());
								if (direction != null)
									block.applyForce(direction, ATTRACTION_STRENGTH);
							}
						}
					}
				}
			}
		}
	}

	// get/set
	// ---------
}
