package com.github.myon.util;

import com.github.myon.evolsim.Constants;
import com.github.myon.evolsim.data.Modifiable;

public class Position implements Modifiable<Position> {

	private Double x,y;
	private Double orientation;

	public Position(final Double x, final Double y, final Double orientation) {
		this.x = x;
		this.y = y;
		this.orientation = orientation;
	}

	public Position(final Position that) {
		this.x = that.x;
		this.y = that.y;
		this.orientation = that.orientation;
	}

	public Position(final Position left, final Position right) {
		switch(Util.nextInt(3)) {
		case 0:
			this.orientation = left.orientation;
			break;
		case 1:
			this.orientation = right.orientation;
			break;
		case 2:
			this.orientation = (left.orientation + right.orientation)/2;
			break;
		}

		switch(Util.nextInt(3)) {
		case 0:
			this.x = left.x;
			break;
		case 1:
			this.x = right.x;
			break;
		case 2:
			this.x = (left.x + right.x)/2;
			break;
		}

		switch(Util.nextInt(3)) {
		case 0:
			this.y = left.y;
			break;
		case 1:
			this.y = right.y;
			break;
		case 2:
			this.y = (left.y + right.y)/2;
			break;
		}
	}

	public double x() {
		return this.x;
	}

	public double y() {
		return this.y;
	}

	public void x(final double x) {
		this.x += x;
		if (this.x > Constants.WORLD_SIZE) {
			this.x -= 2*Constants.WORLD_SIZE;
		}
		if (this.x < -Constants.WORLD_SIZE) {
			this.x += 2*Constants.WORLD_SIZE;
		}
		this.angle.clear();
	}

	public void y(final double y) {
		this.y += y;
		if (this.y > Constants.WORLD_SIZE) {
			this.y -= 2*Constants.WORLD_SIZE;
		}
		if (this.y < -Constants.WORLD_SIZE) {
			this.y += 2*Constants.WORLD_SIZE;
		}
		this.angle.clear();
	}

	public double orientation() {
		return this.orientation;
	}

	public void orientation(final double alpha) {
		this.orientation += alpha;
	}

	public double length() {
		return Math.sqrt(this.x()*this.x()+this.y()*this.y());
	}

	private final Cache<Double> angle = new Cache<Double>() {
		@Override
		public Double calc() {
			return Math.atan(Position.this.y()/Position.this.x());
		}
	};

	public double angle() {
		return this.angle.value();
	}

	@Override
	public void modify(final int strength) {
		switch(Util.nextInt(2)) {
		case 0:
			this.x += Util.nextDouble(-0.01*strength, 0.01*strength);
			break;
		case 1:
			this.y += Util.nextDouble(-0.01*strength, 0.01*strength);
			break;
		case 2:
			this.orientation += Util.nextAngle()/360*strength;
			break;
		}
		this.angle.clear();
	}

}
