package com.somethingyellow.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Pool;
import com.somethingyellow.utility.ObjectSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents an Poolable libgdx Actor displayed with set of animations
 * Stores a Map of String tag -> Animation
 * To call initialize() to initialize the object with a temporary map of tag -> AnimationDefs
 * All animations are shown at the start
 * Animation Map cannot be modified after initialization
 * animations() and draw() returns/renders animations in z-index order
 */

public class AnimatedActor extends Actor implements Pool.Poolable {
	private static HashMap<String, AnimationDef> TempAnimationDefMap = new HashMap<String, AnimationDef>();
	private static ArrayList<Animation> TempAnimationsList = new ArrayList<Animation>();
	private static ObjectSet<String> TempAnimationTagsList = new ObjectSet<String>();
	private HashMap<String, Animation> _animations = new HashMap<String, Animation>(); // For access by tag
	private ArrayList<Animation> _animationsList = new ArrayList<Animation>(); // For ordering by z-index
	private AnimationListener _animationListener = new AnimationListener();
	private ObjectSet<Listener> _listeners = new ObjectSet<Listener>();
	private boolean _isVisible = true;
	private boolean _isPaused = false;

	public void initialize(AnimationDef def) {
		TempAnimationDefMap.clear();
		TempAnimationDefMap.put("", def);
		initialize(TempAnimationDefMap);
	}

	public void initialize(Map<String, AnimationDef> defs) {
		for (String tag : defs.keySet()) {
			Animation animation = new Animation(defs.get(tag), tag);
			animation.listeners().add(_animationListener);
			_animations.put(animation.tag(), animation);
			_animationsList.add(animation);
		}

		// Sort animations by z-index
		Collections.sort(_animationsList);
	}

	@Override
	public void reset() {
		_animations.clear();
		_listeners.clear();
		_animationsList.clear();
		_isVisible = true;
		_isPaused = false;
		clearActions();
	}

	/**
	 * Convenience method that configures:
	 * When Animation `fromTag` ends, Animation`toTag` is shown and `fromTag` is hidden
	 */

	protected void setTransition(final String fromTag, final String toTag) {
		_listeners.add(new Listener() {
			@Override
			public void animationEnded(AnimatedActor actor, Animation animation) {
				if (animation.tag().equals(fromTag)) swapAnimation(toTag, fromTag);
			}
		});
	}

	public ObjectSet<Listener> listeners() {
		return _listeners;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);

		if (_isVisible) {
			for (Animation animation : _animationsList) {
				Sprite sprite = animation.getSprite();
				sprite.setScale(getScaleX(), getScaleY());
				sprite.setPosition(getX() + animation.renderDisplacementX(), getY() + animation.renderDisplacementY());
				sprite.draw(batch, parentAlpha);
			}
		}
	}

	@Override
	public int getZIndex() {
		int zIndex = Integer.MIN_VALUE;
		for (Animation animation : animations()) {
			if (animation.isActive()) zIndex = Math.max(zIndex, animation.zIndex());
		}

		return zIndex;
	}

	public List<Animation> animations() {
		return _animationsList;
	}

	public List<Animation> getActiveAnimations() {
		TempAnimationsList.clear();
		for (Animation animation : _animations.values()) {
			if (animation.isActive()) TempAnimationsList.add(animation);
		}
		return TempAnimationsList;
	}

	public Collection<String> getActiveAnimationTags() {
		TempAnimationTagsList.clear();
		for (Animation animation : _animations.values()) {
			if (animation.isActive()) TempAnimationTagsList.add(animation.tag());
		}
		return TempAnimationTagsList;
	}

	public Set<String> animationTags() {
		return _animations.keySet();
	}

	@Override
	public void act(float timeDelta) {
		super.act(timeDelta);

		if (!_isPaused) updateAnimations(timeDelta);
	}

	public void updateAnimations(float timeDelta) {
		for (Animation animation : _animationsList) animation.update(timeDelta);
	}

	public void setIsVisible(boolean isVisible) {
		_isVisible = isVisible;
	}

	public void setIsPaused(boolean isPaused) {
		_isPaused = isPaused;
	}

	protected void setAnimationShown(String tag, boolean isShown) {
		if (isShown) showAnimation(tag); else hideAnimation(tag);
	}

	protected void hideAnimation(String tag) {
		Animation animation = _animations.get(tag);
		if (animation == null) throw new IllegalArgumentException("Animation '" + tag + "' not found!");

		if (animation.hide()) {
			for (Listener listener : _listeners) listener.animationHidden(this, animation);
		}
	}

	protected void showAnimation(String tag) {
		Animation animation = _animations.get(tag);
		if (animation == null) throw new IllegalArgumentException("Animation '" + tag + "' not found!");

		if (animation.show()) {
			for (Listener listener : _listeners) listener.animationShown(this, animation);
		}
	}

	protected void showAnimations(String... tags) {
		for (String tag : tags) {
			showAnimation(tag);
		}
	}

	protected void showAnimations(Collection<String> tags) {
		for (String tag : tags) {
			showAnimation(tag);
		}
	}

	protected void hideAnimations(String... tags) {
		for (String tag : tags) {
			hideAnimation(tag);
		}
	}

	protected void swapAnimation(String showTag, String hideTag) {
		showAnimation(showTag);
		hideAnimation(hideTag);
	}

	protected void hideAllButAnimations(String... showTags) {
		showAnimations(showTags);
		loop:
		for (String tag : _animations.keySet()) {
			for (String showTag : showTags) {
				if (showTag.equals(tag)) continue loop;
			}

			hideAnimation(tag);
		}
	}

	protected boolean isAnimationActive(String tag) {
		Animation animation = _animations.get(tag);
		return (animation == null) ? false : animation.isActive();
	}

	protected boolean isAnyAnimationActive(String... tags) {
		for (String tag : tags) {
			if (isAnimationActive(tag)) return true;
		}

		return false;
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
	}

	private class AnimationListener extends Animation.Listener {
		@Override
		public void animationEnded(Animation animation) {
			for (Listener listener : _listeners) listener.animationEnded(AnimatedActor.this, animation);
		}
	}
}
