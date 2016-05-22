package com.somethingyellow.magnets;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.somethingyellow.tiled.TiledStage;
import com.somethingyellow.tiled.TiledStageActor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class MagneticSource extends TiledStageActor implements TiledStage.CoordinateEventListener {

	private int _range;

	public MagneticSource(int type, boolean[] bodyArea, int bodyWidth, HashMap<String, Animation> animations,
	                      TiledStage stage, TiledStage.Coordinate origin, int range) {
		super(type, bodyArea, bodyWidth, animations, stage, origin);
		_range = range;
	}

	@Override
	public TiledStageActor setOrigin(TiledStage.Coordinate origin) {
		for (TiledStage.Coordinate coordinate : coordinatesInRange()) {
			coordinate.removeEventListener(this);
		}
		super.setOrigin(origin);
		for (TiledStage.Coordinate coordinate : coordinatesInRange()) {
			coordinate.addEventListener(this);
		}
		return this;
	}

	@Override
	public void act(float delta) {
		super.act(delta);
	}

	@Override
	public void onActorsChanged(TiledStage.Coordinate coordinate) {
		if (!coordinatesInRange().contains(coordinate)) return;

		// TODO: magnetise blocks in range
	}

	// get/set
	// ---------
	public int range() {
		return _range;
	}

	public List<TiledStage.Coordinate> coordinatesInRange() {
		LinkedList<TiledStage.Coordinate> coordinates = new LinkedList<TiledStage.Coordinate>();

		for (TiledStage.Coordinate coordinate : bodyCoordinates()) {
			if (!coordinates.contains(coordinate)) {
				coordinates.add(coordinate);
			}

			for (TiledStage.DIRECTION direction : TiledStage.DIRECTION.values()) {
				TiledStage.Coordinate adjCoordinate = coordinate.getAdjacentCoordinate(direction);
				if (!coordinates.contains(adjCoordinate)) {
					coordinates.add(adjCoordinate);
				}
			}
		}

		return coordinates;
	}
}
