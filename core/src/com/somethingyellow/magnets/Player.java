package com.somethingyellow.magnets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

import com.somethingyellow.tiled.*;

public class Player extends PlayerActor {
	public static final String LAYER_WALL = "Wall";
	public static final String TEXTURE_PATH = "Actors/Player.png";
	public static final int TEXTURE_OFFSET_X = -16;
	public static final int TEXTURE_OFFSET_Y = -16;

	private Texture _texture = new Texture(Gdx.files.internal(TEXTURE_PATH));

	@Override
	public void act(float delta) {
		super.act(delta);

		if (!isMoving() && momentumX() == 0 && momentumY() == 0) {
			if (isKeyLeftHeld()) addMomentum(TiledStage.DIRECTION.LEFT, 1);
			else if (isKeyRightHeld()) addMomentum(TiledStage.DIRECTION.RIGHT, 1);
			else if (isKeyUpHeld()) addMomentum(TiledStage.DIRECTION.UP, 1);
			else if (isKeyDownHeld()) addMomentum(TiledStage.DIRECTION.DOWN, 1);
		}
	}

	@Override
	public boolean canMove(TiledStage.Coordinate coordinate, TiledStage.DIRECTION direction) {
		if (!super.canMove(coordinate, direction)) return false;
		if (coordinate.getTile(LAYER_WALL) != null) return false;

		for (TiledStageActor actor : coordinate.actors()) {
			if (actor instanceof Block) {
				actor.addMomentum(direction, 1);
				return false;
			}
		}

		return true;

	}

	// visual
	// -------

	@Override
	public void create(TiledStage stage, TiledStage.Coordinate coordinate) {
		super.create(stage, coordinate);
	}

	@Override
	public void draw(Batch batch, float alpha) {
		batch.draw(_texture, getX() + TEXTURE_OFFSET_X, getY() + TEXTURE_OFFSET_Y);
	}

	@Override
	public void dispose() {
		_texture.dispose();
	}
}
