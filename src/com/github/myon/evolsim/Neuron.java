package com.github.myon.evolsim;

import com.github.myon.evolsim.engine.CollisionObject;
import com.github.myon.evolsim.engine.Modifiable;
import com.github.myon.evolsim.generator.Generator;
import com.github.myon.util.Color;
import com.github.myon.util.Position;
import com.github.myon.util.Util;


public class Neuron extends CollisionObject<Neuron> implements Modifiable {

	private final Position position;
	private final Creature creature;
	private final Color color;

	public Neuron(final Creature creature, final Generator generator) {
		this.creature = creature;
		this.schwellwert = generator.generateSchwellwert();
		this.initial = generator.generateInitial();
		this.type = generator.generateNeuronType();
		this.position = generator.generateNeuronPosition();
		this.color = generator.generateColor();
		this.relocate();
	}

	public Neuron(final Creature creature, final Neuron that) {
		this.creature = creature;
		this.schwellwert = that.schwellwert;
		this.initial = that.initial;
		this.type = that.type;
		this.position = new Position(that.position);
		this.color = new Color(that.color);
		this.relocate();
	}

	@Override
	public void relocate() {
		if (this.type == NeuronType.Collector) {
			this.relocate(this.creature().world().getCollectorSpace());
		} else {
			this.locate(null);
		}
	}

	private NeuronType type;
	private Double schwellwert;
	private Double initial;

	public Color color() {
		return this.color;
	}

	public NeuronType type() {
		return this.type;
	}

	public Double init() {
		return this.initial;
	}

	public Position position() {
		return this.position;
	}

	public Creature creature() {
		return this.creature;
	}



	public Double calculateValue() {
		return this.phi(this.Sigma() - this.schwellwert);
	}

	public Double value() {
		return this.creature().value(this);
	}


	private Double Sigma() {
		Double result = 0.0;
		switch(this.type) {
		case Motor:
		case Collector:
		case Predator:
		case Donator:
		case Divider:
		case Pigmetor:
		case Normal:
			for(final Neuron e : this.creature.neurons()) {
				result += this.creature.value(e) * this.creature.connection(this, e);
			}
			return result;
		case Sensor:
			for(final Creature current : this.creature().world().getCollisionSpace().getAllInRadius(this.position().x(), this.position().y(), 10.0)){
				final Double distance = Util.distance(this.position().x(), this.position().y(), current.x(), current.y());
				result += 1/(distance*distance);
			}
			return result;
		case Detector:
			// TODO
			return 0.0;
		case InternalEnergySensor:
			return this.creature().energy() / this.creature().capacity();
		case Compas:
			return (this.creature.orientation() + this.position.alpha()) / (2*Math.PI) % 1;
		default:
			throw new Error("Unimplemented Neuron Type");
		}
	}

	private Double phi(final Double value) {
		if (value >= 0.5) {
			return 1.0;
		} else if (value <= -0.5) {
			return 0.0;
		} else {
			return value + 0.5;
		}
	}



	public void act() {
		switch (this.type) {
		case Normal:
		case Sensor:
		case Detector:
		case Compas:
		case InternalEnergySensor:
			break;
		case Motor:
			this.creature.move(this);
			break;
		case Collector:
			this.creature.collect(this);
			break;
		case Predator:
			this.creature.predate(this);
			break;
		case Donator:
			this.creature.donate(this);
			break;
		case Divider:
			this.creature.divide(this);
			break;
		case Pigmetor:
			// TODO
			break;
		default:
			throw new Error("Unimplemented Neuron Type "+this.type.name());
		}
	}




	@Override
	public void modify(final int strength) {
		if (Util.nextInt(10000) <= strength) {
			this.type = NeuronType.values()[Util.nextInt(NeuronType.values().length)];
			this.relocate();
		} else {
			switch(Util.nextInt(3)) {
			case 0:
				this.color.modify(strength);
				break;
			case 1:
				this.initial += Util.nextDouble(-0.01*strength, 0.01*strength);
				break;
			case 2:
				this.schwellwert += Util.nextDouble(-0.01*strength, 0.01*strength);
				break;
			case 3:
				this.position.modify(strength);
				this.relocate();
				this.creature.resetRadius();
				break;
			}
		}
	}

	@Override
	public double r() {
		return 0.0;
	}



}
