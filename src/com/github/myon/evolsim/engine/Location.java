package com.github.myon.evolsim.engine;

import java.util.Objects;

import com.github.myon.evolsim.data.Color;
import com.github.myon.util.Anything;

/**
 * @author 0xMyon
 *
 * CollisionObjects may be stored in CollisionSpaces.
 *
 * @param <T> concrete implementation
 */
public final class Location<T extends Locateable<T>> extends Anything {

	private LocationManager<T> manager;

	private final double bounds;

	public Location(final LocationManager<T> manager, final double bounds, final T object, final Double x, final Double y) {
		this.object = object;
		this.bounds = bounds;
		this.manager = manager;
		this.x = x;
		this.y = y;
	}

	public double spaceSize() {
		return this.manager.size();

	}

	private double x, y;

	public double x() {
		return this.x;
	}
	public double y() {
		return this.y;
	}

	public void xy(final double x, final double y) {
		this.x(x);
		this.y(y);
		this.relocate();
	}

	private void x(final double x) {
		this.x += x;
		while (this.x < 0.0) {
			this.x += this.bounds;
		}
		while (this.x > this.bounds) {
			this.x -= this.bounds;
		}
	}

	private void y(final Double y) {
		this.y += y;
		while (this.y < 0.0) {
			this.y += this.bounds;
		}
		while (this.y > this.bounds) {
			this.y -= this.bounds;
		}
	}

	protected void relocate() {
		this.remove();
		this.manager = this.manager.put(this);
	}

	private final T object;
	public T getObject() {
		return this.object;
	}

	public Color getColor() {
		return this.object.getColor();
	}

	public double getRadius() {
		return this.object.getRadius();
	}

	public boolean remove() {
		return this.manager.remove(this);
	}

	@Override
	public String toString() {
		return this.object.toString() + "["+this.x+","+this.y+"]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.object, this.x, this.y);
	}

	@Override
	public boolean equals(final Object other) {
		if (other instanceof Location<?>) {
			final Location<?> that = (Location<?>) other;
			return this.object.equals(that.object) && this.y == that.x && this.y == that.y;
		}
		return false;
	}



}
