package com.somethingyellow.tiled;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.somethingyellow.graphics.AnimatedActor;
import com.somethingyellow.graphics.Animation;
import com.somethingyellow.graphics.AnimationDef;
import com.somethingyellow.utility.ObjectSet;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Represents an actor in TiledStage
 * Tightly coupled with TiledStage, TiledStageMapRenderer
 * Not solid by default
 */

public abstract class TiledStageActor extends AnimatedActor {
	public static final boolean[] BodyArea1x1 = new boolean[]{true};
	public int[] SUBTICKS = new int[0];
	public ObjectSet<String> _statuses = new ObjectSet<String>();
	private TiledStage.Coordinate _origin;
	private LinkedList<TiledStage.Coordinate> _bodyCoordinates = new LinkedList<TiledStage.Coordinate>();
	private boolean[] _bodyArea;
	private int _bodyWidth;
	private int _bodyHeight;
	private int _movingTicks;
	private TiledStage _stage;
	private TiledStageMotionResolver.Motion _motion;
	private float _z;

	/**
	 * Initializes the actor and puts it in TiledStage
	 */
	public void initialize(Map<String, AnimationDef> animationDefs, TiledStage.Coordinate origin) {
		super.initialize(animationDefs);

		hideAllAnimations();

		_z = 0;
		_movingTicks = 0;
		_motion = new TiledStageMotionResolver.Motion();
		_stage = origin.stage();
		_origin = origin;
		_stage.addActor(this);
		setBody(BodyArea1x1, 1);
		Vector2 pos = _origin.position();
		setPosition(pos.x, pos.y);
	}

	public void setBody(boolean[] bodyArea, int bodyWidth) {
		if (bodyArea.length % bodyWidth != 0)
			throw new IllegalArgumentException("Length of 'Body Area' should be a multiple of 'Body Width'!");

		_bodyArea = bodyArea;
		_bodyWidth = bodyWidth;
		_bodyHeight = bodyArea.length / bodyWidth;
		setOrigin(_origin);
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
		if (_origin != null) {
			setOrigin(null);
			super.remove();
			return true;
		} else {
			return false;
		}
	}

	public boolean isRemoved() {
		return (_origin == null);
	}

	public void subtick() {
		if (_movingTicks > 0) {
			_movingTicks--;
		}
	}

	public abstract void updateAnimation();

	public abstract void subtick(int subtick);

	public boolean isSolid() {
		return false;
	}

	protected boolean bodyCanBeAt(TiledStage.Coordinate coordinate) {
		return !coordinate.isWall();
	}

	public void tryToMoveDirection(TiledStage.DIRECTION direction, int ticks) {
		if (!_stage.motionResolver().hasMotion(this)) {
			_stage.motionResolver().addMotion(this, _motion.initialize(direction, ticks));
		}
	}

	public boolean moveDirection(TiledStage.DIRECTION direction, int ticks) {
		if (isMoving()) return false;
		int unitRow = TiledStage.GetUnitRow(direction);
		int unitCol = TiledStage.GetUnitColumn(direction);
		moveTo(_stage.getCoordinate(origin().row() + unitRow, origin().column() + unitCol), ticks);
		return true;
	}

	public void moveTo(TiledStage.Coordinate targetCoordinate, int ticks) {
		if (targetCoordinate == _origin) return;
		setOrigin(targetCoordinate);
		Vector2 pos = targetCoordinate.position();
		_movingTicks = ticks;
		addAction(Actions.moveTo(pos.x, pos.y, _stage.ticksToTime(ticks)));

		for (AnimatedActor.Listener listener : listeners()) {
			if (listener instanceof Listener) ((Listener) listener).stateChanged(this);
		}
	}

	public boolean isMoving() {
		return _movingTicks > 0;
	}

	/**
	 * Sets the origin for actor
	 * If origin == null, actor is automatically removed from stage
	 * If origin != null, actor is automatically added to stage
	 */
	protected void setOrigin(TiledStage.Coordinate origin) {
		for (TiledStage.Coordinate coordinate : _bodyCoordinates) {
			coordinate.remove(this);
		}

		if (origin == null) {

			if (_origin != null) {
				_stage.removeActor(this);
				_origin = null;
			}

		} else {

			if (_origin == null) {
				_stage.addActor(this);

				System.out.println("WELCOME BACK " + this);
			}

			_origin = origin;
			_bodyCoordinates = getBodyCoordinates(_origin);
			for (TiledStage.Coordinate coordinate : _bodyCoordinates) {
				coordinate.add(this);
			}

			// Update z
			_z = 0;
			for (TiledStage.Coordinate coordinate : bodyCoordinates()) {
				_z = Math.max(_z, coordinate.elevation());
			}
		}
	}

	public boolean getBodyAreaAt(int bodyRow, int bodyColumn) {
		if (bodyRow < 0 || bodyColumn < 0 || bodyRow >= _bodyHeight || bodyColumn >= _bodyWidth)
			return false;
		return _bodyArea[bodyRow * _bodyWidth + bodyColumn];
	}

	public TiledStage.Coordinate origin() {
		return _origin;
	}

	public Vector2 center() {
		return new Vector2(getX() + (float) _bodyWidth / 2 * _stage.tileWidth(), getY() + (float) _bodyHeight / 2 * _stage.tileHeight());
	}

	public TiledStage.Coordinate topLeftBodyCoordinate() {
		TiledStage.Coordinate topLeft = null;
		for (TiledStage.Coordinate bodyCoordinate : _bodyCoordinates) {
			if (topLeft == null || bodyCoordinate.row() > topLeft.row()) { // actor coordinate is more top than current topleft coordinate
				topLeft = bodyCoordinate;
			} else if (bodyCoordinate.row() == topLeft.row() && // actor coordinate is on the same row than current topleft coordinate
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
				TiledStage.Coordinate coordinate = getCoordinateByBodyIndex(origin, i);
				if (coordinate != null) tempBodyCoordinates.add(coordinate);
			}
		}

		return tempBodyCoordinates;
	}

	/**
	 * Get a unique index of the body of actor at coordinate
	 * Returns -1 if coordinate doesn't coincide with body of actor
	 */
	public int getBodyIndex(TiledStage.Coordinate coordinate) {
		int row = getBodyRow(coordinate);
		int col = getBodyColumn(coordinate);
		if (row == -1 || col == -1) return -1;
		return row * _bodyWidth + col;
	}

	/**
	 * Get the stage coordinate for a coordinate of the body by bodyIndex
	 */
	public TiledStage.Coordinate getCoordinateByBodyIndex(int bodyIndex) {
		return getCoordinateByBodyIndex(_origin, bodyIndex);
	}

	private TiledStage.Coordinate getCoordinateByBodyIndex(TiledStage.Coordinate origin, int bodyIndex) {
		if (bodyIndex < 0 || bodyIndex >= _bodyWidth * _bodyHeight) return null;
		int tileRow = _bodyHeight - 1 - Math.floorDiv(bodyIndex, _bodyWidth) + origin.row();
		int tileCol = (bodyIndex % _bodyWidth) + origin.column();
		return _stage.getCoordinate(tileRow, tileCol);
	}

	/**
	 * Get body row of actor at coordinate
	 * Returns -1 if coordinate doesn't coincide with body of actor
	 */
	public int getBodyRow(TiledStage.Coordinate coordinate) {
		int row = coordinate.row() - _origin.row() - _bodyHeight + 1;
		if (row < 0 || row >= _bodyHeight) return -1;
		return row;
	}

	/**
	 * Get body column of actor at coordinate
	 * Returns -1 if coordinate doesn't coincide with body of actor
	 */
	public int getBodyColumn(TiledStage.Coordinate coordinate) {
		int col = coordinate.column() - _origin.column();
		if (col < 0 || col >= _bodyWidth) return -1;
		return col;
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
		return _stage;
	}

	public int initiative() {
		TiledStage.Coordinate topLeft = topLeftBodyCoordinate();
		return topLeft.row() * _stage.tileColumns() + topLeft.column();
	}

	@Override
	protected void positionChanged() {
		for (AnimatedActor.Listener listener : listeners()) {
			if (listener instanceof Listener) ((Listener) listener).positionChanged(this);
		}
	}

	@Override
	public String toString() {
		return getName() + " @ " + _origin + " with statuses " + _statuses.toString();
	}

	public abstract static class Listener extends AnimatedActor.Listener {
		/**
		 * When the position of actor is changed (x/y)
		 */
		public void positionChanged(TiledStageActor actor) {
		}

		/**
		 * When a status is added
		 */
		public void statusAdded(TiledStageActor actor, String status) {
		}

		/**
		 * When a status is removed
		 */
		public void statusRemoved(TiledStageActor actor, String status) {
		}

		/**
		 * When a parameter contributing to its state changes
		 */
		public void stateChanged(TiledStageActor actor) {
		}
	}

	public class State {
		private TiledStage.Coordinate _origin;
		private ObjectSet<String> _statuses;

		protected State() {
			_origin = TiledStageActor.this._origin;
			_statuses = TiledStageActor.this._statuses.clone();
		}

		public TiledStageActor actor() {
			return TiledStageActor.this;
		}

		public void restore() {
			restore(0f);
		}

		public void restore(float time) {
			// Restore actor's origin
			setOrigin(_origin);
			if (_origin != null) {
				Vector2 pos = _origin.position();
				addAction(Actions.moveTo(pos.x, pos.y, time));
			}

			// Restore actor's statuses
			for (String status : TiledStageActor.this._statuses) {
				removeStatus(status);
			}
			for (String status : _statuses) {
				addStatus(status);
			}
		}
	}
}
