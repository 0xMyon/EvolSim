package com.github.myon.evolsim.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.github.myon.evolsim.generator.DefaultGenerator;
import com.github.myon.evolsim.generator.Generator;
import com.github.myon.util.Util;

public class CreatureData implements Modifiable<CreatureData> {

	private final Color color;
	private final Vector<NeuronData> neurons;
	private final Map<Integer,Map<Integer,Double>> connections;

	@Override
	public void modify(final int strength) {
		if (Util.nextInt(100) < strength) {
			if (3 > Util.nextInt(3+this.neurons()*8)) {
				this.color.modify(strength);
			} else {
				this.neurons.get(Util.nextInt(this.neurons.size())).modify(strength);
			}
		} else {
			final Map<Integer,Double> c = this.connections.get(Util.nextInt(this.neurons.size()));
			final int i = Util.nextInt(this.neurons.size());
			c.put(i, c.get(i) + Util.nextDouble(-0.01*strength, 0.01*strength));
		}
	}

	public CreatureData(final Generator generator) {
		this.color = generator.generateColor();
		this.neurons = new Vector<>();
		this.connections = new HashMap<>();
		final int n = generator.neuronCount();
		for (int i = 0; i < n; i++) {
			this.neurons.add(new NeuronData(generator));
			this.connections.put(i, new HashMap<>());
			for (int j = 0; j < n; j++) {
				this.connections.get(i).put(j, generator.generateConnection());
			}
		}
	}

	public CreatureData(final CreatureData that) {
		this.color = new Color(that.color);

		this.neurons = new Vector<>();
		this.connections = new HashMap<>();
		for (int i = 0; i < that.neurons.size(); i++) {
			this.neurons.add(new NeuronData(that.neurons.get(i)));
			final Map<Integer,Double> m = new HashMap<>();
			for (int j = 0; j < that.neurons.size(); j++) {
				m.put(j, that.connections.get(i).get(j));
			}
			this.connections.put(i, m);
		}

		if (Util.nextInt(that.neurons.size() * 500) == 0) {

			System.out.println(that.neurons()+1);

			this.connections.put(this.neurons.size(), new HashMap<>());
			for (int i = 0; i < this.neurons.size(); i++) {
				this.connections.get(this.neurons.size()).put(i, DefaultGenerator.INSTANCE.generateConnection());
				this.connections.get(i).put(this.neurons.size(), DefaultGenerator.INSTANCE.generateConnection());
			}
			this.connections.get(this.neurons.size()).put(this.neurons.size(), DefaultGenerator.INSTANCE.generateConnection());
			this.neurons.add(new NeuronData(DefaultGenerator.INSTANCE));
		}


	}

	public CreatureData(final CreatureData left, final CreatureData right) {
		this.color = new Color(left.color, right.color);
		this.neurons = new Vector<>();
		this.connections = new HashMap<>();

		for (int i = 0; i < left.neurons() && i < right.neurons(); i++) {
			switch (Util.nextInt(3)) {
			case 0:
				this.neurons.add(new NeuronData(left.neuron(i)));
				break;
			case 1:
				this.neurons.add(new NeuronData(right.neuron(i)));
				break;
			case 2:
				this.neurons.add(new NeuronData(left.neuron(i), right.neuron(i)));
				break;
			}

			final Map<Integer,Double> m = new HashMap<>();
			for (int j = 0; j < left.neurons() && j < right.neurons(); j++) {
				switch (Util.nextInt(3)) {
				case 0:
					m.put(j, left.connection(i,j));
					break;
				case 1:
					m.put(j, right.connection(i,j));
					break;
				case 2:
					m.put(j, (left.connection(i,j) + right.connection(i,j))/2 );
					break;
				}
			}
			this.connections.put(i, m);

		}

		for (int i = this.neurons(); i < left.neurons(); i++) {
			this.neurons.add(new NeuronData(left.neuron(i)));
			final Map<Integer,Double> m = new HashMap<>();
			for (int j = 0; j < left.neurons(); j++) {
				m.put(j, left.connection(i, j));
				if (i > j) {
					this.connections.get(j).put(i, left.connection(j, i));
				}
			}
			this.connections.put(i, m);
		}

		for (int i = this.neurons(); i < right.neurons(); i++) {
			this.neurons.add(new NeuronData(right.neuron(i)));
			final Map<Integer,Double> m = new HashMap<>();
			for (int j = 0; j < right.neurons(); j++) {
				m.put(j, right.connection(i, j));
				if (i > j) {
					this.connections.get(j).put(i, right.connection(j, i));
				}
			}
			this.connections.put(i, m);
		}

	}

	public Color color() {
		return this.color;
	}
	public int neurons() {
		return this.neurons.size();
	}
	public NeuronData neuron(final int index) {
		return this.neurons.get(index);
	}
	public double connection(final int source, final int target) {
		return this.connections.get(source).get(target);
	}

	public CreatureData combine(final CreatureData data) {
		// TODO Auto-generated method stub
		return null;
	}



}
