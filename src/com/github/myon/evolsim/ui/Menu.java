package com.github.myon.evolsim.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import javafx.scene.input.KeyCode;

public class Menu implements IMenu {

	private final Map<KeyCode, String> descriptions = new HashMap<>();
	private final Map<KeyCode, Supplier<IMenu>> functions = new HashMap<>();

	private final String name;

	public Menu(final String name) {
		this.name = name;
	}

	@Override
	public String name() {
		return this.name;
	}

	@Override
	public void regitser(final KeyCode code, final String description, final Supplier<IMenu> function) {
		this.descriptions.put(code, description);
		this.functions.put(code, function);
	}

	@Override
	public Map<KeyCode, String> getKeys() {
		return this.descriptions;
	}

	@Override
	public IMenu apply(final KeyCode code) {
		return this.functions.get(code).get();
	}



}
