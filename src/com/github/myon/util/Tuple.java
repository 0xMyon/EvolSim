package com.github.myon.util;

import java.util.Objects;

public class Tuple<T> extends Anything {

	public final T source, target;

	public Tuple(final T source, final T target) {
		this.source = source;
		this.target = target;
	}

	@Override
	public String toString() {
		return "<"+this.source+","+this.target+">";
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.source,this.target);
	}

	@Override
	public boolean equals(final Object other) {
		if (other instanceof Tuple) {
			final Tuple<?> that = (Tuple<?>) other;
			return this.source.equals(that.source) && this.target.equals(that.target);
		}
		return false;
	}

}
