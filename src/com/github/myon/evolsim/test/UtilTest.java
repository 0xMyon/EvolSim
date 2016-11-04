package com.github.myon.evolsim.test;

import org.junit.Assert;
import org.junit.Test;

import com.github.myon.evolsim.data.Position;



public class UtilTest {

	@Test
	public void test() {


		Assert.assertEquals(0.0/8*2*Math.PI, new Position(1.0, 0.0, 0.0).angle(), 0.1);
		Assert.assertEquals(1.0/8*2*Math.PI, new Position(1.0, 1.0, 0.0).angle(), 0.1);
		Assert.assertEquals(2.0/8*2*Math.PI, new Position(0.0, 1.0, 0.0).angle(), 0.1);
		Assert.assertEquals(3.0/8*2*Math.PI, new Position(-1.0, 1.0, 0.0).angle()+Math.PI, 0.1);
		Assert.assertEquals(4.0/8*2*Math.PI, new Position(-1.0, 0.0, 0.0).angle()+Math.PI, 0.1);
		Assert.assertEquals(5.0/8*2*Math.PI, new Position(-1.0, -1.0, 0.0).angle()+Math.PI, 0.1);
		Assert.assertEquals(6.0/8*2*Math.PI, new Position(0.0, -1.0, 0.0).angle()+2*Math.PI, 0.1);
		Assert.assertEquals(7.0/8*2*Math.PI, new Position(1.0, -1.0, 0.0).angle()+2*Math.PI, 0.1);

	}

}
