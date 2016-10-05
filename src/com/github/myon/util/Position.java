package com.github.myon.util;

import com.github.myon.evolsim.Constants;
import com.github.myon.evolsim.engine.Modifiable;

public class Position implements Modifiable {

	private Double x,y;
	private Double alpha;

	public Position(final Double x, final Double y, final Double alpha) {
		this.x = x;
		this.y = y;
		this.alpha = alpha;
	}

	public Position(final Position that) {
		this.x = that.x;
		this.y = that.y;
		this.alpha = that.alpha;
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

	public double alpha() {
		return this.alpha;
	}

	public void alpha(final double alpha) {
		this.alpha += alpha;
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
			this.alpha += Util.nextAngle()/360*strength;
			break;
		}
		this.angle.clear();
	}

}
