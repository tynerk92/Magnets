package com.somethingyellow.graphics;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class LightSource extends Animation {
	private float _intensity;
	private Actor _actorFollowing = null;

	// TODO: All tiles can be defined as light sources
	public LightSource(AnimationDef def, Actor actorFollowing, float renderDisplacementX, float renderDisplacementY) {
		super(def);
		setRenderDisplacement(renderDisplacementX, renderDisplacementY);
		_intensity = 1f;
		_actorFollowing = actorFollowing;
	}

	@Override
	public Sprite getSprite() {
		Sprite sprite = super.getSprite();
		sprite.setPosition(_actorFollowing.getX() + renderDisplacementX(), _actorFollowing.getY() + renderDisplacementY());
		sprite.setAlpha(alpha() * _intensity);
		return sprite;
	}

	public void setIntensity(float intensity) {
		_intensity = intensity;
	}

	public float intensity() {
		return _intensity;
	}
}
