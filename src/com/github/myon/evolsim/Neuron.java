package com.github.myon.evolsim;

import com.github.myon.evolsim.data.Color;
import com.github.myon.evolsim.data.NeuronData;
import com.github.myon.evolsim.data.Position;

public class Neuron {

	private final Creature creature;

	private final int id;
	private final NeuronData data;

	public Neuron(final Creature creature, final int id, final NeuronData data) {
		this.creature = creature;
		this.id = id;
		this.data = data;
	}

	public NeuronType type() {
		return this.data.type();
	}

	public double init() {
		return this.data.initial();
	}

	public Creature creature() {
		return this.creature;
	}

	private Double threshold() {
		return this.data.threshold();
	}

	public Color color() {
		return this.data.color();
	}

	public Position position() {
		return this.data.position();
	}

	public Double calculateValue() {
		return this.phi(this.Sigma() - this.threshold());
	}

	public Double value() {
		return this.creature().value(this.id);
	}

	private Double Sigma() {
		Double result = 0.0;
		switch (this.type()) {
		case Motor:
		case Collector:
		case Predator:
		case Donator:
		case Divider:
		case Pigmetor:
		case Normal:
			for (int i = 0; i < this.creature.neurons(); i++) {
				result += this.creature.value(i) * this.creature.connection(i, this.id);
			}
			return result;
		case Sensor:
			// TODO
			return result;
		case Detector:
			// TODO
			return 0.0;
		case InternalEnergySensor:
			return this.creature().energy() / this.creature().capacity();
		case Compas:
			return (this.creature.orientation() + this.position().orientation()) / (2 * Math.PI) % 1;
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
		switch (this.type()) {
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
			throw new Error("Unimplemented Neuron Type " + this.type().name());
		}
	}

	public double getRadius() {
		return 0.0;
	}

	public Color getColor() {
		return this.data.color();
	}

}
