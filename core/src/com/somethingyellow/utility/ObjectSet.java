package com.somethingyellow.utility;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Convenience class to manage a set of objects
 * When iterating, a temporary copy of the set is created (avoiding any "concurrent modification" errors)
 */

public class ObjectSet<T> implements Iterable<T>, Collection<T> {
	private HashSet<T> _set = new HashSet<T>();
	private LinkedList<T> _tempList = new LinkedList<T>();

	public ObjectSet() {
	}

	public ObjectSet(Collection<T> collection) {
		_set.addAll(collection);
	}

	@Override
	public boolean remove(Object object) {
		return _set.remove(object);
	}

	@Override
	public boolean containsAll(Collection<?> collection) {
		return _set.containsAll(collection);
	}

	@Override
	public boolean addAll(Collection<? extends T> collection) {
		return _set.addAll(collection);
	}

	@Override
	public boolean removeAll(Collection<?> collection) {
		return _set.removeAll(collection);
	}

	@Override
	public boolean retainAll(Collection<?> collection) {
		return _set.retainAll(collection);
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
	public int size() {
		return _set.size();
	}

	@Override
	public boolean isEmpty() {
		return _set.isEmpty();
	}

	@Override
	public boolean contains(Object object) {
		return _set.contains(object);
	}

	@Override
	public Iterator<T> iterator() {
		return new ListIterator();
	}

	@Override
	public Object[] toArray() {
		return _set.toArray();
	}

	@Override
	public <T> T[] toArray(T[] array) {
		return _set.toArray(array);
	}

	@Override
	public boolean add(T object) {
		return _set.add(object);
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
