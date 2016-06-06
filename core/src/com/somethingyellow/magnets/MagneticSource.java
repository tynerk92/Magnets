package com.somethingyellow.magnets;

import com.somethingyellow.tiled.TiledStage;
import com.somethingyellow.tiled.TiledStageActor;
import com.somethingyellow.tiled.TiledStageBody;
import com.somethingyellow.tiled.TiledStageVisual;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class MagneticSource extends TiledStageActor {
	public static final int MAGNETISE_RANGE = 1;
	public static final int ATTRACTION_RANGE = 2;
	public static final int ATTRACTION_STRENGTH = 1;
	public static final int[] SUBTICKS = new int[]{
			PlayScreen.SUBTICKS.MAGNETISATION.ordinal(),
			PlayScreen.SUBTICKS.FORCES.ordinal(),
			PlayScreen.SUBTICKS.GRAPHICS.ordinal()
	};

	private ActionListener _actionListener;
	private HashMap<List<Object>, TiledStageVisual> _magneticAttractionVisual = new HashMap<List<Object>, TiledStageVisual>();
	private LinkedList<List<Object>> _tempAttractionData = new LinkedList<List<Object>>();

	public void initialize(HashMap<String, FrameSequence> animationFrames, TiledStage.Coordinate origin, ActionListener actionListener) {
		super.initialize(TiledStageActor.BodyArea1x1, 1, animationFrames, origin);

		_actionListener = actionListener;
	}

	@Override
	public void reset() {
		super.reset();
		_magneticAttractionVisual.clear();
		_tempAttractionData.clear();
	}

	@Override
	public void act(int subtick) {
		if (subtick == PlayScreen.SUBTICKS.MAGNETISATION.ordinal()) {

			for (TiledStage.Coordinate bodyCoordinate : bodyCoordinates()) {

				// Magnetise blocks within magnetisation range
				for (TiledStage.Coordinate coordinate : bodyCoordinate.getCoordinatesInRange(MAGNETISE_RANGE, false)) {
					for (TiledStageActor actor : coordinate.actors()) {
						if (actor == this) continue;
						if (actor instanceof Block) {
							Block block = (Block) actor;
							if (!block.isMagnetised()) block.magnetise();
						}
					}
				}

			}

		} else if (subtick == PlayScreen.SUBTICKS.FORCES.ordinal()) {

			for (TiledStage.Coordinate bodyCoordinate : bodyCoordinates()) {

				// Attract blocks within attraction range
				for (TiledStage.Coordinate coordinate : bodyCoordinate.getCoordinatesAtRange(ATTRACTION_RANGE, false)) {
					for (TiledStageActor actor : coordinate.actors()) {
						if (actor == this) continue;
						if (actor instanceof Block) {
							Block block = (Block) actor;
							if (block.isMagnetised() || block.isMoving()) continue;

							TiledStage.DIRECTION direction = bodyCoordinate.getDirectionFrom(coordinate);
							if (direction == null) continue;

							block.applyForce(direction, ATTRACTION_STRENGTH);

							_tempAttractionData.add(Arrays.asList((Object) block, bodyCoordinate, coordinate));
						}
					}
				}
			}
		} else if (subtick == PlayScreen.SUBTICKS.GRAPHICS.ordinal()) {

			for (final List<Object> attractionData : _tempAttractionData) {
				Block block = (Block) attractionData.get(0);
				if (block.isMoving())
					continue; // if block was moving now, means it could be attracted, abandon showing of attraction visual

				TiledStageVisual visual;
				if (_magneticAttractionVisual.containsKey(attractionData)) {
					visual = _magneticAttractionVisual.get(attractionData);
				} else {
					TiledStage.Coordinate bodyCoordinate = (TiledStage.Coordinate) attractionData.get(1);
					TiledStage.Coordinate coordinate = (TiledStage.Coordinate) attractionData.get(2);
					TiledStage.DIRECTION attractionDirection = bodyCoordinate.getDirectionFrom(coordinate);

					TiledStage.Coordinate visualCoordinate = bodyCoordinate.getAdjacentCoordinate(TiledStage.ReverseDirection(attractionDirection));
					if (visualCoordinate == null) continue;

					visual = _actionListener.spawnMagneticAttractionVisual(visualCoordinate, attractionDirection);
					if (visual == null) continue;

					_magneticAttractionVisual.put(attractionData, visual);

					visual.addListener(new TiledStageBody.Listener() {
						@Override
						public void removed() {
							_magneticAttractionVisual.remove(attractionData);
						}
					});
				}

				visual.setDuration(1);
			}

			_tempAttractionData.clear();
		}
	}

	@Override
	public boolean bodyCanBeAt(TiledStage.Coordinate coordinate) {
		return false;
	}

	// get/set
	// ---------
	@Override
	public int[] subticks() {
		return SUBTICKS;
	}

	public interface ActionListener {
		TiledStageVisual spawnMagneticAttractionVisual(TiledStage.Coordinate coordinate, TiledStage.DIRECTION direction);
	}
}
