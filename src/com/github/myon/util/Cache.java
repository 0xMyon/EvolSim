package com.github.myon.util;

public abstract class Cache<T> {

	private T value = null;

	public T value() {
		if (this.value == null) {
			this.value = this.calc();
		}
		return this.value;
	}

	public void clear() {
		this.value = null;
	}

	public abstract T calc();

}
