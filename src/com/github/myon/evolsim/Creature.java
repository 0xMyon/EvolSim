package com.github.myon.evolsim;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Vector;

import com.github.myon.evolsim.engine.CollisionObject;
import com.github.myon.evolsim.engine.Modifiable;
import com.github.myon.util.Cache;
import com.github.myon.util.Color;
import com.github.myon.util.Tuple;
import com.github.myon.util.Util;


public class Creature extends CollisionObject<Creature> implements Modifiable {

	private static int ID = 0;

	// final attributes
	private final int id = Creature.ID++;
	private final String name;
	private final int generation;
	private final World world;


	// none-final attributes
	private final Map<Tuple<Neuron>, Double> connections = new HashMap<>();
	private final Vector<Neuron> neurons = new Vector<>();
	private final Color color;

	private Map<Neuron, Double> state = new HashMap<>();
	private int age = 0;
	private double capacity;
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
	public Color color() {
		return this.color;
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


	public Creature(final World world) {
		super(world.getCollisionSpace());
		this.world = world;
		this.generation = 0;
		final int numberOfNeurons = world.generator().neuronCount();
		for(int i = 0; i < numberOfNeurons; i++) {
			final Neuron neuron = new Neuron(this, world.generator());
			this.neurons.add(neuron);
		}

		for (final Neuron source : this.neurons) {
			for (final Neuron target : this.neurons) {
				this.connections.put(new Tuple<>(source,target), world.generator().generateConnection());
			}
		}

		this.color = world.generator().generateColor();
		this.orientation = Util.nextAngle();
		this.name = world.generator().generateName();
		this.energy = 5.0*this.neurons.size();
		this.capacity = world.generator().generateCapacity();
	}



	private Creature(final Creature that, final Double x, final Double y) {
		super(that.world.getCollisionSpace(), x, y);
		this.world = that.world;
		this.color = new Color(that.color);
		this.energy = that.energy * Constants.CREATURE_DEVIDE_FACTOR;
		this.generation = that.generation+1;
		this.orientation = Util.nextAngle();
		final Map<Neuron,Neuron> map = new HashMap<>();
		for(final Neuron current: that.neurons) {
			map.put(current, new Neuron(this, current));
		}
		this.neurons.addAll(map.values());
		for(final Entry<Tuple<Neuron>, Double> entry: that.connections.entrySet()) {
			this.connections.put(new Tuple<Neuron>(map.get(entry.getKey().source), map.get(entry.getKey().target)), entry.getValue());
		}
		this.name = this.world.generator().generateName();
		this.capacity = that.capacity;
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



	public Double value(final Neuron neuron) {
		return this.state.getOrDefault(neuron, neuron.init());
	}


	@Override
	public void modify(final int strength) {
		this.x(Util.nextDouble(-0.01, 0.01));
		this.y(Util.nextDouble(-0.01, 0.01));
		if (Util.nextInt(1000000) <= strength/this.neurons.size()) {
			final Neuron neuron = new Neuron(this, this.world.generator());
			this.neurons.add(neuron);
			this.radius.clear();
		} else if (Util.nextInt(1000) == 0) {
			final int r = Util.nextInt(1+this.neurons.size()*6+this.neurons.size()*this.neurons.size());
			if (r < 1) {
				if (Util.nextBoolean()) {
					this.color.modify(strength);
				} else {
					this.capacity += Util.nextDouble(-0.01*strength, 0.01*strength);
				}
			} else if (r < 1+this.neurons.size()*6) {
				this.randomNeuron().modify(strength);
			} else {
				final Tuple<Neuron> x = new Tuple<>(this.randomNeuron(),this.randomNeuron());
				this.connections.put(x, this.connections.getOrDefault(x, 0.0) + Util.nextDouble(-0.01*strength, 0.01*strength));
			}
		}
		for (final Neuron current: this.neurons) {
			current.relocate();
		}
	}




	private Neuron randomNeuron() {
		return this.neurons.get(Util.nextInt(this.neurons.size()-1));
	}



	private final Cache<Double> radius = new Cache<Double>() {
		@Override
		public Double calc() {
			Double result = 0.0;
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
		final double e = neuron.value();
		final double r = neuron.position().length() * Math.cos(neuron.position().alpha() - neuron.position().angle());
		final double f = e * Math.sin(neuron.position().alpha() - neuron.position().angle());
		final double fx = f * Math.cos(this.orientation + neuron.position().angle());
		final double fy = f * Math.sin(this.orientation + neuron.position().angle());
		//System.out.println(this.name + " moved a:"+ (r/(2*Math.PI)) + " x:" + fx + " y:" + fy);
		this.orientation += r;
		this.x(fx);
		this.y(fy);
		this.energy -= e * 0.01;
	}


	public void collect(final Neuron neuron) {
		final Double size = neuron.spaceSize();
		this.energy += neuron.value()*Constants.GLOBAL_COLLECTABLE_ENERGY/(size*size);
	}

	public void predate(final Neuron neuron) {
		// TODO implement
	}

	public void donate(final Neuron neuron) {
		// TODO implement
	}

	public void divide(final Neuron neuron) {
		if (neuron.value() == 1.0) {
			final double x = this.x() + Math.cos(this.orientation+neuron.position().alpha()) * neuron.position().x();
			final double y = this.y() + Math.sin(this.orientation+neuron.position().alpha()) * neuron.position().y();
			if (!this.world.getCollisionSpace().checkPoint(x, y, this)) {
				if (this.energy * Constants.CREATURE_DEVIDE_FACTOR > this.capacity/10) {
					final Creature child = new Creature(this, x, y);
					child.locate(this.world.getCollisionSpace());
					child.modify(1000);
					this.world.add(child);
					this.energy *= Constants.CREATURE_DEVIDE_FACTOR;
				}
			}
		}
	}






	public Collection<Neuron> neurons() {
		return this.neurons;
	}



	public Double connection(final Neuron source, final Neuron target) {
		return this.connections.getOrDefault(new Tuple<>(source,target),0.0);
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


}
