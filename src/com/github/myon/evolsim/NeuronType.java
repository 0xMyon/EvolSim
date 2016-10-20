package com.github.myon.evolsim;

public enum NeuronType {

	/**
	 * common neuron
	 */
	Normal,

	// INPUT

	/**
	 * propagates the distance to the nearest Creature
	 */
	Sensor,

	/**
	 * propagates the distance to the nearest Creature in the Neurons orientation
	 */
	Detector,

	/**
	 * propagates the Creatures internal energy level
	 */
	InternalEnergySensor,

	/**
	 * propagates the Creatures orientation
	 */
	Compas,



	// OUTPUT

	/**
	 * moves the Creature inside the environment
	 */
	Motor,

	/**
	 * collects energy from the environment
	 */
	Collector,

	/**
	 * steals energy from other creatures
	 */
	Predator,

	/**
	 * donates energy to other creatures
	 */
	Donator,

	/**
	 * changes the Creatures Color
	 */
	Pigmetor,

	/**
	 * creates offsprings of the creature
	 */
	Divider;

	/**
	 * one-character symbol for display
	 * @return the types associated symbol
	 */
	public String symbol() {
		switch(this) {
		case Collector:
			return "C";
		case Divider:
			return "+";
		case Donator:
			return "G";
		case InternalEnergySensor:
			return "E";
		case Motor:
			return "M";
		case Normal:
			return "N";
		case Predator:
			return "T";
		case Sensor:
			return "S";
		case Detector:
			return "A";
		case Pigmetor:
			return "P";
		case Compas:
			return "D";
		default:
			throw new Error("unhadled type: "+this.name());
		}


	}

	public static NeuronType convert(final byte value) {
		return NeuronType.values()[(value<0?-value:value) % NeuronType.values().length];
	}

}
