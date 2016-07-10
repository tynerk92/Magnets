package com.somethingyellow.tiled;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.somethingyellow.graphics.AnimatedActor;
import com.somethingyellow.graphics.Animation;
import com.somethingyellow.graphics.AnimationDef;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * Represents an actor in TiledStage
 * Tightly coupled with TiledStage, TiledStageMapRenderer
 */

public abstract class TiledStageActor extends AnimatedActor implements Comparable<TiledStageActor> {
	public static final boolean[] BodyArea1x1 = new boolean[]{true};
	public int[] SUBTICKS = new int[0];
	public TreeSet<String> _statuses = new TreeSet<String>();
	private TreeSet<String> _tempStatuses = new TreeSet<String>();
	private TiledStage.Coordinate _origin;
	private LinkedList<TiledStage.Coordinate> _bodyCoordinates = new LinkedList<TiledStage.Coordinate>();
	private boolean[] _bodyArea;
	private int _bodyWidth;
	private int _bodyHeight;
	private float _z;
	private int _movingTicks;

	public void initialize(Map<String, AnimationDef> animationDefs, boolean[] bodyArea, int bodyWidth, TiledStage.Coordinate origin) {
		super.initialize(animationDefs);

		if (bodyArea.length % bodyWidth != 0)
			throw new IllegalArgumentException("Length of 'Body Area' should be a multiple of 'Body Width'!");

		_z = 0;
		_movingTicks = 0;
		_bodyArea = bodyArea;
		_bodyWidth = bodyWidth;
		_bodyHeight = bodyArea.length / bodyWidth;

		setOrigin(origin);
		Vector2 pos = _origin.position();
		setPosition(pos.x, pos.y);
	}

	@Override
	public List<Animation> animations() {
		return super.animations();
	}

	@Override
	public void reset() {
		super.reset();
		_statuses.clear();
	}

	@Override
	public boolean remove() {
		if (_origin == null) return false;

		stage().removeActor(this);

		for (TiledStage.Coordinate coordinate : _bodyCoordinates) {
			coordinate.remove(this);
		}

		_origin = null;

		return super.remove();
	}

	public void tick() {
		if (_movingTicks > 0) {
			_movingTicks--;
		}
	}

	public abstract void tick(int subtick);

	public abstract boolean bodyCanBeAt(TiledStage.Coordinate coordinate);

	public boolean canBeAt(TiledStage.Coordinate origin) {
		// Check if all coordinates of body can move to their direction
		LinkedList<TiledStage.Coordinate> targetCoordinates = getBodyCoordinates(origin);
		for (TiledStage.Coordinate bodyCoordinate : targetCoordinates) {
			if (!bodyCanBeAt(bodyCoordinate)) return false;
		}

		return true;
	}

	public boolean moveDirection(TiledStage.DIRECTION direction, int ticks) {
		if (isMoving()) return false;

		TiledStage.Coordinate checkCoordinate;

		int unitRow = TiledStage.GetUnitRow(direction);
		int unitCol = TiledStage.GetUnitColumn(direction);

		if (unitRow != 0) {
			checkCoordinate = stage().getCoordinate(origin().row() + unitRow, origin().column());
			if (checkCoordinate == null || !canBeAt(checkCoordinate)) {
				return false;
			}
		}

		if (unitCol != 0) {
			checkCoordinate = stage().getCoordinate(origin().row(), origin().column() + unitCol);
			if (checkCoordinate == null || !canBeAt(checkCoordinate)) {
				return false;
			}
		}

		// Checking diagonal coordinate
		if (unitRow != 0 && unitCol != 0) {
			checkCoordinate = stage().getCoordinate(origin().row() + unitRow, origin().column() + unitCol);
			if (checkCoordinate == null || !canBeAt(checkCoordinate)) {
				return false;
			}
		}

		moveTo(stage().getCoordinate(origin().row() + unitRow, origin().column() + unitCol), ticks);
		return true;
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

	public State getState() {
		return new State();
	}

	public TiledStage stage() {
		return origin().stage();
	}

	public float ticksToTime(int ticks) {
		return ticks * stage().tickDuration();
	}

	@Override
	public int compareTo(TiledStageActor body) {
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
		public void positionChanged(TiledStageActor actor) {
		}

		public void statusAdded(TiledStageActor actor, String status) {
		}

		public void statusRemoved(TiledStageActor actor, String status) {
		}

		public void stateChanged(TiledStageActor actor) {
		}
	}

	public class State {
		private TiledStage.Coordinate _origin;
		private TreeSet<String> _statuses;

		private State() {
			_origin = TiledStageActor.this._origin;
			_statuses = new TreeSet<String>(TiledStageActor.this._statuses);
		}

		public TiledStageActor body() {
			return TiledStageActor.this;
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
				TiledStageActor.this.addStatus(status);
			}
			_tempStatuses.clear();
			for (String status : TiledStageActor.this._statuses) {
				if (!_statuses.contains(status)) _tempStatuses.add(status);
			}
			for (String status : _tempStatuses) {
				TiledStageActor.this.removeStatus(status);
			}
		}

		@Override
		public String toString() {
			return getName() + " @ " + _origin.toString() + " with statuses " + _statuses.toString();
		}
	}
}
