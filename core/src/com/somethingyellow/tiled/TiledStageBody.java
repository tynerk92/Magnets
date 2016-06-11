package com.somethingyellow.tiled;

import com.badlogic.gdx.math.Vector2;
import com.somethingyellow.graphics.AnimatedActor;
import com.somethingyellow.graphics.AnimationDef;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public abstract class TiledStageBody extends AnimatedActor implements Comparable<TiledStageBody> {
	public static final boolean[] BodyArea1x1 = new boolean[]{
			true
	};
	public TreeSet<String> _states = new TreeSet();
	private TiledStage.Coordinate _origin;
	private LinkedList<TiledStage.Coordinate> _bodyCoordinates = new LinkedList<TiledStage.Coordinate>();
	private boolean[] _bodyArea;
	private int _bodyWidth;
	private int _bodyHeight;
	private boolean _hasShadow;
	private int _shadowDisplacementY;
	private float _z;

	public void initialize(Map<String, AnimationDef> animationDefs, boolean[] bodyArea, int bodyWidth, TiledStage.Coordinate origin) {
		super.initialize(animationDefs);

		if (bodyArea.length % bodyWidth != 0)
			throw new IllegalArgumentException("Length of 'Body Area' should be a multiple of 'Body Width'!");

		_z = 0;
		_hasShadow = false;
		_shadowDisplacementY = 0;
		_bodyArea = bodyArea;
		_bodyWidth = bodyWidth;
		_bodyHeight = bodyArea.length / bodyWidth;

		setOrigin(origin);
		Vector2 pos = _origin.position();
		setPosition(pos.x, pos.y);
	}

	@Override
	public boolean remove() {
		if (_origin == null) return false;

		stage().removeBody(this);

		for (TiledStage.Coordinate coordinate : _bodyCoordinates) {
			coordinate.remove(this);
		}

		_origin = null;

		return super.remove();
	}

	public void act() {
	}

	@Override
	public void reset() {
		super.reset();
		_states.clear();
	}

	protected void setOrigin(TiledStage.Coordinate origin) {
		for (TiledStage.Coordinate coordinate : _bodyCoordinates) {
			coordinate.remove(this);
		}
		_origin = origin;
		_bodyCoordinates = getBodyCoordinates(_origin);
		for (TiledStage.Coordinate coordinate : _bodyCoordinates) {
			coordinate.add(this);
		}
	}

	public boolean getBodyAreaAt(int bodyRow, int bodyColumn) {
		return _bodyArea[bodyRow * _bodyWidth + bodyColumn];
	}

	// get/set
	// ---------

	public TiledStage.Coordinate origin() {
		return _origin;
	}

	public Vector2 center() {
		return new Vector2(getX() + (float) _bodyWidth / 2 * stage().tileWidth(), getY() + (float) _bodyHeight / 2 * stage().tileHeight());
	}

	public TiledStage.Coordinate topLeftBodyCoordinate() {
		TiledStage.Coordinate topLeft = null;
		for (TiledStage.Coordinate bodyCoordinate : _bodyCoordinates) {
			if (topLeft == null || bodyCoordinate.row() > topLeft.row()) { // body coordinate is more top than current topleft coordinate
				topLeft = bodyCoordinate;
			} else if (bodyCoordinate.row() == topLeft.row() && // body coordinate is on the same row than current topleft coordinate
					bodyCoordinate.column() < topLeft.column()) { // but it is more left than current topleft coordinate
				topLeft = bodyCoordinate;
			}
		}

		if (topLeft == null) topLeft = _origin;

		return topLeft;
	}

	public LinkedList<TiledStage.Coordinate> bodyCoordinates() {
		return _bodyCoordinates;
	}

	public LinkedList<TiledStage.Coordinate> getBodyCoordinates(TiledStage.Coordinate origin) {
		LinkedList<TiledStage.Coordinate> tempBodyCoordinates = new LinkedList<TiledStage.Coordinate>();

		for (int i = 0; i < _bodyArea.length; i++) {
			if (_bodyArea[i]) {
				int tileRow = _bodyHeight - 1 - Math.floorDiv(i, _bodyWidth) + origin.row();
				int tileCol = (i % _bodyWidth) + origin.column();
				TiledStage.Coordinate coordinate = stage().getCoordinate(tileRow, tileCol);
				if (coordinate != null) tempBodyCoordinates.add(coordinate);
			}
		}

		return tempBodyCoordinates;
	}

	public void addState(String state) {
		if (!_states.contains(state)) {
			_states.add(state);

			for (AnimatedActor.Listener listener : listeners()) {
				if (listener instanceof Listener) ((Listener) listener).stateAdded(this, state);
			}
		}
	}

	public boolean hasState(String state) {
		return _states.contains(state);
	}

	public void removeState(String state) {
		if (_states.contains(state)) {
			_states.remove(state);

			for (AnimatedActor.Listener listener : listeners()) {
				if (listener instanceof Listener) ((Listener) listener).stateRemoved(this, state);
			}
		}
	}

	public int bodyWidth() {
		return _bodyWidth;
	}

	public boolean[] bodyArea() {
		return _bodyArea;
	}

	public int bodyHeight() {
		return _bodyHeight;
	}

	public float getZ() {
		return _z;
	}

	public void setZ(float z) {
		_z = z;
	}

	public void setHasShadow(boolean hasShadow) {
		_hasShadow = hasShadow;
	}

	public boolean hasShadow() {
		return _hasShadow;
	}

	public int shadowDisplacementY() {
		return _shadowDisplacementY;
	}

	public void setShadowDisplacementY(int displacementY) {
		_shadowDisplacementY = displacementY;
	}

	public TiledStage stage() {
		return origin().stage();
	}

	public float ticksToTime(int ticks) {
		return ticks * stage().tickDuration();
	}

	@Override
	public int compareTo(TiledStageBody body) {
		TiledStage.Coordinate topLeft = topLeftBodyCoordinate();
		TiledStage.Coordinate actorTopLeft = body.topLeftBodyCoordinate();

		if (topLeft.row() > actorTopLeft.row()) {
			return -1;
		} else if (topLeft.row() < actorTopLeft.row()) {
			return 1;
		} else {
			if (topLeft.column() < actorTopLeft.column()) {
				return -1;
			} else if (topLeft.column() > actorTopLeft.column()) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	@Override
	public void positionChanged() {
		for (AnimatedActor.Listener listener : listeners()) {
			if (listener instanceof Listener) ((Listener) listener).positionChanged(this);
		}
	}

	public abstract static class Listener extends AnimatedActor.Listener {
		public void positionChanged(TiledStageBody body) {
		}

		public void stateAdded(TiledStageBody body, String state) {
		}

		public void stateRemoved(TiledStageBody body, String state) {

		}
	}
}
