package fr.satie.optimization.graph;

/**
 * File <b>GeneratorColor</b> located on fr.satie.optimization.graph
 * GeneratorColor is a part of optimization.
 * <p>
 * Copyright (c) 2021 - 2021 ENS Paris Saclay SATIE.
 * <p>
 *
 * @author Valentin Burgaud (Xharos), {@literal <xharos@islandswars.fr>}
 * Created the 21/05/2021 at 11:47
 * @since 0.2
 */
public enum GeneratorColor {

	GREEN("green"),
	ORANGE("orange"),
	RED("red");

	private final String color;

	GeneratorColor(String color) {
		this.color = color;
	}

	public String getColor() {
		return color;
	}
}
