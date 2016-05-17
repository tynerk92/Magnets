package com.somethingyellow.magnets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

import com.somethingyellow.tiled.*;

public class Block extends TiledStageActor {
	public static final float MOVE_SPEED = 5f;
	public static final String LAYER_WALL = "Wall";
	public static final String TEXTURE_PATH = "Actors/Lodestone.png";
	public static final int TEXTURE_OFFSET_X = -16;
	public static final int TEXTURE_OFFSET_Y = -16;

	private Texture _texture = new Texture(Gdx.files.internal(TEXTURE_PATH));

	@Override
	public void act(float delta) {
		super.act(delta);
	}

	@Override
	public boolean canMove(TiledStage.Coordinate coordinate, TiledStage.DIRECTION direction) {
		if (!super.canMove(coordinate, direction)) return false;
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
		batch.draw(_texture, getX() + TEXTURE_OFFSET_X, getY() + TEXTURE_OFFSET_Y);
	}

	@Override
	public void dispose() {
		_texture.dispose();
	}
}
