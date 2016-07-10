package com.somethingyellow.graphics;

import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * A frame in Animation
 * Stores a template Sprite and an associated duration
 */

public class AnimationFrame {
	private Sprite _sprite;
	private Sprite _tempSprite;
	private float _duration;

	public AnimationFrame(Sprite sprite, float duration) {
		_sprite = sprite;
		_tempSprite = new Sprite(sprite);
		_duration = duration;
	}

	/**
	 * @return an internally reused Sprite that duplicates properties of template Sprite
	 */

	public Sprite getSprite() {
		_tempSprite.set(_sprite);
		return _tempSprite;
	}

	public int height() {
		return _sprite.getRegionHeight();
	}

	public int width() {
		return _sprite.getRegionWidth();
	}

	public float duration() {
		return _duration;
	}
}