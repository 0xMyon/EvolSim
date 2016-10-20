package com.github.myon.evolsim;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;

import com.github.myon.evolsim.data.Color;
import com.github.myon.evolsim.data.CreatureData;
import com.github.myon.evolsim.engine.CollisionObject;
import com.github.myon.util.Cache;
import com.github.myon.util.Util;


public class Creature extends CollisionObject<Creature> {

	private static int ID = 0;

	// final attributes
	private final int id = Creature.ID++;
	private final String name;
	private final int generation;
	private final World world;

	private final CreatureData data;

	protected final Vector<Neuron> neurons = new Vector<>();

	private Map<Neuron, Double> state = new HashMap<>();
	private int age = 0;
	private final double capacity;
	private double energy;
	private double orientation;


	public double energy() {
		return this.energy;
	}
	public double orientation() {
		return this.orientation;
	}
	public double capacity() {
		return this.capacity;
	}
	public World world() {
		return this.world;
	}
	public String name() {
		return this.name;
	}
	public int age() {
		return this.age;
	}
	@Override
	public Color color() {
		return this.data.color();
	}


	public Creature(final World world) {
		super(world.getCollisionSpace());
		this.world = world;
		this.generation = 0;

		this.data = new CreatureData(world.generator());


		for(int i = 0; i < this.data.neurons(); i++) {
			final Neuron neuron = new Neuron(this, i, this.data.neuron(i));
			this.neurons.add(neuron);
		}

		//this.color = world.generator().generateColor();
		this.orientation = Util.nextAngle();
		this.name = world.generator().generateName();
		this.energy = 5.0*this.neurons.size();
		this.capacity = world.generator().generateCapacity();
	}



	private Creature(final Creature that, final Double x, final Double y) {
		super(that.world.getCollisionSpace(), x, y);

		this.data = new CreatureData(that.data);
		for(int i = 0; i < this.data.neurons(); i++) {
			final Neuron neuron = new Neuron(this, i, this.data.neuron(i));
			this.neurons.add(neuron);
		}

		this.world = that.world;
		this.energy = that.energy * Constants.CREATURE_DEVIDE_FACTOR;
		this.generation = that.generation+1;
		this.orientation = Util.nextAngle();
		this.name = this.world.generator().generateName();
		this.capacity = that.capacity;
	}




	public Creature(final Creature mother, final Creature father, final double x, final double y) {
		super(mother.world.getCollisionSpace(), x, y);

		this.data = new CreatureData(mother.data, father.data);
		for(int i = 0; i < this.data.neurons(); i++) {
			final Neuron neuron = new Neuron(this, i, this.data.neuron(i));
			this.neurons.add(neuron);
		}

		this.world = mother.world;
		this.energy = mother.energy * Constants.CREATURE_DEVIDE_FACTOR;
		this.generation = mother.generation+1;
		this.orientation = Util.nextAngle();
		this.name = this.world.generator().generateName();
		this.capacity = mother.capacity;
	}

	@Override
	public String toString() {
		return this.name;
	}
	@Override
	public int hashCode() {
		return Objects.hash(this.id);
	}
	@Override
	public boolean equals(final Object other) {
		if (other instanceof Creature) {
			final Creature that = (Creature) other;
			return this.id == that.id;
		}
		return false;
	}



	public Double value(final int i) {
		return this.state.getOrDefault(i, this.data.neuron(i).initial());
	}





	private Neuron randomNeuron() {
		return this.neurons.get(Util.nextInt(this.neurons.size()-1));
	}



	private final Cache<Double> radius = new Cache<Double>() {
		@Override
		public Double calc() {
			Double result = 1.0;
			for(final Neuron current: Creature.this.neurons) {
				result = Math.max(result, current.position().length());
			}
			return result;
		}
	};

	public void step() {
		this.age++;

		if (this.energy > this.capacity) {
			this.energy = this.capacity;
		}

		if (this.isActive()) {
			final Map<Neuron, Double> state = new HashMap<>();
			for(final Neuron current : this.neurons) {
				state.put(current, current.calculateValue());
			}
			this.state = state;

			this.energy -= this.radius()*0.1;

			for(final Neuron current : this.neurons) {
				current.act();
			}
		} else if (!this.isDead()) {
			this.energy *= 0.75;
		}
	}

	public boolean isDead() {
		return this.energy < 1;
	}

	public boolean isActive() {
		return this.energy > this.capacity/10;
	}







	public Double radius() {
		if (this.radius == null) {
			return 1.0;
		}
		return this.radius.value();
	}









	public void move(final Neuron neuron) {
		final double e = neuron.value()*0.01;
		final double r = neuron.position().length() * Math.cos(neuron.position().orientation() - neuron.position().angle());
		final double f = e * Math.sin(neuron.position().orientation() - neuron.position().angle());
		final double fx = f * Math.cos(this.orientation + neuron.position().angle());
		final double fy = f * Math.sin(this.orientation + neuron.position().angle());
		//System.out.println(this.name + " moved a:"+ (r/(2*Math.PI)) + " x:" + fx + " y:" + fy);
		this.orientation += r;
		this.x(fx);
		this.y(fy);
		this.energy -= e;
	}


	public void collect(final Neuron neuron) {
		final Double size = neuron.spaceSize();
		this.energy += neuron.value()*Constants.GLOBAL_COLLECTABLE_ENERGY/this.world.creatures();
	}

	public void predate(final Neuron neuron) {
		final double x = this.x() + Math.cos(this.orientation+neuron.position().orientation()) * neuron.position().x();
		final double y = this.y() + Math.sin(this.orientation+neuron.position().orientation()) * neuron.position().y();
		final Creature c = this.world.getCollisionSpace().checkPoint(x, y, this, neuron.color());
		if (c != null) {
			this.energy += neuron.value()*0.5;
			c.energy -= neuron.value();
		} else {
			this.energy -= neuron.value();
		}
	}

	public void donate(final Neuron neuron) {
		// TODO implement
	}

	public void divide(final Neuron neuron) {
		if (neuron.value() > 0.5) {
			final double x = this.x() + Math.cos(this.orientation+neuron.position().orientation()) * neuron.position().x();
			final double y = this.y() + Math.sin(this.orientation+neuron.position().orientation()) * neuron.position().y();
			final Creature c = this.world.getCollisionSpace().checkPoint(x, y, this, neuron.color());
			if (this.energy * Constants.CREATURE_DEVIDE_FACTOR > this.capacity/10) {
				if (null == c) {

					final Creature child = new Creature(this, x, y);
					child.locate(this.world.getCollisionSpace());
					child.modify(50);
					this.world.add(child);
					this.energy *= Constants.CREATURE_DEVIDE_FACTOR;

				} else {
					//final Creature child = new Creature(this, c, x, y);
					//child.locate(this.world.getCollisionSpace());
					//child.modify(50);
					//this.world.add(child);
					//this.energy *= Constants.CREATURE_DEVIDE_FACTOR;

				}
			}
		}
	}






	public int neurons() {
		return this.data.neurons();
	}
	public double connection(final int source, final int target) {
		return this.data.connection(source, target);
	}



	public void resetRadius() {
		this.radius.clear();
	}

	public int generation() {
		return this.generation;
	}


	public String species() {
		String result = "";
		for(final NeuronType t : NeuronType.values()) {
			Integer count = 0;
			for(final Neuron n : this.neurons) {
				if (n.type() == t) {
					count++;
				}
			}
			if (count > 0) {
				result += t.symbol()+((count>1)?count:"");
			}
		}
		return result;
	}



	public void kill() {
		this.energy = 0.0;
	}



	@Override
	public double r() {
		return this.radius();
	}


	public void cleanup() {
		this.locate(null);
		for(final Neuron current: this.neurons) {
			current.locate(null);
		}
	}

	public void modify(final int strength) {
		this.x(Util.nextDouble(-0.1, 0.1));
		this.y(Util.nextDouble(-0.1, 0.1));
		this.data.modify(strength);
	}


}
