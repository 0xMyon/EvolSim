package com.github.myon.evolsim.generator;

import com.github.myon.evolsim.NeuronType;
import com.github.myon.util.Color;
import com.github.myon.util.Position;

public interface Generator {

	int neuronCount();

	Double generateSchwellwert();

	Double generateInitial();

	NeuronType generateNeuronType();

	Position generateNeuronPosition();

	Position generateCreaturePosition();

	Color generateColor();

	String generateName();

	Double generateConnection();

	Double generateCapacity();


}
