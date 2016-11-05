package com.github.myon.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Statistics {

	private final Map<String, Double> values = new HashMap<>();

	public synchronized Set<Entry<String, Double>> values() {
		final Set<Entry<String, Double>> result = new HashSet<>();
		result.addAll(this.values.entrySet());
		return result;
	}

	public synchronized void reset() {
		this.values.clear();
	}

	public synchronized void add(final String name, final double value) {
		this.values.put(name, this.values.getOrDefault(name, 0.0)+value);
	}

	public synchronized void add(final String name, final int value) {
		this.values.put(name, this.values.getOrDefault(name, 0.0)+value);
	}

	public static final Statistics INSTANCE = new Statistics();

}
