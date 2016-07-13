package com.somethingyellow.utility;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Convenience class to manage a set of objects
 * When iterating, a temporary copy of the set is created (avoiding any "concurrent modification" errors)
 */

public class ObjectSet<T> implements Iterable<T> {
	private HashSet<T> _set = new HashSet<T>();
	private LinkedList<T> _tempList = new LinkedList<T>();

	public ObjectSet() {
	}

	public ObjectSet(ObjectSet<T> objectSet) {
		_set.addAll(objectSet._set);
	}

	public T add(T object) {
		_set.add(object);
		return object;
	}

	public boolean contains(T object) {
		return _set.contains(object);
	}

	public void remove(T object) {
		_set.remove(object);
	}

	public ObjectSet<T> clone() {
		return new ObjectSet<T>(this);
	}

	public void clear() {
		_set.clear();
		_tempList.clear();
	}

	@Override
	public String toString() {
		return _set.toString();
	}

	@Override
	public Iterator<T> iterator() {
		return new ListIterator();
	}

	private class ListIterator implements Iterator<T> {
		public ListIterator() {
			_tempList.clear();
			_tempList.addAll(_set);
		}

		@Override
		public boolean hasNext() {
			return !_tempList.isEmpty();
		}

		@Override
		public T next() {
			return _tempList.removeFirst();
		}
	}
}
