package com.somethingyellow.magnets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

import com.somethingyellow.tiled.*;

public class Block extends TiledStageActor {
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
		if (coordinate.getTileBooleanProp(PlayScreen.LAYER_OBJECTS, PlayScreen.TILE_PROP_WALL))
			return false;
		for (TiledStageActor actor : coordinate.actors()) {
			if (actor.type() == PlayScreen.OBJECT_TYPES.PLAYER.ordinal() ||
					actor.type() == PlayScreen.OBJECT_TYPES.STONE.ordinal()) return false;
		}

		return true;
	}

	// visual
	// -------

	@Override
	public void create(TiledStage stage, TiledStage.Coordinate coordinate, int type) {
		super.create(stage, coordinate, type);
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
