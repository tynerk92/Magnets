package com.somethingyellow.magnets;

import com.somethingyellow.tiled.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Lodestone extends TiledStageActor {
	public static final int MAGNETISED_ATTRACTION_RANGE = 2;
	public static final int MAGNETISED_MAGNETISE_RANGE = 1;
	public static final int MAGNETISED_ATTRACTION_STRENGTH = 1;

	public static final int[] SUBTICKS = new int[]{
			PlayScreen.SUBTICKS.RESET.ordinal(),
			PlayScreen.SUBTICKS.FORCES.ordinal(),
			PlayScreen.SUBTICKS.BLOCK_MOVEMENT.ordinal(),
			PlayScreen.SUBTICKS.GRAPHICS.ordinal()
	};

	private boolean _isPushable;
	private boolean _isMagnetisable;
	private boolean _isMagnetised;
	private int _forceX;
	private int _forceY;
	private ActionListener _actionListener;
	private LinkedList<List<Object>> _tempAttractionData = new LinkedList<List<Object>>();
	private HashMap<List<Object>, TiledStageVisual> _magneticAttractionVisual = new HashMap<List<Object>, TiledStageVisual>();

	public void initialize(boolean[] bodyArea, int bodyWidth, HashMap<String, FrameSequence> animationFrames,
	                       TiledStage.Coordinate origin, boolean isPushable, boolean isMagnetisable, ActionListener actionListener) {
		super.initialize(bodyArea, bodyWidth, animationFrames, origin);

		_isPushable = isPushable;
		_isMagnetisable = isMagnetisable;
		_actionListener = actionListener;
		_forceX = 0;
		_forceY = 0;
		_isMagnetised = false;
	}

	@Override
	public void reset() {
		super.reset();
		_magneticAttractionVisual.clear();
		_tempAttractionData.clear();
	}

	@Override
	public void act(int subtick) {
		if (subtick == PlayScreen.SUBTICKS.RESET.ordinal()) {

			if (!isMoving()) {
				_isMagnetised = false;
				_forceX = _forceY = 0;
			}

		} else if (subtick == PlayScreen.SUBTICKS.FORCES.ordinal()) {

			if (_isMagnetised) {
				if (!isMoving()) {
					for (TiledStage.Coordinate bodyCoordinate : bodyCoordinates()) {

						// Attract blocks within attraction range
						for (TiledStage.Coordinate coordinate : bodyCoordinate.getCoordinatesAtRange(MAGNETISED_ATTRACTION_RANGE, false)) {
							for (TiledStageActor actor : coordinate.actors()) {
								if (actor == this) continue;
								if (actor instanceof Lodestone) {
									Lodestone lodestone = (Lodestone) actor;
									if (lodestone.isMagnetised() || lodestone.isMoving()) continue;

									TiledStage.DIRECTION direction = bodyCoordinate.getDirectionFrom(coordinate);
									if (direction == null) continue;

									lodestone.attract(direction, coordinate, MAGNETISED_ATTRACTION_STRENGTH);
								}
							}
						}

					}
				}
				if (!hasState(Config.LODESTONE_STATE_MAGNETISED))
					addState(Config.LODESTONE_STATE_MAGNETISED);
			} else {
				if (hasState(Config.LODESTONE_STATE_MAGNETISED))
					removeState(Config.LODESTONE_STATE_MAGNETISED);
			}


		} else if (subtick == PlayScreen.SUBTICKS.BLOCK_MOVEMENT.ordinal()) {

			if (!isMoving()) {
				if (_forceX != 0 || _forceY != 0) {
					moveDirection(TiledStage.GetDirection(_forceY, _forceX), Config.LODESTONE_MOVE_TICKS);
				}
			}

		} else if (subtick == PlayScreen.SUBTICKS.GRAPHICS.ordinal()) {

			if (!isMoving()) {
				for (final List<Object> attractionData : _tempAttractionData) {
					TiledStageVisual visual;
					if (_magneticAttractionVisual.containsKey(attractionData)) {
						visual = _magneticAttractionVisual.get(attractionData);
					} else {
						TiledStage.DIRECTION attractionDirection = (TiledStage.DIRECTION) attractionData.get(0);
						TiledStage.Coordinate bodyCoordinate = (TiledStage.Coordinate) attractionData.get(1);

						TiledStage.Coordinate visualCoordinate = bodyCoordinate.getAdjacentCoordinate(attractionDirection);
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

					visual.setDuration(Config.LODESTONE_MOVE_TICKS + 1);
				}
			}

			_tempAttractionData.clear();
		}
	}

	public void applyForce(TiledStage.DIRECTION direction, int magnitude) {
		_forceX += TiledStage.GetUnitColumn(direction) * magnitude;
		_forceY += TiledStage.GetUnitRow(direction) * magnitude;
	}

	public void attract(TiledStage.DIRECTION direction, TiledStage.Coordinate bodyCoordinate, int magnitude) {
		if (!bodyCoordinates().contains(bodyCoordinate)) return;

		applyForce(direction, magnitude);

		_tempAttractionData.add(Arrays.asList((Object) direction, bodyCoordinate));
	}

	public void magnetise() {
		if (isMoving()) return;

		if (_isMagnetisable && !_isMagnetised) {
			_isMagnetised = true;

			for (TiledStage.Coordinate bodyCoordinate : bodyCoordinates()) {

				// Magnetise blocks within magnetisation range
				for (TiledStage.Coordinate coordinate : bodyCoordinate.getCoordinatesInRange(MAGNETISED_MAGNETISE_RANGE, false)) {
					for (TiledStageActor actor : coordinate.actors()) {
						if (actor == this) continue;
						if (actor instanceof Lodestone) {
							Lodestone lodestone = (Lodestone) actor;
							if (!lodestone.isMagnetised()) lodestone.magnetise();
						}
					}
				}

				for (TiledStageBody.Listener listener : listeners()) {
					if (listener instanceof Listener) ((Listener) listener).magnetised();
				}
			}
		}
	}

	public boolean push(TiledStage.DIRECTION direction) {
		return moveDirection(direction, Config.LODESTONE_MOVE_TICKS);
	}

	@Override
	public boolean bodyCanBeAt(TiledStage.Coordinate coordinate) {
		if (coordinate.getTileProp(Config.ACTORS_LAYER_NAME, Config.TILE_TYPE, "").equals(Config.TILE_TYPE_WALL))
			return false;
		for (TiledStageActor actor : coordinate.actors()) {
			if (actor == this) continue;
			if (actor instanceof Player || actor instanceof Lodestone || actor instanceof MagneticSource ||
					actor instanceof ObstructedFloor || (actor instanceof Door && !((Door) actor).isOpen()))
				return false;
		}

		return true;
	}

	public boolean isPushable() {
		return _isPushable;
	}

	public boolean isMagnetisable() {
		return _isMagnetisable;
	}

	// get/set
	// ---------

	public boolean isMagnetised() {
		return _isMagnetised;
	}

	public int[] subticks() {
		return SUBTICKS;
	}

	public interface ActionListener {
		TiledStageVisual spawnMagneticAttractionVisual(TiledStage.Coordinate coordinate, TiledStage.DIRECTION direction);
	}

	public abstract static class Listener extends TiledStageActor.Listener {
		public void magnetised() {
		}
	}
}
