package com.github.myon.evolsim.test;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.junit.Test;

import com.github.myon.evolsim.data.Color;
import com.github.myon.evolsim.engine.Locateable;
import com.github.myon.evolsim.engine.Location;
import com.github.myon.evolsim.engine.LocationManager;
import com.github.myon.util.Util;

public class TestCollision {

	private static class CollisionDummy implements Locateable<CollisionDummy> {

		public CollisionDummy() {

		}

		@Override
		public double getRadius() {
			return 1.0;
		}

		@Override
		public Color getColor() {
			return null;
		}

		private Location<CollisionDummy> location;

		@Override
		public Location<CollisionDummy> getLoaction() {
			return this.location;
		}

		@Override
		public void setLoaction(final Location<CollisionDummy> location) {
			this.location = location;
		}

	}

	@Test
	public void test() {

		final LocationManager<CollisionDummy> space = new LocationManager<>(512, 1);

		final Set<CollisionDummy> objects = new HashSet<>();

		for (int i = 0; i < 1000; i++) {
			final CollisionDummy dummy = new CollisionDummy();
			objects.add(dummy);
			dummy.locate(space, 0, 0);

		}

		for (int i = 0; i < 1000; i++) {
			final Iterator<CollisionDummy> it = objects.iterator();
			while (it.hasNext()) {
				final CollisionDummy current = it.next();
				current.getLoaction().xy(Util.nextDouble(-1.0, 1.0), Util.nextDouble(-1.0, 1.0));
				space.checkPoint(Util.nextDouble(0.0, 512d), Util.nextDouble(0.0, 512d), null, null);
				if (Util.nextInt(100) == 0) {
					current.remove();
					it.remove();
				}
			}
		}

	}

}
