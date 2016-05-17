package com.somethingyellow.magnets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

import com.somethingyellow.tiled.*;

public class Player extends PlayerActor {
	public static final float MOVE_SPEED = 5f;
	public static final String LAYER_WALL = "Wall";

	private Texture _texture = new Texture(Gdx.files.internal("Actors/Player.png"));


	@Override
	public void act(float delta) {
		super.act(delta);

		if (isKeyLeftHeld())
			tryMoveTo(getStage().getCoordinate(coordinate().row(), coordinate().column() - 1), MOVE_SPEED);
		if (isKeyRightHeld())
			tryMoveTo(getStage().getCoordinate(coordinate().row(), coordinate().column() + 1), MOVE_SPEED);
		if (isKeyUpHeld())
			tryMoveTo(getStage().getCoordinate(coordinate().row() + 1, coordinate().column()), MOVE_SPEED);
		if (isKeyDownHeld())
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
	public void dispose() {
		_texture.dispose();
	}
}
