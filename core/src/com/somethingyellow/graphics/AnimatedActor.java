package com.somethingyellow.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AnimatedActor extends Actor implements Animation.Listener, Pool.Poolable {
	private HashMap<String, Animation> _animations = new HashMap<String, Animation>();
	private ArrayList<Animation> _animationsArray = new ArrayList<Animation>();
	private ArrayList<Sprite> _tempSpritesArray = new ArrayList<Sprite>();
	private LinkedList<Listener> _listeners = new LinkedList<Listener>();
	private LinkedList<Listener> _tempListeners = new LinkedList<Listener>();

	public void initialize(Map<String, AnimationDef> animationDefs) {
		for (String tag : animationDefs.keySet()) {
			Animation animation = animationDefs.get(tag).instantiate(tag, this);
			_animations.put(animation.tag(), animation);
			_animationsArray.add(animation);
		}
		Collections.sort(_animationsArray);
	}

	public AnimatedActor setTransition(final String fromTag, final String toTag) {
		Animation animation = _animations.get(fromTag);
		if (animation == null) {
			throw new IllegalArgumentException("`fromTag` doesn't exist!");
		}

		if (!_animations.containsKey(toTag)) {
			throw new IllegalArgumentException("`toTag` doesn't exist!");
		}

		addListener(new Listener() {
			@Override
			public void animationEnded(AnimatedActor actor, Animation animation) {
				if (animation.tag().equals(fromTag)) {
					showAnimation(toTag);
					hideAnimation(fromTag);
				}
			}
		});

		return this;
	}

	@Override
	public void animationEnded(Animation animation) {
		for (Listener listener : _listeners) {
			listener.animationEnded(this, animation);
		}
	}

	@Override
	public void reset() {
		_animations.clear();
		_listeners.clear();
		_tempListeners.clear();
		_animationsArray.clear();
		_tempSpritesArray.clear();
	}

	@Override
	public boolean remove() {
		for (Listener listener : _listeners) {
			listener.removed(this);
		}

		Pools.free(this);

		return super.remove();
	}

	public Listener addListener(Listener listener) {
		_listeners.add(listener);
		return listener;
	}

	public void removeListener(Listener listener) {
		_listeners.remove(listener);
	}

	public LinkedList<Listener> listeners() {
		_tempListeners.clear();
		_tempListeners.addAll(_listeners);
		return _tempListeners;
	}


	@Override
	public void draw(Batch batch, float parentAlpha) {
		for (Animation animation : _animationsArray) {
			Sprite sprite = animation.getSprite();
			sprite.setPosition(getX(), getY());
			sprite.draw(batch, parentAlpha);
		}
	}

	public int minZIndex() {
		int i = 0;
		while (_animationsArray.size() > i) {
			if (_animationsArray.get(i).alpha() > 0) return _animationsArray.get(i).zIndex();
			i++;
		}
		return 0;
	}

	public int maxZIndex() {
		int i = _animationsArray.size() - 1;
		while (i >= 0) {
			if (_animationsArray.get(i).alpha() > 0) return _animationsArray.get(i).zIndex();
			i--;
		}
		return 0;
	}

	public List<Animation> animations() {
		return _animationsArray;
	}

	public List<Sprite> spritesAtZIndex(int zIndex) {
		_tempSpritesArray.clear();
		for (Animation animation : _animationsArray) {
			if (zIndex < animation.zIndex()) continue;
			if (zIndex > animation.zIndex()) break;
			Sprite sprite = animation.getSprite();
			sprite.setPosition(getX(), getY());
			_tempSpritesArray.add(sprite);
		}
		return _tempSpritesArray;
	}

	@Override
	public void act(float timeDelta) {
		super.act(timeDelta);

		for (Animation animation : _animations.values()) {
			animation.update(timeDelta);
		}
	}

	public void hideAnimation(String tag) {
		Animation animation = _animations.get(tag);
		if (animation != null) {
			animation.hide();
			for (Listener listener : _listeners) {
				listener.animationHidden(this, animation);
			}
		}
	}

	public void showAnimation(String tag) {
		Animation animation = _animations.get(tag);
		if (animation != null) {
			animation.show();
			for (Listener listener : _listeners) {
				listener.animationShown(this, animation);
			}
		}
	}

	public boolean isAnimationActive(String tag) {
		Animation animation = _animations.get(tag);
		return (animation == null) ? false : animation.isActive();
	}

	public Animation getAnimation(String tag) {
		return _animations.get(tag);
	}

	public abstract static class Listener {
		public void animationEnded(AnimatedActor actor, Animation animation) {
		}

		public void animationShown(AnimatedActor actor, Animation animation) {
		}

		public void animationHidden(AnimatedActor actor, Animation animation) {
		}

		public void removed(AnimatedActor actor) {
		}
	}
}
