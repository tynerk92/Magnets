package com.somethingyellow.graphics;

import com.badlogic.gdx.graphics.g2d.Sprite;

public class Frame {
	private Sprite _sprite;
	private Sprite _tempSprite;
	private float _duration;

	public Frame(Sprite sprite, float duration) {
		_sprite = sprite;
		_tempSprite = new Sprite(sprite);
		_duration = duration;
	}

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