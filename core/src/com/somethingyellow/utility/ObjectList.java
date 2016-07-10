package com.somethingyellow.utility;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Convenience class to manage a linked list of objects
 * When iterating, a temporary copy of the list is created (avoiding any "concurrent modification" errors)
 */

public class ObjectList<T> implements Iterable<T> {
	private LinkedList<T> _list = new LinkedList<T>();
	private LinkedList<T> _tempList = new LinkedList<T>();

	public T add(T listener) {
		_list.add(listener);
		return listener;
	}

	public void remove(T listener) {
		_list.remove(listener);
	}

	public void clear() {
		_list.clear();
		_tempList.clear();
	}

	@Override
	public Iterator<T> iterator() {
		return new ListIterator();
	}

	private class ListIterator implements Iterator<T> {
		public ListIterator() {
			_tempList.clear();
			_tempList.addAll(_list);
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
