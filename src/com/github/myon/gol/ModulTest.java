package com.github.myon.gol;

import org.junit.Assert;
import org.junit.Test;

public class ModulTest {

	@Test
	public void test() {


		Assert.assertEquals(100, 100%512);
		Assert.assertEquals(0, 512%512);
		Assert.assertEquals(1, 513%512);
		Assert.assertEquals(0, 0%512);
		Assert.assertEquals(511, Math.floorMod(-1, 512));

		GameOfLife gol = new GameOfLife(64);

		gol = gol.next();


	}

}
