package com.somethingyellow.tiled;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;

import java.util.LinkedList;

public class TiledStageLightSource extends Actor implements Pool.Poolable, Disposable {
	private Texture _texture;
	private float _renderDisplacementX;
	private float _renderDisplacementY;
	private float _intensity;
	private LinkedList<Listener> _listeners = new LinkedList<Listener>();
	private LinkedList<Listener> _tempListeners = new LinkedList<Listener>();

	public void initialize(Texture texture) {
		if (_texture != null) _texture.dispose();
		_texture = texture;
		_renderDisplacementX = 0f;
		_renderDisplacementY = 0f;
		_intensity = 1f;
	}

	public void setRenderDisplacement(float x, float y) {
		_renderDisplacementX = x;
		_renderDisplacementY = y;
	}

	public void setIntensity(float intensity) {
		_intensity = intensity;
	}

	public float renderX() {
		return getX() - _renderDisplacementX;
	}

	public float renderY() {
		return getY() - _renderDisplacementY;
	}

	public float intensity() {
		return _intensity;
	}

	@Override
	public void reset() {
		_listeners.clear();
	}

	public Listener addListener(Listener listener) {
		_listeners.add(listener);
		return listener;
	}

	@Override
	public boolean remove() {
		for (Listener listener : _listeners) {
			listener.removed(this);
		}

		Pools.free(this);

		return super.remove();
	}

	public Texture texture() {
		return _texture;
	}

	@Override
	public void dispose() {
		_texture.dispose();
	}

	public abstract static class Listener {
		public void removed(TiledStageLightSource lightSource) {
		}
	}
}
