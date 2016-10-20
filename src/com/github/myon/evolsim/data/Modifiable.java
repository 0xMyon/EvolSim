package com.github.myon.evolsim.data;

public interface Modifiable<T extends Modifiable<T>> {

	void modify(int strength);

}
