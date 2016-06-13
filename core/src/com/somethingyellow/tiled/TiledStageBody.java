package com.somethingyellow.tiled;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.somethingyellow.graphics.AnimatedActor;
import com.somethingyellow.graphics.AnimationDef;

import java.util.LinkedList;
import java.util.Map;
import java.util.TreeSet;

public abstract class TiledStageBody extends AnimatedActor implements Comparable<TiledStageBody> {
	public static final boolean[] BodyArea1x1 = new boolean[]{
			true
	};
	public TreeSet<String> _statuses = new TreeSet<String>();
	private TreeSet<String> _tempStatuses = new TreeSet<String>();
	private TiledStage.Coordinate _origin;
	private LinkedList<TiledStage.Coordinate> _bodyCoordinates = new LinkedList<TiledStage.Coordinate>();
	private boolean[] _bodyArea;
	private int _bodyWidth;
	private int _bodyHeight;
	private boolean _hasShadow;
	private int _shadowDisplacementY;
	private float _z;
	private int _movingTicks;

	public void initialize(Map<String, AnimationDef> animationDefs, boolean[] bodyArea, int bodyWidth, TiledStage.Coordinate origin) {
		super.initialize(animationDefs);

		if (bodyArea.length % bodyWidth != 0)
			throw new IllegalArgumentException("Length of 'Body Area' should be a multiple of 'Body Width'!");

		_z = 0;
		_hasShadow = false;
		_shadowDisplacementY = 0;
		_movingTicks = 0;
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
		if (_movingTicks > 0) {
			_movingTicks--;
		}
	}

	public void moveTo(TiledStage.Coordinate targetCoordinate, int ticks) {
		if (targetCoordinate == _origin) return;

		_movingTicks = ticks;
		setOrigin(targetCoordinate);
		Vector2 pos = targetCoordinate.position();
		addAction(Actions.moveTo(pos.x, pos.y, ticksToTime(ticks)));

		for (AnimatedActor.Listener listener : listeners()) {
			if (listener instanceof Listener) ((Listener) listener).stateChanged(this);
		}
	}

	public boolean isMoving() {
		return _movingTicks > 0;
	}

	@Override
	public void reset() {
		super.reset();
		_statuses.clear();
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

	public void addStatus(String status) {
		if (!_statuses.contains(status)) {
			_statuses.add(status);

			for (AnimatedActor.Listener listener : listeners()) {
				if (listener instanceof Listener) {
					((Listener) listener).statusAdded(this, status);
					((Listener) listener).stateChanged(this);
				}
			}
		}
	}

	public void setStatus(String status, boolean ifHas) {
		if (ifHas) addStatus(status);
		else removeStatus(status);
	}

	public boolean hasStatus(String status) {
		return _statuses.contains(status);
	}

	public void removeStatus(String status) {
		if (_statuses.contains(status)) {
			_statuses.remove(status);

			for (AnimatedActor.Listener listener : listeners()) {
				if (listener instanceof Listener) {
					((Listener) listener).statusRemoved(this, status);
					((Listener) listener).stateChanged(this);
				}
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

	public State getState() {
		return new State();
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

		public void statusAdded(TiledStageBody body, String status) {
		}

		public void statusRemoved(TiledStageBody body, String status) {
		}

		public void stateChanged(TiledStageBody body) {
		}
	}

	public class State {
		private TiledStage.Coordinate _origin;
		private TreeSet<String> _statuses;

		private State() {
			_origin = TiledStageBody.this._origin;
			_statuses = new TreeSet<String>(TiledStageBody.this._statuses);
		}

		public TiledStageBody body() {
			return TiledStageBody.this;
		}

		public void restore() {
			restore(0f);
		}

		public void restore(float time) {
			setOrigin(_origin);
			Vector2 pos = _origin.position();
			addAction(Actions.moveTo(pos.x, pos.y, time));

			// Check statuses
			for (String status : _statuses) {
				TiledStageBody.this.addStatus(status);
			}
			_tempStatuses.clear();
			for (String status : TiledStageBody.this._statuses) {
				if (!_statuses.contains(status)) _tempStatuses.add(status);
			}
			for (String status : _tempStatuses) {
				TiledStageBody.this.removeStatus(status);
			}
		}

		@Override
		public String toString() {
			return getName() + " @ " + _origin.toString() + " with statuses " + _statuses.toString();
		}
	}
}
