package com.github.myon.evolsim.ui;


import java.util.Map;
import java.util.function.Supplier;

import javafx.scene.input.KeyCode;

public interface IMenu {

	public Map<KeyCode,String> getKeys();

	IMenu apply(KeyCode code);

	public void regitser(final KeyCode code, final String description, final Supplier<IMenu> function);
	public String name();
}
