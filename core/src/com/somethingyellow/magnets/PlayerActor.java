package com.somethingyellow.magnets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

import com.somethingyellow.TiledStage;
import com.somethingyellow.TiledStageActor;

public class PlayerActor extends TiledStageActor {
	public static final float MOVE_SPEED = 5f;
	public static final String LAYER_WALL = "Wall";

	private Texture _texture = new Texture(Gdx.files.internal("Actors/Player.png"));

	private boolean _isKeyLeftPressed = false;
	private boolean _isKeyRightPressed = false;
	private boolean _isKeyUpPressed = false;
	private boolean _isKeyDownPressed = false;

	public PlayerActor() {
		super();

		addListener(this);
	}

	// event listener
	// ---------------

	@Override
	public boolean keyDown(InputEvent event, int keycode) {
		switch (keycode) {
			case Input.Keys.LEFT:
				_isKeyLeftPressed = true;
				break;
			case Input.Keys.RIGHT:
				_isKeyRightPressed = true;
				break;
			case Input.Keys.UP:
				_isKeyUpPressed = true;
				break;
			case Input.Keys.DOWN:
				_isKeyDownPressed = true;
				break;
		}

		return true;
	}

	@Override
	public boolean keyUp(InputEvent event, int keycode) {
		switch (keycode) {
			case Input.Keys.LEFT:
				_isKeyLeftPressed = false;
				break;
			case Input.Keys.RIGHT:
				_isKeyRightPressed = false;
				break;
			case Input.Keys.UP:
				_isKeyUpPressed = false;
				break;
			case Input.Keys.DOWN:
				_isKeyDownPressed = false;
				break;
		}

		return true;
	}

	@Override
	public void act(float delta) {
		super.act(delta);

		if (_isKeyLeftPressed)
			tryMoveTo(getStage().getCoordinate(coordinate().row(), coordinate().column() - 1), MOVE_SPEED);
		if (_isKeyRightPressed)
			tryMoveTo(getStage().getCoordinate(coordinate().row(), coordinate().column() + 1), MOVE_SPEED);
		if (_isKeyUpPressed)
			tryMoveTo(getStage().getCoordinate(coordinate().row() + 1, coordinate().column()), MOVE_SPEED);
		if (_isKeyDownPressed)
			tryMoveTo(getStage().getCoordinate(coordinate().row() - 1, coordinate().column()), MOVE_SPEED);
	}

	@Override
	public boolean canMoveTo(TiledStage.Coordinate coordinate) {
		if (!super.canMoveTo(coordinate)) return false;
		return coordinate.getTile(LAYER_WALL) == null;

	}

	// visual
	// -------

	@Override
	public void create(TiledStage stage, TiledStage.Coordinate coordinate) {
		super.create(stage, coordinate);
	}

	@Override
	public void draw(Batch batch, float alpha) {
		batch.draw(_texture, getX() - 32f / 2, getY() - 32f / 2);
	}

	@Override
	public void destroy() {
	}

	@Override
	public void dispose() {
		_texture.dispose();
	}
}
