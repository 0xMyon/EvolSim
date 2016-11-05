package com.github.myon.evolsim.engine;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.github.myon.evolsim.data.Color;
import com.github.myon.util.Statistics;
import com.github.myon.util.Util;

public class LocationManager<T extends Locateable<T>> {

	private final HorizontalPosition parent_H;
	private final VerticalPosition parent_V;

	private static enum HorizontalPosition {
		TOP,
		BOTTOM,
	}

	private static enum VerticalPosition {
		LEFT,
		RIGHT,
	}

	public LocationManager(final double size, final int max) {
		this.parent = null;
		this.x = 0.0;
		this.y = 0.0;
		this.size = size;
		this.MAX_OBJECTS_PER_SUB_SPACE = max;
		this.parent_H = null;
		this.parent_V = null;
	}

	private LocationManager(final LocationManager<T> parent, final HorizontalPosition h, final VerticalPosition v) {
		this.parent_H = h;
		this.parent_V = v;
		this.parent = parent;
		this.size = parent.size/2;
		this.x = parent.x + ((v == VerticalPosition.LEFT)?0.0:this.size);
		this.y = parent.y + ((h == HorizontalPosition.BOTTOM)?0.0:this.size);
		this.MAX_OBJECTS_PER_SUB_SPACE = parent.MAX_OBJECTS_PER_SUB_SPACE;
	}

	private final int MAX_OBJECTS_PER_SUB_SPACE;

	private final LocationManager<T> parent;

	private final Set<Location<T>> edgeObjects = new HashSet<>();
	private final Set<Location<T>> leafObjects = new HashSet<>();


	private final double x, y;
	private final double size;

	protected synchronized boolean remove(final Location<T> object) {
		boolean result = true;
		if (!this.edgeObjects.remove(object) && !this.leafObjects.remove(object)) {
			result = false;
		}
		this.removeTransitive();
		return result;
	}

	private void removeTransitive() {
		if (this.isEmpty() && this.parent != null) {
			this.parent.remove(this);
		}
	}

	private void remove(final LocationManager<T> child) {
		if (child.horizontal() == HorizontalPosition.TOP) {
			if (child.vertical() == VerticalPosition.LEFT) {
				this.lefttop = null;
			} else {
				this.righttop = null;
			}
		} else {
			if (child.vertical() == VerticalPosition.LEFT) {
				this.leftbottom = null;
			} else {
				this.rightbottom = null;
			}
		}
		this.removeTransitive();
	}

	private HorizontalPosition horizontal() {
		return this.parent_H;
	}

	private VerticalPosition vertical() {
		return this.parent_V;
	}

	protected synchronized LocationManager<T> put(final Location<T> object) {
		if (!this.isLeaf()) {
			if (this.leafObjects.size() == this.MAX_OBJECTS_PER_SUB_SPACE) {
				final Iterator<Location<T>> it = this.leafObjects.iterator();
				while(it.hasNext()) {
					this.putInternal(it.next());
					it.remove();
				}
			}
		}
		return this.putInternal(object);
	}

	private LocationManager<T> putInternal(final Location<T> object) {
		if (this.includes(object)) {
			if (!this.isLeaf()) {
				final boolean left = object.x()+object.getRadius() <= this.x+this.size/2;
				final boolean right = object.x()-object.getRadius() > this.x+this.size/2;
				final boolean bottom = object.y()+object.getRadius() <= this.y+this.size/2;
				final boolean top = object.y()-object.getRadius() > this.y+this.size/2;
				if (left && top) {
					return this.LeftTop().put(object);
				} else if (right && top) {
					return this.RightTop().put(object);
				} else if (left && bottom) {
					return this.LeftBottom().put(object);
				} else if (right && bottom) {
					return this.RightBottom().put(object);
				} else {
					this.edgeObjects.add(object);
					return this;
				}

			} else {
				this.leafObjects.add(object);
				return this;
			}
		} else if (this.parent != null) {
			return this.parent.put(object);
		} else {
			this.edgeObjects.add(object);
			return this;
		}

	}


	private boolean isLeaf() {
		return this.leftbottom == null
				&& this.lefttop == null
				&& this.righttop == null
				&& this.rightbottom == null
				&& this.leafObjects.size() < this.MAX_OBJECTS_PER_SUB_SPACE;
	}

	private boolean isEmpty() {
		return this.leftbottom == null
				&& this.lefttop == null
				&& this.righttop == null
				&& this.rightbottom == null
				&& this.leafObjects.size() == 0
				&& this.edgeObjects.size() == 0;
	}

	private LocationManager<T> leftbottom, lefttop, righttop, rightbottom;
	private LocationManager<T> LeftBottom() {
		if (this.leftbottom == null) {
			this.leftbottom = new LocationManager<>(this, HorizontalPosition.BOTTOM, VerticalPosition.LEFT);
		}
		return this.leftbottom;
	}
	private LocationManager<T> LeftTop() {
		if (this.lefttop == null) {
			this.lefttop = new LocationManager<>(this, HorizontalPosition.TOP, VerticalPosition.LEFT);
		}
		return this.lefttop;
	}
	private LocationManager<T> RightTop() {
		if (this.righttop == null) {
			this.righttop = new LocationManager<>(this, HorizontalPosition.TOP, VerticalPosition.RIGHT);
		}
		return this.righttop;
	}
	private LocationManager<T> RightBottom() {
		if (this.rightbottom == null) {
			this.rightbottom = new LocationManager<>(this, HorizontalPosition.BOTTOM, VerticalPosition.RIGHT);
		}
		return this.rightbottom;
	}

	private boolean includes(final Location<T> object) {
		return (this.x + this.size >= object.x()+object.getRadius())
				&& (this.x <= object.x()-object.getRadius())
				&& (this.y + this.size >= object.y()+object.getRadius())
				&& (this.y <= object.y()-object.getRadius());
	}



	public synchronized T checkPoint(final double x, final double y, final Location<T> self, final Color color) {
		Statistics.INSTANCE.add("CollisionSpace::checkPoint", 1);
		final long time = System.currentTimeMillis();
		try {
			for(final Location<T> object : this.leafObjects) {
				if (object != self && Util.PointInCircle(x,y,object.x(), object.y(), object.getRadius()) && (color == null || color.match(object.getColor()))) {
					return object.getObject();
				}
			}
			for(final Location<T> object : this.edgeObjects) {
				if (object != self && Util.PointInCircle(x,y,object.x(), object.y(), object.getRadius()) && (color == null || color.match(object.getColor()))) {
					return object.getObject();
				}
			}
			if (x < this.x + this.size/2) {
				if (y < this.y + this.size/2 && this.leftbottom != null) {
					return this.leftbottom.checkPoint(x, y, self, color);
				} else if (y > this.y + this.size/2 && this.lefttop != null) {
					return this.lefttop.checkPoint(x, y, self, color);
				}
			} else if (x > this.x + this.size/2) {
				if (y < this.y + this.size/2 && this.rightbottom != null) {
					return this.rightbottom.checkPoint(x, y, self, color);
				} else if (y > this.y + this.size/2 && this.righttop != null) {
					return this.righttop.checkPoint(x, y, self, color);
				}
			}
			return null;
		} finally {
			Statistics.INSTANCE.add("CollisionSpace::checkPoint time", ((double)System.currentTimeMillis()-time)/1000);
		}
	}






	private boolean intersects(final Double x, final Double y, final Double r) {
		return Util.PointInCircle(this.x, this.y, x, y, r)
				|| Util.PointInCircle(this.x + this.size, this.y, x, y, r)
				|| Util.PointInCircle(this.x, this.y + this.size, x, y, r)
				|| Util.PointInCircle(this.x + this.size, this.y + this.size, x, y, r);
	}

	public synchronized Double size() {
		return this.size;
	}

	public int count() {
		int result = this.leafObjects.size();
		result += this.edgeObjects.size();
		if (this.rightbottom != null) {
			result += this.rightbottom.count();
		}
		if (this.leftbottom != null) {
			result += this.leftbottom.count();
		}
		if (this.lefttop != null) {
			result += this.lefttop.count();
		}
		if (this.righttop != null) {
			result += this.righttop.count();
		}
		return result;
	}

	private boolean contains(final Location<T> object) {
		return this.edgeObjects.contains(object) || this.leafObjects.contains(object);
	}

	protected synchronized void add(final T object, final double x, final double y) {
		final Location<T> loaction = new Location<>(this, this.size, object, x, y);
		object.setLoaction(loaction);
		this.putInternal(loaction);
	}

	protected void add(final T object) {
		this.add(object,
				Util.nextDouble(this.size/4, 3*this.size/4),
				Util.nextDouble(this.size/4, 3*this.size/4)
				);
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "["+this.x+","+this.y+"]:"+this.size;
	}



}
