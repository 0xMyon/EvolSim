package com.github.myon.evolsim.ui;

import com.github.myon.evolsim.World;

import javafx.scene.input.KeyCode;

public class MenuFactory {

	public static IMenu create(final World world) {

		final IMenu main = new Menu("Main");
		final IMenu alterCreature = new Menu("Alter Creature");

		main.regitser(KeyCode.A, "alter the current selected creature", () -> alterCreature );

		alterCreature.regitser(KeyCode.ESCAPE, "return", () -> main );

		return main;

	}

}
