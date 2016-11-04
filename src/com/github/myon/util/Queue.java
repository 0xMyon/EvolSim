package com.github.myon.util;

import java.util.Deque;
import java.util.LinkedList;

public class Queue<T> {

	private final Deque<T> data;
	private final Lock lock;

	public Queue(){
		this.data = new LinkedList<>();
		this.lock = new Lock(true);
	}

	public synchronized void add(final T object) {
		this.data.add(object);
		this.lock.unlock();
	}

	public T pop() {
		this.lock.lock();
		synchronized (this) {
			final T result = this.data.pop();
			if (!this.data.isEmpty()) {
				this.lock.unlock();
			}
			return result;
		}
	}

}
