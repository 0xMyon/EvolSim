package com.github.myon.util;

import java.security.SecureRandom;
import java.util.Random;

public class Util {

	private final static Random random = new SecureRandom();

	public static Double nextDouble(final Double min, final Double max) {
		return min + (max - min) * Util.random.nextDouble();
	}

	public static Double nextAngle() {
		return Util.nextDouble(0.0, Math.PI*2);
	}

	public static int nextInt(final int i) {
		return Util.random.nextInt(i);
	}

	public static boolean nextBoolean() {
		return Util.random.nextBoolean();
	}

	public static Double distance(final double x1, final double y1, final double x2, final double y2) {
		final double dx = Math.max(x1, x2) - Math.min(x1, x2);
		final double dy = Math.max(y1, y2) - Math.min(y1, y2);
		return Math.sqrt(dx*dx+dy*dy);
	}

	public static boolean PointInCircle(final Double x, final Double y, final Double cx, final Double cy, final Double r) {
		return r > Util.distance(cx, cy, x, y);
	}

	public static byte[] nextBytes(final int size) {
		final byte[] result = new byte[size];
		Util.random.nextBytes(result);
		return result;
	}


}
