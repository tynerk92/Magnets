package com.somethingyellow.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.somethingyellow.utility.ObjectList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents an Poolable libgdx Actor displayed with set of animations
 * Stores a Map of String tag -> Animation
 * To call initialize() to initialize the object
 * Animations are hidden at the start
 * Animation Map cannot be modified after initialization
 * animations() and draw() returns/renders animations in z-index order
 * remove() frees itself from Pools
 */

public class AnimatedActor extends Actor implements Pool.Poolable {
	private HashMap<String, Animation> _animations = new HashMap<String, Animation>(); // For access by tag
	private ArrayList<Animation> _animationsArray = new ArrayList<Animation>(); // For ordering by z-index
	private AnimationListener _animationListener = new AnimationListener();
	private ObjectList<Listener> _listeners = new ObjectList<Listener>();

	public void initialize(Map<String, AnimationDef> defs) {
		for (String tag : defs.keySet()) {
			Animation animation = new Animation(defs.get(tag), tag);
			animation.listeners().add(_animationListener);
			animation.hide();
			_animations.put(animation.tag(), animation);
			_animationsArray.add(animation);
		}

		// Sort animations by z-index
		Collections.sort(_animationsArray);
	}

	@Override
	public void reset() {
		_animations.clear();
		_listeners.clear();
		_animationsArray.clear();
		clearActions();
	}

	/**
	 * Convenience method that configures:
	 * When Animation `fromTag` ends, Animation`toTag` is shown and `fromTag` is hidden
	 */

	protected void setTransition(final String fromTag, final String toTag) {
		Animation animation = _animations.get(fromTag);
		if (animation == null) {
			throw new IllegalArgumentException("`fromTag` doesn't exist!");
		}

		if (!_animations.containsKey(toTag)) {
			throw new IllegalArgumentException("`toTag` doesn't exist!");
		}

		_listeners.add(new Listener() {
			@Override
			public void animationEnded(AnimatedActor actor, Animation animation) {
				if (animation.tag().equals(fromTag)) {
					showAnimation(toTag);
					hideAnimation(fromTag);
				}
			}
		});
	}

	@Override
	public boolean remove() {
		for (Listener listener : _listeners) {
			listener.removed(this);
		}

		Pools.free(this);

		return super.remove();
	}

	public ObjectList<Listener> listeners() {
		return _listeners;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		for (Animation animation : _animationsArray) {
			Sprite sprite = animation.getSprite();
			sprite.setPosition(getX(), getY());
			sprite.draw(batch, parentAlpha);
		}
	}

	protected List<Animation> animations() {
		return _animationsArray;
	}

	@Override
	public void act(float timeDelta) {
		super.act(timeDelta);

		for (Animation animation : _animationsArray) animation.update(timeDelta);
	}

	protected void hideAnimation(String tag) {
		Animation animation = _animations.get(tag);
		if (animation == null) throw new IllegalArgumentException("Animation '" + tag + "' not found!");

		animation.hide();
		for (Listener listener : _listeners) listener.animationHidden(this, animation);
	}

	protected void showAnimation(String tag) {
		Animation animation = _animations.get(tag);
		if (animation == null) throw new IllegalArgumentException("Animation '" + tag + "' not found!");

		animation.show();
		for (Listener listener : _listeners) listener.animationShown(this, animation);
	}

	protected boolean isAnimationActive(String tag) {
		Animation animation = _animations.get(tag);
		return (animation == null) ? false : animation.isActive();
	}

	public static abstract class Listener {
		/**
		 * When one of its animation ends (after which it loops)
		 */
		public void animationEnded(AnimatedActor actor, Animation animation) {
		}

		/**
		 * When one of its animation is shown
		 */
		public void animationShown(AnimatedActor actor, Animation animation) {
		}

		/**
		 * When one of its animation is hidden
		 */
		public void animationHidden(AnimatedActor actor, Animation animation) {
		}

		/**
		 * When this is removed
		 */
		public void removed(AnimatedActor actor) {
		}
	}

	private class AnimationListener extends Animation.Listener {
		@Override
		public void animationEnded(Animation animation) {
			for (Listener listener : _listeners) listener.animationEnded(AnimatedActor.this, animation);
		}
	}
}
