package com.github.myon.evolsim.test;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.Test;

import com.github.myon.evolsim.data.Color;
import com.github.myon.evolsim.engine.CollisionObject;
import com.github.myon.evolsim.engine.CollisionSpace;
import com.github.myon.util.Util;

public class TestCollision {

	private static class CollisionDummy extends CollisionObject<CollisionDummy> {

		public CollisionDummy(final CollisionSpace<CollisionDummy> space) {
			super(space);
		}
		@Override
		public double r() {
			return 1.0;
		}
		@Override
		public Color color() {
			return null;
		}

	}

	@Test
	public void test() {


		final CollisionSpace<CollisionDummy> space = new CollisionSpace<CollisionDummy>(CollisionSpace.SPACE_ROOT_SIZE, 1);

		final Set<CollisionDummy> objects = new HashSet<>();

		for(int i = 0; i < 1000; i++) {
			objects.add(new CollisionDummy(space));
		}

		for (int i = 0;i < 1000; i++) {
			final Iterator<CollisionDummy> it = objects.iterator();
			while (it.hasNext()) {
				final CollisionDummy current = it.next();
				current.x(Util.nextDouble(-1.0, 1.0));
				current.y(Util.nextDouble(-1.0, 1.0));
				space.checkPoint(
						Util.nextDouble(0.0, CollisionSpace.SPACE_ROOT_SIZE),
						Util.nextDouble(0.0, CollisionSpace.SPACE_ROOT_SIZE), null, null);
				if (Util.nextInt(100) == 0) {
					current.locate(null);
					it.remove();
				}
			}
		}


	}

}
