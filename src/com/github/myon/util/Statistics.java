package com.github.myon.util;

import java.util.HashMap;
import java.util.Map;

public class Statistics {

	public final Map<String, Double> values = new HashMap<>();

	public void reset() {
		this.values.clear();
	}

	public void add(final String name, final double value) {
		this.values.put(name, this.values.getOrDefault(name, 0.0)+value);
	}

	public void add(final String name, final int value) {
		this.values.put(name, this.values.getOrDefault(name, 0.0)+value);
	}

	public static final Statistics INSTANCE = new Statistics();

}
