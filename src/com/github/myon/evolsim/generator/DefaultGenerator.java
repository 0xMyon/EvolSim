package com.github.myon.evolsim.generator;

import com.github.myon.evolsim.NeuronType;
import com.github.myon.evolsim.data.Color;
import com.github.myon.evolsim.data.Position;
import com.github.myon.util.Util;

public class DefaultGenerator implements Generator {

	public static final Generator INSTANCE = new DefaultGenerator();


	@Override
	public int neuronCount() {
		return 2 + Util.nextInt(2);
	}

	@Override
	public double generateThreshold() {
		return Util.nextDouble(-10.0, 10.0);
	}

	@Override
	public double generateInitial() {
		return Util.nextDouble(0.0, 1.0);
	}

	@Override
	public NeuronType generateNeuronType() {
		return NeuronType.values()[Util.nextInt(NeuronType.values().length)];
	}

	@Override
	public Position generateNeuronPosition() {
		final Double dist = Util.nextDouble(-5.0, 5.0);
		final Double rot = Util.nextAngle();
		return new Position(Math.sin(rot)*dist, Math.cos(rot)*dist, Util.nextAngle());
	}

	@Override
	public Position generateCreaturePosition() {
		final Double dist = Util.nextDouble(-100.0, 100.0);
		final Double rot = Util.nextAngle();
		return new Position(Math.sin(rot)*dist, Math.cos(rot)*dist, Util.nextAngle());
	}

	@Override
	public Color generateColor() {
		return new Color(Util.nextInt(255), Util.nextInt(255), Util.nextInt(255));
	}

	private static char[] consonants = {'b','c','d','f','g','h','j','k','l','m','n','p','q','r','s','t','v','w','x','z'};
	private static char[] vocals = {'a','i','u','e','o','y'};


	@Override
	public String generateName() {
		String result = "";
		int i = 2 + Util.nextInt(3);
		for(;i>0;i--) {
			if (Util.nextBoolean()) {
				result += DefaultGenerator.consonants[Util.nextInt(DefaultGenerator.consonants.length-1)];
			}
			result += DefaultGenerator.vocals[Util.nextInt(DefaultGenerator.vocals.length-1)];
			if (Util.nextBoolean()) {
				result += DefaultGenerator.consonants[Util.nextInt(DefaultGenerator.consonants.length-1)];
			}
		}
		return result.substring(0,1).toUpperCase() + result.substring(1);
	}

	@Override
	public double generateConnection() {
		return Util.nextDouble(-10.0, 10.0);
	}

	@Override
	public double generateCapacity() {
		return Util.nextDouble(100.0, 500.0);
	}



}
