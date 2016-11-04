package com.github.myon.evolsim.engine;

import com.github.myon.evolsim.data.Color;
import com.github.myon.util.Util;

/**
 * @author 0xMyon
 *
 * CollisionObjects may be stored in CollisionSpaces.
 *
 * @param <T> concrete implementation
 */
public abstract class CollisionObject<T extends CollisionObject<T>> {


	public CollisionObject() {
		this(null);
	}

	public CollisionObject(final CollisionSpace<T> space) {
		this(space,
				CollisionSpace.SPACE_ROOT_SIZE/4 + Util.nextDouble(0.0, CollisionSpace.SPACE_ROOT_SIZE/4) ,
				CollisionSpace.SPACE_ROOT_SIZE/4 + Util.nextDouble(0.0, CollisionSpace.SPACE_ROOT_SIZE/4));
	}


	public CollisionObject(final CollisionSpace<T> space, final Double x, final Double y) {
		this.x = x;
		this.y = y;
	}


	private CollisionSpace<T> space = null;

	public Double spaceSize() {
		if (this.space != null) {
			return this.space.size();
		} else {
			return 0.0;
		}
	}

	private double x, y;

	public double x() {
		return this.x;
	}
	public double y() {
		return this.y;
	}
	public abstract double r();

	public void x(final double x) {
		this.x += x;
		while (this.x < 0.0) {
			this.x += CollisionSpace.SPACE_ROOT_SIZE;
		}
		while (this.x > CollisionSpace.SPACE_ROOT_SIZE) {
			this.x -= CollisionSpace.SPACE_ROOT_SIZE;
		}
		//this.relocate();
	}

	public void y(final Double y) {
		this.y += y;
		while (this.y < 0.0) {
			this.y += CollisionSpace.SPACE_ROOT_SIZE;
		}
		while (this.y > CollisionSpace.SPACE_ROOT_SIZE) {
			this.y -= CollisionSpace.SPACE_ROOT_SIZE;
		}
		//this.relocate();
	}

	public void relocate() {
		this.locate(this.space);
	}

	@SuppressWarnings("unchecked")
	public void locate(final CollisionSpace<T> space) {
		if (this.space != null) {
			this.space.remove(this);
			this.space = null;
		}
		if (space != null) {
			this.space = space.put((T)this);
		}
	}

	public void relocate(final CollisionSpace<T> space) {
		if (this.space != null) {
			this.locate(this.space);
		} else {
			this.locate(space);
		}
	}

	public abstract Color color();


}
