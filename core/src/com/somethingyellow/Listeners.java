package com.somethingyellow;

import java.util.Iterator;
import java.util.LinkedList;

public class Listeners<T> implements Iterable<T> {
	private LinkedList<T> _listeners = new LinkedList<T>();
	private LinkedList<T> _tempListeners = new LinkedList<T>();

	public T add(T listener) {
		_listeners.add(listener);
		return listener;
	}

	public void remove(T listener) {
		_listeners.remove(listener);
	}

	public void clear() {
		_listeners.clear();
		_tempListeners.clear();
	}

	@Override
	public Iterator<T> iterator() {
		return new ListenersIterator();
	}

	private class ListenersIterator implements Iterator<T> {
		public ListenersIterator() {
			_tempListeners.clear();
			_tempListeners.addAll(_listeners);
		}

		@Override
		public boolean hasNext() {
			return !_tempListeners.isEmpty();
		}

		@Override
		public T next() {
			return _tempListeners.removeFirst();
		}
	}
}
