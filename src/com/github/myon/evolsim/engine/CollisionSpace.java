package com.github.myon.evolsim.engine;

import java.util.HashSet;
import java.util.Set;

import com.github.myon.evolsim.data.Color;
import com.github.myon.util.Statistics;
import com.github.myon.util.Util;

public class CollisionSpace<T extends CollisionObject<T>> {

	protected final static Double SPACE_ROOT_SIZE = 512.0;
	protected final static Integer LEAF_MAX_OBJECTS_BEFORE_SPLITTING = 10;

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

	public CollisionSpace(final double size, final int max) {
		this.parent = null;
		this.x = 0.0;
		this.y = 0.0;
		this.size = size;;
		this.max_objects_per_space = max;
		this.parent_H = null;
		this.parent_V = null;
	}

	private CollisionSpace(final CollisionSpace<T> parent, final HorizontalPosition h, final VerticalPosition v) {
		this.parent_H = h;
		this.parent_V = v;
		this.parent = parent;
		this.size = parent.size/2;
		this.x = parent.x + ((v == VerticalPosition.LEFT)?0.0:this.size);
		this.y = parent.y + ((h == HorizontalPosition.BOTTOM)?0.0:this.size);
		this.max_objects_per_space = parent.max_objects_per_space;
	}

	private final int max_objects_per_space;

	private final CollisionSpace<T> parent;

	private final Set<T> edgeObjects = new HashSet<>();
	private final Set<T> leafObjects = new HashSet<>();


	private final double x, y;
	private final double size;

	protected void remove(final CollisionObject<T> object) {
		if (!this.edgeObjects.remove(object) && !this.leafObjects.remove(object)) {
			System.out.println("could not rem");
		}
		this.removeTransitive();
	}

	private void removeTransitive() {
		if (this.isEmpty() && this.parent != null) {
			this.parent.remove(this);
		}
	}

	private void remove(final CollisionSpace<T> child) {
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

	protected CollisionSpace<T> put(final T object) {

		if (this.includes(object)) {
			if (!this.isLeaf()) {
				if (this.leafObjects.size() == this.max_objects_per_space) {
					for(final T current : this.leafObjects) {
						current.relocate();
					}
				}
				final boolean left = object.x()+object.r() <= this.x+this.size/2;
				final boolean right = object.x()-object.r() > this.x+this.size/2;
				final boolean bottom = object.y()+object.r() <= this.y+this.size/2;
				final boolean top = object.y()-object.r() > this.y+this.size/2;
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
				&& this.leafObjects.size() < this.max_objects_per_space;
	}

	private boolean isEmpty() {
		return this.leftbottom == null
				&& this.lefttop == null
				&& this.righttop == null
				&& this.rightbottom == null
				&& this.leafObjects.size() == 0
				&& this.edgeObjects.size() == 0;
	}

	private CollisionSpace<T> leftbottom, lefttop, righttop, rightbottom;
	private CollisionSpace<T> LeftBottom() {
		if (this.leftbottom == null) {
			this.leftbottom = new CollisionSpace<T>(this, HorizontalPosition.BOTTOM, VerticalPosition.LEFT);
		}
		return this.leftbottom;
	}
	private CollisionSpace<T> LeftTop() {
		if (this.lefttop == null) {
			this.lefttop = new CollisionSpace<T>(this, HorizontalPosition.TOP, VerticalPosition.LEFT);
		}
		return this.lefttop;
	}
	private CollisionSpace<T> RightTop() {
		if (this.righttop == null) {
			this.righttop = new CollisionSpace<T>(this, HorizontalPosition.TOP, VerticalPosition.RIGHT);
		}
		return this.righttop;
	}
	private CollisionSpace<T> RightBottom() {
		if (this.rightbottom == null) {
			this.rightbottom = new CollisionSpace<T>(this, HorizontalPosition.BOTTOM, VerticalPosition.RIGHT);
		}
		return this.rightbottom;
	}

	private boolean includes(final T object) {
		return (this.x + this.size >= object.x()+object.r())
				&& (this.x <= object.x()-object.r())
				&& (this.y + this.size >= object.y()+object.r())
				&& (this.y <= object.y()-object.r());
	}



	public T checkPoint(final Double x, final Double y, final T self, final Color color) {
		Statistics.INSTANCE.add("CollisionSpace::checkPoint", 1);
		final long time = System.currentTimeMillis();
		try {
			for(final CollisionObject<T> object : this.leafObjects) {
				if (object != self && Util.PointInCircle(x,y,object.x(), object.y(), object.r()) && object.color().match(color)) {
					return (T)object;
				}
			}
			for(final CollisionObject<T> object : this.edgeObjects) {
				if (object != self && Util.PointInCircle(x,y,object.x(), object.y(), object.r()) && object.color().match(color)) {
					return (T)object;
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



	public Set<T> getAllInRadius(final Double x, final Double y, final Double r) {
		Statistics.INSTANCE.add("CollisionSpace::getAllInRadius", 1);
		final long time = System.currentTimeMillis();
		try {
			final Set<T> result = new HashSet<>();
			for (int ix = -1 ; ix <= 1; ix++) {
				for (int iy = -1 ; iy <= 1; iy++) {
					this.fillAllInRadius(x + ix*CollisionSpace.SPACE_ROOT_SIZE,y + iy*CollisionSpace.SPACE_ROOT_SIZE,r, result);
				}
			}
			return result;
		} finally {
			Statistics.INSTANCE.add("CollisionSpace::getAllInRadius time", ((double)System.currentTimeMillis()-time)/1000);
		}
	}

	private void fillAllInRadius(final Double x, final Double y, final Double r, final Set<T> result) {
		if (this.intersects(x,y,r)) {
			result.addAll(this.leafObjects);
			result.addAll(this.edgeObjects);
			if (this.leftbottom != null) {
				result.addAll(this.leftbottom.getAllInRadius(x, y, r));
			}
			if (this.lefttop != null) {
				result.addAll(this.lefttop.getAllInRadius(x, y, r));
			}
			if (this.rightbottom != null) {
				result.addAll(this.rightbottom.getAllInRadius(x, y, r));
			}
			if (this.righttop != null) {
				result.addAll(this.righttop.getAllInRadius(x, y, r));
			}
		}
	}

	private boolean intersects(final Double x, final Double y, final Double r) {
		return Util.PointInCircle(this.x, this.y, x, y, r)
				|| Util.PointInCircle(this.x + this.size, this.y, x, y, r)
				|| Util.PointInCircle(this.x, this.y + this.size, x, y, r)
				|| Util.PointInCircle(this.x + this.size, this.y + this.size, x, y, r);
	}

	public Double size() {
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

	protected boolean contains(final CollisionObject<T> object) {
		return this.edgeObjects.contains(object) || this.leafObjects.contains(object);
	}


}
