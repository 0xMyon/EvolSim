package com.github.myon.evolsim.generator;

import com.github.myon.evolsim.NeuronType;
import com.github.myon.evolsim.data.Color;
import com.github.myon.evolsim.data.Position;

public interface Generator {

	int neuronCount();

	double generateThreshold();

	double generateInitial();

	NeuronType generateNeuronType();

	Position generateNeuronPosition();

	Position generateCreaturePosition();

	Color generateColor();

	String generateName();

	double generateConnection();

	double generateCapacity();


}
