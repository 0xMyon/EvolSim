package com.github.myon.evolsim;

public enum NeuronType {

	Normal,
	Sensor,
	InternalEnergySensor,
	Motor,
	Collector,

	Compas,

	Predator,
	Donator,

	Detector,
	Pigmetor,

	Divider;

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

}
