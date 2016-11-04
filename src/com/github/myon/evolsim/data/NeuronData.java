package com.github.myon.evolsim.data;

import com.github.myon.evolsim.NeuronType;
import com.github.myon.evolsim.generator.Generator;
import com.github.myon.util.Util;

public class NeuronData implements Modifiable<NeuronData> {

	public NeuronData(final Generator generator) {
		this.type = generator.generateNeuronType();
		this.color = generator.generateColor();
		this.initial = generator.generateInitial();
		this.threshold = generator.generateThreshold();
		this.position = generator.generateNeuronPosition();
	}

	public NeuronData(final NeuronData that) {
		this.type = that.type;
		this.color = new Color(that.color);
		this.initial = that.initial;
		this.threshold = that.threshold;
		this.position = new Position(that.position);

	}

	public NeuronData(final NeuronData left, final NeuronData right) {
		if (Util.nextBoolean()) {
			this.type = left.type;
		} else {
			this.type = right.type;
		}

		this.color = new Color(left.color, right.color);

		switch(Util.nextInt(3)) {
		case 0:
			this.threshold = left.threshold;
			break;
		case 1:
			this.threshold = right.threshold;
			break;
		case 2:
			this.threshold = (left.threshold + right.threshold)/2;
			break;
		}

		switch(Util.nextInt(3)) {
		case 0:
			this.initial = left.initial;
			break;
		case 1:
			this.initial = right.initial;
			break;
		case 2:
			this.initial = (left.initial + right.initial)/2;
			break;
		}

		this.position = new Position(left.position, right.position);

	}

	private NeuronType type;
	private final Color color;
	private final Position position;
	private double initial;
	private double threshold;

	@Override
	public void modify(final int strength) {
		if (Util.nextInt(1000) < strength) {
			this.type = NeuronType.values()[Util.nextInt(NeuronType.values().length)];
		} else {
			switch(Util.nextInt(3)) {
			case 0:
				this.color.modify(strength);
				break;
			case 1:
				this.position.modify(strength);
				break;
			case 2:
				this.initial += Util.nextDouble(-0.01*strength, 0.01*strength);
				break;
			case 3:
				this.threshold += Util.nextDouble(-0.01*strength, 0.01*strength);
				break;
			}
		}
	}

	public NeuronType type() {
		return this.type;
	}
	public Color color() {
		return this.color;
	}
	public Position position() {
		return this.position;
	}
	public double initial() {
		return this.initial;
	}
	public double threshold() {
		return this.threshold;
	}





}
