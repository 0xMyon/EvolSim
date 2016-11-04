package com.github.myon.evolsim.data;

import java.util.Objects;

import com.github.myon.util.Anything;
import com.github.myon.util.Util;


public class Color extends Anything implements Modifiable<Color> {

	private int r;
	private int g;
	private int b;

	public Color(final int r, final int g, final int b) {
		this.r = r % 0xFF;
		this.g = g % 0xFF;
		this.b = b % 0xFF;
	}


	public Color(final Color that) {
		this.r = that.r;
		this.g = that.g;
		this.b = that.b;
	}


	public Color(final Color left, final Color right) {
		this.r = left.r ^ right.r;
		this.g = left.g ^ right.g;
		this.b = left.b ^ right.b;
	}


	public boolean match(final Color that) {
		return (that == null) || (this.r & that.r) != 0 || (this.g & that.g) != 0 || (this.b & that.b) != 0;
	}


	@Override
	public String toString() {
		return "#"+Integer.toHexString(this.r)+"."+Integer.toHexString(this.g)+"."+Integer.toHexString(this.b);
	}


	@Override
	public int hashCode() {
		return Objects.hash(this.r,this.b,this.g);
	}


	@Override
	public boolean equals(final Object other) {
		if (other instanceof Color) {
			final Color that = (Color) other;
			return this.r == that.r && this.b == that.b && this.g == that.g;
		}
		return false;
	}

	public int strength() {
		int result = 0;
		for (int i=0;i<7;i++) {
			if ((this.r & (1<<i)) != 0) {
				result++;
			}
			if ((this.b & (1<<i)) != 0) {
				result++;
			}
			if ((this.g & (1<<i)) != 0) {
				result++;
			}
		}
		return result;
	}


	@Override
	public void modify(final int strength) {
		switch(Util.nextInt(6)) {
		case 0:
			this.r |= 1 << Util.nextInt(7);
			break;
		case 1:
			this.g |= 1 << Util.nextInt(7);
			break;
		case 2:
			this.b |= 1 << Util.nextInt(7);
			break;
		case 3:
			this.r &= ~(1 << Util.nextInt(7));
			break;
		case 4:
			this.b &= ~(1 << Util.nextInt(7));
			break;
		case 5:
			this.g &= ~(1 << Util.nextInt(7));
			break;
		}
	}


	public int red() {
		return this.r;
	}
	public int green() {
		return this.g;
	}
	public int blue() {
		return this.b;
	}

}
