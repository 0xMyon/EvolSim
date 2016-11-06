package com.github.myon.util;

import java.util.Deque;
import java.util.LinkedList;

public class Queue<T> {

	private final Deque<T> data;
	private final Lock lock;

	public Queue() {
		this.data = new LinkedList<>();
		this.lock = new Lock(true);
	}

	public synchronized void add(final T object) {
		this.data.add(object);
		this.lock.unlock();
		System.out.println(Thread.currentThread().getName() + " queueing " + object + " @ " + this.data.size());
	}

	public T pop() {
		this.lock.lock();
		synchronized (this) {
			final T result = this.data.pop();
			if (!this.data.isEmpty()) {
				this.lock.unlock();
			}
			System.out.println(Thread.currentThread().getName() + " popping " + result + " @ " + this.data.size());
			return result;
		}
	}

	public void remove(final Runnable task) {
		synchronized (this) {
			this.data.remove(task);
			if (!this.data.isEmpty()) {
				this.lock.unlock();
			}

		}
	}

}
