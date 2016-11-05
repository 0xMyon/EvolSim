package com.github.myon.evolsim.engine;

import com.github.myon.evolsim.data.Color;

public interface Locateable<T extends Locateable<T>> {

	Color getColor();
	double getRadius();

	Location<T> getLoaction();
	void setLoaction(Location<T> location);

	@SuppressWarnings("unchecked")
	public default void locate(final LocationManager<T> manager, final double x, final double y) {
		manager.add((T)this, x, y);
	}

	@SuppressWarnings("unchecked")
	public default void locate(final LocationManager<T> manager) {
		manager.add((T)this);
	}

	public default void remove() {
		this.getLoaction().remove();
	}

}
